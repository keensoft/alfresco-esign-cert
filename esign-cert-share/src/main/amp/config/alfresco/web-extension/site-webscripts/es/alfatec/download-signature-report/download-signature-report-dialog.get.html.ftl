<div id="downloadSignatureReport" class="" style="min-height:200px;">
   <div class="hd">${msg("window.title")}</div>
   <div class="bd">
   		<#if !jsonError>
   		    
   		    <#if defaultSignatureInfoPlace != "" && defaultCSVPlace != "">
				<form id="downloadSignatureReport-form" action="" method="POST">
					<input type="hidden" id="nodeRef" name="nodeRef" value="${nodeRef}" />
					<input type="hidden" id="csvPlace" name="csvPlace" value="${defaultCSVPlace}" />
        			<input type="hidden" id="signatureInfoPlace" name="signatureInfoPlace" value="${defaultSignatureInfoPlace}" />		
        			
        			<div id="downloadSignatureReport-form-container" style="padding:10px;">			
        			
	        			<p>${msg("esign.run.generation")}</p>        			
	        			<div class="bdft" style="margin-top:70px; background-color:white;">
				         	<input type="button" id="downloadSignatureReport-ok" value="${msg("button.ok")}" />
				         	<input type="button" id="downloadSignatureReport-cancel" value="${msg("button.cancel")}" />
				         </div>
			         </div>	
			    </form>		    					
			<#else>	
				<form id="downloadSignatureReport-form" action="" method="POST">
					<input type="hidden" id="nodeRef" name="nodeRef" value="${nodeRef}" />
										
					<div id="downloadSignatureReport-form-container" style="padding:10px;">			
						
						<#if defaultCSVPlace != "">
							<input type="hidden" id="csvPlace" name="csvPlace" value="${defaultCSVPlace}" />
						<#else>			   
							<div id="downloadSignatureReport-form-csvPlace" style="width:50%;float:left;">
								<p>${msg("csv.configuration.title")}</p>
								<select id="csvPlace" name="csvPlace" style="width: 95%;">
									<option value="csv.none">${msg("csv.none")}</option>
									<option value="csv.all.pages">${msg("csv.all.pages")}</option>
									<option value="csv.first.page">${msg("csv.first.page")}</option>
									<option value="csv.last.page">${msg("csv.last.page")}</option>
								</select>
							</div>
        				</#if>		
        				
        				<#if defaultSignatureInfoPlace != "">
        					<input type="hidden" id="signatureInfoPlace" name="signatureInfoPlace" value="${defaultSignatureInfoPlace}" />
						<#else>			   
							<div id="downloadSignatureReport-form-signatureInfoPlace" style="width:50%;float:left;">
								<p>${msg("signature.info.configuration.title")}</p>
								<select id="signatureInfoPlace" name="signatureInfoPlace" style="width: 95%;">
									<option value="signature.info.none">${msg("signature.info.none")}</option>
									<option value="signature.info.all.pages">${msg("signature.info.all.pages")}</option>
									<option value="signature.info.first.page">${msg("signature.info.first.page")}</option>
									<option value="signature.info.last.page">${msg("signature.info.last.page")}</option>
								</select>
							</div>
        				</#if>
        				
			         	<div class="bdft" style="margin-top:70px; background-color:white;">
				         	<input type="button" id="downloadSignatureReport-ok" value="${msg("button.ok")}" />
				         	<input type="button" id="downloadSignatureReport-cancel" value="${msg("button.cancel")}" />
				         </div>
				    </div>
			    </form>					   
        	</#if>
		<#else>
			<form id="downloadSignatureReport-form" action="" method="POST">
				<div id="downloadSignatureReport-form-container" style="padding:10px;">			
					<p>${msg("error.unknown")}</p>
	         		<div class="bdft" style="margin-top:70px; background-color:white;">
			         	<input type="button" id="downloadSignatureReport-ok" value="${msg("button.ok")}" />
			         	<input type="button" id="downloadSignatureReport-cancel" value="${msg("button.cancel")}" />
			        </div>
			    </div>
	      	</form>
        </#if>
 	</div>
</div>