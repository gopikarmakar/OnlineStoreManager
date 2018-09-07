package com.hyend.project.EcommerceManager.data.model;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import org.bson.Document;

import com.hyend.project.EcommerceManager.handler.MainHandler;
import com.hyend.project.EcommerceManager.util.ConstantFields;

public class SoldItemDetails {
	
	private String id = "";
	private String eCommercePlatformName = "NA";		
	private final String sellerPostalCode = "208006";	
	private final String sellerPanNumber = "BYMPK0932L";
	private final String sellerGSTNumber = "09BYMPK0932L1Z1";
	private final String sellerName = "karmakar enterprises";
	private final String sellerPhoneNumber = "+91-7827605845";
	private final String sellerAddress = "House no. 9, Vidhyarthi market, " +
			"Govind nagar, near lakme salon, Kanpur";

	public TaxDetails taxDetails = null;	
	public OrderDetails orderDetails = null;
	public BuyerDetails buyerDetails = null;
	public PaymentDetails paymentDetails = null;	
	public ProductDetails productDetails = null;
	public InvoiceDetails invoiceDetails = null;	
	public CourierDetails courierDetails = null;
	
	public SoldItemDetails() {
		taxDetails = new TaxDetails();
		orderDetails = new OrderDetails();
		buyerDetails = new BuyerDetails();
		paymentDetails = new PaymentDetails();
		productDetails = new ProductDetails();
		invoiceDetails = new InvoiceDetails();		
		courierDetails = new CourierDetails();	
	}
	
	@Override
	public String toString() {
		
		String invoiceDetails = "_id : "			+ this.id + "\n" +
								"Seller Name : "	+ this.sellerName + "\n" + 
								"Seller Address : "	+ this.sellerAddress + "-" + this.sellerPostalCode + "\n" +
								"Seller GSTIN : "	+ this.sellerGSTNumber + "\n" +
								"Seller PAN No. : "	+ this.sellerPanNumber + "\n" +						
								"CPD : " 			+ this.productDetails.getCPD() + "\n"+
								"Quantity : " 		+ this.orderDetails.getTotalQuantity() + "\n" +
								"Order Id : " 		+ this.orderDetails.getOrderId() + "\n" +
								"OrderDate : "		+ this.orderDetails.getOrderDate().toString() + "\n" +
								"Tracking Id : " 	+ this.courierDetails.getCourierTrackingNumber() + "\n" +
								"Invoice No : "		+ this.invoiceDetails.getInvoiceNumber() + "\n" +
								"Invoice Date : "	+ this.invoiceDetails.getInvoiceDate().toString() + "\n" +														
								"Total Price : "	+ this.paymentDetails.getTotalAmount() + "\n" +
								"Courier Name : "	+ this.courierDetails.getCourierName() + "\n" +
								"Courier Status : "	+ this.courierDetails.getCourierStatus() + "\n" +
								"Courier Return Status : "	+ this.courierDetails.getCourierReturnStatus() + "\n";
				                
		
		return invoiceDetails;
	}
	
	public void mapDetails(final String tag, final String value) 
			throws ParseException, NumberFormatException {
		
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		switch(tag) {		
			case "CPD: ":
				productDetails.setCPD(value);
				break;
			case "TOTAL QTY: ":
				String[] qnty = value.split(" ");
				//System.out.println(qnty[0]);
				orderDetails.setTotalQuantity(Integer.parseInt(qnty[0]));
				break;
			case "Order ID: ":
				orderDetails.setOrderId(value);
				break;
			case "Order Date: ":
				String[] orderDateNTime = parseDateNTime(value);
				//Date orderDate = dateFormat.parse(orderDateNTime[0]);				
				orderDetails.setOrderDate(new MyDate().getFormattedDate(orderDateNTime[0]));
				orderDetails.setOrderTime(orderDateNTime[1]);
				break;				
			case "Tracking ID: ":
			case "Courier AWB No: ": 
				courierDetails.setCourierTrackingNumber(value);
				break;	
			case "Invoice No: ":
				invoiceDetails.setInvoiceNumber(value);
				break;
			case "Invoice Date: ":
				String[] invoiceDateNTime = parseDateNTime(value);
				//System.out.println("Invoice Date = " + invoiceDateNTime[0]);
				//Date invoiceDate = dateFormat.parse(invoiceDateNTime[0]);
				invoiceDetails.setInvoiceDate(new MyDate().getFormattedDate(invoiceDateNTime[0]));
				invoiceDetails.setInvoiceTime(invoiceDateNTime[1]);
				break;
			case "TOTAL PRICE: ":
				double amt;				
				if(value.contains("TOTAL PRICE: ")) {
					String[] price = value.split(" ");
					amt = Double.parseDouble(price[2]);	
				}
				else
					amt = Double.parseDouble(value);
					
				//System.out.println(amt);
				paymentDetails.setTotalAmount(amt);
				break;
			case "Courier Name: ":
				courierDetails.setCourierName(value);
				break;
		}
	}
	
