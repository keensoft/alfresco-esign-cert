package es.alfatec.alfresco;

public class AlfatecException extends Exception{

	private static final long serialVersionUID = 1L;
	private String exceptionMessage;
	private int resultCode;

	public AlfatecException(String exceptionMessage, int resultCode){
		this.exceptionMessage = exceptionMessage;
		this.resultCode = resultCode;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public int getResultCode() {
		return resultCode;
	}
}