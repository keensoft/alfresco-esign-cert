package es.alfatec.alfresco.webscripts.bean;

public class DownloadSignatureReportResponse {
	private String nodeRef;
	private String base64Content;
	private String documentName;
	private String uuid;
	private String storeProtocol;
	private String storeIdentifier;
	
	public DownloadSignatureReportResponse(String nodeRef, String documentName, String base64Content, String storeProtocol, String storeIdentifier, String uuid) {
		this.nodeRef = nodeRef;
		this.documentName = documentName;
		this.base64Content = base64Content;
		this.storeProtocol = storeProtocol;
		this.storeIdentifier = storeIdentifier;
		this.uuid = uuid;
	}
	
	public String getNodeRef() {
		return nodeRef;
	}
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
	public String getBase64Content() {
		return base64Content;
	}
	public void setBase64Content(String base64Content) {
		this.base64Content = base64Content;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getStoreProtocol() {
		return storeProtocol;
	}
	public void setStoreProtocol(String storeProtocol) {
		this.storeProtocol = storeProtocol;
	}
	public String getStoreIdentifier() {
		return storeIdentifier;
	}
	public void setStoreIdentifier(String storeIdentifier) {
		this.storeIdentifier = storeIdentifier;
	}
}