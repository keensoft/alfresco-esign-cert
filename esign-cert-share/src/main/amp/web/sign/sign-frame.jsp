<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="es" lang="es">
<head>
	<script type="text/javascript" src="<%=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()%>/sign/miniapplet.js"></script>
</head>

<body>	
	<script type="text/javascript" charset="UTF-8">
	
	    MiniApplet.cargarMiniApplet('<%=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()%>/sign');
	    
	    var signatureAlg = '<%=request.getParameter("signatureAlg")%>';
	    var signaturePurpose = null;
        <% if (request.getParameter("signReason") != null) { %>
	        signaturePurpose = '<%=request.getParameter("signReason")%>';
        <% } %>
	    	    
	    <% if (request.getParameter("mimeType").equals("pdf")) { %>
	    
	        var str = '<%=request.getParameter("paramsPades")%>';
	        str = str.split('\t').join('\n');
	        
	        if (signaturePurpose)
	        {
	        	str = str.concat('\nsignReason=' + unicodeEscape(signaturePurpose));
	        }
			
			function doSign(dataToSign, signedData, signerRole) {
				signedData.value = MiniApplet.sign(dataToSign.value, 
						signatureAlg, 
						"PAdES", 
						str, 
						parent.show_signed, 
						parent.show_error);			
			}
			
		<% } else { %>
		
	        var str = '<%=request.getParameter("paramsCades")%>';
	        str = str.split('\t').join('\n');
        
			function doSign(dataToSign, signedData, signerRole) {
				signedData.value = MiniApplet.sign(dataToSign.value, 
						signatureAlg, 
						"CAdES", 
						str, 
						parent.show_signed, 
						parent.show_error);			
			}

		<% } %>

		function waitToSign(dataToSign, signedData, signerRole) {
			setTimeout(function(){ doSign(dataToSign, signedData, signerRole); }, 2000);
		}
		
		function unicodeEscape(str)
  		{
			for (var result = '', index = 0, charCode; !isNaN(charCode = str.charCodeAt(index++));) 
			{
				result += '\\u' + ('0000' + charCode.toString(16)).slice(-4);
			}
			return result;
		}
		
	</script>		
</body>
</html>
