
esign-cert
==================
Provides an Alfresco Share action for signing PDF files (PAdES-BES format) and any other file (CAdES-BES format detached) via java applet (@firma miniApplet, opensource at https://github.com/ctt-gob-es/clienteafirma) or local application AutoFirma by protocol (http://forja-ctt.administracionelectronica.gob.es/web/clienteafirma) where applets are not possible (i. e. Google Chrome).

Now with an Angular 5 Component based in Alfresco ADF Framework. Available at [esign-cert-ng2](https://github.com/keensoft/alfresco-esign-cert/tree/master/esign-cert-ng2)


## PREVIOUS BACKGROUND

***This addons includes a button to perform electronic signature based in electronic certificates from client computer. This addons has nothing to see with SSL or server electronic signature. Every user of Alfresco require a configured Personal Electronic Certificate (available to be picked from the browser) to use the addon***


**IMPORTANT NOTICE**

If you installed [alfresco-firma-pdf](https://github.com/keensoft/alfresco-firma-pdf), it's required to uninstall it before using **esign-cert** addon.

```bash
java -jar alfresco-mmt.jar uninstall sign-document ../tomcat/webapps/alfresco.war
java -jar alfresco-mmt.jar uninstall sign-document-share ../tomcat/webapps/share.war
```

## esign-cert features

**AutoFirma** local application for computers is currently supported only for Windows, Mac OS and Linux. 

Available for download at [AutoFirma](http://firmaelectronica.gob.es/Home/Descargas.html)
Source code available at [clienteafirma](https://github.com/ctt-gob-es/clienteafirma)

**Cliente movil @firma** local application for devices is currently supported for iOS and Android:
* Google Play - [Cliente movil @firma](https://play.google.com/store/apps/details?id=es.gob.afirma)
* App Store - [Cliente @firma movil](https://itunes.apple.com/us/app/cliente-firma-movil/id627410001?mt=8) 

Currently following browser and OS combinations are supported:

Windows
* IE Edge: not supported by now
* IE Classic: Local application / Applet
* Google Chrome: Local application
* Mozilla Firefox: Local application

Mac OS 
* Mozilla Firefox: Applet
* Apple Safari: Local application (currently not working)
* Google Chrome: Local application (currently not working)

Linux Ubuntu
* Mozilla Firefox: Local application
* Google Chrome: Local application

iOS
* Apple Safari: Local application
* Google Chrome: Local application

Android
* Google Chrome: Local application

**Notice**: this module supersede previous one [alfresco-firma-pdf](https://github.com/keensoft/alfresco-firma-pdf)

This module uses a software digital certificate or a cryptographic hardware supported by a smart card.

**License**
The plugin is licensed under the [LGPL v3.0](http://www.gnu.org/licenses/lgpl-3.0.html). 

**State**
Current addon release 1.6.2 is ***PROD***

**Compatibility**
The current version has been developed using Alfresco 5.0.d and Alfresco SDK 2.1.1, although it runs in Alfresco 5.1.x

Browser compatibility: 100% supported (refer previous paragraph)

**Languages**
Currently provided in English,Spanish, Macedonian and Brazilian Portuguese.

***No original Alfresco resources have been overwritten***


Downloading the ready-to-deploy-plugin
--------------------------------------
The binary distribution is made of two amp files:

* [repo AMP](https://github.com/keensoft/alfresco-esign-cert/releases/download/1.6.2/esign-cert-repo.amp)
* [share AMP](https://github.com/keensoft/alfresco-esign-cert/releases/download/1.6.2/esign-cert-share.amp)

You can install them by using standard [Alfresco deployment tools](http://docs.alfresco.com/community/tasks/amp-install.html)


Building the artifacts
----------------------
If you are new to Alfresco and the Alfresco Maven SDK, you should start by reading [Jeff Potts' tutorial on the subject](http://ecmarchitect.com/alfresco-developer-series-tutorials/maven-sdk/tutorial/tutorial.html).

You can build the artifacts from source code using maven
```$ mvn clean package```


Signing the applet
------------------
You can download plain applet from http://forja-ctt.administracionelectronica.gob.es/web/clienteafirma

Oracle [jarsigner](http://docs.oracle.com/javase/7/docs/technotes/tools/windows/jarsigner.html) can be used to perform a signature on [miniapplet-full_1_6_2.jar](https://github.com/keensoft/alfresco-esign-cert/raw/master/esign-cert-share/src/main/amp/web/sign/miniapplet-full_1_6_2.jar). To deploy this change, just replace current JAR for your signed JAR and rebuild the artifacts.

Below a sample `jarsigner` invocation is provided

```
$ jarsigner -storetype pkcs12 -keystore keensoft_sign_code_valid-until_20170811.pfx miniapplet-full_1_5.jar -tsa http://tss.accv.es:8318/tsa te-9b5d5438-2bb6-435f-8542-6d711bc9784f
```


Running under SSL
-----------------
Signature window is built on an IFRAME, so when running Alfresco under SSL, following JavaScript console error may appear:

```Refused to display 'https://alfresco.keensoft.es/share/sign/sign-frame.jsp?mimeType=pdf' in a frame because it set 'X-Frame-Options' to 'DENY'.```

If so, check your web server configuration in order to set appropiate **X-Frame-Options** header value.

For instance, Apache HTTP default configuration for SSL includes...

```Header always set X-Frame-Options DENY```

... and it should be set to **SAMEORIGIN** instead

```Header always set X-Frame-Options SAMEORIGIN```


Configuration
----------------------
Before installation, following properties must be included in **alfresco-global.properties**

**Sample configuration 1**

```
# Native @firma parameters separated by tab (\t)
esign.cert.params.pades=signaturePage=1\tsignaturePositionOnPageLowerLeftX=120\tsignaturePositionOnPageLowerLeftY=50\tsignaturePositionOnPageUpperRightX=220\tsignaturePositionOnPageUpperRightY=150\t
esign.cert.params.cades=mode=explicit
# Signature algorithm: SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA
esign.cert.signature.alg=SHA512withRSA
esign.cert.params.firstSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=50\tsignaturePositionOnPageLowerLeftY=45\tsignaturePositionOnPageUpperRightX=305\tsignaturePositionOnPageUpperRightY=69\t
esign.cert.params.secondSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=310\tsignaturePositionOnPageLowerLeftY=45\tsignaturePositionOnPageUpperRightX=565\tsignaturePositionOnPageUpperRightY=69\t
esign.cert.params.thirdSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=50\tsignaturePositionOnPageLowerLeftY=23\tsignaturePositionOnPageUpperRightX=305\tsignaturePositionOnPageUpperRightY=47\t
esign.cert.params.fourthSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=310\tsignaturePositionOnPageLowerLeftY=23\tsignaturePositionOnPageUpperRightX=565\tsignaturePositionOnPageUpperRightY=47\t
esign.cert.params.fifthSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=50\tsignaturePositionOnPageLowerLeftY=1\tsignaturePositionOnPageUpperRightX=305\tsignaturePositionOnPageUpperRightY=25\t
esign.cert.params.sixthSignaturePosition=signaturePage={page}\tsignaturePositionOnPageLowerLeftX=310\tsignaturePositionOnPageLowerLeftY=1\tsignaturePositionOnPageUpperRightX=565\tsignaturePositionOnPageUpperRightY=25\t

# Property for disable sign other docs
esign.cert.signOtherDocs=false

# Sign Purpose (Default enabled=false)
esign.cert.params.signPurpose.enabled=false

# Enable or disable download signature resume on pdf files (Default false)
esign.download.signature.report.enabled=true

# Web application URL to download signed document by CSV (Default null)
esign.csv.url=

# CSV image position to print(Default null).
esign.download.signature.report.default.csv.place=

# Signature resume position to print (Default Default null).
esign.download.signature.report.default.signature.info.place=
```

If no signature position selection form is required, `signaturePosition` properties must be declared blank. PDF signature will be performed by using `esign.cert.params.pades` in this scenario.

**Sample configuration 2**

```
# Native @firma parameters separated by tab (\t)
esign.cert.params.pades=signaturePage=1\tsignaturePositionOnPageLowerLeftX=120\tsignaturePositionOnPageLowerLeftY=50\tsignaturePositionOnPageUpperRightX=220\tsignaturePositionOnPageUpperRightY=150\t
esign.cert.params.cades=mode=explicit
# Signature algorithm: SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA
esign.cert.signature.alg=SHA512withRSA
esign.cert.params.firstSignaturePosition=
esign.cert.params.secondSignaturePosition=
esign.cert.params.thirdSignaturePosition=
esign.cert.params.fourthSignaturePosition=
esign.cert.params.fifthSignaturePosition=
esign.cert.params.sixthSignaturePosition=

# Property for disable sign other docs
esign.cert.signOtherDocs=false

# Sign Purpose (Default enabled=false)
esign.cert.params.signPurpose.enabled=false

# Enable or disable download signature resume on pdf files (Default false)
esign.download.signature.report.enabled=false

# Web application URL to download signed document by CSV (Default null)
esign.csv.url=https://www.google.es

# CSV image position to print(Default null).
esign.download.signature.report.default.csv.place=csv.all.pages

# Signature resume position to print (Default Default null).
esign.download.signature.report.default.signature.info.place=signature.info.first.page
```

Usage
----------------------
Every document is including a **Sign** action to perform a client signature depending on the mime type:
* PDF files are signed as PAdES (with a visible signature)
* Other files are signed as CAdES (detached)

Both documents include also signer metadata:
```
Format: CAdES-BES Detached
Date: Wed 2 Mar 2016 22:31:32
Signer: CN=NOMBRE BORROY LOPEZ ANGEL FERNANDO - NIF 25162750Z, OU=500050546, OU=FNMT Clase 2 CA, O=FNMT, C=ES
Serial number: 1022640006
Caducity: Tue 12 Apr 2016
Issuer: OU=FNMT Clase 2 CA, O=FNMT, C=ES
```

PDF files can be signed up to 6 times on 6 different positions. Once a PDF is signed in a certain position that position is no longer available for signing. The signatures positions are defined in **alfresco-global.properties**.

To enable download signature report action, is necessary set to true property **esign.download.signature.report.enabled** on alfresco-global.properties. Then, you can configure default position of CSV and/or signature resume watermarks with properties **esign.download.signature.report.default.csv.place** and/or **esign.download.signature.report.default.signature.info.place**.

Availables values are:

* esign.download.signature.report.default.signature.info.place
	* signature.info.first.page
	* signature.info.last.page
	* signature.info.all.pages
	* signature.info.none
* esign.download.signature.report.default.csv.place
	* csv.first.page
	* csv.last.page
	* csv.all.pages
	* csv.none

If you don't set those properties, Alfresco asks user the position of CSV and signature resume.

Download signature report action is only available for nodes with next features:
* PDF nodes.
* Documents signed on this Alfresco instance. (Documents signed only by your organization).

Todo
----------------------
Pending features to be included (aka "wishlist"):
* Signatures associated to workflow steps (Activiti)
* Massive signature over a set of documents in one action
* Signature formats: XAdES, ODF, OOXML
* Visible signature including custom image for PAdES
* Signature verification
* AdES signatures elevation
* LTA integration

## Contributors

* Daniel E. Fernández
* Douglas C. R. Paes
* Vasil Iliev
* [Pedro González](https://www.linkedin.com/in/pedro-gonz%C3%A1lez-cano-310b40120/)
* [Alberto Ramírez Losilla](https://www.linkedin.com/in/alramlo/)
* Mikel Asla
* Maria Tsiakmaki
* [Sergio Campoy](https://es.linkedin.com/in/sergio-campoy-arnau-79697568)
