package com.hyend.project.EcommerceManager.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	    System.out.println(" ### DB Connection : " + mongoClient.getConnectPoint());
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
	public boolean isConnectedToDB() throws NullPointerException {
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
		//System.out.println(cursor.next());
		try {
		    while (cursor.hasNext()) {
		    	//System.out.println(cursor.next());
		        records.add(SoldItemDetails.fromDocument(cursor.next()));
		    }
		} finally {
		    cursor.close();
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
	
	public void updateCourierStatusForOrderId(String orderId) throws NullPointerException {
		Bson filter = Filters.and(Filters.eq(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_ID_FIELD, orderId));
		Document doc = new Document("$set", new Document(
				ConstantFields.COURIER_DETAILS + "." + 
				ConstantFields.COURIER_STATUS_FIELD, 
				ConstantFields.COURIER_STATUS_DELIVERED));
		dbCollection.updateOne(filter, doc);
	}
	
	public List<SoldItemDetails> getInvoicesBetweenOrderDate(
			Date startDate, Date endDate) throws NullPointerException {
		final List<SoldItemDetails> records = new ArrayList<>();
		Bson filter = Filters.and(Filters.gte(
				ConstantFields.ORDER_DETAILS + "." + 
				ConstantFields.ORDER_DATE_FIELD, startDate), 
				Filters.lte(ConstantFields.ORDER_DETAILS + "." +
				ConstantFields.ORDER_DATE_FIELD, endDate));
		Block<Document> dateBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {
			    //System.out.println(document.toJson());
				records.add(SoldItemDetails.fromDocument(document));
			}
		};
		dbCollection.find(filter).forEach(dateBlock);		
		return records;
	}
	
	private List<Document> createDocuments() {
		List<Document> documents = new ArrayList<>();
		for(SoldItemDetails details : purchasedItemsCollection.getSoldItemsDetailsList()) {
			//System.out.println(details.toString());
			documents.add(details.toDocument());
		}
		return documents;
	}
}
