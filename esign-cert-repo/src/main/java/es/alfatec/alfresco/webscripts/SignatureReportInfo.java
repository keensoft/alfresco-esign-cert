package es.alfatec.alfresco.webscripts;

import java.io.File;
import java.io.IOException;
import java.security.SignatureException;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;

import es.alfatec.alfresco.AlfatecException;
import es.alfatec.alfresco.AlfatecSignUtils;
import es.alfatec.alfresco.webscripts.bean.DownloadSignatureReportBean;
import es.alfatec.alfresco.webscripts.bean.DownloadSignatureReportResponse;

public class SignatureReportInfo extends AbstractWebScript {

    private AlfatecSignUtils alfatecSignUtils;
    private NodeService nodeService;
        
    @Override
	public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException{
    	Gson gson = new Gson();
    	File tmpFile = null;
    	try{
	    	String postRequest = request.getContent().getContent();
	    	DownloadSignatureReportBean requestBean = gson.fromJson(postRequest, DownloadSignatureReportBean.class);
	    	
	    	if(null != requestBean && null != requestBean.getNodeRef() && !requestBean.getNodeRef().isEmpty()){
		    	NodeRef document = new NodeRef(requestBean.getNodeRef());
		    	
		    	if(nodeService.exists(document)){
		    		String documentName = (String) nodeService.getProperty(document, ContentModel.PROP_NAME);

					boolean isSignOk = alfatecSignUtils.isSignOk(document);
					if(isSignOk){
						alfatecSignUtils.getOriginalDocumentWithoutSignature(document);
						
						DownloadSignatureReportResponse downloadSignatureReportResponse = new DownloadSignatureReportResponse(document.toString(), documentName, requestBean.getCsvPlace(), requestBean.getSignatureInfoPlace());
						response.setStatus(HttpServletResponse.SC_OK);
				    	response.setContentType(MimetypeMap.MIMETYPE_JSON);
				    	response.getWriter().write(gson.toJson(downloadSignatureReportResponse));
					}else{
			    		throw new AlfatecException("Document "+documentName+" signature is not valid.", HttpServletResponse.SC_CONFLICT);
					}
		    	}else{
		    		throw new AlfatecException("NodeRef "+requestBean.getNodeRef()+" does not exist.", HttpServletResponse.SC_NOT_FOUND);
		    	}
	    	}else{
	    		throw new AlfatecException("Bad request.",HttpServletResponse.SC_BAD_REQUEST);
	    	}
    	}catch(AlfatecException exception){
    		response.setStatus(exception.getResultCode());
    		response.getWriter().write(exception.getExceptionMessage());
    	}catch(IOException | SignatureException exception){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		response.getWriter().write("Opps... There was an uncontrolled error. Please contact with your support team.");
		}finally{
			if(null != tmpFile){
				tmpFile.delete();
			}
		}
    }

	public void setAlfatecSignUtils(AlfatecSignUtils alfatecSignUtils) {
		this.alfatecSignUtils = alfatecSignUtils;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}