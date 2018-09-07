package com.hyend.project.EcommerceManager.handler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.hyend.project.EcommerceManager.data.model.SoldItemDetails;
import com.hyend.project.EcommerceManager.data.model.SoldItemsCollection;
import com.hyend.project.EcommerceManager.gui.OnlineStoreManager;
import com.hyend.project.EcommerceManager.util.ConstantFields;

public final class MainHandler {	
	
	public static String CURRENT_FILE_NAME = "";
	public static String CURRENT_FILE_LOCATION = "";
	
	private boolean isConnectedToDB = false;
	
	private final String dbName = "HyendStore";
	private final String invoiceTableNameTag = "_invoices"; 

	private final PDFInvoiceHandler pdfHandler;
	private final DatabaseHandler dbHandler;
	private final SpreadSheetHandler sheetHandler;
	
	public MainHandler() {		
		dbHandler = new DatabaseHandler(this);
		pdfHandler = new PDFInvoiceHandler(this);		
		sheetHandler = new SpreadSheetHandler(this);
	}
	
	public boolean init() {
				
		return connectToDB();
		/*if(isConnectedToDB) {
			//TODO: Show connect failed message alert dialog box
			fetchCollection();
			//storeAllInvoicesToDB();
			//showAllRecordsFromDB();
			//showInvoiceForOrderId("OD112973057490800000");
			//updateCourierStatusForOrderId("OD112973057490800000");
			//showInvoiceForOrderId("OD112973057490800000");
			//getAllInvoicesBetween("02-04-2018", "29-07-2018");
			generateSpreadSheetBetween("02-04-2018", "29-07-2018");
		}*/
	}
	
	public void setEcommercePlatform(String plateformName) {		
		ConstantFields.CURRENT_ECOMM_PLATFORM_NAME = plateformName;		
		fetchInvoicesCollection();
		System.out.println(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME);
	}
	
	public void saveInvoicePdfToDB() {
		if(!isConnectedToDB) {
			System.out.println("Not Connected To DB! May Be DB Server Is Down!");
			return;
		}
		if(!isPlatformNameAvailable()) return;
		fetchAndInitInvoicesData();		
		storeAllInvoicesToDB();
	}
	
	public void generateSpreadSheetBetween(String startDate, String endDate) {
		try {
			if(!isPlatformNameAvailable()) return;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date startDt = dateFormat.parse(startDate);  
			Date endDt = dateFormat.parse(endDate);				
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDt);
			String fromMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			cal.setTime(endDt);
			String tillMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());			
			getAllInvoicesBetween(startDt, endDt);
			sheetHandler.generateInvoiceSpreadSheet(fromMonth, tillMonth);
		} catch (ParseException pex) {			
			OnlineStoreManager.showErrorMessage("Date Error", "Please Enter A Valid Date In DD-MM-YYYY Format!");					
		} catch (IOException ioex) {
			// TODO: handle exception
			System.out.println("Failed To Create The Spread Sheet!");
		} 
	}
	
	private boolean isPlatformNameAvailable() {
		boolean isAvailble = true;
		if(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME.contains(
				ConstantFields.ECOMMERCE_PLATFORMS[0])) {
			isAvailble = false;
			OnlineStoreManager.showErrorMessage("No Platform Name", 
					"Please Select A Valid Platform Name!");
		}
		return isAvailble;
	}
	
	private void checkDBConnection() {
		
	}
	
	private boolean connectToDB() {
		try {
			dbHandler.connectToDB(dbName);					
			isConnectedToDB = dbHandler.isConnectedToDB();
			System.out.println("Successfully Connected To DB! " + isConnectedToDB);
		} catch (RuntimeException rex) {
			isConnectedToDB = false;			
			//rex.printStackTrace();
		}
		return isConnectedToDB;
	}
	
	private void fetchInvoicesCollection() {		
		try {
			dbHandler.fetchCollection(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME +
					invoiceTableNameTag);
			//TODO: Show connect successful message alert dialog box
			System.out.println("Found The Collection!");
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			System.out.println("Couldn't Find The Collection!");
		}
	}
		
	private void fetchAndInitInvoicesData() {
		int status = pdfHandler.readPdfDcoument();
		switch (status) {
			case ConstantFields.PDF_NULL_FILE_ERROR:				
				//TODO: Show error message
				System.out.println("Please Choose At Least A File!");
				break;
			case ConstantFields.PDF_NO_PAGES_ERROR:
				//TODO: Show error message
				System.out.println("There No Pages In PDF File Error!");
				break;
			case ConstantFields.PDF_ENCRYPTED_ERROR:
				//TODO: Show error message
				System.out.println("PDF File Is Encrypted Error!");
				break;
			case ConstantFields.NOT_A_PDF_FILE_ERROR:
				//TODO: Show error message
				System.out.println("It's Not A PDF File Error!");
				break;
			case ConstantFields.PDF_LOAD_READ_CLOSE_ERROR:
				//TODO: Show error message
				System.out.println("Load, Read or Close PDF File Error!");
				break;
			default:
				//TODO: Show success message alert dialog box
				System.out.println(MainHandler.CURRENT_FILE_LOCATION + 
								   MainHandler.CURRENT_FILE_NAME + 
								   " File Read & Parsed Successfully!");
				break;
		}
	}
	
	private void storeAllInvoicesToDB() {		
		try {
			dbHandler.insertAllToCollection();
			//TODO: Show insert success message alert dialog box
			System.out.println("Data Inserted Successfully!");
		} catch (RuntimeException rex) {
			//TODO: Show insert failed message alert dialog box
			System.out.println("Failed To Insert Data To DB!");
			rex.printStackTrace();
		}		
	}
	
	private void showAllRecordsFromDB() {		
		try {		
			for(SoldItemDetails record : dbHandler.getAllInvoices()) {
				System.out.println(record.toString());
			}
		} catch (NullPointerException npex) {
			// TODO: handle exception
			npex.printStackTrace();
			System.out.println("No Invoices Found");
		}		
	}
	
	private void showInvoiceForOrderId(String orderId) {		
		try {
			SoldItemDetails record = dbHandler.getInvoiceForOrderId(orderId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Order Id = " + orderId);
		}		
	}
	
	private void showInvoiceForInvoiceNumber(String invoiceId) {		
		try {
			SoldItemDetails record = dbHandler.getInvoiceForOrderId(invoiceId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Invoice Number = " + invoiceId);
		}		
	}
	
	private void getAllInvoicesBetween(Date startDate, Date endDate) {
		try {
			SoldItemsCollection.get().addSoldItemDetailsList(
					dbHandler.getInvoicesBetweenOrderDate(startDate, endDate));			
			for(SoldItemDetails record : SoldItemsCollection.get().getSoldItemsDetailsList()) {
				System.out.println(record.toString());
			}						
		} catch (NullPointerException npex) {
			// TODO: handle exception
			npex.printStackTrace();
			System.out.println("No Invoice Found Between " + startDate + " and " + endDate + " Dates.");
		}
	}
	
	private void updateCourierStatusForOrderId(String orderId) {
		try {
			dbHandler.updateCourierStatusForOrderId(orderId);
		} catch (NullPointerException npex) {
			npex.printStackTrace();
			System.out.println("Couldn't Update. No Invoice Found For Order Id = " + orderId);
		}
	}	
}