	/**
	 * Parses the date time information 
	 * as per the ECommerce platform
	 * 
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	private String[] parseDateNTime(String dateTime) {
		String[] dateNTime = {""};
		switch (ConstantFields.CURRENT_ECOMM_PLATFORM_NAME) {
			case "paytm":			
				break;
			case "amazon":
				break;
			case "flipkart":
				dateNTime = dateTime.split(", ");				
				break;
			case "snapdeal":
				break;
			case "shopclues":
				break;
		}
		return dateNTime;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getSellerName() {
		return this.sellerName;
	}		
	
	public String getSellerAddress() {
		return this.sellerAddress;
	}
	
	public String getSellerGSTNumber() {
		return this.sellerGSTNumber;
	}
	
	public String getSellerPanNumber() {
		return this.sellerPanNumber;
	}
	
	public String getSellerPostalCode() {
		return this.sellerPostalCode;
	}
	
	public String getSellerPhoneNumber() {
		return this.sellerPhoneNumber;
	}	
	
	public void setECommercePlatformName(String eCommercePlatformName) {
		this.eCommercePlatformName = eCommercePlatformName;
	}
	
	public String getECommercePlatformName() {
		return this.eCommercePlatformName;
	}
	
	public Document toDocument() {
        Document document = new Document();
        document.append("_id", getId())
		.append(ConstantFields.ECOMMERCE_PLATFORM_NAME_FIELD, getECommercePlatformName())
		.append(ConstantFields.BUYER_DETAILS, new Document()
    		.append(ConstantFields.BUYER_NAME_FIELD, buyerDetails.getBuyerName())
            .append(ConstantFields.BUYER_POSTAL_CODE_FIELD, buyerDetails.getBuyerPostalCode())
    		.append(ConstantFields.BUYER_PHONE_NUMBER_FIELD, buyerDetails.getBuyerPhoneNumber())
    		.append(ConstantFields.BUYER_EMAIL_ADDRESS_FIELD, buyerDetails.getBuyerEmailAddress())
    		.append(ConstantFields.BUYER_BILLING_ADDRESS_FIELD, buyerDetails.getBuyerBillingAddress())
    		.append(ConstantFields.BUYER_DELIVERY_ADDRESS_FIELD, buyerDetails.getBuyerDeliveryAddress())
    		.append(ConstantFields.BUYER_SHIPPING_ADDRESS_FIELD, buyerDetails.getBuyerShippingAddress()))
		.append(ConstantFields.PRODUCT_DETAILS, new Document()
    		.append(ConstantFields.CPD_FIELD, productDetails.getCPD())
    		.append(ConstantFields.PRODUCT_ID_FIELD, productDetails.getProductId())
    		.append(ConstantFields.PRODUCT_SKU_FIELD, productDetails.getProductSKU())
    		.append(ConstantFields.PRODUCT_NAME_FIELD, productDetails.getProductName())
    		.append(ConstantFields.PRODUCT_WEIGHT_FIELD, productDetails.getProductWeight())
    		.append(ConstantFields.PRODUCT_DESC_FIELD, productDetails.getProductDescription()))   		
		.append(ConstantFields.ORDER_DETAILS, new Document()
			.append(ConstantFields.ORDER_ID_FIELD, orderDetails.getOrderId())				
			.append(ConstantFields.ORDER_DATE_FIELD, orderDetails.getOrderDate())
    		.append(ConstantFields.ORDER_TIME_FIELD, orderDetails.getOrderTime())
			.append(ConstantFields.TOTAL_QUANTITY_FIELD, orderDetails.getTotalQuantity()))
        .append(ConstantFields.INVOICE_DETAILS, new Document()                		
    		.append(ConstantFields.INVOICE_DATE_FIELD, invoiceDetails.getInvoiceDate())
    		.append(ConstantFields.INVOICE_TIME_FIELD, invoiceDetails.getInvoiceTime())
    		.append(ConstantFields.INVOICE_NUMBER_FIELD, invoiceDetails.getInvoiceNumber()))
		.append(ConstantFields.COURIER_DETAILS, new Document()
    		.append(ConstantFields.COURIER_NAME_FIELD, courierDetails.getCourierName())
    		.append(ConstantFields.COURIER_STATUS_FIELD, courierDetails.getCourierStatus())
    		.append(ConstantFields.COURIER_RETURN_STATUS_FIELD, courierDetails.getCourierReturnStatus())     
    		.append(ConstantFields.COURIER_TRACKING_ID_FIELD, courierDetails.getCourierTrackingNumber())
    		.append(ConstantFields.COURIER_RETURN_RCVD_DATE_FIELD, courierDetails.getCourierReturnRcvdDate())                		
    		.append(ConstantFields.COURIER_RETURN_CONDITION_FIELD, courierDetails.getCourierReturnCondition()))
		.append(ConstantFields.PAYMENT_DETAILS, new Document()
    		.append(ConstantFields.TOTAL_AMOUNT_FIELD, paymentDetails.getTotalAmount())
    		.append(ConstantFields.PAYMENT_METHOD_FIELD, paymentDetails.getPaymentMethod())
    		.append(ConstantFields.SHIPPING_CHARGE_FIELD, paymentDetails.getShippingCharge()))
        .append(ConstantFields.TAX_DETAILS, new Document()
			.append(ConstantFields.GST_RATE_FIELD, taxDetails.getGSTRate())
			.append(ConstantFields.GST_AMOUNT_FIELD, taxDetails.getGSTAmount())
			.append(ConstantFields.TAXABLE_AMOUNT_FIELD, taxDetails.getTaxableAmount()));                        		
        return document;
    }
	
	public static SoldItemDetails fromDocument(Document document) {
		
		//System.out.println(document.toJson());
		SoldItemDetails soldItemDetails = new SoldItemDetails();
		
		soldItemDetails.setId(document.getString("_id"));		
		soldItemDetails.setECommercePlatformName(document.getString(ConstantFields.ECOMMERCE_PLATFORM_NAME_FIELD));
		
		Document buyerDoc = (Document) document.get(ConstantFields.BUYER_DETAILS);
		soldItemDetails.buyerDetails.setBuyerName(buyerDoc.getString(ConstantFields.BUYER_NAME_FIELD));		
		soldItemDetails.buyerDetails.setBuyerPostalCode(buyerDoc.getString(ConstantFields.BUYER_POSTAL_CODE_FIELD));
		soldItemDetails.buyerDetails.setBuyerPhoneNumber(buyerDoc.getString(ConstantFields.BUYER_PHONE_NUMBER_FIELD));
		soldItemDetails.buyerDetails.setBuyerEmailAddress(buyerDoc.getString(ConstantFields.BUYER_EMAIL_ADDRESS_FIELD));		
		soldItemDetails.buyerDetails.setBuyerBillingAddress(buyerDoc.getString(ConstantFields.BUYER_BILLING_ADDRESS_FIELD));
		soldItemDetails.buyerDetails.setBuyerDeliveryAddress(buyerDoc.getString(ConstantFields.BUYER_DELIVERY_ADDRESS_FIELD));
		soldItemDetails.buyerDetails.setBuyerShippingAddress(buyerDoc.getString(ConstantFields.BUYER_SHIPPING_ADDRESS_FIELD));
		
		Document productDoc = (Document) document.get(ConstantFields.PRODUCT_DETAILS);
		soldItemDetails.productDetails.setCPD(productDoc.getString(ConstantFields.CPD_FIELD));
		soldItemDetails.productDetails.setProductId(productDoc.getString(ConstantFields.PRODUCT_ID_FIELD));
		soldItemDetails.productDetails.setProductSKU(productDoc.getString(ConstantFields.PRODUCT_SKU_FIELD));
		soldItemDetails.productDetails.setProductName(productDoc.getString(ConstantFields.PRODUCT_NAME_FIELD));		
		soldItemDetails.productDetails.setProductWeight(productDoc.getDouble(ConstantFields.PRODUCT_WEIGHT_FIELD));		
		soldItemDetails.productDetails.setProductDescription(productDoc.getString(ConstantFields.PRODUCT_DESC_FIELD));
		
		Document orderDoc = (Document) document.get(ConstantFields.ORDER_DETAILS);
		soldItemDetails.orderDetails.setOrderId(orderDoc.getString(ConstantFields.ORDER_ID_FIELD));
		soldItemDetails.orderDetails.setOrderDate(orderDoc.getDate(ConstantFields.ORDER_DATE_FIELD));		
		soldItemDetails.orderDetails.setOrderTime(orderDoc.getString(ConstantFields.ORDER_TIME_FIELD));
		soldItemDetails.orderDetails.setTotalQuantity(orderDoc.getInteger(ConstantFields.TOTAL_QUANTITY_FIELD));
		
		Document invoiceDoc = (Document) document.get(ConstantFields.INVOICE_DETAILS);		
		soldItemDetails.invoiceDetails.setInvoiceDate(invoiceDoc.getDate(ConstantFields.INVOICE_DATE_FIELD));		
		soldItemDetails.invoiceDetails.setInvoiceTime(invoiceDoc.getString(ConstantFields.INVOICE_TIME_FIELD));
		soldItemDetails.invoiceDetails.setInvoiceNumber(invoiceDoc.getString(ConstantFields.INVOICE_NUMBER_FIELD));
		
		Document logisticDoc = (Document) document.get(ConstantFields.COURIER_DETAILS);
		soldItemDetails.courierDetails.setCourierName(logisticDoc.getString(ConstantFields.COURIER_NAME_FIELD));
		soldItemDetails.courierDetails.setCourierStatus(logisticDoc.getString(ConstantFields.COURIER_STATUS_FIELD));
		soldItemDetails.courierDetails.setCourierReturnStatus(logisticDoc.getString(ConstantFields.COURIER_RETURN_STATUS_FIELD));
		soldItemDetails.courierDetails.setCourierTrackingNumber(logisticDoc.getString(ConstantFields.COURIER_TRACKING_ID_FIELD));		
		soldItemDetails.courierDetails.setCourierReturnRcvdDate(logisticDoc.getDate(ConstantFields.COURIER_RETURN_RCVD_DATE_FIELD));		
		soldItemDetails.courierDetails.setCourierReturnCondition(logisticDoc.getString(ConstantFields.COURIER_RETURN_CONDITION_FIELD));
		
		Document amountDoc = (Document) document.get(ConstantFields.PAYMENT_DETAILS);
		soldItemDetails.paymentDetails.setTotalAmount(amountDoc.getDouble(ConstantFields.TOTAL_AMOUNT_FIELD));
		soldItemDetails.paymentDetails.setPaymentMethod(amountDoc.getString(ConstantFields.PAYMENT_METHOD_FIELD));		
		soldItemDetails.paymentDetails.setShippingCharge(amountDoc.getDouble(ConstantFields.SHIPPING_CHARGE_FIELD));
		
		Document taxDoc = (Document) document.get(ConstantFields.TAX_DETAILS);
		soldItemDetails.taxDetails.setGSTRate(taxDoc.getInteger(ConstantFields.GST_RATE_FIELD));
		soldItemDetails.taxDetails.setGSTAmount(taxDoc.getDouble(ConstantFields.GST_AMOUNT_FIELD));
		soldItemDetails.taxDetails.setTaxableAmount(taxDoc.getDouble(ConstantFields.TAXABLE_AMOUNT_FIELD));
		
		//System.out.println("SoldItemDetails" + soldItemDetails.toString());
		return soldItemDetails;
	}
	
	public class MyDate extends Date {
	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	    public MyDate() {}
	    
	    public Date getFormattedDate(String date) throws ParseException {
	    	return dateFormat.parse(date);
	    }

	    @Override
	    public String toString() {
	        return dateFormat.format(this);
	    }
	}
	
	public class TaxDetails {
		
		private int gstRate = 0;	
		private double gstAmount = 0.0;
		private double taxableAmount = 0.0;

		public TaxDetails() {}
		
		public void setGSTRate(int gstRate) {
			this.gstRate = gstRate;
		}
		
		public int getGSTRate() {
			return this.gstRate;
		}
		
		public void setGSTAmount(double gstAmount) {
			this.gstAmount = gstAmount;
		}
		
		public double getGSTAmount() {
			return this.gstAmount;
		}
		
		public void setTaxableAmount(double taxableAmount) {
			this.taxableAmount = taxableAmount;
		}
		
		public double getTaxableAmount() {
			return this.taxableAmount;
		}
	}
	
	public class OrderDetails {
		
		private Date orderDate;
		private String orderId = "NA";
		private String orderTime = "NA";
		private int totalQuantity = 0;

		public OrderDetails() {}
		
		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}
		
		public String getOrderId() {
			return this.orderId;
		}
		
		public void setOrderDate(Date orderDate) {
			this.orderDate = orderDate;
		}
		
		public Date getOrderDate() {
			return this.orderDate;
		}
		
		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		
		public String getOrderTime() {
			return this.orderTime;
		}
		
		public void setTotalQuantity(int totalQuantity) {
			this.totalQuantity = totalQuantity;
		}
		
		public int getTotalQuantity() {
			return this.totalQuantity;
		}
	}
	
	public class InvoiceDetails {
		
		private Date invoiceDate;
		private String invoiceTime = "NA";
		private String invoiceNumber = "NA";
		
		public InvoiceDetails() {}

		public void setInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
		}
		
		public String getInvoiceNumber() {
			return this.invoiceNumber;
		}
		
		public void setInvoiceDate(Date invoiceDate) {
			this.invoiceDate = invoiceDate;
		}
		
		public Date getInvoiceDate() {
			return this.invoiceDate;
		}
		
		public void setInvoiceTime(String invoiceTime) {
			this.invoiceTime = invoiceTime;
		}	
		
		public String getInvoiceTime() {
			return this.invoiceTime;
		}
	}
	
	public class BuyerDetails {
		
		private String buyerName = "NA";		
		private String buyerPostalCode = "NA";
		private String buyerPhoneNumber = "NA";
		private String buyerEmailAddress = "NA";
		private String buyerBillingAddress = "NA";
		private String buyerShippingAddress = "NA";
		private String buyerDeliveryAddress = "NA";	

		public BuyerDetails() {}
		
		public void setBuyerName(String buyerName) {
			this.buyerName = buyerName;
		}
		
		public String getBuyerName() {
			return this.buyerName;
		}

		public void setBuyerPostalCode(String buyerPostalCode) {
			this.buyerPostalCode = buyerPostalCode;
		}
		
		public String getBuyerPostalCode() {
			return this.buyerPostalCode;
		}
		
		public void setBuyerPhoneNumber(String buyerPhoneNumber) {
			this.buyerPhoneNumber = buyerPhoneNumber;
		}
		
		public String getBuyerPhoneNumber() {
			return this.buyerPhoneNumber;
		}
		
		public void setBuyerEmailAddress(String buyerEmailAddress) {
			this.buyerEmailAddress = buyerEmailAddress;
		}
		
		public String getBuyerEmailAddress() {
			return this.buyerEmailAddress;
		}
		
		public void setBuyerBillingAddress(String buyerBillingAddress) {
			this.buyerBillingAddress = buyerBillingAddress;
		}
		
		public String getBuyerBillingAddress() {
			return this.buyerBillingAddress;
		}
		
		public void setBuyerShippingAddress(String buyerShippingAddress) {
			this.buyerShippingAddress = buyerShippingAddress;
		}
		
		public String getBuyerShippingAddress() {
			return this.buyerShippingAddress;
		}
		
		public void setBuyerDeliveryAddress(String buyerDeliveryAddress) {
			this.buyerDeliveryAddress = buyerDeliveryAddress;
		}
		
		public String getBuyerDeliveryAddress() {
			return this.buyerDeliveryAddress;
		}
	}
	
	public class PaymentDetails {
		
		private Double totalAmount = 0.0;	
		private String paymentMethod = "NA";
		private Double shippingCharge = 0.0;

		public PaymentDetails() {}
		
		public void setTotalAmount(double totalAmount) {
			this.totalAmount = totalAmount;
		}
		
		public double getTotalAmount() {
			return this.totalAmount;
		}
		
		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
		
		public String getPaymentMethod() {
			return this.paymentMethod;
		}
		
		public void setShippingCharge(double shippingCharge) {
			this.shippingCharge = shippingCharge;
		}
		
		public double getShippingCharge() {
			return this.shippingCharge;
		}
	}
	
	public class ProductDetails {
		
		private Double productWeight = 0.0;	
		private String cpd = "NA";
		private String productId = "NA";
		private String productSKU = "NA";				
		private String productName = "NA";
		private String productDescription = "NA";

		public ProductDetails() {}
		
		public void setCPD(String cpd) {
			this.cpd = cpd;
		}
		
		public String getCPD() {
			return this.cpd;
		}
		
		public void setProductId(String productId) {
			this.productId = productId;
		}
		
		public String getProductId() {
			return this.productId;
		}
		
		public void setProductWeight(double productWeight) {
			this.productWeight = productWeight;
		}
		
		public double getProductWeight() {
			return this.productWeight;
		}
		
		public void setProductSKU(String productSKU) {
			this.productSKU = productSKU;
		}
		
		public String getProductSKU() {
			return this.productSKU;
		}
		
		public void setProductName(String productName) {
			this.productName = productName;
		}
		
		public String getProductName() {
			return this.productName;
		}
		
		public void setProductDescription(String productDescription) {
			this.productDescription = productDescription;
		}
		
		public String getProductDescription() {
			return this.productDescription;
		}
	}

	public class CourierDetails {
		
		private Date courierReturnRcvdDate = null;
		private String courierName = "NA";
		private String courierStatus = "NA";
		private String courierReturnStatus = "NA";	
		private String courierTrackingNumber = "NA";	
		private String courierReturnCondition = "NA";

		public CourierDetails() {
			//this.courierReturnRcvdDate = new MyDate();
		}
		
		public void setCourierStatus(String courierStatus) {
			this.courierStatus = courierStatus;
		}
		
		public String getCourierStatus() {
			return this.courierStatus;
		}	
		
		public void setCourierName(String courierName) {
			this.courierName = courierName;
		}
		
		public String getCourierName() {
			return this.courierName;
		}
		
		public void setCourierReturnStatus(String courierReturnStatus) {
			this.courierReturnStatus = courierReturnStatus;
		}
		
		public String getCourierReturnStatus() {
			return this.courierReturnStatus;
		}
		
		public void setCourierReturnCondition(String courierReturnCondition) {
			this.courierReturnCondition = courierReturnCondition;
		}
		
		public String getCourierReturnCondition() {
			return this.courierReturnCondition;
		}
		
		public void setCourierTrackingNumber(String courierTrackingNumber) {
			this.courierTrackingNumber = courierTrackingNumber;
		}
		
		public String getCourierTrackingNumber() {
			return this.courierTrackingNumber;
		}
		
		public void setCourierReturnRcvdDate(Date courierReturnRcvdDate) {
			this.courierReturnRcvdDate = courierReturnRcvdDate;
		}
		
		public Date getCourierReturnRcvdDate() {			
			return this.courierReturnRcvdDate;
		}
	}
}
