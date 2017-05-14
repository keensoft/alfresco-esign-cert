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

public class SignOtherDocsEvaluator  extends BaseEvaluator{

	private final static String value = "true";
	private String propertyValue = null;
	private Boolean caseSensitive = false;
	
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
				
				Response response = connector.call("/alfatec/alfresco-global/signOtherDocs");
				
				if(response.getStatus().getCode() == Status.STATUS_OK){
					
					org.json.JSONObject json = new org.json.JSONObject(response.getResponse());
					propertyValue = (String) json.get("signOtherDocs");
				}
				 
				//Compare values
				if(caseSensitive){
					return propertyValue.equals(value);
				}
				else{
					return propertyValue.equalsIgnoreCase(value);
				}
				
				
			}
			
		} catch (Exception e) {
			
			throw new AlfrescoRuntimeException("Failed to run action evaluator: "+e.getMessage());
	
		}
			
	}

	public void setCaseSensitive(Boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}



}
