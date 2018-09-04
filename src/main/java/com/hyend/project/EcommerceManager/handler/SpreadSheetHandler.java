package com.hyend.project.EcommerceManager.handler;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hyend.project.EcommerceManager.util.ConstantFields;

public final class SpreadSheetHandler {
	
	private final String flipkartInvoiceSheetName = "flipkart_invoice";
	
	private MainHandler dataHandler;
	private HSSFWorkbook invoiceWorkbook;

	public SpreadSheetHandler(MainHandler dataHandler) {
		this.dataHandler = dataHandler;
		invoiceWorkbook = new HSSFWorkbook();
	}
	
	/*public void generateSpreadSheet(String platform) throws IOException {
		switch (platform.toLowerCase()) {
			case "paytm":
				break;
			case "amazon":
				break;
			case "flipkart":
				generateInvoiceSpreadSheet();
				break;
			case "snapdeal":
				break;
			case "shopclues":
				break;
			default:
				break;
		}
	}*/
	
	public void generateInvoiceSpreadSheet() throws IOException {
				 
		HSSFSheet spreadsheet = invoiceWorkbook.createSheet(MainHandler.CURRENT_ECOMM_PLATFORM_NAME + "_sales_details");		
		createHeadingRow(spreadsheet);
		FileOutputStream out = new FileOutputStream(new File(
			MainHandler.CURRENT_FILE_LOCATION + MainHandler.CURRENT_ECOMM_PLATFORM_NAME + "_invoice_details.xls"));
	    invoiceWorkbook.write(out);
	    out.close();	    
	    invoiceWorkbook.close();
	    System.out.println("invoice_details.xlsx written successfully");
	}
	
	private void createHeadingRow(HSSFSheet spreadsheet) {
		int rowCount = 1;
		int cellCount = 1;
		HSSFRow row = spreadsheet.createRow(rowCount);
		HSSFCell cell;		
		for(String key : ConstantFields.tableColumnFields) {
			cell = row.createCell(cellCount++);
			cell.setCellValue(key);
		}				
	}
}
