



package org.ofbiz.accounting.invoice;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import javolution.util.FastList;
//import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.accounting.invoice.InvoiceWorker; 

public class InvoiceServicesPayrol {
	 public static Map<String, Object> createInvoiceForPayrol(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
	        String invoiceType="PAYROL_INVOICE";
	        String invoiceId=null;
	        String paymentId=null;

	         
	     
	           String partyIdFrom=(String) context.get("partyIdFrom");
	           //System.out.println("this is employeeId"+partyIdFrom);
	           
	          
	            // create the invoice record


	                Map<String, Object> createInvoiceContext = new HashMap<String, Object>();
	                createInvoiceContext.put("partyId", "Company");
	                createInvoiceContext.put("partyIdFrom",partyIdFrom);
	                
	               
					createInvoiceContext.put("invoiceDate", null);
	                createInvoiceContext.put("dueDate", null);
	                createInvoiceContext.put("invoiceTypeId", "PAYROL_INVOICE");
	                // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
	                createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	                createInvoiceContext.put("currencyUomId",null);
	                createInvoiceContext.put("userLogin", userLogin);

	                // store the invoice first
	                Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
	               
	                // call service, not direct entity op: delegator.create(invoice);
	                invoiceId = (String) createInvoiceResult.get("invoiceId");
	            

	            // order roles to invoice roles
	            /*List<GenericValue> orderRoles = orderHeader.getRelated("OrderRole");
	            Map<String, Object> createInvoiceRoleContext = FastMap.newInstance();
	            createInvoiceRoleContext.put("invoiceId", invoiceId);
	            createInvoiceRoleContext.put("userLogin", userLogin);
	            for (GenericValue orderRole : orderRoles) {
	                createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
	                createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
	                Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
	                if (ServiceUtil.isError(createInvoiceRoleResult)) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "AccountingErrorCreatingInvoiceFromOrder", locale), null, null, createInvoiceRoleResult);
	                }
	            }*/

	            // order terms to invoice terms.
	            // TODO: it might be nice to filter OrderTerms to only copy over financial terms.
	           /* List<GenericValue> orderTerms = orh.getOrderTerms();
	            createInvoiceTerms(delegator, dispatcher, invoiceId, orderTerms, userLogin, locale);*/

	            // billing accounts
	            // List billingAccountTerms = null;
	            // for billing accounts we will use related information
	         /*   if (billingAccount != null) {
	                
	                 * jacopoc: billing account terms were already copied as order terms
	                 *          when the order was created.
	                // get the billing account terms
	                billingAccountTerms = billingAccount.getRelated("BillingAccountTerm");

	                // set the invoice terms as defined for the billing account
	                createInvoiceTerms(delegator, dispatcher, invoiceId, billingAccountTerms, userLogin, locale);
	                
	                // set the invoice bill_to_customer from the billing account
	                List<GenericValue> billToRoles = billingAccount.getRelated("BillingAccountRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"), null);
	                for (GenericValue billToRole : billToRoles) {
	                    if (!(billToRole.getString("partyId").equals(billToCustomerPartyId))) {
	                        createInvoiceRoleContext = UtilMisc.toMap("invoiceId", invoiceId, "partyId", billToRole.get("partyId"),
	                                                                           "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
	                        Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
	                        if (ServiceUtil.isError(createInvoiceRoleResult)) {
	                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                                    "AccountingErrorCreatingInvoiceRoleFromOrder", locale), null, null, createInvoiceRoleResult);
	                        }
	                    }
	                }
*/
	            
	         EntityExpr con = EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, partyIdFrom);
	            
