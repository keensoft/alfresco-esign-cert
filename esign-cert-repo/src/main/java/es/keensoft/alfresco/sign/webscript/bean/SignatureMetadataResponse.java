package es.keensoft.alfresco.sign.webscript.bean;

import java.util.Date;

public class SignatureMetadataResponse {
	
	private Date signatureDate;
	private String signatureFormat;
	private String certificatePrincipal;
	private String certificateSerialNumber;
	private String certificateIssuer;
	private Date certificateNotAfter;
	private String signaturePurpose;
	
	public String getCertificatePrincipal() {
		return certificatePrincipal;
	}
	public void setCertificatePrincipal(String certificatePrincipal) {
		this.certificatePrincipal = certificatePrincipal;
	}
	public String getCertificateSerialNumber() {
		return certificateSerialNumber;
	}
	public void setCertificateSerialNumber(String certificateSerialNumber) {
		this.certificateSerialNumber = certificateSerialNumber;
	}
	public String getCertificateIssuer() {
		return certificateIssuer;
	}
	public void setCertificateIssuer(String certificateIssuer) {
		this.certificateIssuer = certificateIssuer;
	}
	public Date getCertificateNotAfter() {
		return certificateNotAfter;
	}
	public void setCertificateNotAfter(Date certificateNotAfter) {
		this.certificateNotAfter = certificateNotAfter;
	}
	public Date getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	public String getSignatureFormat() {
		return signatureFormat;
	}
	public void setSignatureFormat(String signatureFormat) {
		this.signatureFormat = signatureFormat;
	}
	public String getSignaturePurpose() {
		return signaturePurpose;
	}
	public void setSignaturePurpose(String signaturePurpose) {
		this.signaturePurpose = signaturePurpose;
	}


}
