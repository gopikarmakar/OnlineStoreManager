package com.hyend.project.EcommerceManager.handler;

import java.io.IOException;
import java.text.ParseException;

import com.hyend.project.EcommerceManager.data.model.PurchasedItemDetails;
import com.hyend.project.EcommerceManager.util.ConstantFields;

public class MainHandler {	
	
	public static String CURRENT_FILE_NAME = "";
	public static String CURRENT_FILE_LOCATION = "";
	public static String CURRENT_ECOMM_PLATFORM_NAME = "";
	
	private boolean isConnectedToDB = false;
	
	private final String dbName = "onlinestore";
	private final String invoiceTableNameTag = "_invoices"; 

	private final PDFInvoiceHandler pdfHandler;
	private final DatabaseHandler dbHandler;
	private final SpreadSheetHandler sheetHandler;
	
	public MainHandler() {		
		pdfHandler = new PDFInvoiceHandler(this);
		dbHandler = new DatabaseHandler(this);
		sheetHandler = new SpreadSheetHandler(this);
	}
	
	public void init() {
		
		//TODO: Need to create a queue to handle multiple PDF files one by one.				
		fetchAndInitInvoicesData();
		connectToDB();
		if(isConnectedToDB) {
			//TODO: Show connect failed message alert dialog box
			fetchCollection();
			storeAllInvoicesToDB();
			showAllRecordsFromDB();
			//showInvoiceForOrderId("OD112973057490800000");
			//updateCourierStatusForOrderId("OD112973057490800000");
			//showInvoiceForOrderId("OD112973057490800000");
			//showAllInvoicesBetween("02-04-2018", "29-07-2018");
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
			case ConstantFields.ECOMMERCE_PLATFORM_NOT_FOUND_ERROR:
				//TODO: Show error message
				System.out.println("Wrong File. No ECommerce Platform Found Error!");
				break;
			default:
				//TODO: Show success message alert dialog box
				System.out.println(MainHandler.CURRENT_FILE_LOCATION + 
								   MainHandler.CURRENT_FILE_NAME + 
								   " File Read & Parsed Successfully!");
				break;
		}
	}
	
	private void connectToDB() {
		try {
			dbHandler.connectToDB(dbName);
			//TODO: Show connect successful message alert dialog box
			System.out.println("Successfully Connected To DB!");
			isConnectedToDB = true;
		} catch (RuntimeException rex) {
			isConnectedToDB = false;
			//TODO: Show connect failed message alert dialog box
			System.out.println("Could Not Connect To DB. DB Server Might Be Down!");
			rex.printStackTrace();
		}		
	}
	
	private void fetchCollection() {		
		try {
			dbHandler.fetchCollection(CURRENT_ECOMM_PLATFORM_NAME + invoiceTableNameTag);
			//TODO: Show connect successful message alert dialog box
			System.out.println("Found The Collection!");
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			System.out.println("Couldn't Find The Collection!");
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
			for(PurchasedItemDetails record : dbHandler.getAllInvoices()) {
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
			PurchasedItemDetails record = dbHandler.getInvoiceForOrderId(orderId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Order Id = " + orderId);
		}		
	}
	
	private void showInvoiceForInvoiceNumber(String invoiceId) {		
		try {
			PurchasedItemDetails record = dbHandler.getInvoiceForOrderId(invoiceId);
			System.out.println(record.toString());
		} catch (NullPointerException npex) {
			// TODO: handle exception
			System.out.println("No Invoice Found For Invoice Number = " + invoiceId);
		}		
	}
	
	private void showAllInvoicesBetween(String startDate, String endDate) {		
		try {
			for(PurchasedItemDetails record : dbHandler.getInvoicesBetweenOrderDate(startDate, endDate)) {
				System.out.println(record.toString());
			}
			//createSpreadSheet();
		} catch (ParseException e) {
			System.out.println("Parse error : Couldn't Parse The Date");
			// TODO: handle exception
			e.printStackTrace();			
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
	
	private void createSpreadSheet() {
		try {
			sheetHandler.generateInvoiceSpreadSheet();
		} catch (IOException ioex) {
			// TODO: handle exception
			System.out.println("Failed To Create The Spread Sheet!");
		}
	}
}