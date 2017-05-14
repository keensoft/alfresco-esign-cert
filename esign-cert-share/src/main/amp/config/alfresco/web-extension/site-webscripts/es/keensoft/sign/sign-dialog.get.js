function main() {
	
	// Get Base64 content
	var base64NodeContentResponse = jsonConnection("/keensoft/sign/base64-node-content?nodeRef=" + args.nodeRef);
	if(base64NodeContentResponse == null) {
		model.jsonError = true;
		return;
	}
	
	// Get signature parameters
	var signatureParams = jsonConnection("/keensoft/sign/signature-params");
	model.paramsPades = signatureParams.paramsPades;
	model.paramsCades = signatureParams.paramsCades;
	model.signatureAlg = signatureParams.signatureAlg;
	model.firstSignaturePosition = signatureParams.firstSignaturePosition;
	model.secondSignaturePosition = signatureParams.secondSignaturePosition;
	model.thirdSignaturePosition = signatureParams.thirdSignaturePosition;
	model.fourthSignaturePosition = signatureParams.fourthSignaturePosition;
	model.fifthSignaturePosition = signatureParams.fifthSignaturePosition;
	model.sixthSignaturePosition = signatureParams.sixthSignaturePosition;
	model.signaturePurposeEnabled = signatureParams.signaturePurposeEnabled;
	
	// Detect if no position selection is configured
	model.signaturePosition = model.firstSignaturePosition != "" ||
	    model.secondSignaturePosition != "" ||
	    model.thirdSignaturePosition != "" ||
	    model.fourthSignaturePosition != "" ||
	    model.fifthSignaturePosition != "" ||
	    model.sixthSignaturePosition != "";
	
	// Set available signature places
	var aspects = jsonConnection("/slingshot/doclib/aspects/node/" + args.nodeRef.replace(":/", ""));
	model.showOptionFirstSignature = (aspects.current.indexOf("sign:firstSignature") == -1);
	model.showOptionSecondSignature = (aspects.current.indexOf("sign:secondSignature") == -1);
	model.showOptionThirdSignature = (aspects.current.indexOf("sign:thirdSignature") == -1);
	model.showOptionFourthSignature = (aspects.current.indexOf("sign:fourthSignature") == -1);
	model.showOptionFifthSignature = (aspects.current.indexOf("sign:fifthSignature") == -1);
	model.showOptionSixthSignature = (aspects.current.indexOf("sign:sixthSignature") == -1);

	// Fill up the model with data
	model.base64NodeContent = base64NodeContentResponse.base64NodeContent;
	model.mimeType = args.mimeType;
	model.nodeRef = args.nodeRef;
	model.jsonError = false;
	
	if(signatureParams.useServerTime == true) {
		var dateTime = getNow();
		var signatureFormat = "\tlayer2Text=$$SUBJECTCN$${space}" + dateTime + "\tlayer2FontSize=8";
		model.firstSignaturePosition += signatureFormat;
		model.secondSignaturePosition += signatureFormat;
		model.thirdSignaturePosition += signatureFormat;
		model.fourthSignaturePosition += signatureFormat;
		model.fifthSignaturePosition += signatureFormat;
	}
}

main();

function jsonConnection(url) {
	
	var connector = remote.connect("alfresco"),
		result = connector.get(url);

	if (result.status == 200) {		
		return eval('(' + result + ')')
	} else {
		return null;
	}
}

function getNow() {
	var now = new Date();
	var day = now.getDate();
	var month = now.getMonth() + 1;
	var year = now.getFullYear();
	var hour = now.getHours();
	var minutes = now.getMinutes();
	return day + "." + month + "." + year + "{space}" + formatNumber(hour) + ":" + formatNumber(minutes);
}

function formatNumber(number) {
	return number < 9 ? '0' + number : number;
}