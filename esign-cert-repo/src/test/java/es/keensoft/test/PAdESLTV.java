package es.keensoft.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.QName;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;

import es.keensoft.alfresco.model.SignModel;

public class PAdESLTV {
	
	public static void main(String... args) throws Exception {
		
		
		ClassLoader classLoader = PAdESLTV.class.getClassLoader();
		File file = new File(classLoader.getResource("PADESLTV.pdf").getFile());
		
		Provider provider = null;
        try {
            Class c =
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            java.security.Security.insertProviderAt((java.security.Provider)c.newInstance(), 2000);
            //provider = "BC";
            provider = (Provider)c.newInstance();
            
        } catch(Exception e) {
            provider = null;
               // provider is not available }
        }		
		
		InputStream is = new FileInputStream(file);
		
		PdfReader reader = new PdfReader(is);
        AcroFields af = reader.getAcroFields();
        ArrayList<String> names = af.getSignatureNames();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ArrayList<Map<QName, Serializable>> aspects = new ArrayList<Map<QName, Serializable>>();
        for (String name : names) {
            PdfPKCS7 pk = af.verifySignature(name);
            X509Certificate certificate = pk.getSigningCertificate();
            
            //Set aspect properties for each signature
            Map<QName, Serializable> aspectSignatureProperties = new HashMap<QName, Serializable>(); 
            if (pk.getSignDate() != null) aspectSignatureProperties.put(SignModel.PROP_DATE, pk.getSignDate().getTime());
    		aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_PRINCIPAL, certificate.getSubjectX500Principal().toString());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_SERIAL_NUMBER, certificate.getSerialNumber().toString());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_NOT_AFTER, certificate.getNotAfter());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_ISSUER, certificate.getIssuerX500Principal().toString());   
    	    aspects.add(aspectSignatureProperties);
        }
        
        System.out.println(aspects);
		
		
	}

}
