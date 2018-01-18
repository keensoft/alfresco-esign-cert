package es.alfatec.alfresco.evaluators;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

public class DownloadSignatureReportEvaluator  extends BaseEvaluator{

	private final String VALUE = "true";
	private String propertyValue = "false";
	
	@Override
	public boolean evaluate(JSONObject jsonObject) {

		
		final RequestContext  requestContext = ThreadLocalRequestContext.getRequestContext(); 
		final String userId = requestContext.getUserId();
		
		try {
			
			JSONObject node = (JSONObject) jsonObject.get("node");
			
			if(node == null){
				return false;
			}
			else{
				
				//Get signOtherDocs property form alfresco-global.properties calling web service REST /alfatec/alfresco-global/signOtherDoc
				Connector connector = requestContext.getServiceRegistry().getConnectorService().getConnector("alfresco",userId ,ServletUtil.getSession());
				
				Response response = connector.call("/alfatec/alfresco-global/getAlfatecCustomProperties");
				
				if(response.getStatus().getCode() == Status.STATUS_OK){
					
					org.json.JSONObject json = new org.json.JSONObject(response.getResponse());
					propertyValue = (String) json.get("downloadSignatureReport");
				}
				 
				return VALUE.equals(propertyValue);
			}
			
		} catch (Exception e) {
			
			throw new AlfrescoRuntimeException("Failed to run action evaluator: "+e.getMessage());

		}
			
	}
}