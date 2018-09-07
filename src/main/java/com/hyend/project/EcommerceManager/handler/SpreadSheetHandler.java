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
	
	private HSSFWorkbook invoiceWorkbook;

	public SpreadSheetHandler(MainHandler dataHandler) {
		invoiceWorkbook = new HSSFWorkbook();
	}
	
	public void generateInvoiceSpreadSheet(String fromMonth, String tillMonth) 
			throws IOException, NullPointerException {		
		
		HSSFSheet spreadsheet = invoiceWorkbook.createSheet(
				ConstantFields.CURRENT_ECOMM_PLATFORM_NAME + "_sales_" + fromMonth + "_" + tillMonth);		
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
}
