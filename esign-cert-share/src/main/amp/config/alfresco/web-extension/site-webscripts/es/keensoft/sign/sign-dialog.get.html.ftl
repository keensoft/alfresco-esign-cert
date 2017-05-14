<div id="signDialog" class="">
   <div class="hd">${msg("window.title")}</div>
   <div class="bd">		
     	<#if !jsonError>	     		
		    <div id="info" class="yui-gd" style="padding:30px;text-align:justify;">
				<div id="loading-text" style="display:none;">${msg("document.ready")}</div>
				<div id="loading" style="text-align:center; display: none;">
					<img src="/share/sign/icon.gif" >
				</div>
				<#if mimeType == "pdf">
				<div style="width: 50%; float:left; display: inline-block">
					<div id="position-text">${msg("document.position")}</div>
					<select id="signaturePlace" style="width: 95%;">
						<#if showOptionFirstSignature>
						<option value="sig1">${msg("select.sig1")}</option>
						</#if>
						<#if showOptionSecondSignature>
						<option value="sig2">${msg("select.sig2")}</option>
						</#if>
						<#if showOptionThridSignature>
						<option value="sig3">${msg("select.sig3")}</option>
						</#if>
						<#if showOptionFourthSignature>
						<option value="sig4">${msg("select.sig4")}</option>
						</#if>
						<option value="sig5">${msg("select.sig5")}</option>
					</select>
				</div>
				<div style="width: 50%;  display: inline-block">
					<div id="pageTitle">${msg("page")}}</div>
					<select id="signaturePage" style="width: 95%;">
						<option value="last">${msg("page.last")}</option>
						<option value="first">${msg("page.first")}</option>
					</select>	
					<#if serverDate??>
					 ${serverDate}
					</#if>
				</div>	
				</#if>					  								  			
				<div id="sign-component" style="width:100%;"></div>	 
			</div>			
			<form id="signDialog-form" action="" method="POST">
				<input type="hidden" id="dataToSign" name="dataToSign" value="${base64NodeContent}" />
				<input type="hidden" id="signedData" name="signedData" value="" />
				<input type="hidden" id="signerData" name="signerData" value="" />
				<input type="hidden" id="signerRole" name="signerRole" value="" />
				<input type="hidden" id="mimeType" name="mimeType" value="${mimeType}" />
				<input type="hidden" id="nodeRef" name="nodeRef" value="${nodeRef}" />
				<input type="hidden" id="signerPostition" name="signerPostition" value="" />			 
	         	<div class="bdft" style="display:none;">
		         	<input type="button" id="signDialog-ok" value="${msg("button.ok")}" />
		         	<input type="button" id="signDialog-cancel" value="${msg("button.cancel")}" />
		         </div>
		    </form>
			<#if mimeType == "pdf">
		    <div id="button-div" class="bdft">
		        <button type="button"
		         	id="signPosition-TMPok" 
		         	class="yui-skin-lightTheme yui-button yui-skin-lightTheme"
		         	value="${msg("button.sign")}" 
		         	style="height: 30px;min-width: 70px;margin: 2px;border: 1px solid #ccc;"
		         	onclick="chosePosition()">${msg("button.sign")}</button>
	        </div>
		    </#if>
	      	<script type="text/javascript">//<![CDATA[
	      	                                          
		      	String.prototype.replaceAll = function(search, replacement) {
		      	    var target = this;
		      	    return target.replace(new RegExp(search, 'g'), replacement);
		      	};
		      	
	      		var finalSignaturePosition = "${paramsPades}".replace(" ", "\t");
	      		var options = {
	      			"default": "${paramsPades}".replaceAll(" ", "\t"),
	      			"firstPosition": "${firstSignaturePosition}".replaceAll(" ", "\t").replaceAll("{space}", " "),
	      			"secondPosition": "${secondSignaturePosition}".replaceAll(" ", "\t").replaceAll("{space}", " "),
	      			"thirdPosition": "${thirdSignaturePosition}".replaceAll(" ", "\t").replaceAll("{space}", " "),
	      			"fourthPosition": "${fourthSignaturePosition}".replaceAll(" ", "\t").replaceAll("{space}", " "),
	      			"fifthPosition": "${fifthSignaturePosition}".replaceAll(" ", "\t").replaceAll("{space}", " ")
	      		};
	      		var page = "last_page";
	      		var documentMimetype = "${mimeType}";

	      		var running = false;
	      		var loadingSignComponentInterval = null;
	      		var loadingFrameInterval = null; 

	      		function chosePosition() {
	      			var position = YAHOO.util.Dom.get("signaturePlace").value;
	      			var pageSelect = YAHOO.util.Dom.get("signaturePage").value;
	      			YAHOO.util.Dom.get("button-div").style.display = "none";
	      			YAHOO.util.Dom.get("position-text").style.display = "none";
	      			YAHOO.util.Dom.get("signaturePlace").style.display = "none";
	      			YAHOO.util.Dom.get("signaturePage").style.display = "none";
	      			YAHOO.util.Dom.get("pageTitle").style.display = "none";
	      			YAHOO.util.Dom.get("loading-text").style.display = "block";
	      			YAHOO.util.Dom.get("loading").style.display = "block";
	      			
	      			if(position == "sig1")
	      			{
	      				finalSignaturePosition = options.firstPosition;
	      				YAHOO.util.Dom.get("signerPostition").value = "1";
		      		}
	      			else if(position == "sig2")
	      			{
	      				finalSignaturePosition = options.secondPosition;
	      				YAHOO.util.Dom.get("signerPostition").value = "2";
		      		}
		      		else if(position == "sig3")
		      		{
	      				finalSignaturePosition = options.thirdPosition;
	      				YAHOO.util.Dom.get("signerPostition").value = "3";
			      	}
	      			else if(position == "sig4")
	      			{
	      				finalSignaturePosition = options.fourthPosition;
	      				YAHOO.util.Dom.get("signerPostition").value = "4";
	      			}
	      			else if(position == "sig5")
	      			{
	      				finalSignaturePosition = options.fifthPosition;
	      				YAHOO.util.Dom.get("signerPostition").value = "5";
	      			}
	      			
	      			if(pageSelect == "first")
	      			{
	      				page = "1";
	      			}
	      			
	      			finalSignaturePosition = finalSignaturePosition.replace("{page}", page);
	      			loadingFrameInterval = window.setInterval(checkZIndex, 500);
	      		}
	      		
	      		function show_signed(signatureBase64, certificateB64) {
	      		    signedData.value = signatureBase64;
	      		    signerData.value = certificateB64;
	      			YAHOO.util.Dom.get("info").innerHTML="${msg("signed")}";
                    var submitButton = YAHOO.util.Dom.get("signDialog-ok");
	      			submitButton.click();
	      		}
	      				 
                function show_error(errorType, errorMessage) {
                    YAHOO.util.Dom.get("info").innerHTML="${msg("error.unknown")} - " + errorType + ":" + errorMessage; 
                    var submitButton = YAHOO.util.Dom.get("signDialog-cancel");
                    submitButton.click();
                }
                
	      		if(documentMimetype != "pdf") {
	      			var waitToLoadDOM = setInterval(function() {
	      				if(YAHOO.util.Dom.get("loading-text") != undefined) {
	      					clearInterval(waitToLoadDOM);
		      				YAHOO.util.Dom.get("loading-text").style.display = "block";
			      			YAHOO.util.Dom.get("loading").style.display = "block";
			      			loadingFrameInterval = window.setInterval(checkZIndex, 500);
	      				}
	      			}, 500);      			
	      		}
	      					      		
	      		function checkZIndex() {
	      					      			
	      			var zIndex = YAHOO.util.Dom.getStyle(this.signDialog.parentElement, "z-index");
	      			if(zIndex > 0) {
	      			
	      				window.clearInterval(loadingFrameInterval);
	      				var iframeURL = encodeURI("/share/sign/sign-frame.jsp?mimeType=${mimeType}&paramsCades=${paramsCades}&paramsPades=" + finalSignaturePosition + "&signatureAlg=${signatureAlg}");
	      				YAHOO.util.Dom.get("sign-component").innerHTML="<iframe scrolling='no' frameborder='0' allowTransparency='true' id='sign-frame' src='" + iframeURL + "' style='overflow:hidden;width:100%;border:none;height:0px;' />";			      				
	      				loadingSignComponentInterval = window.setInterval(doSign, 500);
	      				
	      			}		  		
	      		}	

	      		function doSign() {
      				var signFrame = YAHOO.util.Dom.get("sign-frame");
      				var signForm = YAHOO.util.Dom.get("signDialog-form");
	      			if(signFrame.contentWindow.doSign) {
      				    window.clearInterval(loadingSignComponentInterval);
      				    if (!running) {
	      				    running = true;
	      				    signFrame.contentWindow.doSign(signForm.dataToSign, signForm.signedData, signForm.signerRole);
	      				}
      				}
	      		};
	      		
			//]]></script>	
			      				      	
  	    <#else>
  	      	<div class="yui-gd" style="padding:30px;text-align:justify;">
  				${msg("error.unknown")}
  			</div>
			<form id="signDialog-form" action="" method="POST">
	         	<div class="bdft">
	         		<input type="button" id="signDialog-cancel" value="${msg("button.ok")}" />
	         	</div>
	      	</form>      	
        </#if>
 	</div>
</div>