	          List listItem  =delegator.findList("EmplSalAttribute", con, null, null, null, false);
	          if(listItem.size()>=1 ){
	      		//System.out.println("radham");
	      	for (Iterator iterator = listItem.iterator(); iterator.hasNext();) {
	      		GenericValue gv = (GenericValue) iterator.next();
	      		Map<String, Object> createInvoiceItemContext = new HashMap<String, Object>();
	      		//System.out.println("the full details"+invoiceId+gv.getString("attrType")+gv.getString("attrName")+ gv.getString("attrValue"));
	                createInvoiceItemContext.put("invoiceId", invoiceId);
	              //  createInvoiceItemContext.put("invoiceItemSeqId", invoiceItemSeqId);
	                createInvoiceItemContext.put("invoiceItemTypeId", gv.getString("attrType"));
	                createInvoiceItemContext.put("description", gv.getString("attrName"));
	                createInvoiceItemContext.put("quantity", 1);
	                createInvoiceItemContext.put("amount",  gv.getString("attrValue"));
	               // createInvoiceItemContext.put("productId", orderItem.get("productId"));
	               // createInvoiceItemContext.put("productFeatureId", orderItem.get("productFeatureId"));
	                //createInvoiceItemContext.put("overrideGlAccountId", orderItem.get("overrideGlAccountId"));
	                //createInvoiceItemContext.put("uomId", "");
	                createInvoiceItemContext.put("userLogin", userLogin);

	                //System.out.println("the final map"+createInvoiceItemContext);

	                Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
	                
	                
	    //System.out.println("the final result"+createInvoiceItemResult);
	      	}
	          }
	        BigDecimal amt =InvoiceWorker.getInvoiceNotApplied(delegator,invoiceId);
	          
	          Map<String, Object> createPaymentcontext = new HashMap<String, Object>();
	          createPaymentcontext.put("paymentTypeId", "PAYROL_PAYMENT");
	          createPaymentcontext.put("paymentMethodTypeId", "COMPANY_ACCOUNT");
	          createPaymentcontext.put("partyIdTo", partyIdFrom);
	          createPaymentcontext.put("partyIdFrom", "Company");
	          createPaymentcontext.put("statusId", "PMNT_NOT_PAID");
	          createPaymentcontext.put("amount",amt);
	          createPaymentcontext.put("userLogin", userLogin);
	          Map<String, Object> createPaymentResult = dispatcher.runSync("createPayment", createPaymentcontext);
	          
