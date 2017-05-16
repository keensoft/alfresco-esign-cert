package es.keensoft.alfresco.sign.webscript;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;

import es.keensoft.alfresco.model.SignModel;
import es.keensoft.alfresco.sign.webscript.bean.BasicResponse;
import es.keensoft.alfresco.sign.webscript.bean.SaveSignRequest;


public class SaveSign extends AbstractWebScript {
	
	private static Log log = LogFactory.getLog(SaveSign.class);
	
	private static final String RETURN_CODE_OK = "OK";	
	private static final String MIMETYPE_CMS = "application/cms";
	private static final String PDF_EXTENSION = "pdf";
	private static final String JS_UNDEFINED = "undefined";
	private static final String CADES_BES_DETACHED = "CAdES-BES Detached";
	private static final String PADES = "PAdES";
	private static final String CSIG_EXTENSION = ".CSIG";
	
	private CheckOutCheckInService checkOutCheckInService;
	private VersionService versionService;
	private ContentService contentService;
	private NodeService nodeService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		
		Gson gson = new Gson();
		SaveSignRequest request;
		BasicResponse response = new BasicResponse();
		
		try {
			
			String jsonText = req.getContent().getContent();
			request = gson.fromJson(jsonText, SaveSignRequest.class);
			
			if(StringUtils.isBlank(request.getSignedData()) || request.getSignedData().equals(JS_UNDEFINED))
				throw new WebScriptException("Signed data is empty or null.");
			
		    Map<QName, Serializable> aspectSignatureProperties = new HashMap<QName, Serializable>();
            
		    if(StringUtils.isBlank(request.getSignerData()) || request.getSignerData().equals(JS_UNDEFINED)) {
				log.warn("Signer data is empty or null.");
			} else {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(request.getSignerData()));
				X509Certificate certificate = (X509Certificate) cf.generateCertificate(bais);
				aspectSignatureProperties = setCertificateProperties(certificate);
			}
		    
		    //Document to sign
			NodeRef nodeRef = new NodeRef(request.getNodeRef());
			
			versionService.ensureVersioningEnabled(nodeRef, null);
			
			Map<QName, Serializable> aspectSignedProperties = new HashMap<QName, Serializable>();
			if (request.getMimeType().equals(PDF_EXTENSION)) { // PAdES
			    storeSignPDF(nodeRef, request.getSignedData(), request.getSignaturePurpose(), aspectSignatureProperties);
			    aspectSignedProperties.put(SignModel.PROP_TYPE, I18NUtil.getMessage("signature.implicit"));
			    if(request.getSignerPostition() != null)
			    {
			    	handleSignedPositionAspect(nodeRef, request.getSignerPostition());
			    }
			} else { // CAdES
			    storeSignOther(nodeRef, request.getSignedData(), request.getSignaturePurpose(), aspectSignatureProperties);
			    aspectSignedProperties.put(SignModel.PROP_TYPE, I18NUtil.getMessage("signature.explicit"));
			}
			nodeService.addAspect(nodeRef, SignModel.ASPECT_SIGNED, aspectSignedProperties);
			
