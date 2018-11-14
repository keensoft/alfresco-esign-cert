package es.alfatec.alfresco.webscripts.bean;

public class PrintSignatureInformation {
	private String signerName;
	private String signatureDate;
	
	public PrintSignatureInformation(String signerName, String signatureDate){
		this.signerName = signerName;
		this.signatureDate = signatureDate;
	}
	
	public String getSignerName() {
		return signerName;
	}
	public void setSignerName(String signerName) {
		this.signerName = signerName;
	}
	public String getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}
}