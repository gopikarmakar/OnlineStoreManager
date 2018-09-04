package com.hyend.project.EcommerceManager.handler;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import com.hyend.project.EcommerceManager.data.model.PurchasedItemDetails;
import com.hyend.project.EcommerceManager.util.ConstantFields;
import com.hyend.project.EcommerceManager.data.model.PurchasedItemsCollection;

public final class PDFInvoiceHandler {
	
	private final String[] flipkartKeysToFetch = {
			"CPD: ","Order ID: ","Order Date: ", "TOTAL QTY: ", 
			"Tracking ID: ", "Invoice No: ", "Invoice Date: ",
			"TOTAL PRICE: ","Courier Name: ", "Courier AWB No: "};
	
	private static final String[] flipkartKeyWords = {"TOTAL QTY: ", "CPD: ", "IGST", "Total ", "Product",  
			 "Discount", "Description", "Order ID: ", "Order Date: ", "Tracking ID: ",
			 "Invoice No: ", "Invoice Date: ", "Courier Name: ", "DELIVERY ADDRESS: ",
			 "Shipping ADDRESS", "Billing Address", "Gross Amount", "Taxable Value", 	
			 "TOTAL PRICE: ", "Shipping Charge", "Courier Name: ", "Courier AWB No: "};

	private static final String[] amazonKeyWords   = {"Qty", "CPD: ", "PAN: ", "IGST", "GSTIN: ", "Total", "Sold By", "Product",  
		     "Discount", "Description", "Order Id: ", "Order Date: ", "Tracking ID: ",
		     "Invoice No: ", "Invoice Date: ", "Courier Name: ", "DELIVERY ADDRESS: ",
		     "Shipping ADDRESS", "Billing Address", "Gross Amount", "Taxable Value", 	
		     "TOTAL PRICE: ", "Shipping Charge", "Courier Name: ", "Courier AWB No: "};
	
	private MainHandler dataHandler = null;
	private PurchasedItemsCollection purchasedItemsCollection = null;
	
	public PDFInvoiceHandler(MainHandler dataHandler) {
		this.dataHandler = dataHandler;
		purchasedItemsCollection = PurchasedItemsCollection.get();
	}
	
	/**
	 * It splits the multiple pages in a PDF doc.
	 * Then parses each PDF doc.
	 * Finally creates a data map for each PDF doc.
	 *   
	 * @param filePath
	 * @throws IOException
	 */
	public int readPdfDcoument() {
		int errorType = ConstantFields.NO_ERROR;			
		try {
			
			FileDialog dialog = PickAFile();
			MainHandler.CURRENT_FILE_NAME = dialog.getFile();
		    MainHandler.CURRENT_FILE_LOCATION = dialog.getDirectory();		   
		    System.out.println(MainHandler.CURRENT_FILE_NAME + 
		    		" chosen from " + 
		    		MainHandler.CURRENT_FILE_LOCATION);
		    
		    if(!MainHandler.CURRENT_FILE_NAME.contains(".pdf")) {
				return ConstantFields.NOT_A_PDF_FILE_ERROR;
			}		    
		    setECommercePlatformName();			
		    if(MainHandler.CURRENT_ECOMM_PLATFORM_NAME.equals("NA")) {
				return ConstantFields.ECOMMERCE_PLATFORM_NOT_FOUND_ERROR;
			}		    		     
		    PDDocument document = PDDocument.load(new File(
					MainHandler.CURRENT_FILE_LOCATION + 
					MainHandler.CURRENT_FILE_NAME));
			if (!document.isEncrypted()) {
				if(document.getNumberOfPages() > 0) {
					splitPagesAndParse(document);
				}
				else {
					errorType = ConstantFields.PDF_NO_PAGES_ERROR;
				}
			}
			else {
				errorType = ConstantFields.PDF_ENCRYPTED_ERROR;
			}
			document.close();
		} catch (IOException ioex) {
			// TODO: handle exception
			errorType = ConstantFields.PDF_LOAD_READ_CLOSE_ERROR;
			ioex.printStackTrace();
		} catch (ParseException pex) {
			// TODO: handle exception
			errorType = ConstantFields.PDF_LOAD_READ_CLOSE_ERROR;
			pex.printStackTrace();
		} catch (NullPointerException npex) {
			// TODO: handle exception
			errorType = ConstantFields.PDF_NULL_FILE_ERROR;
			npex.getMessage();
		}		
		return errorType;
	}
	
