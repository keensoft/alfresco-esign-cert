package es.alfatec.alfresco.webscripts.bean;

public class DownloadSignatureReportBean {
	private String nodeRef;
	private String csvPlace;
	private String signatureInfoPlace;

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getCsvPlace() {
		return csvPlace;
	}

	public void setCsvPlace(String csvPlace) {
		this.csvPlace = csvPlace;
	}

	public String getSignatureInfoPlace() {
		return signatureInfoPlace;
	}

	public void setSignatureInfoPlace(String signatureInfoPlace) {
		this.signatureInfoPlace = signatureInfoPlace;
	}
}	