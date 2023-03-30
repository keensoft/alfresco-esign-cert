package es.keensoft.alfresco.sign.webscript;

import java.io.IOException;
import java.util.Date;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;

import es.keensoft.alfresco.model.SignModel;
import es.keensoft.alfresco.sign.webscript.bean.SignatureMetadataResponse;

public class SignatureMetadata extends AbstractWebScript {
	
	private static Log log = LogFactory.getLog(SignatureMetadata.class);
	
	private NodeService nodeService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		
		Gson gson = new Gson();
		SignatureMetadataResponse response = new SignatureMetadataResponse();
		
		String reqNodeRef = req.getParameter("nodeRef");

		try {
		    
		    if (NodeRef.isNodeRef(reqNodeRef)) {
			
    		    NodeRef nodeRef = new NodeRef(reqNodeRef);
    			
    			response.setSignatureDate((Date)nodeService.getProperty(nodeRef, SignModel.PROP_DATE));
    			response.setSignatureFormat(nodeService.getProperty(nodeRef, SignModel.PROP_FORMAT).toString());
    			response.setCertificatePrincipal(nodeService.getProperty(nodeRef, SignModel.PROP_CERTIFICATE_PRINCIPAL).toString());
    			response.setCertificateSerialNumber(nodeService.getProperty(nodeRef, SignModel.PROP_CERTIFICATE_SERIAL_NUMBER).toString());
    			response.setCertificateNotAfter((Date)nodeService.getProperty(nodeRef, SignModel.PROP_CERTIFICATE_NOT_AFTER));
    			response.setCertificateIssuer(nodeService.getProperty(nodeRef, SignModel.PROP_CERTIFICATE_ISSUER).toString());
    			
    			if (nodeService.getProperty(nodeRef, SignModel.PROP_SIGNATURE_PURPOSE) != null) {
    				response.setSignaturePurpose(nodeService.getProperty(nodeRef, SignModel.PROP_SIGNATURE_PURPOSE).toString());
    			}
    			
		    }
			
		} catch (Exception e) {
			
			log.error(ExceptionUtils.getRootCauseStackTrace(e));
			throw new WebScriptException(e.getMessage(), e);
			
		}

		res.setContentType(MimetypeMap.MIMETYPE_JSON);
		res.getWriter().write(gson.toJson(response));
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

}
