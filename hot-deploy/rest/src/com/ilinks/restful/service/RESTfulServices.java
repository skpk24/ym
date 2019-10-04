package com.ilinks.restful.service;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.axis.types.NonNegativeInteger;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
/*
import com.fedex.rate.stub.Party;
import com.fedex.rate.stub.RequestedShipment;
import com.fedex.ship.stub.CompletedShipmentDetail;
import com.fedex.ship.stub.DropoffType;
import com.fedex.ship.stub.Notification;
import com.fedex.ship.stub.PackageRateDetail;
import com.fedex.ship.stub.PackagingType;
import com.fedex.ship.stub.Payment;
import com.fedex.ship.stub.ProcessShipmentReply;
import com.fedex.ship.stub.ProcessShipmentRequest;
import com.fedex.ship.stub.RateRequestType;
import com.fedex.ship.stub.RequestedPackageLineItem;
import com.fedex.ship.stub.ServiceType;
import com.fedex.ship.stub.ShipPortType;
import com.fedex.ship.stub.ShipServiceLocator;
import com.fedex.ship.stub.ShippingDocument;
import com.fedex.ship.stub.TrackingId;
import com.fedex.ship.stub.TransactionDetail;
import com.fedex.ship.stub.VersionId;
import com.petbest.fedex.rate.FedexShippingRate;
import com.petbest.fedex.ship.FedExShippingLabel;
*/

