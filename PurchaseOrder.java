package com.periscope.rest.data.po;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.wsc.ecom.bo.purchaseorder.PurchaseOrderItem;

/* API Object to control serialization for a PurchaseOrder object
 * 
/**
 * 
 * @author nthompson
 * 
 */
 */
public class PurchaseOrder {
	private com.wsc.ecom.bo.purchaseorder.PurchaseOrder purchaseOrder; 
	private List<LineItem> lineItems;
	
	public PurchaseOrder() {
		super();
	}
	
	@XmlElement
	public String getStatus(){
		return purchaseOrder.getStatus();
	}


	@XmlElement
	public String getPurchasingAgency(){
		return purchaseOrder.getPurchaser();
	}

	@XmlElement
	public String getPurchaseOrderNumber(){
		return purchaseOrder.getPoId();
	}
		
	@XmlElement
	public List<LineItem> getLineItems(){
		return lineItems;
	}

	public void setPurchaseOrder(com.wsc.ecom.bo.purchaseorder.PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
    public void addLineItem(LineItem lineItem){
    	if (lineItems == null){
    		lineItems = new ArrayList<LineItem>();
    	}
    	lineItems.add(lineItem);
    }

}
