package es.keensoft.alfresco.sign.webscript.bean;

public class SaveSignRequest {
	
	private String nodeRef;
	private String signerRole;
	private String dataToSign;
	private String signedData;
	private String signerData;
	private String mimeType;
	private String signerPostition;
	private String signaturePurpose;

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getSignerRole() {
		return signerRole;
	}

	public void setSignerRole(String signerRole) {
		this.signerRole = signerRole;
	}

	public String getDataToSign() {
		return dataToSign;
	}

	public void setDataToSign(String dataToSign) {
		this.dataToSign = dataToSign;
	}

	public String getSignedData() {
		return signedData;
	}

	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}

	public String getSignerData() {
		return signerData;
	}

	public void setSignerData(String signerData) {
		this.signerData = signerData;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getSignerPostition() {
		return signerPostition;
	}

	public void setSignerPostition(String signerPostition) {
		this.signerPostition = signerPostition;
	}

	public String getSignaturePurpose() {
		return signaturePurpose;
	}

	public void setSignaturePurpose(String signaturePurpose) {
		this.signaturePurpose = signaturePurpose;
	}
	
	

}
