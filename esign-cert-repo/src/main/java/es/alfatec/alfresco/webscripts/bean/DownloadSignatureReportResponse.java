package es.alfatec.alfresco.webscripts.bean;

public class DownloadSignatureReportResponse {
	private String nodeRef;
	private String documentName;
	private String signatureInfoPlace;
	private String csvPlace;
	
	public DownloadSignatureReportResponse(String nodeRef, String documentName, String csvPlace, String signatureInfoPlace) {
		this.signatureInfoPlace = signatureInfoPlace;
		this.csvPlace = csvPlace;
		this.nodeRef = nodeRef;
		this.documentName = documentName;
	}
	
	public String getNodeRef() {
		return nodeRef;
	}
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getSignatureInfoPlace() {
		return signatureInfoPlace;
	}
	public void setSignatureInfoPlace(String signatureInfoPlace) {
		this.signatureInfoPlace = signatureInfoPlace;
	}
	public String getCsvPlace() {
		return csvPlace;
	}
	public void setCsvPlace(String csvPlace) {
		this.csvPlace = csvPlace;
	}
}