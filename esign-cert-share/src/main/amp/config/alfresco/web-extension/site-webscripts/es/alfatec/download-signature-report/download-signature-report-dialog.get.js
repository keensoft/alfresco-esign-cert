function main() {
	var alfatecProperties = jsonConnection("/alfatec/alfresco-global/getAlfatecCustomProperties");
	if(alfatecProperties == null) {
		model.jsonError = true;
		return;
	}
	model.jsonError = false;
	model.defaultCSVPlace = alfatecProperties.defaultCSVPlace;
	model.defaultSignatureInfoPlace = alfatecProperties.defaultSignatureInfoPlace;
	model.nodeRef = args.nodeRef;
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