	          paymentId = (String) createPaymentResult.get("paymentId");
	          //System.out.println("the Payment Id"+paymentId);
	          Map<String, Object> setInvoiceStatusResultApproved = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_APPROVED", "userLogin", userLogin));
	          Map<String, Object> setInvoiceStatusResultReady = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_READY", "userLogin", userLogin));
	               /* if (ServiceUtil.isError(createInvoiceItemResult)) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "AccountingErrorCreatingInvoiceItemFromOrder", locale), null, null, createInvoiceItemResult);
	                }

	                // this item total
	                BigDecimal thisAmount = billingAmount.multiply(billingQuantity).setScale(invoiceTypeDecimals, ROUNDING);

	                // add to the ship amount only if it applies to this item
	                if (shippingApplies) {
	                    invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAmount).setScale(invoiceTypeDecimals, ROUNDING);
	                }

	                // increment the invoice subtotal
	                invoiceSubTotal = invoiceSubTotal.add(thisAmount).setScale(100, ROUNDING);

	                // increment the invoice quantity
	                invoiceQuantity = invoiceQuantity.add(billingQuantity).setScale(invoiceTypeDecimals, ROUNDING);
*/
	                // create the OrderItemBilling record
	                /*Map<String, Object> createOrderItemBillingContext = FastMap.newInstance();
	                createOrderItemBillingContext.put("invoiceId", invoiceId);
	                createOrderItemBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
	                createOrderItemBillingContext.put("orderId", orderItem.get("orderId"));
	                createOrderItemBillingContext.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
	                createOrderItemBillingContext.put("itemIssuanceId", itemIssuanceId);
	                createOrderItemBillingContext.put("quantity", billingQuantity);
	                createOrderItemBillingContext.put("amount", billingAmount);
	                createOrderItemBillingContext.put("userLogin", userLogin);
	                if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
	                    createOrderItemBillingContext.put("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
	                }

	                Map<String, Object> createOrderItemBillingResult = dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);
	                if (ServiceUtil.isError(createOrderItemBillingResult)) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "AccountingErrorCreatingOrderItemBillingFromOrder", locale), null, null, createOrderItemBillingResult);
	                }

	                if ("ItemIssuance".equals(currentValue.getEntityName())) {
	                    List<GenericValue> shipmentItemBillings = delegator.findByAnd("ShipmentItemBilling", UtilMisc.toMap("shipmentId", currentValue.get("shipmentId")));
	                    if (UtilValidate.isEmpty(shipmentItemBillings)) {

	                        // create the ShipmentItemBilling record
	                        GenericValue shipmentItemBilling = delegator.makeValue("ShipmentItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
	                        shipmentItemBilling.put("shipmentId", currentValue.get("shipmentId"));
	                        shipmentItemBilling.put("shipmentItemSeqId", currentValue.get("shipmentItemSeqId"));
	                        shipmentItemBilling.create();
	                    }
	                }

	                String parentInvoiceItemSeqId = invoiceItemSeqId;
	                // increment the counter
	                invoiceItemSeqNum++;
	                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);

	                // Get the original order item from the DB, in case the quantity has been overridden
	                GenericValue originalOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")));

	                // create the item adjustment as line items
	                List<GenericValue> itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
	                for (GenericValue adj : itemAdjustments) {

	                    // Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
	                    BigDecimal adjAlreadyInvoicedAmount = null;
	                    try {
	                        Map<String, Object> checkResult = dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
	                        adjAlreadyInvoicedAmount = (BigDecimal) checkResult.get("invoicedTotal");
	                    } catch (GenericServiceException e) {
	                        Debug.logError(e, "Accounting trouble calling calculateInvoicedAdjustmentTotal service", module);
	                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                                "AccountingTroubleCallingCalculateInvoicedAdjustmentTotalService", locale));
	                    }

	                    // If the absolute invoiced amount >= the abs of the adjustment amount, the full amount has already been invoiced,
	                    //  so skip this adjustment
	                    if (adj.get("amount") == null) { // JLR 17/4/7 : fix a bug coming from POS in case of use of a discount (on item(s) or sale, item(s) here) and a cash amount higher than total (hence issuing change)
	                        continue;
	                    }
	                    if (adjAlreadyInvoicedAmount.abs().compareTo(adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).abs()) > 0) {
	                        continue;
	                    }

	                    BigDecimal originalOrderItemQuantity = OrderReadHelper.getOrderItemQuantity(originalOrderItem);
	                    BigDecimal amount = ZERO;
	                    if (originalOrderItemQuantity.signum() != 0) {
	                        if (adj.get("amount") != null) {
	                            // pro-rate the amount
	                            // set decimals = 100 means we don't round this intermediate value, which is very important
	                            amount = adj.getBigDecimal("amount").divide(originalOrderItemQuantity, 100, ROUNDING);
	                            amount = amount.multiply(billingQuantity);
	                            // Tax needs to be rounded differently from other order adjustments
	                            if (adj.getString("orderAdjustmentTypeId").equals("SALES_TAX")) {
	                                amount = amount.setScale(TAX_DECIMALS, TAX_ROUNDING);
	                            } else {
	                                amount = amount.setScale(invoiceTypeDecimals, ROUNDING);
	                            }
	                        } else if (adj.get("sourcePercentage") != null) {
	                            // pro-rate the amount
	                            // set decimals = 100 means we don't round this intermediate value, which is very important
	                            BigDecimal percent = adj.getBigDecimal("sourcePercentage");
	                            percent = percent.divide(new BigDecimal(100), 100, ROUNDING);
	                            amount = billingAmount.multiply(percent);
	                            amount = amount.divide(originalOrderItemQuantity, 100, ROUNDING);
	                            amount = amount.multiply(billingQuantity);
	                            amount = amount.setScale(invoiceTypeDecimals, ROUNDING);
	                        }
	                    }
	                    if (amount.signum() != 0) {
	                        Map<String, Object> createInvoiceItemAdjContext = FastMap.newInstance();
	                        createInvoiceItemAdjContext.put("invoiceId", invoiceId);
	                        createInvoiceItemAdjContext.put("invoiceItemSeqId", invoiceItemSeqId);
	                        createInvoiceItemAdjContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), null, invoiceType, "INVOICE_ITM_ADJ"));
	                        createInvoiceItemAdjContext.put("quantity", BigDecimal.ONE);
	                        createInvoiceItemAdjContext.put("amount", amount);
	                        createInvoiceItemAdjContext.put("productId", orderItem.get("productId"));
	                        createInvoiceItemAdjContext.put("productFeatureId", orderItem.get("productFeatureId"));
	                        createInvoiceItemAdjContext.put("overrideGlAccountId", adj.get("overrideGlAccountId"));
	                        createInvoiceItemAdjContext.put("parentInvoiceId", invoiceId);
	                        createInvoiceItemAdjContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
	                        //createInvoiceItemAdjContext.put("uomId", "");
	                        createInvoiceItemAdjContext.put("userLogin", userLogin);
	                        createInvoiceItemAdjContext.put("taxAuthPartyId", adj.get("taxAuthPartyId"));
	                        createInvoiceItemAdjContext.put("taxAuthGeoId", adj.get("taxAuthGeoId"));
	                        createInvoiceItemAdjContext.put("taxAuthorityRateSeqId", adj.get("taxAuthorityRateSeqId"));

	                        // some adjustments fill out the comments field instead
	                        String description = (UtilValidate.isEmpty(adj.getString("description")) ? adj.getString("comments") : adj.getString("description"));
	                        createInvoiceItemAdjContext.put("description", description);

	                        // invoice items for sales tax are not taxable themselves
	                        // TODO: This is not an ideal solution. Instead, we need to use OrderAdjustment.includeInTax when it is implemented
	                        if (!(adj.getString("orderAdjustmentTypeId").equals("SALES_TAX"))) {
	                            createInvoiceItemAdjContext.put("taxableFlag", product.get("taxable"));
	                        }

	                        // If the OrderAdjustment is associated to a ProductPromo,
	                        // and the field ProductPromo.overrideOrgPartyId is set,
	                        // copy the value to InvoiceItem.overrideOrgPartyId: this
	                        // represent an organization override for the payToPartyId
	                        if (UtilValidate.isNotEmpty(adj.getString("productPromoId"))) {
	                            try {
	                                GenericValue productPromo = adj.getRelatedOne("ProductPromo");
	                                if (UtilValidate.isNotEmpty(productPromo.getString("overrideOrgPartyId"))) {
	                                    createInvoiceItemAdjContext.put("overrideOrgPartyId", productPromo.getString("overrideOrgPartyId"));
	                                }
	                            } catch (GenericEntityException e) {
	                                Debug.logError(e, "Error looking up ProductPromo with id [" + adj.getString("productPromoId") + "]", module);
	                            }
	                        }

	                        Map<String, Object> createInvoiceItemAdjResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemAdjContext);
	                        if (ServiceUtil.isError(createInvoiceItemAdjResult)) {
	                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                                    "AccountingErrorCreatingInvoiceItemFromOrder", locale), null, null, createInvoiceItemAdjResult);
	                        }

	                        // Create the OrderAdjustmentBilling record
	                        Map<String, Object> createOrderAdjustmentBillingContext = FastMap.newInstance();
	                        createOrderAdjustmentBillingContext.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
	                        createOrderAdjustmentBillingContext.put("invoiceId", invoiceId);
	                        createOrderAdjustmentBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
	                        createOrderAdjustmentBillingContext.put("amount", amount);
	                        createOrderAdjustmentBillingContext.put("userLogin", userLogin);

	                        Map<String, Object> createOrderAdjustmentBillingResult = dispatcher.runSync("createOrderAdjustmentBilling", createOrderAdjustmentBillingContext);
	                        if (ServiceUtil.isError(createOrderAdjustmentBillingResult)) {
	                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                                    "AccountingErrorCreatingOrderAdjustmentBillingFromOrder", locale), null, null, createOrderAdjustmentBillingContext);
	                        }

	                        // this adjustment amount
	                        BigDecimal thisAdjAmount = amount;

	                        // adjustments only apply to totals when they are not tax or shipping adjustments
	                        if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) &&
	                                !"SHIPPING_ADJUSTMENT".equals(adj.getString("orderAdjustmentTypeId"))) {
	                            // increment the invoice subtotal
	                            invoiceSubTotal = invoiceSubTotal.add(thisAdjAmount).setScale(100, ROUNDING);

	                            // add to the ship amount only if it applies to this item
	                            if (shippingApplies) {
	                                invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAdjAmount).setScale(invoiceTypeDecimals, ROUNDING);
	                            }
	                        }

	                        // increment the counter
	                        invoiceItemSeqNum++;
	                        invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
	                    }
	                }
	              
	            }*/

