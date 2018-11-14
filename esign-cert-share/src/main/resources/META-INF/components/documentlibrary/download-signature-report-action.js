(function() {
	YAHOO.Bubbling.fire("registerAction", {
		actionName : "onActionDownloadSignatureReport",
		fn : function sign_action(record, owner) {
			
			var params = this.getAction(record, owner).params;
			
			this.widgets.waitDialog = Alfresco.util.PopupManager.displayMessage({
				text : this.msg("document.loading"),
				spanClass : "wait",
				displayTime : 0
			});
			
			this.widgets.signDialog = new Alfresco.module.SimpleDialog("downloadSignatureReport").setOptions({
				width : "50em",
				templateUrl : Alfresco.constants.URL_SERVICECONTEXT + "alfatec/download-signature-report/download-signature-report-dialog?nodeRef=" + record.nodeRef,
				actionUrl : Alfresco.constants.PROXY_URI + "alfatec/alfresco-global/signatureReportInfo",
				destroyOnHide : true,
				onSuccess : {
					fn : function signDialog_successCallback(response) {
						var json = eval('(' + response.serverResponse.responseText + ')');
						if (json){
							var nodeRef = json.nodeRef;
							var documentName = json.documentName;
							var signatureInfoPlace = json.signatureInfoPlace;
							var csvPlace = json.csvPlace;
							window.open(Alfresco.constants.PROXY_URI + "alfatec/alfresco-global/downloadSignReportFile?nodeRef="+nodeRef+"&csvPlace="+csvPlace+"&signatureInfoPlace="+signatureInfoPlace);
						} 
						Alfresco.util.PopupManager.displayMessage({
							text : this.msg("message.downloadSignReport.success"),
							displayTime : 3
						});
						//YAHOO.Bubbling.fire("metadataRefresh");
					},
					scope : this
				},
				onFailure : {
					fn : function signDialog_failCallback(response) {
						this.widgets.signDialog.hide();
						Alfresco.util.PopupManager.displayMessage({
							text : response.serverResponse.responseText,
							displayTime : 6
						});						
					},
					scope : this
				},
				doBeforeDialogShow : {
					fn : function beforeSign_dialogShow(response) {
						this.widgets.waitDialog.destroy();
					},
					scope : this
				}
			});

			this.widgets.signDialog.show();
		}
	});
})();