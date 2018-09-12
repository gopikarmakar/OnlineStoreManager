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

	private PDFInvoiceHandler pdfHandler;
	private DatabaseHandler dbHandler;
	private SpreadSheetHandler sheetHandler;
	
	/**
	 * Constructor
	 */
	public MainHandler() {		
		dbHandler = new DatabaseHandler(this);
		pdfHandler = new PDFInvoiceHandler(this);		
		sheetHandler = new SpreadSheetHandler(this);
	}
	
	/**
	 * Public Methods
	 */
	public void init() {
		connectToDB();
	}
	
	public void dispose() {
		dbHandler = null;
		pdfHandler = null;
		sheetHandler = null;
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
	
	public void storeInvoicePdfToDB() {
		if(isPlatformNameAvailable()) {
			if(isConnectedToDB()) {
				fetchAndInitInvoicesData();		
				storeAllInvoicesToDB();
			}
		}
	}
	
	public void generateSpreadSheet(int generateForWhom, 
			String startDate, String endDate) {
		try {
			if(!isPlatformNameAvailable()) return;						
			if(!isConnectedToDB()) return;
			switch(generateForWhom) {
				case ConstantFields.GENERATE_FOR_DATES:
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date startDATE = dateFormat.parse(startDate);
					Date endDATE = dateFormat.parse(endDate);
					getAllInvoicesBetween(startDATE, endDATE);
					break;
				case ConstantFields.GENERATE_FOR_PAYMENT_RECEIVED:
					getAllPaymentReceivedInvoices();
					break;
				case ConstantFields.GENERATE_FOR_COURIER_DELIVERED:
					getAllCourierDeliveredInvoices();
					break;					
			}			
			sheetHandler.generateInvoiceSheetForDates();
			showSpreadSheetFileSavedSuccessfullyMessage();
		} catch (ParseException pex) {			
			showInvalidDateErrorMessage();
			return;
		} catch (IOException ioex) {
			showSomethingWrongErrorMessage();
			return;
		} catch (NullPointerException npex) {
			showFileSaveCancelledErrorMessage();
			return;
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToCreateSheetErrorMessage();			
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
			if(dbHandler.updatePaymentStatusAsReceived(orderId))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void updatePaymentMode(String orderId, String mode) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isValidValue(mode)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			if(dbHandler.updatePaymentMode(orderId, mode))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
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
			if(dbHandler.updateCourierStatusAsDelivered(orderId))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void updateReturnStatusAsReturned(String orderId) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;	
			if(dbHandler.updateReturnStatusAsReturned(orderId))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
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
			if(dbHandler.updateReturnRcvdDate(orderId, rcvdDt))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (ParseException pex) {
			showInvalidDateErrorMessage();
			return;
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}		
	}
	
	public void updateReturnCondition(String orderId, String condition) {
		try {
			if(!isValidValue(orderId)) return;
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			if(dbHandler.updateReturnCondition(orderId, condition))
				showRecordsUpdatedSuccessfulMessage(orderId);
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToUpdateErrorMessage(orderId);
			else if(rex.getMessage().equals(ConstantFields.ALREADY_UPDATED_ERROR))
				showRecordAlreadyUpdatedErrorMessage(orderId);
			return;
		}
	}
	
	public void deleteAllPaymentStatusAsReceived() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			if(dbHandler.deleteAllPaymentStatusAsReceived())
				showRecordsDeletedSuccessfulMessage();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	}
	
	public void deleteAllCourierStatusAsDelivered() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			if(dbHandler.deleteAllCourierStatusAsDelivered())
				showRecordsDeletedSuccessfulMessage();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	}
	
	public void deleteAllCourierStatusAsReturned() {
		try {
			if(!isPlatformNameAvailable()) return;
			if(!isConnectedToDB()) return;			
			if(dbHandler.deleteAllCourierStatusAsReturned())
				showRecordsDeletedSuccessfulMessage();
		} catch (RuntimeException rex) {
			if(rex instanceof MongoServerException) {
				showMongoExceptionErrorMessage();
			}
			else if(rex.getMessage().equals(ConstantFields.NO_RECORDS_FOUND_ERROR))
				showNoInvoiceFoundToDeleteErrorMessage();
			return;
		}
	} // End Of Public Methods
	
	/**
	 * Private Methods
	 */
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
			OnlineStoreManager.showErrorMessage("No Platform Name Selected", 
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
	
	/**
	 * All Error Messages
	 */
	private void showDBConnectionLostErrorMessage() {
		OnlineStoreManager.showErrorMessage("DB Connection Lost", 
				"Connection Lost With MongoDB" + "\n" +
				"Try Again By Restarting MongoDB Server and App Both!");
	}
	
	private void showMongoExceptionErrorMessage() {
		OnlineStoreManager.showErrorMessage("Problem Occurred", 
				"There's Some Problem With MongoDB Server" + "\n" +
				"Try Again By Restarting MongoDB Server and App Both!");
	}
	
	private void showSomethingWrongErrorMessage() {
		OnlineStoreManager.showErrorMessage("Problem Occurred", 
				"Something Went Wrong. Please Try Again!");
	}
	
	private void showInvalidDateErrorMessage() {
		OnlineStoreManager.showErrorMessage("Invalid Date", 
			"Please Enter A Valid Date In DD-MM-YYYY Format and Try Again!");
	}
	
	private void showNoInvoiceFoundToDeleteErrorMessage() {
		OnlineStoreManager.showErrorMessage("No Invoices Deleted", 
			"No Invoices Found To Delete. All Up To Date!");
	}
	
	private void showNoInvoiceFoundToUpdateErrorMessage(String orderId) {
		OnlineStoreManager.showErrorMessage("No Invoice Updated", 
			"No Invoice Found For Order Id = " + orderId + "\n" +
			"Please Enter A Valid Order Id and Try Again!");
	}
	
	private void showRecordAlreadyUpdatedErrorMessage(String orderId) {
		OnlineStoreManager.showErrorMessage("Duplicate Invoice", 
			"Cannot Update The Same Value For Order Id = " + orderId + "\n" +
			"Try Again With A New Order Id or With A New Value!");
	}
	
	private void showNoInvoiceFoundToCreateSheetErrorMessage() {
		OnlineStoreManager.showErrorMessage("No Invoices Found", 
			"No Invoices Found To Create Spreadsheet As Per Searching Condition" + "\n" +
			"Please Update DB and Then Generate spreadsheet!");
	}
	
	private void showNoPDFFileSelectedErrorMessage() {
		OnlineStoreManager.showErrorMessage("Not File Selected", 
			"No Valid PDF FIle Selected" + "\n" +
			"Select A Valid PDF File and Try Again!");
	}
	
	private void showNotAPDFFileErrorMessage() {
		OnlineStoreManager.showErrorMessage("Not A PDF File", 
			"It's Not A Valid PDF FIle" + "\n" +
			"Try Again With A Valid PDF File!");
	}
	
	private void showPDFFileHasNoPagesErrorMessage() {
		OnlineStoreManager.showErrorMessage("No Pages", 
			"PDF File Doesn't Have Any Invoice To Read Inside" + "\n" +
			"Try Again With A Valid PDF File With Invoices Inside!");
	}
	
	private void showPDFFileIsEncryptedErrorMessage() {
		OnlineStoreManager.showErrorMessage("Encrypted PDF File", 
			"PDF File Is Encrypted With A Password" + "\n" +
			"Try Again With A Non Encrypted PDF File!");
	}
	
	private void showPDFFileAlreadyProcessedErrorMessage() {
		OnlineStoreManager.showErrorMessage("File Aready Processed", 
			"This PDF FIle Already Processed" + "\n" +
			"Try Again With A New Valid PDF File!");
	}
	
	private void showFileSaveCancelledErrorMessage() {
		OnlineStoreManager.showErrorMessage("Save Cancelled", 
				"Spreadsheet File Did Not Save!");
	}
	// End Of All Error Messages
	
	/**
	 * All Successful Messages
	 */
	private void showFileStoredSuccessfulMessage() {
		OnlineStoreManager.showInfoMessage("File Stored", 
				MainHandler.CURRENT_FILE_NAME + " Stored To DB Successfully!");
	}
	
	private void showSpreadSheetFileSavedSuccessfullyMessage() {
		OnlineStoreManager.showInfoMessage("File Saved", 
				MainHandler.CURRENT_FILE_NAME + " Created and Saved Successfully!");
	}
	
	private void showRecordsUpdatedSuccessfulMessage(String orderId) {
		OnlineStoreManager.showInfoMessage("Records Updated", 
				"Records Updated Successfully for Order Id: " + orderId);
	}
	
	private void showRecordsDeletedSuccessfulMessage() {
		OnlineStoreManager.showInfoMessage("Records Deleted", 
				"Records Deleted Successfully!");
	}
		
	private void fetchAndInitInvoicesData() {
		int status = pdfHandler.readPdfDcoument();
		switch (status) {
			case ConstantFields.PDF_NULL_FILE_ERROR:				
				showNoPDFFileSelectedErrorMessage();
				return;
			case ConstantFields.PDF_NO_PAGES_ERROR:
				showPDFFileHasNoPagesErrorMessage();
				return;
			case ConstantFields.PDF_ENCRYPTED_ERROR:
				showPDFFileIsEncryptedErrorMessage();
				return;
			case ConstantFields.NOT_A_PDF_FILE_ERROR:
				showNotAPDFFileErrorMessage();
				return;
			case ConstantFields.PDF_LOAD_READ_CLOSE_ERROR:
				showSomethingWrongErrorMessage();
				return;
			default:				
				System.out.println(MainHandler.CURRENT_FILE_LOCATION + 
								   MainHandler.CURRENT_FILE_NAME + 
								   " File Read & Parsed Successfully!");
				return;
		}
	}
	
	private void storeAllInvoicesToDB() {		
		try {
			dbHandler.insertAllToCollection();
			showFileStoredSuccessfulMessage();
		} catch (RuntimeException rex) {
			if(rex.getMessage().contains("duplicate key error")) {
				showPDFFileAlreadyProcessedErrorMessage();				
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
	
	private void getAllPaymentReceivedInvoices() throws RuntimeException {
		SoldItemsCollection.get().addSoldItemDetailsList(
			dbHandler.getPaymentReceivedInvoices());										 
	}
	
	private void getAllCourierDeliveredInvoices() throws RuntimeException {
		SoldItemsCollection.get().addSoldItemDetailsList(
			dbHandler.getCourierDeliveredInvoices());										 
	}
}