public class RESTfulServices {
    public static final String MODULE = RESTfulServices.class.getName();
//    public static final VersionId SHIPMENT_VERSION_ID = new VersionId("ship", 12, 1, 0);
    static String END_POINT = "https://ws.fedex.com/web-services/ship";
    public static final String SHIPMENT_PROPERTIES = "shipment.properties";
    public static final String accountNumber = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.accountNbr");
    public static final String meterNumber = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.meterNumber");
    public static final String fedExKey= UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.userCredential.key");
    public static final String password = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.userCredential.password");
    public static final String PETBEST = "PetBest, www.petbest.com";
    public static final String shipperAddress = "31260 Cedar Valley Drive";
    public static final String shipperCity = "Westlake Village";
    public static final String shipperState = "CA";
    public static final String shipperPostalCode = "91362";
    public static final String shipperPhoneNumber = "888-941-2378";
    
    
    public static Map<String, Object> logRequest(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
       
        try{
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            GenericValue serverHit = delegator.makeValue("ServerHit");
            serverHit.set("visitId", context.get("visitID"));
            serverHit.set("hitStartDateTime",UtilDateTime.nowTimestamp());
            serverHit.set("hitTypeId",context.get("action"));
            //serverHit.set("contentId",context.get("version") + "."  +  context.get("webAppName") + "." + context.get("requestURL")  + "." + UtilDateTime.nowTimestamp());
            serverHit.set("contentId",context.get("version") +  "." + UtilDateTime.nowTimestamp());
            serverHit.set("requestUrl",context.get("requestURL"));
            serverHit.set("referrerUrl",context.get("referrerURL"));
            serverHit.set("serverHostName", context.get("serverHostName"));
            if(userLogin != null){
                serverHit.set("userLoginId",userLogin.getString("userLoginId"));
                serverHit.set("partyId", userLogin.getString("partyId"));
            }
            delegator.createOrStore(serverHit);
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Log Request " + context.get("url") + " " + context.get("serverHostName"), MODULE);
        }
        finally{
            
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> calculateStoreShippingCost(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
        try{
        	/*
            FedexShippingRate rate = new FedexShippingRate(accountNumber, meterNumber, fedExKey, password,shipperAddress, shipperCity,shipperState,shipperPostalCode,"US");
            RequestedShipment requestedShipment = rate.getRequestShipment();
            Party recipient =  rate.createRecipientAddress((String)context.get("address"), (String)context.get("city"), (String)context.get("state"), (String)context.get("postalCode"), (String)context.get("country"));
            requestedShipment.setRecipient(recipient);
            rate.setWeight((String)context.get("weight"));
            
            Map<Object,Object> charges = rate.getShippingRate();
            GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
            
            orderAttribute.set("orderId", context.get("orderID"));
            orderAttribute.set("attrName", "totalShipingCost");
            orderAttribute.set("attrValue", charges.get("totalNetCharges").toString());
            orderAttribute.create();
            
            orderAttribute = delegator.makeValue("OrderAttribute");
            orderAttribute.set("orderId",context.get("orderID"));
            orderAttribute.set("attrName", "totalShippingSurCharges");
            orderAttribute.set("attrValue", charges.get("totalSurCharges").toString());
            orderAttribute.create();
            */
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Find Calculate FedEx Shipping Cost " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            
        }
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> createShippingLabel(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        try{
        	/*
            Map<String, Object> queryMap = FastMap.newInstance();
            queryMap.put("shipmentId", (String)context.get("shipmentId"));
            queryMap.put("shipmentPackageSeqId", (String)context.get("shipmentPackageSeqId"));
            queryMap.put("shipmentRouteSegmentId", (String)context.get("shipmentRouteSegmentId"));
            GenericValue shipmentPackageRouteSegment  = delegator.findOne("ShipmentPackageRouteSeg", queryMap, false);
            GenericValue shipment = shipmentPackageRouteSegment.getRelatedOneCache("Shipment");
            
            queryMap.put("shipmentPackageSeqId", "00001");
            queryMap.put("shipmentRouteSegmentId", "00001");
            GenericValue firstShipmentPackageRouteSegment =  delegator.findOne("ShipmentPackageRouteSeg", queryMap, true);
           
            
            // Initialize the service
            ShipServiceLocator service;
            ShipPortType port;
            service = new ShipServiceLocator();
            service.setShipServicePortEndpointAddress(END_POINT);
            port = service.getShipServicePort();
            
            ProcessShipmentRequest request = new ProcessShipmentRequest(); // Build a request object
            request.setClientDetail(FedExShippingLabel.createClientDetail(accountNumber, meterNumber));
            request.setWebAuthenticationDetail(FedExShippingLabel.createWebAuthenticationDetail(fedExKey, password));
            //
            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setCustomerTransactionId("PetBest, Inc - Domestic Ground Shipment"); // The client will get the same value back in the response
            request.setTransactionDetail(transactionDetail);
            //
           
            request.setVersion(SHIPMENT_VERSION_ID);
            //
            com.fedex.ship.stub.RequestedShipment requestedShipment = new com.fedex.ship.stub.RequestedShipment();
            requestedShipment.setShipTimestamp(Calendar.getInstance()); // Ship date and time
            requestedShipment.setServiceType(ServiceType.GROUND_HOME_DELIVERY); // Service types are STANDARD_OVERNIGHT, PRIORITY_OVERNIGHT, FEDEX_GROUND ...
            requestedShipment.setDropoffType(DropoffType.REGULAR_PICKUP);
            requestedShipment.setPackagingType(PackagingType.YOUR_PACKAGING); // Packaging type FEDEX_BOX, FEDEX_PAK, FEDEX_TUBE, YOUR_PACKAGING, ...
            
            
            String masterTrackingCode = firstShipmentPackageRouteSegment.getString("trackingCode");
            
            if(masterTrackingCode != null){
                //TrackingId masterTracking = new TrackingId();
                //masterTracking.setTrackingNumber(masterTrackingCode);
                //requestedShipment.setMasterTrackingId(masterTracking);
            }
            
            com.fedex.ship.stub.Party shipper = FedExShippingLabel.getShipper(PETBEST,"Pet Food Delivery, Made Easy", shipperPhoneNumber, shipperAddress,shipperCity,shipperState,shipperPostalCode);
            requestedShipment.setShipper(shipper); // Sender information
            
            List<GenericValue> shipPackageList = shipment.getRelated("ShipmentPackage");
            String totalNumberOfPackages = shipPackageList.size() + "";
            String packageNumber = (String)context.get("shipmentPackageSeqId");
            packageNumber = packageNumber.replaceAll("0", "");// remove all zeros, should not have more than 9 packages
            
            
            totalNumberOfPackages = "1";
            packageNumber = "1";
            GenericValue postalAddress = shipment.getRelatedOneCache("DestinationPostalAddress");
            GenericValue telephone = shipment.getRelatedOneCache("DestinationTelecomNumber");
            GenericValue person = shipment.getRelatedOne("ToPerson");
            com.fedex.ship.stub.Party  customer = FedExShippingLabel.getRecipient(person.getString("firstName") + " " + person.getString("lastName"), "", telephone.get("contactNumber") == null ? shipperPhoneNumber :  (String)telephone.get("contactNumber"),
                    postalAddress.getString("address1"),  postalAddress.getString("address2") == null ? "" : postalAddress.getString("address2"),  postalAddress.getString("city"), postalAddress.getString("stateProvinceGeoId"), postalAddress.getString("postalCode"));
            requestedShipment.setRecipient(customer);
            Payment payment = FedExShippingLabel.getPayment(accountNumber);
            requestedShipment.setShippingChargesPayment(payment);
            requestedShipment.setLabelSpecification(FedExShippingLabel.getLabelSpecification());
            
            //
            RateRequestType[] rrt= new RateRequestType[] { RateRequestType.ACCOUNT }; // Rate types requested LIST, MULTIWEIGHT, ...
            requestedShipment.setRateRequestTypes(rrt);
            requestedShipment.setPackageCount(new NonNegativeInteger(totalNumberOfPackages));
        
            GenericValue order = shipment.getRelatedOneCache("PrimaryOrderHeader");
            GenericValue shipmentPackage = shipmentPackageRouteSegment.getRelatedOneCache("ShipmentPackage");
            BigDecimal weight = shipmentPackage.getBigDecimal("weight");
            
            RequestedPackageLineItem requestedPackageLineItem = FedExShippingLabel.getRequestedPackageLineItem(weight, order.getString("orderId"), packageNumber);
           
            requestedShipment.setRequestedPackageLineItems(new RequestedPackageLineItem[]{requestedPackageLineItem});
            //
            request.setRequestedShipment(requestedShipment);
            
            
            BigDecimal netCost = BigDecimal.ZERO;
            BigDecimal totalSurCharges = BigDecimal.ZERO;
            
            ProcessShipmentReply reply = port.processShipment(request);
            if (FedExShippingLabel.isResponseOk(reply.getHighestSeverity())){
                CompletedShipmentDetail completedShipmentDetail = reply.getCompletedShipmentDetail(); 
                TrackingId masterTrackingID = completedShipmentDetail.getMasterTrackingId();
                TrackingId packageTrackingID = completedShipmentDetail.getCompletedPackageDetails(0).getTrackingIds(0);
                PackageRateDetail packageRateDetail = completedShipmentDetail.getCompletedPackageDetails(0).getPackageRating().getPackageRateDetails(0);
                ShippingDocument shippingDocument = completedShipmentDetail.getCompletedPackageDetails(0).getLabel();
                
                netCost = packageRateDetail.getNetCharge().getAmount();
                totalSurCharges = packageRateDetail.getTotalSurcharges().getAmount();
                
                Debug.log(
                        String.format("master tracking %s tracking number %s net shipping %s sur charges %s", masterTrackingID != null ? masterTrackingID.getTrackingNumber() : "NO_MASTER_TRACKING",packageTrackingID.getTrackingNumber(),netCost.toString(), totalSurCharges.toString())
                        ,MODULE
                        );  
                shipmentPackageRouteSegment.set("trackingCode", packageTrackingID.getTrackingNumber());
                shipmentPackageRouteSegment.setBytes("labelImage", shippingDocument.getParts(0).getImage());
                shipmentPackageRouteSegment.store();
            }
            else{
                com.fedex.ship.stub.Notification notifications[] = reply.getNotifications();
                for( Notification notification : notifications){
                    Debug.logError("Type " + notification.getSeverity().getValue(), MODULE);
                    Debug.logError("Code " + notification.getCode(), MODULE);
                    Debug.logError("Message " + notification.getMessage(), MODULE);
                    Debug.logError("Source " + notification.getSource(), MODULE);
                }
            }
            results.put("orderId", (String)order.getString("orderId"));
            results.put("netCost", netCost);
            results.put("totalSurCharge", totalSurCharges);
           */
        }
        catch (Exception e){
            Debug.logError(e, "Unable Create Shipping Label " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            
        }
        return results;
    }
    
    
    public static Map<String, Object> storeShippingCost(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try{
            String orderID = (String)context.get("orderId");
            GenericValue netCostAttribute  = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderID, "attrName","totalShipingCost"), false);
            GenericValue surChargeAttribute = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderID, "attrName","totalSurCharge"), false);
            
            BigDecimal totalNetCost = BigDecimal.ZERO;
            BigDecimal totalSurCharge= BigDecimal.ZERO;
            if(netCostAttribute == null){
                netCostAttribute = delegator.makeValue("OrderAttribute");
                netCostAttribute.set("orderId", orderID);
                netCostAttribute.set("attrName", "totalShipingCost");
                
                surChargeAttribute = delegator.makeValue("OrderAttribute");
                surChargeAttribute.set("orderId", orderID);
                surChargeAttribute.set("attrName", "totalSurCharge");
            }
            else{
                totalNetCost  =  new BigDecimal(netCostAttribute.getString("attrValue"));
                totalSurCharge =  new BigDecimal(surChargeAttribute.getString("attrValue"));
            }
            
            totalNetCost = totalNetCost.add((BigDecimal)context.get("netCost"));
            totalSurCharge = totalSurCharge.add((BigDecimal)context.get("totalSurCharge"));
            netCostAttribute.set("attrValue", totalNetCost.toString());
            surChargeAttribute.set("attrValue", totalSurCharge.toString());
            delegator.createOrStore(netCostAttribute);
            delegator.createOrStore(surChargeAttribute);
            
        }
        catch (Exception e){
            Debug.logError(e, "Unable Store FedEx Shipping Cost " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            
        }
        return results;
    }
    
    
    
    
    /**
     * expire existing billing profile then create new profile
     * also update payment method for existing shopping list
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateBillingProfile(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnError("Unable To Update Billing Profile");
        //Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try{
            Map<String, Object> dataContext = FastMap.newInstance();
            dataContext.put("userLogin", userLogin);
            dataContext.put("partyId", userLogin.getString("partyId"));
            dataContext.put("contactMechId",context.get("postalAddressID"));
            // expire billing address
            results = dispatcher.runSync("deletePartyContactMech", dataContext);
            if(ServiceUtil.isSuccess(results)){
                // expire telephone
                dataContext.put("contachMechId",context.get("telephoneID"));
                results = dispatcher.runSync("deletePartyContactMech", dataContext);
                if(ServiceUtil.isSuccess(results)){
                    // payment and billing address
                    dataContext.remove("contactMechId");
                    dataContext.clear();
                    dataContext.put("userLogin", userLogin);
                    dataContext.put("partyId", userLogin.getString("partyId"));
                    String fullName = context.get("firstNameOnCard") +  " " + context.get("lastNameOnCard");
                    dataContext.put("firstNameOnCard", context.get("firstNameOnCard"));
                    dataContext.put("lastNameOnCard", context.get("lastNameOnCard"));
                    dataContext.put("address1", context.get("address1"));
                    dataContext.put("address2", context.get("address2"));
                    dataContext.put("city",context.get("city"));
                    dataContext.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
                    dataContext.put("postalCode", context.get("postalCode"));
                    dataContext.put("countyGeoId", "USA");
                    dataContext.put("cardNumber", context.get("cardNumber"));
                    dataContext.put("expireDate",  context.get("expireDate"));
                    dataContext.put("cardType", context.get("cardType"));
                    dataContext.put("productStoreId", context.get("productStoreID"));
                    dataContext.put("toName", fullName);
                    results = dispatcher.runSync("createCreditCardAndAddress", dataContext);
                    if(ServiceUtil.isSuccess(results)){
                        // telephone
                        String postalAddressContactMechID = (String) results.get("contactMechId");
                        String paymentMethodID = (String) results.get("paymentMethodId");
                        dataContext.clear();
                        dataContext.put("countryCode", "1");
                        dataContext.put("areaCode",  context.get("areaCode"));
                        dataContext.put("contactNumber",  context.get("contactNumber"));
                        dataContext.put("userLogin", userLogin);
                        dataContext.put("partyId", userLogin.getString("partyId"));
                        dataContext.put("contactMechPurposeTypeId", "PHONE_BILLING");
                        results = dispatcher.runSync("createPartyTelecomNumber", dataContext);
                        if (ServiceUtil.isSuccess(results)){
                            String telephoneContactMechID = (String)results.get("contactMechId");
                            results = dispatcher.runSync("createContactMechLink",UtilMisc.toMap("contactMechIdFrom", postalAddressContactMechID,"userLogin",userLogin,"contactMechIdTo", telephoneContactMechID));
                            dataContext.clear();
                            dataContext.put("userLogin", userLogin);
                            dataContext.put("partyId", userLogin.getString("partyId"));
                            dataContext.put("defaultPaymentMethodId", paymentMethodID);
                            dataContext.put("paymentMethodId",context.get("paymentMethodID"));
                            // delete payment method
                            results = dispatcher.runSync("deletePaymentMethod", dataContext);
                            results.put("telephoneID",telephoneContactMechID);
                            results.put("postalAddressID", postalAddressContactMechID);
                            results.put("paymentMethodID", paymentMethodID);
                        }
                    }

                }
            }

        }
        catch (Exception e){
            Debug.logError(e, "Unable To Update Billing Profile " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            if(ServiceUtil.isError(results)){
                results = ServiceUtil.returnError("Unable To Update Billing Profile");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
        }
        return results;
    }
    
    public static Map<String, Object> deleteBillingInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnError("Unable To Delete Billing Profile");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try{
            Map<String, Object> dataContext = FastMap.newInstance();
            dataContext.put("userLogin", userLogin);
            dataContext.put("partyId", userLogin.getString("partyId"));
            dataContext.put("contactMechId",context.get("postalAddressID"));
            // expire billing address
            results = dispatcher.runSync("deletePartyContactMech", dataContext);
            if(ServiceUtil.isSuccess(results)){
                // expire telephone
                dataContext.put("contachMechId",context.get("telephoneID"));
                results = dispatcher.runSync("deletePartyContactMech", dataContext);
            }
            if(ServiceUtil.isSuccess(results)){
                dataContext.clear();
                dataContext.put("userLogin", userLogin);
                dataContext.put("defaultPaymentMethodId", context.get("paymentMethodID"));
                dataContext.put("paymentMethodId",context.get("paymentMethodID"));
                results = dispatcher.runSync("deletePaymentMethod", dataContext);
            }
            
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Delete Billing Profile " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            if(ServiceUtil.isError(results)){
                results = ServiceUtil.returnError("Unable To Delete Billing Profile");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
        }
        return results;
    }
    
    
    public static Map<String, Object> calculateOrderTax(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        ShoppingCart cart =  (ShoppingCart) context.get("cart");
        try{
            CheckOutHelper helper = new CheckOutHelper(dispatcher, delegator, cart);
            helper.calcAndAddTax();
            cart.getTotalSalesTax();
            results.put("tax", cart.getTotalSalesTax());
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Calculate Tax " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            if(ServiceUtil.isError(results)){
                results = ServiceUtil.returnError("Unable To Calculate Tax");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
        }
        return results;
    }
    
    public static Long getFrequency(GenericValue shoppingList){
        Long frequency = Long.parseLong("0");
        try{
            GenericValue recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
            GenericValue recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule");
            frequency = recurrenceRule.getLong("intervalNumber");
        }
        catch (Exception e){
            Debug.logError(e.getMessage(), MODULE);
        }
        return frequency;
    }
    
}
