package es.alfatec.alfresco.webscripts;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;

public class GetAlfatecCustomProperties extends AbstractWebScript{

	private String signOtherDocs;
	private String defaultCSVPlace;
	private String defaultSignatureInfoPlace;
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {

		JSONObject obj = new JSONObject();
        obj.put("signOtherDocs", signOtherDocs);
        obj.put("defaultCSVPlace", defaultCSVPlace);
        obj.put("defaultSignatureInfoPlace",defaultSignatureInfoPlace);
         
        String jsonString = obj.toString();
        response.setContentEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(jsonString);
		
	}

	public void setSignOtherDocs(String signOtherDocs) {
		this.signOtherDocs = signOtherDocs;
	}
	public void setDefaultCSVPlace(String defaultCSVPlace) {
		this.defaultCSVPlace = defaultCSVPlace;
	}
	public void setDefaultSignatureInfoPlace(String defaultSignatureInfoPlace) {
		this.defaultSignatureInfoPlace = defaultSignatureInfoPlace;
	}	
}