package es.alfatec.alfresco.webscripts;

import java.io.IOException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class GetDocumentByCSV extends AbstractWebScript{
	
	private static final Logger log = Logger.getLogger(GetDocumentByCSV.class);
	
	private static final String query = "@sign\\:csv:\"";
	private static final String PARAM_CSV = "csv";
	
	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		try{
			
			log.info("------------------------------------------------------------------------------------");
			log.info("ESIGN-CERT REST API - GET " + req.getServicePath());
			log.info("------------------------------------------------------------------------------------");

			log.info("Petition in progress " + req.getURL());

			//Get the petition parameters
			String param_csv = req.getParameter(PARAM_CSV);
			
			if(param_csv != null){
				//Check if document exists
				StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
				ResultSet result = searchService.query(storeRef, SearchService.LANGUAGE_FTS_ALFRESCO, query + param_csv +"\"");

				if(result.length()>0){
					NodeRef nodeNodeRef = result.getNodeRef(0);
					//Get the content
					byte[] nodeContent = getNodeContent(nodeNodeRef);
					//Get the name
					String nodeName = (String) nodeService.getProperty(nodeNodeRef, ContentModel.PROP_NAME);
					//Generate the response
					res.setContentType(MimetypeMap.MIMETYPE_PDF);
			    	res.setHeader("content-disposition","attachment; filename=" + nodeName);
			    	res.getOutputStream().write(nodeContent); 
			    	res.setContentEncoding("UTF-8");
					res.setStatus(HttpStatus.SC_OK);
					log.info(HttpStatus.SC_OK + " - Petition has been finished. Document: " + nodeName);
				}else{
					throw new WebScriptException("The document with CSV '" + param_csv + "' isn't exist");
				}
			}else{
				throw new IllegalArgumentException("The paramater csv has not received");
			}
		
		}catch(IllegalArgumentException e){
			
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
			res.getWriter().write(e.getMessage());
			log.error(HttpStatus.SC_BAD_REQUEST + " - ERROR");
			log.error(e.getMessage(), e);
			
		}catch(Exception e){
			
			res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().write("Internal Server Error");
			log.error(HttpStatus.SC_INTERNAL_SERVER_ERROR + " - ERROR");
			log.error(e.getMessage(), e);
		}
		
		log.info("------------------------------------------------------------------------------------");
	}
	
	private byte[] getNodeContent(NodeRef nodeRef) throws ContentIOException, IOException {
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		return IOUtils.toByteArray(reader.getContentInputStream());
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

}
