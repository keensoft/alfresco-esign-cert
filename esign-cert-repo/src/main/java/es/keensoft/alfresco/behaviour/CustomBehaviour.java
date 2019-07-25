package es.keensoft.alfresco.behaviour;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;

import es.keensoft.alfresco.model.SignModel;

public class CustomBehaviour implements 
    NodeServicePolicies.OnCreateNodePolicy,
    NodeServicePolicies.OnMoveNodePolicy,
    ContentServicePolicies.OnContentUpdatePolicy {
	
	private static Log logger = LogFactory.getLog(CustomBehaviour.class);
	
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private VersionService versionService;
	private ContentService contentService;
	private MessageService messageService;
	
	private static final String PADES = "PAdES";
	
	public void init() {
		policyComponent.bindClassBehaviour(
		        NodeServicePolicies.OnCreateNodePolicy.QNAME,
		        ContentModel.TYPE_CONTENT,
		        new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnMoveNodePolicy.QNAME,
                SignModel.ASPECT_SIGNED,
                new JavaBehaviour(this, "onMoveNode", NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(
		        ContentServicePolicies.OnContentUpdatePolicy.QNAME,
		        ContentModel.TYPE_CONTENT,
		        new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT));
	}	
	
	@Override
	public void onCreateNode(ChildAssociationRef childNodeRef) {

		NodeRef node = childNodeRef.getChildRef();
		
		if (!nodeService.exists(node)) {
			return; 
		}
		
		processSignatures(node);
	}



	@Override
	public void onContentUpdate(NodeRef nodeRef, boolean newContent) {
		
	    if (nodeService.exists(nodeRef) && !newContent) {
			processSignatures(nodeRef);
		}

	}
	
    @Override
    public void onMoveNode(ChildAssociationRef from, ChildAssociationRef to) {
        
        for (AssociationRef signatureAssoc : nodeService.getTargetAssocs(from.getChildRef(), SignModel.ASSOC_SIGNATURE)) {
            nodeService.moveNode(
                    signatureAssoc.getTargetRef(), 
                    to.getParentRef(), 
                    ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(nodeService.getProperty(signatureAssoc.getTargetRef(), ContentModel.PROP_NAME).toString()));
        }

    }
	
    private void processSignatures(NodeRef node) {
        
        ContentData contentData = (ContentData) nodeService.getProperty(node, ContentModel.PROP_CONTENT);
        
        if (contentData != null && contentData.getMimetype().equalsIgnoreCase(MimetypeMap.MIMETYPE_PDF)) {
            
            ArrayList<Map<QName, Serializable>> signatures = getDigitalSignatures(node);
            
            if (signatures != null) {
                
                // Remove signatures from previous version
                removeSignatureMetadata(node);
                
                // Create signatures from PDF source
                for (Map<QName, Serializable> aspectProperties : signatures) {
                    
                    String originalFileName = nodeService.getProperty(node, ContentModel.PROP_NAME).toString();
                    String signatureFileName = FilenameUtils.getBaseName(originalFileName) + "-" 
                        + System.currentTimeMillis() + "-" + PADES;
                
                    // Creating a node reference without type (no content and no folder): remains invisible for Share
                    NodeRef signatureNodeRef = nodeService.createNode(
                            nodeService.getPrimaryParent(node).getParentRef(),
                            ContentModel.ASSOC_CONTAINS, 
                            QName.createQName(signatureFileName), 
                            ContentModel.TYPE_CMOBJECT).getChildRef();
                    
                    nodeService.createAssociation(node, signatureNodeRef, SignModel.ASSOC_SIGNATURE);
                    nodeService.createAssociation(signatureNodeRef, node, SignModel.ASSOC_DOC);
                    
                    aspectProperties.put(SignModel.PROP_FORMAT, PADES);
                    nodeService.addAspect(signatureNodeRef, SignModel.ASPECT_SIGNATURE, aspectProperties);
                    
                }
                
                // Implicit signature aspect
                Map<QName, Serializable> aspectSignedProperties = new HashMap<QName, Serializable>(); 
                aspectSignedProperties.put(SignModel.PROP_TYPE, I18NUtil.getMessage("signature.implicit"));
                nodeService.addAspect(node,  SignModel.ASPECT_SIGNED, aspectSignedProperties);
                
            } else {
                
                if (nodeService.hasAspect(node, SignModel.ASPECT_SIGNED)) {
                    removeSignatureMetadata(node);
                }
                
            }
        }
        
    }
    
    private void removeSignatureMetadata(NodeRef nodeRef) {
        
        if (nodeService.hasAspect(nodeRef, SignModel.ASPECT_SIGNED)) {
             List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(nodeRef, SignModel.ASSOC_SIGNATURE);
             for (AssociationRef targetAssoc : targetAssocs) {
                 nodeService.removeAssociation(targetAssoc.getSourceRef(), targetAssoc.getTargetRef(), targetAssoc.getTypeQName());
             }
             List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(nodeRef, SignModel.ASSOC_DOC);
             for (AssociationRef sourceAssoc : sourceAssocs) {
                 nodeService.removeAssociation(sourceAssoc.getSourceRef(), sourceAssoc.getTargetRef(), sourceAssoc.getTypeQName());
                 nodeService.deleteNode(sourceAssoc.getSourceRef());
             }
        }
        nodeService.removeAspect(nodeRef, SignModel.ASPECT_SIGNED);

    }
    
    public ArrayList<Map<QName, Serializable>> getDigitalSignatures(NodeRef node) {
		
		InputStream is = null;
		
		try {
		
			ContentReader contentReader = contentService.getReader(node, ContentModel.PROP_CONTENT);
			is = contentReader.getContentInputStream();
			
			// For SHA-256 and upper
			loadBCProvider();
			
			PdfReader reader = new PdfReader(is);
	        AcroFields af = reader.getAcroFields();
	        ArrayList<String> names = af.getSignatureNames();
	        if (names == null || names.isEmpty()) return null;
	        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	        ks.load(null, null);
	        ArrayList<Map<QName, Serializable>> aspects = new ArrayList<Map<QName, Serializable>>();
	        for (String name : names) {
	        	try{
		            PdfPKCS7 pk = af.verifySignature(name);
		            X509Certificate certificate = pk.getSigningCertificate();
		           
		            //Set aspect properties for each signature
		            Map<QName, Serializable> aspectSignatureProperties = new HashMap<QName, Serializable>(); 
		            if (pk.getSignDate() != null) aspectSignatureProperties.put(SignModel.PROP_DATE, pk.getSignDate().getTime());
		    		aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_PRINCIPAL, certificate.getSubjectX500Principal().toString());
		    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_SERIAL_NUMBER, certificate.getSerialNumber().toString());
		    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_NOT_AFTER, certificate.getNotAfter());
		    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_ISSUER, certificate.getIssuerX500Principal().toString());
		    	    aspects.add(aspectSignatureProperties);
		    	    
	        	} catch(Exception e) {
	        		
	        		//Set aspect errorSign properties
		            Map<QName, Serializable> aspectErrorSignatureProperties = new HashMap<QName, Serializable>(); 
		            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		            String date = formatDate.format(new Date());
		            aspectErrorSignatureProperties.put(SignModel.PROP_ERROR_SIGN, messageService.getMessage("sign.error") + " - " + date);    
		            nodeService.addAspect(node, SignModel.ASPECT_ERROR_SIGNATURE, aspectErrorSignatureProperties);
	        		
	    			logger.warn("Signature has an error or it's invalid!", e);
	        	}
	        }
	        
			return aspects;
			
		} catch (Exception e) {
			
			// Not every PDF has a signature inside
			logger.warn("No signature found!", e);
			return null;
			
			// WARN: Do not throw this exception up, as it will break WedDAV PDF files uploading 
		} finally {// As this verification can be included in a massive operation, closing files is required
			try {
			    if (is != null) is.close();
			} catch (IOException ioe) {}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void loadBCProvider() {
        try {
            Class c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            Security.insertProviderAt((Provider)c.newInstance(), 2000);
        } catch(Exception e) {
            // provider is not available
        }		
	}
	
	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public VersionService getVersionService() {
		return versionService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}
	
	public ContentService getContentService() {
		return contentService;
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}