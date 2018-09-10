package com.hyend.project.EcommerceManager.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * It's a Singleton Class.
 * 
 * @author karmakargopi
 *
 */
public final class SoldItemsCollection {
	
	private final Map<String, SoldItemDetails> soldItems;
	
	private final static SoldItemsCollection soldItemsCollection = 
			new SoldItemsCollection();

	private SoldItemsCollection() {
		soldItems = new HashMap<String, SoldItemDetails>();
	}
	
	public static SoldItemsCollection get() {
		return soldItemsCollection;
	}
	
	public boolean isEmpty() {
		return soldItemsCollection.isEmpty();
	}
	
	public void clear() {
		if(!isEmpty())
			soldItems.clear();
	}
	
	public void addSoldItemDetails(String key, SoldItemDetails soldItemDetails) {
		soldItems.put(key, soldItemDetails);
	}
	
	public void addSoldItemDetailsList(List<SoldItemDetails> invoices) {
		for(SoldItemDetails invoice : invoices) {
			addSoldItemDetails(invoice.getId(), invoice);
		}
	}
	
	public Map<String, SoldItemDetails> getCollection() {
		return soldItems;
	}
	
	public List<SoldItemDetails> getSoldItemsDetailsList() {
		return new ArrayList<SoldItemDetails>(soldItems.values());
	}
	
	public SoldItemDetails getSoldItemDetails(String key) {
		return soldItems.get(key);
	}
}
