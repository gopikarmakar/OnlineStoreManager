package com.hyend.project.EcommerceManager.handler;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFontFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.hyend.project.EcommerceManager.data.model.SoldItemDetails;
import com.hyend.project.EcommerceManager.data.model.SoldItemsCollection;
import com.hyend.project.EcommerceManager.util.ConstantFields;

public final class SpreadSheetHandler {
	
	private HSSFWorkbook invoiceWorkbook;

	public SpreadSheetHandler(MainHandler dataHandler) {
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
	
	public void generateInvoiceSpreadSheet() throws IOException, NullPointerException {		
		HSSFSheet spreadsheet = invoiceWorkbook.createSheet(
				MainHandler.CURRENT_ECOMM_PLATFORM_NAME + "_sales_details");		
		createHeadingRow(spreadsheet);
		for(SoldItemDetails invoice: SoldItemsCollection.get().getSoldItemsDetailsList()) {
			createValuesRow(spreadsheet, invoice);
		}
		FileDialog dialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
	    dialog.setVisible(true);
	    String dir = dialog.getDirectory();
        String fileName = (dialog.getFile().contains(".xls")) ? dialog.getFile() : dialog.getFile() + ".xls";
        File file = new File(dir + fileName);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
	    invoiceWorkbook.write(out);
	    out.close();	    
	    invoiceWorkbook.close();
	    System.out.println(file.getName() + " File Written Successfully!");
	}
	
	private void createHeadingRow(HSSFSheet spreadsheet) {
		int rowNum = 1;
		int cellNum = 0;
		HSSFRow row = spreadsheet.createRow(rowNum);
		HSSFCell cell;
		HSSFFont font = invoiceWorkbook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);
		font.setColor(HSSFFont.COLOR_RED);
		HSSFCellStyle style = invoiceWorkbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);		
		for(String key : ConstantFields.COLUMN_FIELDS) {
			spreadsheet.autoSizeColumn(cellNum);
			spreadsheet.setVerticallyCenter(true);			
			cell = row.createCell(cellNum);			
			cell.setCellStyle(style);
			cell.setCellValue(key);
			cellNum += 1;
		}				
	}
	
	private void createValuesRow(HSSFSheet spreadsheet, SoldItemDetails invoice) {
		int rowNum = spreadsheet.getLastRowNum() + 1;
		int cellNum = 0;		
		HSSFRow row = spreadsheet.createRow(rowNum);
		HSSFFont font = invoiceWorkbook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short) 13);
		font.setBold(false);
		font.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style = invoiceWorkbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setFont(font);
		HSSFCell cell = row.createCell(cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.orderDetails.getOrderId());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);		
		cell.setCellStyle(style);
		cell.setCellValue(invoice.orderDetails.getOrderDate().toString());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.invoiceDetails.getInvoiceNumber());		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.invoiceDetails.getInvoiceDate().toString());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.orderDetails.getTotalQuantity());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.paymentDetails.getTotalAmount());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.courierDetails.getCourierName());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.courierDetails.getCourierTrackingNumber());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.courierDetails.getCourierStatus());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.courierDetails.getCourierReturnStatus());		
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue((invoice.courierDetails.getCourierReturnRcvdDate() == null) ? "NA" : 
			invoice.courierDetails.getCourierReturnRcvdDate().toString());
		cell = row.createCell(++cellNum);
		spreadsheet.autoSizeColumn(cellNum);
		spreadsheet.setVerticallyCenter(true);
		cell.setCellStyle(style);
		cell.setCellValue(invoice.courierDetails.getCourierReturnCondition());
	}
}
