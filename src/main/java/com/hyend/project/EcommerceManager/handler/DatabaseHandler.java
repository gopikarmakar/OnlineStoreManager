package com.hyend.project.EcommerceManager.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import java.text.ParseException;
import org.bson.conversions.Bson;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCollection;

import com.hyend.project.EcommerceManager.data.model.PurchasedItemDetails;
import com.hyend.project.EcommerceManager.util.ConstantFields;
import com.hyend.project.EcommerceManager.data.model.PurchasedItemsCollection;

public final class DatabaseHandler {
	
	private final String uri = "mongodb://localhost:27017";
		
	private MongoDatabase mongoDB = null;
	private MongoClient mongoClient = null;
	private MainHandler dataHandler = null;
	private MongoClientURI mongoClientUri = null;
	private MongoCollection<Document> dbCollection = null;

	private final PurchasedItemsCollection purchasedItemsCollection;
	
	public DatabaseHandler(MainHandler dataHandler) {
		this.dataHandler = dataHandler;
		purchasedItemsCollection = PurchasedItemsCollection.get();		
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
	    System.out.println(" ### DB Connection : " + mongoClient.getConnectPoint());
	}
	
	public void fetchCollection(String tableName) throws IllegalArgumentException {
		dbCollection = mongoDB.getCollection(tableName);
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
	
	public List<PurchasedItemDetails> getAllInvoices() throws NullPointerException {
		List<PurchasedItemDetails> records = new ArrayList<>();
		MongoCursor<Document> cursor = dbCollection.find().iterator();		
		try {
		    while (cursor.hasNext()) {
		    	//System.out.println(cursor.next());
		        records.add(PurchasedItemDetails.fromDocument(cursor.next()));
		    }
		} finally {
		    cursor.close();
		}
		return records;
	}
	
	public PurchasedItemDetails getInvoiceForOrderId(String orderId) throws NullPointerException {	
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.ORDER_DETAILS + "." + 
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document document = dbCollection.find(filter).first();
		return PurchasedItemDetails.fromDocument(document);
	}
	
	public PurchasedItemDetails getInvoiceForInvoiceId(String invoiceId) throws NullPointerException {	
		Bson filter = Filters.and(Filters.eq(
				ConstantFields.INVOICE_DETAILS + "." +
				ConstantFields.INVOICE_NUMBER_FIELD, invoiceId));
		Document document = dbCollection.find(filter).first();
		return PurchasedItemDetails.fromDocument(document);
	}
	
	public void updateCourierStatusForOrderId(String orderId) throws NullPointerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_STATUS_FIELD, 
				ConstantFields.COURIER_STATUS_DELIVERED));
		dbCollection.updateOne(filter, doc);
	}
	
	public List<PurchasedItemDetails> getInvoicesBetweenOrderDate(
			String startDate, String endDate) throws ParseException, NullPointerException {
		final List<PurchasedItemDetails> records = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date startDt = dateFormat.parse(startDate);  
		Date endDt = dateFormat.parse(endDate);
		System.out.println(startDt);
		System.out.println(endDt);
		Bson filter = Filters.and(Filters.gte(
				ConstantFields.ORDER_DETAILS + "." + 
				ConstantFields.ORDER_DATE_FIELD, startDt), 
				Filters.lte(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_DATE_FIELD, endDt));
		Block<Document> dateBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {
			    //System.out.println(document.toJson());
				records.add(PurchasedItemDetails.fromDocument(document));
			}
		};
		dbCollection.find(filter).forEach(dateBlock);		
		return records;
	}
	
	private List<Document> createDocuments() {
		List<Document> documents = new ArrayList<>();
		for(PurchasedItemDetails details : purchasedItemsCollection.getPurchasedItemsDetails()) {
			//System.out.println(details.toString());
			documents.add(details.toDocument());
		}
		return documents;
	}
}
