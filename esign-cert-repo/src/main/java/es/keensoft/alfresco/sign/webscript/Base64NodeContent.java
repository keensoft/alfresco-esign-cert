package es.keensoft.alfresco.sign.webscript;

import java.io.IOException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.google.gson.Gson;

import es.keensoft.alfresco.sign.webscript.bean.Base64NodeContentResponse;

public class Base64NodeContent extends AbstractWebScript {
	
	private static Log log = LogFactory.getLog(Base64NodeContent.class);

	public static final String RESPONSE_CODE_OK = "OK";

	private ContentService contentService;	

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		
		Gson gson = new Gson();
		Base64NodeContentResponse response = new Base64NodeContentResponse();

		try {
			
			String docNodeRefStr = req.getParameter("nodeRef");
			NodeRef nodeRef = new NodeRef(docNodeRefStr);			

			byte[] nodeContent = getNodeContent(nodeRef);
			response.setBase64NodeContent(Base64.encodeBase64String(nodeContent));
			response.setNodeRef(nodeRef.getId());
			response.setCode(RESPONSE_CODE_OK);			
			
		} catch (Exception e) {
			
			log.error(ExceptionUtils.getFullStackTrace(e));
			throw new WebScriptException(e.getMessage(), e);
			
		}

		res.setContentType(MimetypeMap.MIMETYPE_JSON);
		res.getWriter().write(gson.toJson(response));
		
	}

	private byte[] getNodeContent(NodeRef nodeRef) throws ContentIOException, IOException {
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		return IOUtils.toByteArray(reader.getContentInputStream());
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}	
}
