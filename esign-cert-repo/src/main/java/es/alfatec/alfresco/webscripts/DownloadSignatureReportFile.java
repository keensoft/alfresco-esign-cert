package es.alfatec.alfresco.webscripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.lowagie.text.DocumentException;

import es.alfatec.alfresco.AlfatecException;
import es.alfatec.alfresco.AlfatecSignUtils;
import es.alfatec.alfresco.WaterMarkUtils;
import es.alfatec.alfresco.webscripts.bean.PrintSignatureInformation;

public class DownloadSignatureReportFile extends AbstractWebScript {

    private AlfatecSignUtils alfatecSignUtils;
    private WaterMarkUtils waterMarkUtils;
    private NodeService nodeService;
    
	private final String _NO_PRINT_SIGNATURE_INFO = "signature.info.none";
	private final String _NO_PRINT_CSV = "csv.none";
    
    @Override
	public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException{
    	File tmpFile = null;
    	String nodeRef = request.getParameter("nodeRef");
    	String csvPlace = request.getParameter("csvPlace");
    	String signatureInfoPlace = request.getParameter("signatureInfoPlace");

    	try{
    		if(null != nodeRef && !nodeRef.isEmpty() && null != csvPlace && !csvPlace.isEmpty() && null != signatureInfoPlace && !signatureInfoPlace.isEmpty()){
		    	NodeRef document = new NodeRef(nodeRef);

		    	if(nodeService.exists(document)){
		    		String documentName = (String) nodeService.getProperty(document, ContentModel.PROP_NAME);

					boolean isSignOk = alfatecSignUtils.isSignOk(document);
					if(isSignOk){
						NodeRef originalDocumentWithoutSignature = alfatecSignUtils.getOriginalDocumentWithoutSignature(document);
						
						tmpFile = alfatecSignUtils.getTmpFile(originalDocumentWithoutSignature);
						if(!_NO_PRINT_SIGNATURE_INFO.equals(signatureInfoPlace)){
							List<PrintSignatureInformation> signatureOptions = alfatecSignUtils.getSignatureOptions(document);
							for(int i = 1; i <= signatureOptions.size();i++){
								waterMarkUtils.printSign(tmpFile,signatureOptions.get(i-1).getSignerName(), i, signatureInfoPlace, signatureOptions.get(i-1).getSignatureDate());
							}
						}
						
						if(!_NO_PRINT_CSV.equals(csvPlace)){
							String csv = alfatecSignUtils.generateCSV(document);
							waterMarkUtils.printCSV(tmpFile, csv, csvPlace, signatureInfoPlace);
						}
						 
						response.setStatus(HttpServletResponse.SC_OK);
				    	response.setContentType(MimetypeMap.MIMETYPE_PDF);
				    	response.setHeader("content-disposition","attachment; filename="+documentName);
				    	response.setContentEncoding("UTF-8");
				    	
				    	response.getOutputStream().write(Files.readAllBytes(tmpFile.toPath())); 	
					}else{
			    		throw new AlfatecException("Document "+documentName+" signature is not valid.", HttpServletResponse.SC_CONFLICT);
					}
		    	}else{
		    		throw new AlfatecException("NodeRef "+document.toString()+" does not exist.", HttpServletResponse.SC_NOT_FOUND);
		    	}
	    	}else{
	    		throw new AlfatecException("Bad request.",HttpServletResponse.SC_BAD_REQUEST);
	    	}
    	}catch(AlfatecException exception){
    		response.setStatus(exception.getResultCode());
    		response.getWriter().write(exception.getExceptionMessage());
    	}catch(IOException | DocumentException exception){
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

	public void setWaterMarkUtils(WaterMarkUtils waterMarkUtils) {
		this.waterMarkUtils = waterMarkUtils;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}