package es.alfatec.alfresco.webscripts;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;

public class SignOtherDocsProperty extends AbstractWebScript{

	private String signOtherDocs;
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(WebScriptRequest request, WebScriptResponse response)
			throws IOException {

		JSONObject obj = new JSONObject();
        obj.put("signOtherDocs", signOtherDocs);
         
        String jsonString = obj.toString();
        response.setContentEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(jsonString);
		
	}


	public void setSignOtherDocs(String signOtherDocs) {
		this.signOtherDocs = signOtherDocs;
	}

	
	
}
