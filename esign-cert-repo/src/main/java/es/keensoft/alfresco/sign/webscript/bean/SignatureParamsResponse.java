package es.keensoft.alfresco.sign.webscript.bean;

public class SignatureParamsResponse {
	
	private String paramsCades;
	private String paramsPades;
	private String signatureAlg;
	private String firstSignaturePosition;
	private String secondSignaturePosition;
	private String thirdSignaturePosition;
	private String fourthSignaturePosition;
	private String fifthSignaturePosition;
	
	private Boolean useServerTime;
	
	public String getParamsCades() {
		return paramsCades;
	}
	public void setParamsCades(String paramsCades) {
		this.paramsCades = paramsCades;
	}
	public String getParamsPades() {
		return paramsPades;
	}
	public void setParamsPades(String paramsPades) {
		this.paramsPades = paramsPades;
	}
	public String getSignatureAlg() {
		return signatureAlg;
	}	
	public void setSignatureAlg(String signatureAlg) {
		this.signatureAlg = signatureAlg;
	}
	public String getFirstSignaturePosition() {
		return firstSignaturePosition;
	}
	public void setFirstSignaturePosition(String firstSignaturePosition) {
		this.firstSignaturePosition = firstSignaturePosition;
	}
	public String getSecondSignaturePosition() {
		return secondSignaturePosition;
	}
	public void setSecondSignaturePosition(String secondSignaturePosition) {
		this.secondSignaturePosition = secondSignaturePosition;
	}
	public String getThirdSignaturePosition() {
		return thirdSignaturePosition;
	}
	public void setThirdSignaturePosition(String thirdSignaturePosition) {
		this.thirdSignaturePosition = thirdSignaturePosition;
	}
	public String getFourthSignaturePosition() {
		return fourthSignaturePosition;
	}
	public void setFourthSignaturePosition(String fourthSignaturePosition) {
		this.fourthSignaturePosition = fourthSignaturePosition;
	}
	public String getFifthSignaturePosition() {
		return fifthSignaturePosition;
	}
	public void setFifthSignaturePosition(String fifthSignaturePosition) {
		this.fifthSignaturePosition = fifthSignaturePosition;
	}
	public Boolean getUseServerTime() {
		return useServerTime;
	}
	public void setUseServerTime(Boolean useServerTyme){
		this.useServerTime = useServerTyme;
	}
}
