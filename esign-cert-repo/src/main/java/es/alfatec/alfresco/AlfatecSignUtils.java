package es.alfatec.alfresco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.util.TempFileProvider;
import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import es.alfatec.alfresco.webscripts.bean.PrintSignatureInformation;
import es.keensoft.alfresco.model.SignModel;

public class AlfatecSignUtils {
	
    private Logger logger = Logger.getLogger(AlfatecSignUtils.class);
	
    private ContentService contentService;
    private NodeService nodeService;
    private VersionService versionService;
	
	public PdfReader getNodeRefPdfReader(NodeRef document) throws IOException{
		ContentReader contentReader = contentService.getReader(document, ContentModel.PROP_CONTENT);
    	InputStream inputStream = contentReader.getContentInputStream();
    	return new PdfReader(inputStream);
	}
	
	public boolean isDocumentSigned(NodeRef document) throws IOException {
		List<String> signatureNames = getDocumentSignatures(document);
		return !signatureNames.isEmpty();
	}

	public List<String> getDocumentSignatures(NodeRef document) throws IOException {
		AcroFields acroFields = getDocumentAcroFields(document);
		List<String> signatureNames = acroFields.getSignatureNames();
		return signatureNames;
	}
	
	public List<PrintSignatureInformation> getSignatureOptions(NodeRef document) throws IOException{
		List<PrintSignatureInformation> signatureOptions = new ArrayList<>();
		AcroFields acroFields = getDocumentAcroFields(document);
		List<String> a = getDocumentSignatures(document);
		for(String c:a){
			PdfPKCS7 pkcs7 = acroFields.verifySignature(c);
			X509Certificate cert = (X509Certificate) pkcs7.getSigningCertificate();
			Calendar cal = pkcs7.getSignDate();
			String date = new SimpleDateFormat(" dd/MM/yyyy HH:mm").format(cal.getTime());
			PrintSignatureInformation psi = new PrintSignatureInformation(getDataSigner(cert), date);
			signatureOptions.add(psi);
		}
		return signatureOptions;
	}

	public AcroFields getDocumentAcroFields(NodeRef document) throws IOException {
		PdfReader pdfReader = getNodeRefPdfReader(document);
		AcroFields acroFields = pdfReader.getAcroFields();
		return acroFields;
	}
	
	public boolean isSignOk(NodeRef document) throws IOException, AlfatecException, SignatureException{
		String documentName = (String)nodeService.getProperty(document, ContentModel.PROP_NAME);  	

	    boolean isDocumentSigned = isDocumentSigned(document);
	    if(isDocumentSigned){
			//KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
			List<String> signatureNames = getDocumentSignatures(document);
			AcroFields acroFields = getDocumentAcroFields(document);

		    for(String name : signatureNames){
		        if (!acroFields.signatureCoversWholeDocument(name)){
			    	logger.warn("Sign "+name+" does not covers the whole document "+documentName+".");
			    	throw new AlfatecException("Sign "+name+" does not covers the whole document "+documentName+".", HttpServletResponse.SC_CONFLICT);
		        }
		        PdfPKCS7 pk = acroFields.verifySignature(name);
		        
		        if (pk.verify()){
		            return true;
		        }
		        else{
		            logger.warn("Sign is not valid on document "+documentName);
		            return false;
		        }
		    }
		    return false;
	    }else{
	    	logger.warn("Document "+documentName+" has not sign.");
	    	throw new AlfatecException("Document "+documentName+" has not sign.", HttpServletResponse.SC_CONFLICT);
	    }
	}
	
	public NodeRef getOriginalDocumentWithoutSignature(NodeRef document) throws IOException, AlfatecException{
		String documentName = (String)nodeService.getProperty(document, ContentModel.PROP_NAME);  	
		
		VersionHistory versionHistory = versionService.getVersionHistory(document);
		Collection<Version> versions = versionHistory.getAllVersions();
		if(!versions.isEmpty()){
			Iterator<Version> it = versions.iterator();
			boolean isDocumentSigned = true;
			NodeRef documentVersion = null;
			while(it.hasNext() && isDocumentSigned){
				Version version = (Version) it.next();
				documentVersion = version.getFrozenStateNodeRef();
				isDocumentSigned = isDocumentSigned(documentVersion);
			}
			
			if(!isDocumentSigned){
				return documentVersion;
			}else{
				logger.warn("Document "+documentName+" has not signed by Alfresco");
				throw new AlfatecException("Document "+documentName+" has not signed by Alfresco", HttpServletResponse.SC_CONFLICT);
			}
		}else{
			logger.warn("Document "+documentName+" has not signed by Alfresco");
			throw new AlfatecException("Document "+documentName+" has not signed by Alfresco", HttpServletResponse.SC_CONFLICT);
		}
	}
	
	public File getTmpFile(NodeRef originalDocumentWithoutSignature) throws IOException, FileNotFoundException, DocumentException {
		//Tmp file
		File tmpDir = TempFileProvider.getTempDir();
		String name = UUID.randomUUID().toString()+".pdf";
		File tmpFile = new File(tmpDir,name);
		
		PdfReader pdfReader = getNodeRefPdfReader(originalDocumentWithoutSignature);
		FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
		
		PdfStamper pdfStamper = new PdfStamper(pdfReader, fileOutputStream);
		pdfReader.close();
		pdfStamper.close();
		return tmpFile;
	}
	
	public String getDataSigner(X509Certificate certificate){
		
		String dataSigner = certificate.getSubjectX500Principal().toString();
	    //Extract name and surname signers
	    int start =	dataSigner.indexOf("CN=");
	    dataSigner = dataSigner.substring(start + 3);
	    start = 0;
	    int finish = dataSigner.length();
	    if(dataSigner.indexOf("-") > 0){
	    	finish = dataSigner.indexOf("-") - 1; //for certificate
	    }else{
	    	finish = dataSigner.indexOf("\","); //for DNIe
	    }
	    dataSigner = "\"" + dataSigner.substring(start, finish).replace("\"","") + "\"";
	    
	    return dataSigner;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	} 

	public String generateCSV(NodeRef document) {
		String csv = (String)nodeService.getProperty(document, SignModel.PROP_CSV);
		if(null == csv || csv.isEmpty()){
			String uuid = (String)nodeService.getProperty(document, ContentModel.PROP_NODE_UUID);
			Date createdDate = (Date) nodeService.getProperty(document, ContentModel.PROP_CREATED);
			Calendar cal = Calendar.getInstance();
			cal.setTime(createdDate);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR);
			int minute = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);
			csv = uuid + "-" + year + ((month < 10)?"0"+month:month) + ((day<10)?"0"+day:day) + ((hour<10)?"0"+hour:hour)+ ((minute<10)?"0"+minute:minute)+ ((seconds<10)?"0"+seconds:seconds);
			nodeService.setProperty(document, SignModel.PROP_CSV, csv);
		}
		return csv;
	}
}