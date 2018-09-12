package com.hyend.project.EcommerceManager.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import java.text.ParseException;
import org.bson.conversions.Bson;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoServerException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.MongoCollection;

import com.hyend.project.EcommerceManager.data.model.SoldItemDetails;
import com.hyend.project.EcommerceManager.util.ConstantFields;
import com.hyend.project.EcommerceManager.data.model.SoldItemsCollection;

public final class DatabaseHandler {
	
	private final String uri = "mongodb://localhost:27017";
		
	private MongoDatabase mongoDB = null;
	private MongoClient mongoClient = null;
	private MainHandler dataHandler = null;
	private MongoClientURI mongoClientUri = null;
	private MongoCollection<Document> dbCollection = null;

	private final SoldItemsCollection purchasedItemsCollection;
	
	public DatabaseHandler(MainHandler dataHandler) {
		this.dataHandler = dataHandler;
		purchasedItemsCollection = SoldItemsCollection.get();		
	}

	/**
	 * It connects to local mongo server
	 * Also creates a database and a 
	 * collection as well
	 * If the it isn't available.
	 * @return
	 */
	public void connectToDB(String dbName) throws RuntimeException {
		mongoClientUri = new MongoClientURI(uri);
	    mongoClient = new MongoClient(mongoClientUri);
	    mongoDB = mongoClient.getDatabase(dbName);
	    //System.out.println(" ### DB Connection : " + mongoClient.getConnectPoint());
	}
	
	public void fetchCollection(String tableName) throws IllegalArgumentException {
		dbCollection = mongoDB.getCollection(tableName);
		System.out.println(" ### Collection Name : " + dbCollection.getNamespace());
	}
	
	/**
	 * If a value for the current key is present.
	 * Which means we are connected to DB.
	 * @return
	 * @throws NullPointerException
	 */
	public boolean isConnectedToDB() throws NullPointerException, MongoException {
		Document serverStatus = mongoDB.runCommand(new Document("serverStatus", 1));
		Map<?, ?> connections = (Map<?, ?>) serverStatus.get("connections");
		return connections.containsKey("current");		
	}
	
	/**
	 * Adding multiple rows to mongoDB
	 * If dbCollection will be null if table not found.
	 * In that case it'll return false.
	 * @param tableName
	 * @return
	 */
	public void insertAllToCollection() throws RuntimeException {		
		dbCollection.insertMany(createDocuments());
	}
	
	public List<SoldItemDetails> getAllInvoices() throws NullPointerException {
		List<SoldItemDetails> records = new ArrayList<>();
		MongoCursor<Document> cursor = dbCollection.find().iterator();
		try {
		    while (cursor.hasNext()) {		    
		        records.add(SoldItemDetails.fromDocument(cursor.next()));
		    }
		} finally {
		    cursor.close();
		}
		return records;
	}
	
