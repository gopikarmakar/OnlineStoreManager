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
public final class PurchasedItemsCollection {
	
	private final Map<String, PurchasedItemDetails> purchasedItemsData;
	
	private final static PurchasedItemsCollection purchasedItemsCollection = 
			new PurchasedItemsCollection();

	private PurchasedItemsCollection() {
		purchasedItemsData = new HashMap<String, PurchasedItemDetails>();
	}
	
	public static PurchasedItemsCollection get() {
		return purchasedItemsCollection;
	}
	
	public boolean isEmpty() {
		return purchasedItemsCollection.isEmpty();
	}
	
	public void addPurchaseItemDetails(String key, PurchasedItemDetails purchaseItemDetails) {
		purchasedItemsData.put(key, purchaseItemDetails);
	}
	
	public Map<String, PurchasedItemDetails> getCollection() {
		return purchasedItemsData;
	}
	
	public List<PurchasedItemDetails> getPurchasedItemsDetails() {
		return new ArrayList<PurchasedItemDetails>(purchasedItemsData.values());
	}
	
	public PurchasedItemDetails getPurchasedItemDetails(String filePath) {
		return purchasedItemsData.get(filePath);
	}
}
