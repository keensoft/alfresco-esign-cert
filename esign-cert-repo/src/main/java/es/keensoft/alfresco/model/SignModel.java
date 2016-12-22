package es.keensoft.alfresco.model;

import org.alfresco.service.namespace.QName;

public class SignModel {
	
	public static final String URI = "http://www.alfresco.com/model/signmodel/1.0";
	
	public static final QName ASPECT_SIGNED = QName.createQName(URI, "signed");
	public static final QName PROP_TYPE = QName.createQName(URI, "type");
	public static enum SIGNATURE_TYPE {
		IMPLICIT, EXPLICIT
	}
	public static final QName ASSOC_SIGNATURE = QName.createQName(URI, "signatureAssoc");

	public static final QName ASPECT_SIGNATURE = QName.createQName(URI, "signature");
	public static final QName PROP_FORMAT = QName.createQName(URI, "format");
	public static final QName PROP_DATE = QName.createQName(URI, "date");
	public static enum SIGNATURE_FORMAT {
		PAdES_BES, CAdES_BES_DETACH
	}
	public static final QName PROP_CERTIFICATE_PRINCIPAL = QName.createQName(URI, "certificatePrincipal");
	public static final QName PROP_CERTIFICATE_NOT_AFTER = QName.createQName(URI, "certificateNotAfter");
	public static final QName PROP_CERTIFICATE_SERIAL_NUMBER = QName.createQName(URI, "certificateSerialNumber");
	public static final QName PROP_CERTIFICATE_ISSUER = QName.createQName(URI, "certificateIssuer");
	public static final QName ASSOC_DOC = QName.createQName(URI, "docAssoc");

	public static final QName PROP_SIGNATURE_PURPOSE = QName.createQName(URI, "signaturePurpose");

	public static final QName ASPECT_FIRST_POSITION_SIGNED = QName.createQName(URI, "firstSignature"); 
	public static final QName ASPECT_SECOND_POSITION_SIGNED = QName.createQName(URI, "secondSignature");
	public static final QName ASPECT_THIRD_POSITION_SIGNED = QName.createQName(URI, "thirdSignature");
	public static final QName ASPECT_FOURTH_POSITION_SIGNED = QName.createQName(URI, "fourthSignature");
	public static final QName ASPECT_FIFTH_POSITION_SIGNED = QName.createQName(URI, "fifthSignature");
	public static final QName ASPECT_SIXTH_POSITION_SIGNED = QName.createQName(URI, "sixthSignature");

}
