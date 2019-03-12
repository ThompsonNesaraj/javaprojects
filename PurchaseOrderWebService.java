package com.periscope.rest.service.po;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.periscope.docrepository.DocumentRepositoryLogin;
import com.periscope.docrepository.login.PeriscopeLoginModule;
import com.periscope.persistance.dao.DaoException;
import com.periscope.persistance.dao.StandardTextDao;
import com.periscope.persistance.dao.impl.StandardTextDaoImpl;
import com.periscope.rest.RestApplicationException;
import com.periscope.rest.annotations.RestAuthentication;
import com.periscope.rest.annotations.RestNoAuthentication;
import com.periscope.rest.annotations.RestRoleAccess;
import com.periscope.rest.data.common.DataTableRest;
import com.periscope.rest.data.common.Status;
import com.periscope.rest.data.po.LineItem;
import com.periscope.rest.data.po.PurchaseOrderSummaryResponse;
import com.periscope.rest.data.po.ReceiptRest;
import com.periscope.rest.data.po.VendorPerformanceRest;
import com.periscope.service.ServiceException;
import com.periscope.service.ServiceFactory;
import com.periscope.service.VendorPerformanceService;
import com.periscope.vendor.performance.bo.VendorPerformance;
import com.sun.jersey.api.view.Viewable;
import com.wsc.ecom.bo.doc.BaseReceiptSearchFactory;
import com.wsc.ecom.bo.doc.customcolumn.BaseDocCustomColumn;
import com.wsc.ecom.bo.doc.customcolumn.CustomColumnFactory;
import com.wsc.ecom.bo.document.DocReceiptSearchFactory;
import com.wsc.ecom.bo.org.Vendor;
import com.wsc.ecom.bo.org.VendorFactory;
import com.wsc.ecom.bo.purchaseorder.PoNotFoundException;
import com.wsc.ecom.bo.purchaseorder.PurchaseOrder;
import com.wsc.ecom.bo.purchaseorder.PurchaseOrderFactory;
import com.wsc.ecom.bo.purchaseorder.PurchaseOrderItem;
import com.wsc.ecom.bo.purchaseorder.receipt.Receipt;
import com.wsc.ecom.bo.purchaseorder.receipt.ReceiptFactory;
import com.wsc.ecom.bo.purchaseorder.receipt.ReceiptItem;
import com.wsc.ecom.bo.security.SessionInfo;
import com.wsc.ecom.bo.status.StatusFactory;
import com.wsc.ecom.bo.struts.actionforms.search.BaseSearchForm;
import com.wsc.ecom.bo.user.Role;
import com.wsc.ecom.bo.util.BoConstants;
import com.wsc.viper.dbi.DBUtil;
import com.wsc.viper.dbi.PagedSearchResult;

/**
 * REST Service for Purchase Orders
 * 
 * @author nthompson
 * 
 */
@Path("/api/purchaseOrder")
public class PurchaseOrderWebService {

	private StandardTextDao standardTextDao = new StandardTextDaoImpl();
	protected static final Log log = LogFactory.getLog(PurchaseOrderWebService.class);
	private final String customTextNotFoundStr = "[warning: standard text was not found for this error condition]";
	public @Context
	UriInfo uriInfo;

	/**
	 * This method is required for documentation, and allows a MVC method of display JSP pages
	 * 
	 * @return Response (i.e. JSP for documentation)
	 */
	@GET
	// The Java method will process HTTP GET requests
	@Produces(MediaType.TEXT_HTML)
	// The Java method will produce content identified by the MIME Media
	@RestNoAuthentication
	public Response displayDocumentation() {

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			return Response.ok(new Viewable("/purchaseOrder/index", parameters)).build();
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new RestApplicationException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}
	
