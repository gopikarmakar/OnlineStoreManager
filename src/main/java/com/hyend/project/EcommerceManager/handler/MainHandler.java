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
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;

public final class MainHandler {	
	
	public static String CURRENT_FILE_NAME = "";
	public static String CURRENT_FILE_LOCATION = "";
	
	private final String dbName = "HyendStore";
	private final String invoiceTableNameTag = "_invoices"; 
	
	private boolean isConnectedToDB = false;

	private final PDFInvoiceHandler pdfHandler;
	private final DatabaseHandler dbHandler;
	private final SpreadSheetHandler sheetHandler;
	
	public MainHandler() {		
		dbHandler = new DatabaseHandler(this);
		pdfHandler = new PDFInvoiceHandler(this);		
		sheetHandler = new SpreadSheetHandler(this);
	}
	
	public void init() {
				
		connectToDB();
		/*if(isConnectedToDB) {
			//updateCourierStatusForOrderId("OD112973057490800000");
		}*/
	}
	
	public boolean isConnectedToDB() {
		boolean isConnected = false;
		try {
			isConnected = dbHandler.isConnectedToDB();	
		} catch (NullPointerException npex) {	
			OnlineStoreManager.showErrorMessage("Connection Lost With MongoDB.", 
					"Something's Wrong. Try Restarting MongoDB Server and This App Again!");
		} catch (MongoTimeoutException e) {
			OnlineStoreManager.showErrorMessage("Connection Lost With MongoDB.", 
					"Try Restarting MongoDB Server and This App Again!");
		} 
		return isConnected;
	}
	
	public void setEcommercePlatform(String plateformName) {		
		ConstantFields.CURRENT_ECOMM_PLATFORM_NAME = plateformName;
		if(isConnectedToDB())
			fetchInvoicesCollection();
	}
	
	public void saveInvoicePdfToDB() {
		if(isPlatformNameAvailable()) {
			if(isConnectedToDB()) {
				fetchAndInitInvoicesData();		
				storeAllInvoicesToDB();
			}
		}
	}
	
	public void generateSpreadSheetBetween(String startDate, String endDate) {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;
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
			OnlineStoreManager.showErrorMessage("Invalid Date", 
					"Please Enter A Valid Date In DD-MM-YYYY Format!");					
		} catch (IOException ioex) {
			// TODO: handle exception
			System.out.println("Failed To Create The Spread Sheet!");
		} 
	}
	
	public void updateCourierStatusAsDelivered(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB) return;
			dbHandler.updateCourierStatusAsDelivered(orderId);
		} catch (NullPointerException npex) {
			showNoInvoiceFoundErrorMessage(orderId);
		}
	}
	
	public void updateReturnStatusForOrderId(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB) return;			
			dbHandler.updateCourierStatusAsDelivered(orderId);
		} catch (NullPointerException npex) {
			showNoInvoiceFoundErrorMessage(orderId);
		}
	}
	
	public void updateReturnRcvdDate(String orderId, String rcvdDate) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB) return;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date rcvdDt = dateFormat.parse(rcvdDate); 			
			dbHandler.updateReturnRcvdDate(orderId, rcvdDt);
		} catch (ParseException pex) {
			OnlineStoreManager.showErrorMessage("Invalid Date", 
					"Please Enter A Valid Date In DD-MM-YYYY Format!");	
		} 
		catch (NullPointerException npex) {
			showNoInvoiceFoundErrorMessage(orderId);
		}
	}
	
	public void updateReturnCondition(String orderId, String condition) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isValidValue(condition)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB) return;			
			dbHandler.updateReturnCondition(orderId, condition);
		} catch (NullPointerException npex) {
			showNoInvoiceFoundErrorMessage(orderId);
		}
	}
	
	private void connectToDB() {
		try {
			dbHandler.connectToDB(dbName);		
		} catch (RuntimeException rex) {			
			rex.printStackTrace();
		}
	}
	
	private void fetchInvoicesCollection() {		
		try {
			dbHandler.fetchCollection(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME +
					invoiceTableNameTag);
		} catch (IllegalArgumentException e) {			
			System.out.println("Couldn't Find The Collection!");
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
	
	private boolean isValidValue(String value) {
		if(value == null || value.isEmpty())
			return false;
		return true;
	}
	
	private void showNoInvoiceFoundErrorMessage(String orderId) {
		OnlineStoreManager.showErrorMessage("No Invoice Found", 
				"No Invoice Found For Order Id = " + orderId + "\n" +
				"Please Enter A Valid Order Id");
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
			OnlineStoreManager.showInfoMessage("File Saved", 
					"File Data Stored Successfully!");
		} catch (RuntimeException rex) {
			if(rex.getMessage().contains("duplicate key error")) {
				OnlineStoreManager.showErrorMessage("Duplicate File Error", 
						"This File's Saved Already. Please Select A New File!");
			}
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
	
	
}