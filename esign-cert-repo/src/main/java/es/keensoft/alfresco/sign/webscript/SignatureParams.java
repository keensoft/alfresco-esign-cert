package es.keensoft.alfresco.sign.webscript;

import java.io.IOException;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;

import es.keensoft.alfresco.sign.webscript.bean.SignatureParamsResponse;

public class SignatureParams extends AbstractWebScript {
	
	private static Log log = LogFactory.getLog(SignatureParams.class);
	
	private String paramsCades;
	private String paramsPades;
	private String signatureAlg;
	private String firstSignaturePosition;
	private String secondSignaturePosition;
	private String thirdSignaturePosition;
	private String fourthSignaturePosition;
	private String fifthSignaturePosition;
	private String sixthSignaturePosition;
	private boolean signaturePurposeEnabled;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		
		Gson gson = new Gson();
		SignatureParamsResponse response = new SignatureParamsResponse();

		try {
			
			response.setParamsCades(paramsCades);
			response.setParamsPades(paramsPades);
			response.setSignatureAlg(signatureAlg);
			
			response.setFirstSignaturePosition(firstSignaturePosition);
			response.setSecondSignaturePosition(secondSignaturePosition);
			response.setThirdSignaturePosition(thirdSignaturePosition);
			response.setFourthSignaturePosition(fourthSignaturePosition);
			response.setFifthSignaturePosition(fifthSignaturePosition);
			response.setSixthSignaturePosition(sixthSignaturePosition);
			
			response.setSignaturePurposeEnabled(signaturePurposeEnabled);
			
		} catch (Exception e) {
			
			log.error(ExceptionUtils.getFullStackTrace(e));
			throw new WebScriptException(e.getMessage(), e);
			
		}

		res.setContentType(MimetypeMap.MIMETYPE_JSON);
		res.getWriter().write(gson.toJson(response));
	}

	public void setParamsCades(String paramsCades) {
		this.paramsCades = paramsCades;
	}

	public void setParamsPades(String paramsPades) {
		this.paramsPades = paramsPades;
	}

	public void setSignatureAlg(String signatureAlg) {
		this.signatureAlg = signatureAlg;
	}

	public void setFirstSignaturePosition(String firstSignaturePosition) {
		this.firstSignaturePosition = firstSignaturePosition;
	}
	
	public void setSecondSignaturePosition(String secondSignaturePosition) {
		this.secondSignaturePosition = secondSignaturePosition;
	}
	
	public void setThirdSignaturePosition(String thirdSignaturePosition) {
		this.thirdSignaturePosition = thirdSignaturePosition;
	}
	
	public void setFourthSignaturePosition(String fourthSignaturePosition) {
		this.fourthSignaturePosition = fourthSignaturePosition;
	}
	
	public void setFifthSignaturePosition(String fifthSignaturePosition) {
		this.fifthSignaturePosition = fifthSignaturePosition;
	}
	
	public void setSixthSignaturePosition(String sixthSignaturePosition) {
		this.sixthSignaturePosition = sixthSignaturePosition;
	}

	public void setSignaturePurposeEnabled(boolean signaturePurposeEnabled) {
		this.signaturePurposeEnabled = signaturePurposeEnabled;
	}
}