			response.setCode(RETURN_CODE_OK);
			
		} catch (Exception e) {
			throw new WebScriptException(e.getMessage(), e);
		}

		res.getWriter().write(gson.toJson(response));
	}
	
	private void storeSignOther(NodeRef originalNodeRef, String signedData, String purpose, Map<QName, Serializable> aspectProperties) throws IOException {
		
		NodeRef parentRef = nodeService.getPrimaryParent(originalNodeRef).getParentRef();
		String originalFileName = nodeService.getProperty(originalNodeRef, ContentModel.PROP_NAME).toString();
		String signatureFileName = FilenameUtils.getBaseName(originalFileName) + CSIG_EXTENSION;
		NodeRef signatureNodeRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, 
				QName.createQName(signatureFileName), ContentModel.TYPE_CONTENT).getChildRef();

		ContentWriter writer = contentService.getWriter(signatureNodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MIMETYPE_CMS);
		OutputStream contentOutputStream = writer.getContentOutputStream();
		IOUtils.write(Base64.decodeBase64(signedData), contentOutputStream);
		contentOutputStream.close();
		
		nodeService.setProperty(signatureNodeRef, ContentModel.PROP_NAME, signatureFileName);
		
		nodeService.createAssociation(originalNodeRef, signatureNodeRef, SignModel.ASSOC_SIGNATURE);
		nodeService.createAssociation(signatureNodeRef, originalNodeRef, SignModel.ASSOC_DOC);
		
	    aspectProperties.put(SignModel.PROP_FORMAT, CADES_BES_DETACHED);
	    aspectProperties.put(SignModel.PROP_DATE, new Date());
	    if(purpose != null)
	    {
	    	aspectProperties.put(SignModel.PROP_SIGNATURE_PURPOSE, purpose);
	    }
		nodeService.addAspect(signatureNodeRef, SignModel.ASPECT_SIGNATURE, aspectProperties);
		
	}
	
	private void storeSignPDF(NodeRef originalNodeRef, String signedData, String purpose, Map<QName, Serializable> aspectProperties) throws IOException {
		
		String originalFileName = nodeService.getProperty(originalNodeRef, ContentModel.PROP_NAME).toString();
		String signatureFileName = FilenameUtils.getBaseName(originalFileName) + "-" + System.currentTimeMillis() + "-" + PADES;
		
		// Update signature
		ContentWriter writer = contentService.getWriter(originalNodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_PDF);
		OutputStream contentOutputStream = writer.getContentOutputStream();
		IOUtils.write(Base64.decodeBase64(signedData), contentOutputStream);
		contentOutputStream.close();
		
		// Creating a node reference without type (no content and no folder), remains invisible for Share
		NodeRef signatureNodeRef = nodeService.createNode(
				nodeService.getPrimaryParent(originalNodeRef).getParentRef(),
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(signatureFileName), 
				ContentModel.TYPE_CMOBJECT).getChildRef();
		
		nodeService.createAssociation(originalNodeRef, signatureNodeRef, SignModel.ASSOC_SIGNATURE);
		nodeService.createAssociation(signatureNodeRef, originalNodeRef, SignModel.ASSOC_DOC);
		
	    aspectProperties.put(SignModel.PROP_FORMAT, PADES);
	    aspectProperties.put(SignModel.PROP_DATE, new Date());
	    if(purpose != null)
	    {
	    	aspectProperties.put(SignModel.PROP_SIGNATURE_PURPOSE, purpose);
	    }
		nodeService.addAspect(signatureNodeRef, SignModel.ASPECT_SIGNATURE, aspectProperties);
	
	}
	
	public NodeRef getSignatureFolder(NodeRef nodeRef, String folderName) {
		
		NodeRef folderNodeRef = null;
		
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(nodeRef, new HashSet<QName>(Arrays.asList(ContentModel.TYPE_FOLDER)));
		for (ChildAssociationRef child : childs) {
			String name = nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME).toString();
			if (folderName.equals(name)) {
				folderNodeRef = child.getChildRef();
				break;
			}
		}
		
		if (folderNodeRef == null) {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, folderName);
			folderNodeRef = nodeService.createNode(
					nodeService.getPrimaryParent(nodeRef).getParentRef(), 
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(folderName), 
					ContentModel.TYPE_FOLDER,
					properties).getChildRef();
		}
		
		return folderNodeRef;
		
	}
	
	private Map<QName, Serializable> setCertificateProperties(X509Certificate certificate) {
	    Map<QName, Serializable> aspectSignatureProperties = new HashMap<QName, Serializable>(); 
	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_PRINCIPAL, certificate.getSubjectX500Principal().toString());
	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_SERIAL_NUMBER, certificate.getSerialNumber().toString());
	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_NOT_AFTER, certificate.getNotAfter());
	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_ISSUER, certificate.getIssuerX500Principal().toString());
	    return aspectSignatureProperties;
	}
	
	private void handleSignedPositionAspect(NodeRef nodeRef, String signerPostition) {
		switch (signerPostition)
		{
		case "1":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_FIRST_POSITION_SIGNED, null);
			break;
		case "2":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_SECOND_POSITION_SIGNED, null);
			break;
		case "3":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_THIRD_POSITION_SIGNED, null);
			break;
		case "4":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_FOURTH_POSITION_SIGNED, null);
			break;
		case "5":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_FIFTH_POSITION_SIGNED, null);
			break;
		case "6":
			nodeService.addAspect(nodeRef, SignModel.ASPECT_SIXTH_POSITION_SIGNED, null);
			break;
		}
		
	}	
	
	public CheckOutCheckInService getCheckOutCheckInService() {
		return checkOutCheckInService;
	}

	public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService) {
		this.checkOutCheckInService = checkOutCheckInService;
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

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

}