	public List<SoldItemDetails> getInvoicesBetweenOrderDate(
			Date startDate, Date endDate) throws MongoServerException {
		final List<SoldItemDetails> records = new ArrayList<>();
		Bson filter = Filters.and(Filters.gte(
				ConstantFields.ORDER_DETAILS + "." + 
				ConstantFields.ORDER_DATE_FIELD, startDate), 
				Filters.lte(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_DATE_FIELD, endDate));
		Block<Document> dateBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {			    
				records.add(SoldItemDetails.fromDocument(document));
			}
		};
		dbCollection.find(filter).forEach(dateBlock);
		if(records.isEmpty()) {
			throw new RuntimeException(ConstantFields.NO_RECORDS_FOUND_ERROR);
		}
		return records;
	}
	
	public List<SoldItemDetails> getPaymentReceivedInvoices() throws MongoServerException {
		final List<SoldItemDetails> records = new ArrayList<>();
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.PAYMENT_DETAILS + "." + 
				ConstantFields.PAYMENT_STATUS_FIELD,
				ConstantFields.PAYMENT_STATUS_RECEIVED));
		Block<Document> dateBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {			    
				records.add(SoldItemDetails.fromDocument(document));
			}
		};
		dbCollection.find(filter).forEach(dateBlock);
		if(records.isEmpty()) {
			throw new RuntimeException(ConstantFields.NO_RECORDS_FOUND_ERROR);
		}
		return records;
	}
	
	public List<SoldItemDetails> getCourierDeliveredInvoices() throws MongoServerException {
		final List<SoldItemDetails> records = new ArrayList<>();
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_STATUS_FIELD,
				ConstantFields.COURIER_STATUS_DELIVERED));
		Block<Document> dateBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {			    
				records.add(SoldItemDetails.fromDocument(document));
			}
		};
		dbCollection.find(filter).forEach(dateBlock);
		if(records.isEmpty()) {
			throw new RuntimeException(ConstantFields.NO_RECORDS_FOUND_ERROR);
		}
		return records;
	}
	
	public SoldItemDetails getInvoiceForOrderId(String orderId) throws NullPointerException {	
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.ORDER_DETAILS + "." + 
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document document = dbCollection.find(filter).first();
		return SoldItemDetails.fromDocument(document);
	}
	
	public SoldItemDetails getInvoiceForInvoiceId(String invoiceId) throws NullPointerException {	
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.INVOICE_DETAILS + "." +
				ConstantFields.INVOICE_NUMBER_FIELD, invoiceId));
		Document document = dbCollection.find(filter).first();
		return SoldItemDetails.fromDocument(document);
	}
	
	public boolean updatePaymentStatusAsReceived(String orderId) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.PAYMENT_DETAILS + "." + 
				ConstantFields.PAYMENT_STATUS_FIELD, 
				ConstantFields.PAYMENT_STATUS_RECEIVED));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean updateCourierStatusAsDelivered(String orderId) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_STATUS_FIELD, 
				ConstantFields.COURIER_STATUS_DELIVERED));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean updateReturnStatusAsReturned(String orderId) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_RETURN_STATUS_FIELD, 
				ConstantFields.COURIER_RETURN_STATUS_RETURNED));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean updatePaymentMode(String orderId, String mode) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.PAYMENT_DETAILS + "." + 
				ConstantFields.PAYMENT_MODE_FIELD, mode));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean updateReturnCondition(String orderId, String condition) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_RETURN_CONDITION_FIELD, condition));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean updateReturnRcvdDate(String orderId, Date rcvdDate) throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_RETURN_RCVD_DATE_FIELD, rcvdDate));
		return validateUpdate(dbCollection.updateOne(filter, doc));
	}
	
	public boolean deleteAllPaymentStatusAsReceived() throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.PAYMENT_DETAILS + "." +
				ConstantFields.PAYMENT_STATUS_FIELD, 
				ConstantFields.PAYMENT_STATUS_RECEIVED));		
		return validateDelete(dbCollection.deleteMany(filter));
	}
	
	public boolean deleteAllCourierStatusAsDelivered() throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.COURIER_DETAILS + "." +
				ConstantFields.COURIER_STATUS_FIELD, 
				ConstantFields.COURIER_STATUS_DELIVERED));		
		return validateDelete(dbCollection.deleteMany(filter));
	}
	
	public boolean deleteAllCourierStatusAsReturned() throws MongoServerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.COURIER_DETAILS + "." +
				ConstantFields.COURIER_RETURN_STATUS_FIELD, 
				ConstantFields.COURIER_RETURN_STATUS_RETURNED));		
		return validateDelete(dbCollection.deleteMany(filter));
	}
	
	private List<Document> createDocuments() {
		List<Document> documents = new ArrayList<>();
		for(SoldItemDetails details : purchasedItemsCollection.getSoldItemsDetailsList()) {			
			documents.add(details.toDocument());
		}
		return documents;
	}
	
	private boolean validateUpdate(UpdateResult result) {
		boolean isUpdated = true;
		if(result.getMatchedCount() == 0) {
			isUpdated = false;
			throw new RuntimeException(ConstantFields.NO_RECORDS_FOUND_ERROR);
		}
		else if(result.getModifiedCount() == 0) {
			isUpdated = false;
			throw new RuntimeException(ConstantFields.ALREADY_UPDATED_ERROR);
		}
		return isUpdated;
	}
	
	private boolean validateDelete(DeleteResult result) {
		boolean isDeleted = true;
		if(result.getDeletedCount() == 0) {
			isDeleted = false;
			throw new RuntimeException(ConstantFields.NO_RECORDS_FOUND_ERROR);
		}
		return isDeleted;
	}
}