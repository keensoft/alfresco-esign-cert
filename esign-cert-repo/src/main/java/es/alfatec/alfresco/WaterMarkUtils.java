package es.alfatec.alfresco;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.alfresco.repo.i18n.MessageService;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class WaterMarkUtils {
	private MessageService messageService;
	
	// Values from alfresco-global.properties
	private String url;
	
	// Fonts sign
	private static final float TEXT_SIZE = 7;
	private static final Font FONT_SIGN = FontFactory.getFont(FontFactory.HELVETICA, TEXT_SIZE, Font.NORMAL, Color.BLACK);
	
	// Fonts CSV
	private static final float TEXT_SIZE_CSV_1 = 10;
	private static final Font FONT_CSV_1 = FontFactory.getFont(FontFactory.HELVETICA, TEXT_SIZE_CSV_1, Font.BOLD, Color.BLACK);
	private static final float TEXT_SIZE_CSV_2 = 7;
	private static final Font FONT_CSV_2 = FontFactory.getFont(FontFactory.HELVETICA, TEXT_SIZE_CSV_2, Font.NORMAL, Color.BLACK);
	private static final float TEXT_SIZE_CSV_3 = 7;
	private static final Font FONT_CSV_3 = FontFactory.getFont(FontFactory.HELVETICA, TEXT_SIZE_CSV_3, Font.NORMAL, Color.BLACK);
	
	// Sizes sign
	private static final float RECTANGLE_SIGN_HEIGHT=33;
	private static final float RECTANGLE_SIGN_MARGIN_X=10;
	private static final float RECTANGLE_SIGN_MARGIN_Y=10;
	private static final float RECTANGLE_SIGN_MARGIN_INTERLINE=2;
	private static final float RECTANGLE_SIGN_MARGIN_TEXT=4;	
	
	// Sizes CSV
	private static final float RECTANGLE_CSV_MARGIN_TEXT_X = 2;
	private static final float RECTANGLE_CSV_MARGIN_TEXT_Y = 8;
	
	private final String _PRINT_SIGNATURE_INFO_ON_ALL_PAGES = "signature.info.all.pages";
	private final String _PRINT_SIGNATURE_INFO_ON_FIRST_PAGE = "signature.info.first.page";
	private final String _PRINT_SIGNATURE_INFO_ON_LAST_PAGE = "signature.info.last.page";
	private final String _NO_PRINT_SIGNATURE_INFO = "signature.info.none";
	
	private final String _PRINT_CSV_ON_ALL_PAGES = "csv.all.pages";
	private final String _PRINT_CSV_ON_FIRST_PAGE = "csv.first.page";
	private final String _PRINT_CSV_ON_LAST_PAGE = "csv.last.page";
	
	public void printSign(File tmpFile, String signer, Integer position, String signatureInfoPlace, String signatureDate) throws IOException, DocumentException{
		
		// Reader
		FileInputStream fileInputStream = new FileInputStream(tmpFile);
		PdfReader pdfReader = new PdfReader(fileInputStream);
		
		//Stamper
		FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, fileOutputStream);
		
		// Number of Pages
		int numPages = pdfReader.getNumberOfPages();
		
		// PageSize Calculation
		Rectangle pageSize;
		if(pdfReader.getPageRotation(1)==90 || pdfReader.getPageRotation(1)==270){
			pageSize = pdfReader.getPageSize(1).rotate();
		}
		else{
			pageSize = pdfReader.getPageSize(1);
		}
		
		//Rectangle sing positions
		float xLowerLeft = 0 + RECTANGLE_SIGN_MARGIN_X ;
		float yLowerLeft = 0 + RECTANGLE_SIGN_MARGIN_Y;
		float xUppeRight  = pageSize.getWidth() - RECTANGLE_SIGN_MARGIN_X;
		float yUppeRight = yLowerLeft + RECTANGLE_SIGN_HEIGHT;
		
		// Rectangle sing properties
		Rectangle signerRectangle = new Rectangle(xLowerLeft, yLowerLeft, xUppeRight, yUppeRight);
		signerRectangle.setBorder(Rectangle.BOX);
		signerRectangle.setBorderColor(Color.DARK_GRAY);
		signerRectangle.setBorderWidth(1);
		
		// Text with sign information
		if((pageSize.getHeight()>pageSize.getWidth()) && (signer.length()>39)){
			signer = signer.substring(0, 39)+"...";
		}else if(signer.length()>69){
				signer = signer.substring(0, 69)+"...";
		}
		String text = messageService.getMessage("sign.signed")+" "+signer+"   "+messageService.getMessage("sign.date")+signatureDate+" CET";
		Chunk chunk = new Chunk(text,FONT_SIGN); 
		
		// Calculation x  
		float xText;
		if(position%2!=0)
			xText= xLowerLeft + RECTANGLE_SIGN_MARGIN_TEXT;
		else
			xText= xLowerLeft + RECTANGLE_SIGN_MARGIN_TEXT + ((xUppeRight-xLowerLeft)/2);
		
		// Calculation y  
		float yText;
		if(position%2!=0)
			yText = yUppeRight - RECTANGLE_SIGN_MARGIN_TEXT - (RECTANGLE_SIGN_MARGIN_INTERLINE*(position/2)) - (TEXT_SIZE*(position-position/2));
		else
			yText = yUppeRight - RECTANGLE_SIGN_MARGIN_TEXT - (RECTANGLE_SIGN_MARGIN_INTERLINE*((position/2)-1)) - (TEXT_SIZE*(position/2));
		
		PdfContentByte canvas;
		
		
		switch(signatureInfoPlace){
			case _PRINT_SIGNATURE_INFO_ON_ALL_PAGES:
				for(int page=1; page<=numPages; page++){
					
					// Add rectangle
					canvas = pdfStamper.getOverContent(page);
					canvas.saveState();
					canvas.rectangle(signerRectangle);
					canvas.restoreState();
					
					// Add text
					ColumnText.showTextAligned(canvas, -1, new Phrase(chunk), xText, yText, 0);
				}
				break;
			case _PRINT_SIGNATURE_INFO_ON_FIRST_PAGE:
				// Add rectangle
				canvas = pdfStamper.getOverContent(1);
				canvas.saveState();
				canvas.rectangle(signerRectangle);
				canvas.restoreState();
				
				// Add text
				ColumnText.showTextAligned(canvas, -1, new Phrase(chunk), xText, yText, 0);
				break;
			case _PRINT_SIGNATURE_INFO_ON_LAST_PAGE:
				// Add rectangle
				canvas = pdfStamper.getOverContent(numPages);
				canvas.saveState();
				canvas.rectangle(signerRectangle);
				canvas.restoreState();
				
				// Add text
				ColumnText.showTextAligned(canvas, -1, new Phrase(chunk), xText, yText, 0);
		}
		
		// Close objects
		pdfStamper.close();
		pdfReader.close();
				
	}
	
	public void printCSV(File tmpFile, String csv, String csvPlace, String signatureInfoPlace) throws IOException, DocumentException{
		
				// Reader
				FileInputStream fileInputStream = new FileInputStream(tmpFile);
				PdfReader pdfReader = new PdfReader(fileInputStream);
				
				// Stamper
				FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
				PdfStamper pdfStamper = new PdfStamper(pdfReader, fileOutputStream);
				
				// Number of Pages
				int numPages = pdfReader.getNumberOfPages();
				
				// PageSize Calculation
				Rectangle pageSize;
				if(pdfReader.getPageRotation(1)==90 || pdfReader.getPageRotation(1)==270){
					pageSize = pdfReader.getPageSize(1).rotate();
				}
				else{
					pageSize = pdfReader.getPageSize(1);
				}
				
				// Rectangle positions
				float xLowerLeft = 0 + RECTANGLE_SIGN_MARGIN_X ;
				float yLowerLeft;
				if(!_NO_PRINT_SIGNATURE_INFO.equals(signatureInfoPlace)){
					yLowerLeft = 0 + RECTANGLE_SIGN_MARGIN_Y + RECTANGLE_SIGN_HEIGHT; 
				}else{
					yLowerLeft = 0 + RECTANGLE_SIGN_MARGIN_Y;
				}
				float xUppeRight = xLowerLeft + RECTANGLE_SIGN_HEIGHT;
				float yUppeRight = pageSize.getHeight() - RECTANGLE_SIGN_MARGIN_Y;
				
				// Rectangle properties
				Rectangle signerRectangle = new Rectangle(xLowerLeft, yLowerLeft, xUppeRight, yUppeRight);
				signerRectangle.setBorder(Rectangle.BOX);
				signerRectangle.setBorderColor(Color.DARK_GRAY);
				signerRectangle.setBorderWidth(1);	
				
				// CSV Text 
				
				String text1 = messageService.getMessage("csv.text1");
				Chunk chunk1 = new Chunk(text1,FONT_CSV_1); 
				float x1= xLowerLeft + RECTANGLE_CSV_MARGIN_TEXT_X + TEXT_SIZE_CSV_1;
				float y1= yLowerLeft + RECTANGLE_CSV_MARGIN_TEXT_Y;
				
				String text2 = messageService.getMessage("csv.text2");
				Chunk chunk2 = new Chunk(text2 + " " + csv,FONT_CSV_2); 
				float x2= x1 + RECTANGLE_SIGN_MARGIN_INTERLINE + TEXT_SIZE_CSV_2;
				float y2= y1;
				
				String text3 = messageService.getMessage("csv.text3");
				Chunk chunk3 = new Chunk(text3 + " " + url,FONT_CSV_3); 
				float x3= x2 + RECTANGLE_SIGN_MARGIN_INTERLINE + TEXT_SIZE_CSV_3;
				float y3= y1;
						
				
				PdfContentByte canvas;
				
				// CSV pages
				int page=0;
				int endPage=0;
				
				switch (csvPlace) {
				case _PRINT_CSV_ON_FIRST_PAGE:					
					page=1;
					endPage=1;
					break;					
				case _PRINT_CSV_ON_LAST_PAGE:					
					page=numPages;
					endPage=numPages;
					break;					
				case _PRINT_CSV_ON_ALL_PAGES:
					page=1;
					endPage=numPages;
					break;
				}
				
				// Add CSV 
				while(page<=endPage){
					
					// Add rectangle
					canvas = pdfStamper.getOverContent(page);
					canvas.saveState();
					canvas.rectangle(signerRectangle);
					canvas.restoreState();
					
					// Add text
					ColumnText.showTextAligned(canvas, -1, new Phrase(chunk1), x1, y1, 90);
					ColumnText.showTextAligned(canvas, -1, new Phrase(chunk2), x2, y2, 90);
					ColumnText.showTextAligned(canvas, -1, new Phrase(chunk3), x3, y3, 90);
					
					page++;
				}
				
				// Close objects
				pdfStamper.close();
				pdfReader.close();
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}