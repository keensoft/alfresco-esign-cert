package es.alfatec.alfresco.webscripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;

import es.alfatec.alfresco.AlfatecException;
import es.alfatec.alfresco.AlfatecSignUtils;
import es.alfatec.alfresco.WaterMarkUtils;
import es.alfatec.alfresco.webscripts.bean.DownloadSignatureReportBean;
import es.alfatec.alfresco.webscripts.bean.DownloadSignatureReportResponse;

public class DownloadSignatureReport extends AbstractWebScript {

    private AlfatecSignUtils alfatecSignUtils;
    private WaterMarkUtils waterMarkUtils;
    private NodeService nodeService;
    
	private final String _NO_PRINT_SIGNATURE_INFO = "signature.info.none";
	private final String _NO_PRINT_CSV = "csv.none";
    
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
						NodeRef originalDocumentWithoutSignature = alfatecSignUtils.getOriginalDocumentWithoutSignature(document);
						
						tmpFile = alfatecSignUtils.getTmpFile(originalDocumentWithoutSignature);
						if(!_NO_PRINT_SIGNATURE_INFO.equals(requestBean.getSignatureInfoPlace())){
							List<String> signers = alfatecSignUtils.getSigners(document);
							for(int i = 1; i <= signers.size();i++){
								waterMarkUtils.printSign(tmpFile,signers.get(i-1), i, requestBean.getSignatureInfoPlace());
							}
						}
						
						if(!_NO_PRINT_CSV.equals(requestBean.getCsvPlace())){
							String csv = alfatecSignUtils.generateCSV(document);
							waterMarkUtils.printCSV(tmpFile, csv, requestBean.getCsvPlace(), requestBean.getSignatureInfoPlace());
						}
						
						InputStream inputStreamToPrint = new FileInputStream(tmpFile);
						byte[] base64Content = IOUtils.toByteArray(inputStreamToPrint);
						inputStreamToPrint.close();
												
						String workSpace = (String) nodeService.getProperty(originalDocumentWithoutSignature, ContentModel.PROP_STORE_PROTOCOL);
						String spacesStore = (String) nodeService.getProperty(originalDocumentWithoutSignature, ContentModel.PROP_STORE_IDENTIFIER);
						String uiid = (String) nodeService.getProperty(originalDocumentWithoutSignature, ContentModel.PROP_NODE_UUID);
						
						DownloadSignatureReportResponse downloadSignatureReportResponse = new DownloadSignatureReportResponse(originalDocumentWithoutSignature.toString(), documentName, Base64.encodeBase64String(base64Content), workSpace, spacesStore, uiid);
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
    	}catch(IOException | DocumentException | SignatureException exception){
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