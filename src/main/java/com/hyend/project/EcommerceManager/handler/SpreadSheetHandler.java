package com.hyend.project.EcommerceManager.handler;

import java.io.File;
import java.awt.Frame;
import java.awt.FileDialog;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.hyend.project.EcommerceManager.util.ConstantFields;
import com.hyend.project.EcommerceManager.data.model.SoldItemDetails;
import com.hyend.project.EcommerceManager.data.model.SoldItemsCollection;

public final class SpreadSheetHandler {
	
	private final String sheetTagForDate = "_sales_report";
	
	private HSSFWorkbook invoiceWorkbook = null;

	public SpreadSheetHandler(MainHandler dataHandler) {}
	
	public void generateInvoiceSheetForDates() 
			throws IOException, NullPointerException {
				
		openSaveAsDialogBox();		
		String sheetName = ConstantFields.CURRENT_ECOMM_PLATFORM_NAME + sheetTagForDate;
		createSpreadsheet(sheetName);
		createWorkbook();
	}
	
	private void createSpreadsheet(String sheetName) {
		invoiceWorkbook = new HSSFWorkbook();
		HSSFSheet sheet = getSheet(sheetName);
		createHeadingRow(sheet);
		int totalItemsSold = 0;
		double grossAmount = 0.0;
		for(SoldItemDetails invoice: SoldItemsCollection.get().getSoldItemsDetailsList()) {
			totalItemsSold += invoice.orderDetails.getTotalQuantity();
			grossAmount += invoice.paymentDetails.getTotalAmount();
			createValuesRow(sheet, invoice);
		}
		createSummaryRow(sheet, totalItemsSold, grossAmount);
	}
	
	private void openSaveAsDialogBox() throws NullPointerException {
		MainHandler.CURRENT_FILE_NAME = "";
		FileDialog dialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
	    dialog.setVisible(true);
	    String filePath = dialog.getDirectory();
        String fileName = (dialog.getFile().contains(".xls")) ? dialog.getFile() : dialog.getFile() + ".xls";
        MainHandler.CURRENT_FILE_NAME = (filePath + fileName);
	}
	
	private void createWorkbook() throws IOException {		
		File file = new File(MainHandler.CURRENT_FILE_NAME);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
	    invoiceWorkbook.write(out);
	    out.close();	    
	    invoiceWorkbook.close();
	    MainHandler.CURRENT_FILE_NAME = "";
	    MainHandler.CURRENT_FILE_NAME = file.getName();
	}
	
	private HSSFSheet getSheet(String name) {
		HSSFSheet sheet = invoiceWorkbook.getSheet(name);
		if(invoiceWorkbook.getSheet(name) == null) {
			sheet = invoiceWorkbook.createSheet(name);
		}		
		return sheet;
	}
	
	private HSSFFont getHeardingRowFont() {
		HSSFFont font = invoiceWorkbook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);
		font.setColor(HSSFFont.COLOR_RED);
		return font;
	}
	
	private HSSFCellStyle getHeadingRowStyle() {
		HSSFCellStyle style = invoiceWorkbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(getHeardingRowFont());
		return style;
	}
	
	private void createHeadingRow(HSSFSheet spreadsheet) {
		int rowNum = 1;
		int cellNum = 0;
		HSSFRow row = spreadsheet.createRow(rowNum);
		HSSFCell cell;						
		for(String key : ConstantFields.COLUMN_FIELDS) {
			spreadsheet.autoSizeColumn(cellNum);
			spreadsheet.setVerticallyCenter(true);			
			cell = row.createCell(cellNum);			
			cell.setCellStyle(getHeadingRowStyle());
			cell.setCellValue(key);
			cellNum += 1;
		}				
	}
	
	private HSSFFont getValuesRowFont() {
		HSSFFont font = invoiceWorkbook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short) 13);
		font.setBold(false);
		font.setColor(HSSFFont.COLOR_NORMAL);
		return font;
	}
	
	private HSSFCellStyle getValuesRowStyle() {
		HSSFCellStyle style = invoiceWorkbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setFont(getValuesRowFont());
		return style;
	}
	
	private void createValuesRow(HSSFSheet spreadsheet, SoldItemDetails invoice) {
		int rowNum = spreadsheet.getLastRowNum() + 1;
		int cellNum = 0;
		HSSFRow row = spreadsheet.createRow(rowNum);
		
		HSSFCell cell = row.createCell(cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.orderDetails.getOrderId());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);		
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.orderDetails.getOrderDate().toString());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.invoiceDetails.getInvoiceNumber());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.invoiceDetails.getInvoiceDate().toString());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.orderDetails.getTotalQuantity());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.paymentDetails.getTotalAmount());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.paymentDetails.getPaymentMode());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.paymentDetails.getPaymentStatus());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.courierDetails.getCourierName());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.courierDetails.getCourierTrackingNumber());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.courierDetails.getCourierStatus());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.courierDetails.getCourierReturnStatus());	
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue((invoice.courierDetails.getCourierReturnRcvdDate() == null) ? "NA" : 
			invoice.courierDetails.getCourierReturnRcvdDate().toString());
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getValuesRowStyle());
		cell.setCellValue(invoice.courierDetails.getCourierReturnCondition());
	}
	
	private void createSummaryRow(HSSFSheet spreadsheet, 
			int totalItemsSold, double grossAmount) {
		int rowNum = spreadsheet.getLastRowNum() + 2;
		int cellNum = 3;
		
		HSSFRow row = spreadsheet.createRow(rowNum);
		
		HSSFCell cell = row.createCell(cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getHeadingRowStyle());
		cell.setCellValue("Total Sales");
				
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getHeadingRowStyle());
		cell.setCellValue(totalItemsSold);
		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(getHeadingRowStyle());
		cell.setCellValue(grossAmount);
	}
}
