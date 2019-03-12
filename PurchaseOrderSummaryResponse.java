
/**
 * REST Service for Purchase Orders
 Summary Response * 
 * @author nthompson
 * 
 */
package com.periscope.rest.data.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.periscope.rest.data.common.Status;

@XmlRootElement
@XmlType(propOrder={"status","purchaseOrder"})
public class PurchaseOrderSummaryResponse {
	private Status status;
	private PurchaseOrder purchaseOrder;

	@XmlElement
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}
	
	@XmlElement
	public Status getStatus() {
		return status;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	
	
}
