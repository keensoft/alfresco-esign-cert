<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />


<#assign optionSeparator=",">
<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
   <#if context.properties[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
   <#elseif args[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
   </#if>
</#if>

<#if fieldValue?string != "">
   <#assign values=fieldValue?split(",")>
<#else>
   <#assign values=[]>
</#if>

<div class="form-field">
      <div class="viewmode-field">
         <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if fieldValue?string == "">
            <#assign valueToShow=msg("form.control.novalue")>
         <#else>
            <#if field.control.params.options?? && field.control.params.options != "" &&
                 field.control.params.options?index_of(labelSeparator) != -1>
                 <#assign valueToShow="">
                 <#assign firstLabel=true>
                 <#list field.control.params.options?split(optionSeparator) as nameValue>
                    <#assign choice=nameValue?split(labelSeparator)>
                    <#if isSelected(choice[0])>
                       <#if !firstLabel>
                          <#assign valueToShow=valueToShow+",">
                       <#else>
                          <#assign firstLabel=false>
                       </#if>
                       <#assign valueToShow=valueToShow+choice[1]>
                    </#if>
                 </#list>
            <#else>
               <#assign valueToShow=fieldValue>
            </#if>
         </#if>
         <div id="value-${fieldHtmlId}" class="viewmode-value"></div>
         
		 <script language="Javascript" type="text/javascript">//<![CDATA[
		
		    (function() {
		    
		        var nodes = "${valueToShow?html}".split(",");
		        
		        for (var i = 0; i < nodes.length; i++) {
		        
		              Alfresco.util.Ajax.jsonGet({
			          url: encodeURI(Alfresco.constants.PROXY_URI + '/keensoft/sign/signature-metadata?nodeRef=' + nodes[i]),
			          successCallback:
			          {
			             fn: function loadWebscript_successCallback(response, config)
			             {
			                 var obj = eval('(' + response.serverResponse.responseText + ')');
			                 if (obj)
			                 {
					        	 if(obj.signaturePurpose)
					        	 {
						        	 YAHOO.util.Dom.get("value-${fieldHtmlId}").innerHTML = 
						        	 YAHOO.util.Dom.get("value-${fieldHtmlId}").innerHTML + "<br/>" +
						        	     "<div class='set-bordered-panel-heading'>${msg("panel.signature")}</div>" +
						        	     "<div class='set-bordered-panel-body'>" +
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.date")}:</span><span class='viewmode-value'>" + obj.signatureDate + "</span>" +
						        	     "</div>" + 
						        	     "</div>" + 
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.format")}:</span><span class='viewmode-value'>" + obj.signatureFormat + "</span>" +
						        	     "</div>" +
						        	     "</div>"+ 
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.principal")}:</span><span class='viewmode-value'>" + obj.certificatePrincipal + "</span>" +
						        	     "</div>" + 
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.serial.number")}:</span><span class='viewmode-value'>" + obj.certificateSerialNumber + "</span>" +
						        	     "</div>" + 
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.not.after")}:</span><span class='viewmode-value'>" + obj.certificateNotAfter + "</span>" +
						        	     "</div>"+
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.issuer")}:</span><span class='viewmode-value'>" + obj.certificateIssuer + "</span>" +
						        	     "</div>"+
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.purpose")}:</span><span class='viewmode-value'>" + obj.signaturePurpose + "</span>" +
						        	     "</div>"+
						        	     "</div>";
					        	 }
					        	 else
					        	 {
					        	 	YAHOO.util.Dom.get("value-${fieldHtmlId}").innerHTML = 
						        	YAHOO.util.Dom.get("value-${fieldHtmlId}").innerHTML + "<br/>" +
						        	     "<div class='set-bordered-panel-heading'>${msg("panel.signature")}</div>" +
						        	     "<div class='set-bordered-panel-body'>" +
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.date")}:</span><span class='viewmode-value'>" + obj.signatureDate + "</span>" +
						        	     "</div>" + 
						        	     "</div>" + 
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.format")}:</span><span class='viewmode-value'>" + obj.signatureFormat + "</span>" +
						        	     "</div>" +
						        	     "</div>"+ 
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.principal")}:</span><span class='viewmode-value'>" + obj.certificatePrincipal + "</span>" +
						        	     "</div>" + 
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.serial.number")}:</span><span class='viewmode-value'>" + obj.certificateSerialNumber + "</span>" +
						        	     "</div>" + 
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.not.after")}:</span><span class='viewmode-value'>" + obj.certificateNotAfter + "</span>" +
						        	     "</div>"+
						        	     "</div>"+
						        	     "<div class='form-field'>" +
						        	     "<div class='viewmode-field'>" +  
						        	     "<span class='viewmode-label'>${msg("prop.certificate.issuer")}:</span><span class='viewmode-value'>" + obj.certificateIssuer + "</span>" +
						        	     "</div>"+
						        	     "</div>";
					        	 }
			                 }
			             }
			          }
				   });
		            
		        }
		    
	        })();
	         
	      //]]></script>
         
      </div>
</div>

<#function isSelected optionValue>
   <#list values as value>
      <#if optionValue == value?string || (value?is_number && value?c == optionValue)>
         <#return true>
      </#if>
   </#list>
   <#return false>
</#function>

<#function isValidMode modeValue>
   <#return modeValue == "OR" || modeValue == "AND">
</#function>