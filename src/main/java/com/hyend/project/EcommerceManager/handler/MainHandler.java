package com.hyend.project.EcommerceManager.handler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.hyend.project.EcommerceManager.data.model.SoldItemDetails;
import com.hyend.project.EcommerceManager.data.model.SoldItemsCollection;
import com.hyend.project.EcommerceManager.main.OnlineStoreManager;
import com.hyend.project.EcommerceManager.util.ConstantFields;
import com.mongodb.MongoException;
import com.mongodb.MongoServerException;
import com.mongodb.MongoTimeoutException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

public final class MainHandler {	
	
	public static String CURRENT_FILE_NAME = "";
	public static String CURRENT_FILE_LOCATION = "";
	
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
			showDBConnectionLostErrorMessage();
		} catch (MongoException mex) {
			showDBConnectionLostErrorMessage();
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
			System.out.println("Start Date: " + startDate);
			System.out.println("End Date: " + endDate);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date startDATE = dateFormat.parse(startDate);
			Date endDATE = dateFormat.parse(endDate);
			/*Calendar cal = Calendar.getInstance();
			cal.setTime(startDt);
			String fromMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			cal.setTime(endDt);
			String tillMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());*/
			getAllInvoicesBetween(startDATE, endDATE);
			sheetHandler.generateInvoiceSheetForDates(startDate, endDate);
		} catch (ParseException pex) {			
			showInvalidDateErrorMessage();
			return;
		} catch (IOException ioex) {
			OnlineStoreManager.showErrorMessage("Problem Occurred", 
					"Something Went Wrong. Please Try Save Again!");
			return;
		} catch (NullPointerException npex) {
			OnlineStoreManager.showErrorMessage("Save Cancelled", 
					"It's Cancelled. File Did Not Save!");
			return;
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundErrorMessageForDates();			
			return;
		} finally {
			/**
			 * Clear fetched records.
			 */
			SoldItemsCollection.get().clear();			
		}
	}
	
	public void updatePaymentStatusAsReceived(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			dbHandler.updatePaymentStatusAsReceived(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void updateCourierStatusAsDelivered(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;
			dbHandler.updateCourierStatusAsDelivered(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void updateReturnStatusFAsReturned(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;	
			dbHandler.updateReturnStatusAsReturned(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void updateReturnRcvdDate(String orderId, String rcvdDate) {
		try {
			if(!isValidValue(orderId)) return;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date rcvdDt = dateFormat.parse(rcvdDate);
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			 			
			dbHandler.updateReturnRcvdDate(orderId, rcvdDt);
		} catch (ParseException pex) {
			showInvalidDateErrorMessage();
			return;
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}		
	}
	
	public void updateReturnCondition(String orderId, String condition) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isValidReturnCondition(condition)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			dbHandler.updateReturnCondition(orderId, condition);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void deleteAllPaymentStatusAsReceived() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			dbHandler.deleteAllPaymentStatusAsReceived();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	}
	
	public void deleteAllCourierStatusAsDelivered() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			dbHandler.deleteAllCourierStatusAsDelivered();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	}
	
	public void deleteAllCourierStatusAsReturned() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			dbHandler.deleteAllCourierStatusAsReturned();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_MATCH_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	}
	
	private void connectToDB() {
		try {
			dbHandler.connectToDB(dbName);		
		} catch (RuntimeException rex) {			
			rex.printStackTrace();
			return;
		}
	}
	
	private void fetchInvoicesCollection() {		
		try {
			dbHandler.fetchCollection(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME +
					invoiceTableNameTag);
		} catch (IllegalArgumentException e) {			
			System.out.println("Couldn't Find The Collection!");
			return;
		}
	}
	
	private boolean isPlatformNameAvailable() {
		boolean isAvailble = true;
		if(ConstantFields.CURRENT_ECOMM_PLATFORM_NAME.contains(
				ConstantFields.ECOMMERCE_PLATFORMS[0])) {
			isAvailble = false;
			OnlineStoreManager.showErrorMessage("No Platform Name", 
					"Please Select A Valid Platform Name and Try Again!");
		}
		return isAvailble;
	}
	
	private boolean isValidValue(String value) {
		if(value == null || value.isEmpty()) {
			OnlineStoreManager.showErrorMessage("Wrong Value", 
					"Order Id or Return Received Date Can't Be Null" + 
					"Please Enter Correct Value and Try Again!");
			return false;
		}
		return true;
	}
	
	private boolean isValidReturnCondition(String condition) {
		if(condition.equals("")) {
			OnlineStoreManager.showErrorMessage("Wrong Return Condition", 					
					"Please Select A Valid Return Condition and Try Again!");
			return false;
		}
		return true;
	}
	
	private void showDBConnectionLostErrorMessage() {
		OnlineStoreManager.showErrorMessage("DB Connection Lost", 
				"Connection Lost With MongoDB" + "\n" +
				"Try Again By Restarting MongoDB Server and App Both!");
	}
	
	private void showMongoExceptionErrorMessage() {
		OnlineStoreManager.showErrorMessage("Problem Occurred", 
				"There's Some Problem With MongoDB" + "\n" +
				"Try Again By Restarting MongoDB Server and App Both!");
	}
	
	private void showInvalidDateErrorMessage() {
		OnlineStoreManager.showErrorMessage("Invalid Date", 
			"Please Enter A Valid Date In DD-MM-YYYY Format and Try Again!");
	}
	
	private void showNoInvoiceFoundToDeleteErrorMessage() {
		OnlineStoreManager.showErrorMessage("No Invoices Deleted", 
			"No Invoices Found To Delete. All Up To Date!");
	}
	
	private void showNoInvoiceFoundErrorMessage(String orderId) {
		OnlineStoreManager.showErrorMessage("No Invoice Updated", 
			"No Invoice Found For Order Id = " + orderId + "\n" +
			"Please Enter A Valid Order Id and Try Again!");
	}
	
	private void showRecordAlreadyUpdatedErrorMessage(String orderId) {
		OnlineStoreManager.showErrorMessage("Duplicate Invoice", 
			"Invoice Already Updated For Order Id = " + orderId + "\n" +
			"Please Enter A New Order Id and Try Again!");
	}
	
	private void showNoInvoiceFoundErrorMessageForDates() {
		OnlineStoreManager.showErrorMessage("No Invoices Found", 
			"No Invoices Found Between Those Dates" + "\n" +
			"Please Try Again With Other Dates!");
	}
		
	private void fetchAndInitInvoicesData() {
		int status = pdfHandler.readPdfDcoument();
		switch (status) {
			case ConstantFields.PDF_NULL_FILE_ERROR:				
				//TODO: Show error message
				System.out.println("Please Choose At Least A File!");
				return;
			case ConstantFields.PDF_NO_PAGES_ERROR:
				//TODO: Show error message
				System.out.println("There No Pages In PDF File Error!");
				return;
			case ConstantFields.PDF_ENCRYPTED_ERROR:
				//TODO: Show error message
				System.out.println("PDF File Is Encrypted Error!");
				return;
			case ConstantFields.NOT_A_PDF_FILE_ERROR:
				//TODO: Show error message
				System.out.println("It's Not A PDF File Error!");
				return;
			case ConstantFields.PDF_LOAD_READ_CLOSE_ERROR:
				//TODO: Show error message
				System.out.println("Load, Read or Close PDF File Error!");
				return;
			default:
				//TODO: Show success message alert dialog box
				System.out.println(MainHandler.CURRENT_FILE_LOCATION + 
								   MainHandler.CURRENT_FILE_NAME + 
								   " File Read & Parsed Successfully!");
				return;
		}
	}
	
	private void storeAllInvoicesToDB() {		
		try {
			dbHandler.insertAllToCollection();
			OnlineStoreManager.showInfoMessage("File Saved", 
					"Invoice Data Stored To DB Successfully!");
		} catch (RuntimeException rex) {
			if(rex.getMessage().contains("duplicate key error")) {
				OnlineStoreManager.showErrorMessage("Duplicate File Error", 
						"This File's Saved Already. Please Select A New File!");				
			}
			return;
		} finally {
			/**
			 * Clear saved invoices in temporary map. 
			 * To avoid duplicates.
			 */
			SoldItemsCollection.get().clear();
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
			return;
		}		
	}
	
	private void showInvoiceForOrderId(String orderId) {		
		try {
			SoldItemDetails record = dbHandler.getInvoiceForOrderId(orderId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Order Id = " + orderId);
			return;
		}		
	}
	
	private void showInvoiceForInvoiceNumber(String invoiceId) {		
		try {
			SoldItemDetails record = dbHandler.getInvoiceForOrderId(invoiceId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Invoice Number = " + invoiceId);
			return;
		}		
	}
	
	private void getAllInvoicesBetween(Date startDate, Date endDate) throws RuntimeException {
		SoldItemsCollection.get().addSoldItemDetailsList(
			dbHandler.getInvoicesBetweenOrderDate(startDate, endDate));										 
	}
}