	            // create header adjustments as line items -- always to tax/shipping last
	         /*   Map<GenericValue, BigDecimal> shipAdjustments = FastMap.newInstance();
	            Map<GenericValue, BigDecimal> taxAdjustments = FastMap.newInstance();

	            List<GenericValue> headerAdjustments = orh.getOrderHeaderAdjustments();
	            for (GenericValue adj : headerAdjustments) {

	                // Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
	                BigDecimal adjAlreadyInvoicedAmount = null;
	                try {
	                    Map<String, Object> checkResult = dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
	                    adjAlreadyInvoicedAmount = ((BigDecimal) checkResult.get("invoicedTotal")).setScale(invoiceTypeDecimals, ROUNDING);
	                } catch (GenericServiceException e) {
	                    Debug.logError(e, "Accounting trouble calling calculateInvoicedAdjustmentTotal service", module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "AccountingTroubleCallingCalculateInvoicedAdjustmentTotalService", locale));                    
	                }

	                // If the absolute invoiced amount >= the abs of the adjustment amount, the full amount has already been invoiced,
	                //  so skip this adjustment
	                if (null == adj.get("amount")) { // JLR 17/4/7 : fix a bug coming from POS in case of use of a discount (on item(s) or sale, sale here) and a cash amount higher than total (hence issuing change)
	                    continue;
	                }
	                if (adjAlreadyInvoicedAmount.abs().compareTo(adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).abs()) > 0) {
	                    continue;
	                }

	                if ("SHIPPING_CHARGES".equals(adj.getString("orderAdjustmentTypeId"))) {
	                    shipAdjustments.put(adj, adjAlreadyInvoicedAmount);
	                } else if ("SALES_TAX".equals(adj.getString("orderAdjustmentTypeId"))) {
	                    taxAdjustments.put(adj, adjAlreadyInvoicedAmount);
	                } else {
	                    // these will effect the shipping pro-rate (unless commented)
	                    // other adjustment type
	                    calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, orderSubTotal, invoiceSubTotal,
	                            adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING), invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
	                    // invoiceShipProRateAmount += adjAmount;
	                    // do adjustments compound or are they based off subtotal? Here we will (unless commented)
	                    // invoiceSubTotal += adjAmount;

	                    // increment the counter
	                    invoiceItemSeqNum++;
	                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
	                }
	            }

	            // next do the shipping adjustments.  Note that we do not want to add these to the invoiceSubTotal or orderSubTotal for pro-rating tax later, as that would cause
	            // numerator/denominator problems when the shipping is not pro-rated but rather charged all on the first invoice
	            for (GenericValue adj : shipAdjustments.keySet()) {
	                BigDecimal adjAlreadyInvoicedAmount = shipAdjustments.get(adj);

	                if ("N".equalsIgnoreCase(prorateShipping)) {

	                    // Set the divisor and multiplier to 1 to avoid prorating
	                    BigDecimal divisor = BigDecimal.ONE;
	                    BigDecimal multiplier = BigDecimal.ONE;

	                    // The base amount in this case is the adjustment amount minus the total already invoiced for that adjustment, since
	                    //  it won't be prorated
	                    BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).subtract(adjAlreadyInvoicedAmount);
	                    calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, divisor, multiplier, baseAmount, 
	                            invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
	                } else {

	                    // Pro-rate the shipping amount based on shippable information
	                    BigDecimal divisor = shippableAmount;
	                    BigDecimal multiplier = invoiceShipProRateAmount;

	                    // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
	                    BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING);
	                    calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, divisor, multiplier, 
	                            baseAmount, invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
	                }

	                // Increment the counter
	                invoiceItemSeqNum++;
	                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
	            }

	            // last do the tax adjustments
	            String prorateTaxes = productStore != null ? productStore.getString("prorateTaxes") : "Y";
	            if (prorateTaxes == null) {
	                prorateTaxes = "Y";
	            }
	            for (GenericValue adj : taxAdjustments.keySet()) {
	                BigDecimal adjAlreadyInvoicedAmount = taxAdjustments.get(adj);
	                BigDecimal adjAmount = null;

	                if ("N".equalsIgnoreCase(prorateTaxes)) {

	                    // Set the divisor and multiplier to 1 to avoid prorating
	                    BigDecimal divisor = BigDecimal.ONE;
	                    BigDecimal multiplier = BigDecimal.ONE;

	                    // The base amount in this case is the adjustment amount minus the total already invoiced for that adjustment, since
	                    //  it won't be prorated
	                    BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(TAX_DECIMALS, TAX_ROUNDING).subtract(adjAlreadyInvoicedAmount);
	                    adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId,
	                             divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dispatcher, locale);
	                } else {

	                    // Pro-rate the tax amount based on shippable information
	                    BigDecimal divisor = orderSubTotal;
	                    BigDecimal multiplier = invoiceSubTotal;

	                    // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
	                    BigDecimal baseAmount = adj.getBigDecimal("amount");
	                    adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId,
	                            divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dispatcher, locale);
	                }
	                invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(invoiceTypeDecimals, ROUNDING);

	                // Increment the counter
	                invoiceItemSeqNum++;
	                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
	            }

	            // check for previous order payments
	            List<EntityExpr> paymentPrefConds = UtilMisc.toList(
	                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
	                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
	            List<GenericValue> orderPaymentPrefs = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(paymentPrefConds, EntityOperator.AND), null, null, null, false);
	            List<GenericValue> currentPayments = FastList.newInstance();
	            for (GenericValue paymentPref : orderPaymentPrefs) {
	                List<GenericValue> payments = paymentPref.getRelated("Payment");
	                currentPayments.addAll(payments);
	            }
	            // apply these payments to the invoice if they have any remaining amount to apply
	            for (GenericValue payment : currentPayments) {
	                if ("PMNT_VOID".equals(payment.getString("statusId")) || "PMNT_CANCELLED".equals(payment.getString("statusId"))) {
	                    continue;
	                }
	                BigDecimal notApplied = PaymentWorker.getPaymentNotApplied(payment);*/
	                if (amt.signum() > 0) {
	                    Map<String, Object> appl = new HashMap<String, Object>();
	                    appl.put("paymentId", paymentId);
	                    appl.put("invoiceId", invoiceId);
	                    //appl.put("billingAccountId", billingAccountId);
	                    appl.put("amountApplied", amt);
	                    appl.put("userLogin", userLogin);
	                    Map<String, Object> createPayApplResult = dispatcher.runSync("createPaymentApplication", appl);
	                  
	                }
	          // }

	            // Should all be in place now. Depending on the ProductStore.autoApproveInvoice setting, set status to INVOICE_READY (unless it's a purchase invoice, which we set to INVOICE_IN_PROCESS)
	          /*  String autoApproveInvoice = productStore != null ? productStore.getString("autoApproveInvoice") : "Y";
	            if (!"N".equals(autoApproveInvoice)) {
	                String nextStatusId = "PURCHASE_INVOICE".equals(invoiceType) ? "INVOICE_IN_PROCESS" : "INVOICE_READY";
	                Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
	                if (ServiceUtil.isError(setInvoiceStatusResult)) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "AccountingErrorCreatingInvoiceFromOrder", locale), null, null, setInvoiceStatusResult);
	                }
	            }*/
	                
	              Map<String, Object> setInvoiceStatusResultPaid = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_PAID", "userLogin", userLogin));
	                
	                

	            Map<String, Object> resp = ServiceUtil.returnSuccess();
	            resp.put("invoiceId", invoiceId);
	            resp.put("invoiceTypeId", invoiceType);
	            return resp;
	       
	    }

}