	private FileDialog PickAFile() {
		
		FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
	    dialog.setMode(FileDialog.LOAD);
	    dialog.setVisible(true);
	    if(dialog.getFile() == null)
	    	throw new NullPointerException("Choose A File");
	    
	    return dialog;
	}
	
	private void splitPagesAndParse(PDDocument document) throws IOException, ParseException {
		
		//Instantiating Splitter class
		Splitter splitter = new Splitter();
		
		//splitting the pages of a PDF document
		List<PDDocument> Pages = splitter.split(document);
		
		//Creating an iterator 
		Iterator<PDDocument> iterator = Pages.listIterator();
		
		//Saving each page as an individual document
		int page = 1;
		ArrayList<String>totalPages = new ArrayList<>();
		while(iterator.hasNext()) {
	         PDDocument pd = iterator.next();
	         //String path = "/Users/karmakargopi/Downloads/hyend/sample-"+ page++ +".pdf";
	         //TODO:Need to consider the windows OS path format.
	         //TODO:Delete all temporary files.
	         String path = MainHandler.CURRENT_FILE_LOCATION + "/sample-"+ page++ +".pdf";
	         totalPages.add(path);
	         pd.save(path);	       
	         pd.close(); //Closing the document
	    }
	    System.out.println("Multiple PDF’s created");
	    Iterator<String> pagesIterator = totalPages.listIterator();
		while(pagesIterator.hasNext()) {
		   String path = pagesIterator.next();
		   parsePdfDocument(path);
		}
		document.close(); //Closing the document
	}

	private void parsePdfDocument(String filePath) throws IOException, ParseException {
		
		//System.out.println("parsePdfDocument = " + filePath);
		try (PDDocument document = PDDocument.load(new File(filePath))) {

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            PDFTextStripper tStripper = new PDFTextStripper();

            String pdfFileInText = tStripper.getText(document);
         
			// split by whitespace
            String lines[] = pdfFileInText.split("\\r?\\n");
                                 
            PurchasedItemDetails purchaseDetails = new PurchasedItemDetails();
            purchaseDetails.setECommercePlatformName(MainHandler.CURRENT_ECOMM_PLATFORM_NAME);        	
        	String[] keys = getInvoiceKeysToFetch();
            for (String line : lines) {            	
            	for(String key : keys) {
                	if(line.toLowerCase().contains(key.toLowerCase())) {
                		purchaseDetails.mapDetails(key, line.substring(key.length()));                		
                	}               	
                }
            	//System.out.println(line);
            }                        
            document.close(); //Closing the document            
            purchaseDetails.setId(purchaseDetails.orderDetails.getOrderId() + 
            					  purchaseDetails.invoiceDetails.getInvoiceNumber());
            purchasedItemsCollection.addPurchaseItemDetails(purchaseDetails.getId(), purchaseDetails);
        }
	}
	
	/*private void decodeCurrentFilePath(String filePath) {
		MainHandler.CURRENT_FILE_NAME = "";
		MainHandler.CURRENT_FILE_LOCATION = "";
		String[] paths = filePath.split("/");
		for(String path : paths) {			
			if(path.contains(".pdf")) 
				MainHandler.CURRENT_FILE_NAME = path;
			else {
				//TODO:Need to consider the windows OS case. 
				MainHandler.CURRENT_FILE_LOCATION += path+"/";
			}
		}
		System.out.println("Current File Name = " + MainHandler.CURRENT_FILE_NAME);
		System.out.println("Current File Location = " + MainHandler.CURRENT_FILE_LOCATION);
	}*/
	
	private String setECommercePlatformName() {
		String pdfFile = (MainHandler.CURRENT_FILE_LOCATION +
					  MainHandler.CURRENT_FILE_NAME).toLowerCase();
		if(pdfFile.contains("paytm"))
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "paytm";
		else if(pdfFile.contains("amazon"))
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "amazon";
		else if(pdfFile.contains("flipkart"))
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "flipkart";		
		else if(pdfFile.contains("snapdeal"))
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "snapdeal";
		else if(pdfFile.contains("shopclues"))
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "shopclues";
		else
			MainHandler.CURRENT_ECOMM_PLATFORM_NAME = "NA";
		
		return MainHandler.CURRENT_ECOMM_PLATFORM_NAME;
	}
	
	public String[] getInvoiceKeysToFetch() {
		String[] keys = {""};
		switch (MainHandler.CURRENT_ECOMM_PLATFORM_NAME) {
			case "paytm":
				break;
			case "amazon":
				break;
			case "flipkart":
				keys = flipkartKeysToFetch.clone();
				break;
			case "snapdeal":
				break;
			case "shopclues":
				break;
		}
		return keys;
	}
}