	@Path("/summary/vendorPerformance")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//@RestRoleAccess(role=Role.BUYER, documentType=BoConstants.DOCTYPE_PO, documentIds="poNum,releaseNum") // Security check, to make sure user has access
	@RestAuthentication
	// Possible Parameters: {iDisplayStart=[0], sColumns=[], releaseNum=[0], iColumns=[4], _=[1382992065779], mDataProp_0=[vendorPerformanceNbr], mDataProp_1=[desc], iDisplayLength=[10], mDataProp_2=[deptLoc], sEcho=[1], mDataProp_3=[status], poNum=[ADSPO12-004531]}
	// {iDisplayStart=[2], sColumns=[], releaseNum=[0], iColumns=[4], _=[1383051473500], mDataProp_0=[vendorPerformanceNbr], mDataProp_1=[desc], iDisplayLength=[2], mDataProp_2=[deptLoc], sEcho=[2], mDataProp_3=[status], poNum=[ADSPO12-004531]}
	// Example:  http://phi-mpasko:8080/bso/api/purchaseOrder/summary/vendorPerformance?poNum=ADSPO12-004531&releaseNum=0&iDisplayStart=4&iDisplayLength=2
	public DataTableRest getPurchaseOrderVendorPerformanceDocuments( @DefaultValue("") @QueryParam("poNum") String poNum, 
			@DefaultValue("0")  @QueryParam("releaseNum") long releaseNum, @DefaultValue("0")  @QueryParam("iDisplayStart") int iDisplayStart, 
			@DefaultValue("1")  @QueryParam("iDisplayLength") int iDisplayLength, @DefaultValue("1")  @QueryParam("sEcho") int sEcho, @Context HttpServletRequest request) throws RestApplicationException {
		HttpSession session = request.getSession();
		SessionInfo loggedIn = (SessionInfo)session.getAttribute(SessionInfo.SESSINFO_KEY);
		
		DataTableRest table = new DataTableRest();
		List<VendorPerformanceRest> list = new ArrayList<VendorPerformanceRest>();
		VendorPerformanceService service = ServiceFactory.getVendorPerformanceService();
		try {
			int currentPage=(iDisplayStart/iDisplayLength)+1;
			PagedSearchResult result=service.getVendorPerformanceByPO(poNum, releaseNum, loggedIn.getLoginId(), currentPage, iDisplayLength);
			for (Object vp:result.getPageData()) {
				list.add(new VendorPerformanceRest((VendorPerformance)vp));
			}
			table.setAaData(list);
			table.setsEcho(++sEcho);
			table.setiTotalRecords(result.getPageData().length);
			table.setiTotalDisplayRecords((int)result.getTotalCount());
			return table;
		} catch (ServiceException e) {
			throw new RestApplicationException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Path("/summary/receipts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//@RestRoleAccess(role=Role.BUYER, documentType=BoConstants.DOCTYPE_PO, documentIds="poNum,releaseNum") // Security check, to make sure user has access
	@RestAuthentication
	// Possible Parameters: {iDisplayStart=[0], sColumns=[], releaseNum=[0], iColumns=[4], _=[1382992065779], mDataProp_0=[vendorPerformanceNbr], mDataProp_1=[desc], iDisplayLength=[10], mDataProp_2=[deptLoc], sEcho=[1], mDataProp_3=[status], poNum=[ADSPO12-004531]}
	// {iDisplayStart=[2], sColumns=[], releaseNum=[0], iColumns=[4], _=[1383051473500], mDataProp_0=[vendorPerformanceNbr], mDataProp_1=[desc], iDisplayLength=[2], mDataProp_2=[deptLoc], sEcho=[2], mDataProp_3=[status], poNum=[ADSPO12-004531]}
	// http://phi-mpasko:8080/bso/api/purchaseOrder/summary/vendorPerformance?poNum=ADSPO12-004531&releaseNum=0&iDisplayStart=4&iDisplayLength=2
	public DataTableRest getPurchaseOrderReceiptDocuments( @DefaultValue("") @QueryParam("poNum") String poNum, 
			@DefaultValue("0")  @QueryParam("releaseNum") long releaseNum, @DefaultValue("0")  @QueryParam("iDisplayStart") int iDisplayStart, 
			@DefaultValue("1")  @QueryParam("iDisplayLength") int iDisplayLength, @DefaultValue("1")  @QueryParam("sEcho") int sEcho, @Context HttpServletRequest request) throws RestApplicationException {
		HttpSession session = request.getSession();
		SessionInfo loggedIn = (SessionInfo)session.getAttribute(SessionInfo.SESSINFO_KEY);
		
		DataTableRest table = new DataTableRest();
		List<ReceiptRest> list = new ArrayList<ReceiptRest>();
		try {
			int currentPage=(iDisplayStart/iDisplayLength)+1;
			

			BaseReceiptSearchFactory receiptSearchFactory = DocReceiptSearchFactory.getReceiptSearchFactory(BoConstants.DOCTYPE_PO);
			String sql=receiptSearchFactory.makeQueryToGetPagedDocReceiptList(poNum, releaseNum, 0, new BaseSearchForm());
			
			PagedSearchResult result = DBUtil.doPagedSearch(Receipt.TABLE_NAME, sql, Receipt.class, currentPage, iDisplayLength);
	                
			for (Object rp:result.getPageData()) {
				list.add(new ReceiptRest((Receipt)rp));
			}
			table.setAaData(list);
			table.setsEcho(++sEcho);
			table.setiTotalRecords(result.getPageData().length);
			table.setiTotalDisplayRecords((int)result.getTotalCount());
			return table;
		} catch (Exception e) {
			throw new RestApplicationException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		} 
		
	}

	/**
	 * Method to return a list of PO Items then the Bid vendor's are sent instead.
	 * Example: /bso/api/purchaseOrder/summary?poNum=SOMEORG12-004562&releaseNum=0
	 * @param poNum
	 *            - Purchase order number
	 * @param releaseNum
	 *            - Release number
	 * @param request
	 *            - HttpServletRequest injected from Context (used to get SessionInfo for Security)
	 * @return poItemList
	 * @throws DaoException
	 */
	@Path("/summary")
	@GET
	// The Java method will process HTTP GET requests
	@Produces(MediaType.APPLICATION_XML)
	// The Java method will produce content identified by the MIME Media
	@RestRoleAccess(role = Role.BUYER, documentType = BoConstants.DOCTYPE_PO, documentIds = "poNum,releaseNum")
	// Security check, to make sure user has access
	@RestAuthentication
	public PurchaseOrderSummaryResponse getPurchaseOrderSummary(@QueryParam("poNum") String poNum, @QueryParam("releaseNum") long releaseNum, @Context HttpServletRequest request) throws RestApplicationException {
		PurchaseOrderSummaryResponse res = new PurchaseOrderSummaryResponse();
		Status status = null;
		PurchaseOrder po = null;
		// for some reason the code is designed to throw an exception when a PO
		// is not found
		// so separate try/catch for this condition
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSINFO_KEY);
		if (sessionInfo == null) {
			sessionInfo = (SessionInfo) request.getAttribute(SessionInfo.SESSINFO_KEY);
		}
		if (sessionInfo == null) {
			getStatusObject("1", false, "PO_NOT_FOUND", null, "Login information not found, not logged in");
			res.setStatus(status);
			return res;
		}
		String orgId = sessionInfo.getOrgId();
		try {
			po = PurchaseOrderFactory.getPoById(poNum, releaseNum);
		} catch (PoNotFoundException e) {
			status = getStatusObject("1", false, "PO_NOT_FOUND", orgId, "Purchase Order Not Found " + customTextNotFoundStr);
			res.setStatus(status);
			return res;
		}
		// PO is found
		try {
			status = validatePO(po, orgId);
			// if its an error condition just set status object and return
			if (status.isSuccess() == false) {
				res.setStatus(status);
				return res;
			}
			// valid PO conditions
			// get the line items
			PurchaseOrderItem[] items = PurchaseOrderFactory.getPoItems(poNum, releaseNum);
			com.periscope.rest.data.po.PurchaseOrder purchaseOrder = new com.periscope.rest.data.po.PurchaseOrder();
			purchaseOrder.setPurchaseOrder(po);
			// for each line item, get receipts and aggregate the recieved count
			for (PurchaseOrderItem item : items) {
				ReceiptItem[] receipts = ReceiptFactory.getReceiptItems(poNum, releaseNum, item.getItemNbr());
				double count = 0.0;
				for (ReceiptItem receipt : receipts) {
					count = count + receipt.getQuantity();
				}
				LineItem lineItem = new LineItem();
				lineItem.setLineItem(item);
				lineItem.setQuantityRecieved(count);

				// /these custom columns are for michigan, if not present they are ignored
				String leaseFlag = getCustomColumnValue("leaseFlag", item, orgId);
				if (leaseFlag != null) {
					lineItem.setLeaseFlag(leaseFlag);
				}
				String passThroughFlag = getCustomColumnValue("passThroughFlag", item, orgId);
				if (passThroughFlag != null) {
					lineItem.setPassThroughFlag(passThroughFlag);
				}
				String itemCategory = getCustomColumnValue("itemCategory", item, orgId);
				if (itemCategory != null) {
					lineItem.setItemCategory(itemCategory);
				}
				purchaseOrder.addLineItem(lineItem);
			}
			res.setPurchaseOrder(purchaseOrder);
			res.setStatus(status);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RestApplicationException(new ServiceException(e.getMessage()));
		}
		return res;
	}

	private Status validatePO(PurchaseOrder po, String orgId) throws DaoException {
		if (po == null) {
			return getStatusObject("1", false, "PO_NOT_FOUND", orgId, "Purchase Order Not Found " + customTextNotFoundStr);
		} else if (po.getStatus().equals(StatusFactory.PO_CLOSED)) {
			return getStatusObject("2", false, "PO_CLOSED", orgId, "Purchase Order Closed " + customTextNotFoundStr);
		} else if (po.getStatus().equals(StatusFactory.PO_COMPLETE_RECEIPT)) {
			return getStatusObject("3", false, "PO_REC_BUT_UNPAID", orgId, "Purchase Order Fully Received but Unpaid " + customTextNotFoundStr);
		} else if (po.getStatus().equals(StatusFactory.PO_CANCELLED)) {
			return getStatusObject("4", false, "PO_CANCELLED", orgId, "Purchase Order Cancelled " + customTextNotFoundStr);
		} else if (po.getStatus().equals(StatusFactory.PO_IN_PROGRESS) || po.getStatus().equals(StatusFactory.PO_READY_TO_SEND) || po.getStatus().equals(StatusFactory.PO_RETURNED) || po.getStatus().equals(StatusFactory.PO_READY_FOR_APPROVAL)) {
			return getStatusObject("5", false, "PO_UNSENT", orgId, "Purchase Order Unsent " + customTextNotFoundStr);
		} else if (po.isBlanket() && po.getMasterBlanketContractEndDate().before(new Date())) {
			return getStatusObject("6", false, "PO_BLANKET_EXPIRED", orgId, "Purchase Order Blanket Agreement Expired " + customTextNotFoundStr);
		} else {
			Status status = new Status();
			status.setCode("0");
			status.setSuccess(true);
			return status;
		}
	}

	private Status getStatusObject(String code, boolean success, String msgKey, String orgId, String defaultMessage) throws RestApplicationException {
		try {
			Status status = new Status();
			status.setCode(code);
			status.setMessage(standardTextDao.getById(msgKey, orgId, defaultMessage, null).getNote());
			status.setSuccess(success);
			return status;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RestApplicationException(new ServiceException(e.getMessage()));
		}
		
	}

	private String getCustomColumnValue(String columnId, PurchaseOrderItem item, String orgId) throws SQLException {
		List<String[]> primaryKeyValues = CustomColumnFactory.getPoCustomColPrimaryKeyValues(columnId, item.getDocId(), item.getReleaseNbr(), item.getItemNbr(), orgId);
		BaseDocCustomColumn[] columns = CustomColumnFactory.getPoCustomColumns(BoConstants.DOCTYPE_PO, BoConstants.DOC_TAB_ITEM, CustomColumnFactory.RoleAgency, primaryKeyValues, orgId, null);
		for (BaseDocCustomColumn col : columns) {
			if (col.getColumnId().equals((columnId))) {
				return col.getColumnValue();
			}
		}
		return null;
	}
	
	/**
	 * Returns 200 code if the PO is NOT locked, otherwise a 403 code is sent to indicate it IS locked (i.e. not able to be changed)
	 * @param deptNbr - The department that has all of the locations
	 * @param request - HttpServletRequest
	 * @return Options - list of options with text/value
	 */
	@Path("/locked")
	@GET    							 // The Java method will process HTTP GET requests
	//@Produces(MediaType.WILDCARD) // The Java method will produce content identified by the MIME Media
	@RestAuthentication
	public Response b(@DefaultValue("") @QueryParam("docId") String docId, @QueryParam("releaseNbr") long releaseNbr, @Context HttpServletRequest request) {

		try {
			log.debug("Checking status of PO: "+docId);
			PurchaseOrder po = PurchaseOrderFactory
					.getPoById(docId, releaseNbr, null);
			if (po!=null && po.isLocked()) {
				return Response.status(HttpStatus.SC_FORBIDDEN).entity("Purchase order is Locked").type("text/plain").build();
			}
			return Response.status(HttpStatus.SC_OK).entity("Purchase order is not locked").type("text/plain").build();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RestApplicationException(e);
		}
		
	}
	
	@Path("/changestatus")
	@GET // The Java method will process HTTP GET requests
	@RestAuthentication
	public Response changeDocumentStatus(@QueryParam("poNum") String poNum, @QueryParam("releaseNum") long releaseNum, @QueryParam("toStatus") String toStatus, @QueryParam("loginId") String loginId, @QueryParam("password") String password) throws RestApplicationException {
		
		log.debug("Changing document status via web service call.");
		
		SessionInfo sessionInfo = new SessionInfo();
		sessionInfo.setLoginId(PeriscopeLoginModule.UTILILTY_USER);
		sessionInfo.setRoleId(Role.INTERNAL_ADMIN);
		sessionInfo.setPassword(PeriscopeLoginModule.UTILILTY_USER);
		
		DocumentRepositoryLogin.set(sessionInfo);
		
		try {
			
			PurchaseOrder po = PurchaseOrderFactory.getPoById(poNum,
					releaseNum, null);

			PurchaseOrderFactory.changeStatus(null, po,
					toStatus, loginId, null);

			return Response.status(HttpStatus.SC_OK)
					.entity("Purchase order status changed.").type("text/plain")
					.build();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RestApplicationException(e);
		}
	}
}
