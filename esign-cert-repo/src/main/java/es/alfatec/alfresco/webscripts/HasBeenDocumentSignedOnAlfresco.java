package es.alfatec.alfresco.webscripts;

import java.io.File;
import java.io.IOException;
import java.security.SignatureException;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import es.alfatec.alfresco.AlfatecException;
import es.alfatec.alfresco.AlfatecSignUtils;

public class HasBeenDocumentSignedOnAlfresco extends AbstractWebScript {

    private AlfatecSignUtils alfatecSignUtils;
    private NodeService nodeService;
        
    @Override
	public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException{
    	File tmpFile = null;
    	try{
    		String nodeRefString = request.getParameter("nodeRef");
	    	
	    	if(null != nodeRefString && !nodeRefString.isEmpty()){
		    	NodeRef document = new NodeRef(nodeRefString);
		    	
		    	if(nodeService.exists(document)){
		    		String documentName = (String) nodeService.getProperty(document, ContentModel.PROP_NAME);

					boolean isSignOk = alfatecSignUtils.isSignOk(document);
					if(isSignOk){
						alfatecSignUtils.getOriginalDocumentWithoutSignature(document);
						
						response.setStatus(HttpServletResponse.SC_OK);
				    	response.getWriter().write("Document has been signed on Alfresco");
					}else{
			    		throw new AlfatecException("Document "+documentName+" signature is not valid.", HttpServletResponse.SC_CONFLICT);
					}
		    	}else{
		    		throw new AlfatecException("NodeRef "+nodeRefString+" does not exist.", HttpServletResponse.SC_NOT_FOUND);
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