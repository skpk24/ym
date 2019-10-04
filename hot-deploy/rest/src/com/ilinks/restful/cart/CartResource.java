package com.ilinks.restful.cart;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.order.finaccount.FinAccountHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.service.ServiceUtil;
//import org.ofbiz.ecommerce.cart.CartEvents;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import javax.servlet.http.HttpSession;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;

@Path("/cart")
public class CartResource extends OFBizRestfulBase{
    private ShoppingCart cart;
    /**
    private GenericValue value;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
    public CartResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        cart = null;
    }
    
    
    /**
     * add items to cart, one to many
     * @param callbackFunction
     * @param items -- products to add
     * @return
     */
   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
  
    public Response addProductsToCart(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("items") String items){
        super.initRequestAndDelegator();
        try{
            cart = ShoppingCartEvents.getCartObject(request);
            JSONArray products = (JSONArray)JSONSerializer.toJSON(items);
            for(int index = 0; index < products.size(); index++){
                JSONObject product = products.getJSONObject(index);
                BigDecimal maxQuantity = new BigDecimal(product.getString("maxQuantity"));
                
                List<ShoppingCartItem> itemList = cart.findAllCartItems(product.getString("productID"));
                
                BigDecimal productQuantity = new BigDecimal(product.getInt("quantity"));
                
                for(ShoppingCartItem item : itemList){
                    if(item.getProductId().equals(product.getString("productID"))){
                        productQuantity  = productQuantity.add(item.getQuantity());
                    }
                }
               
                if(productQuantity.compareTo(maxQuantity) > 0){
                    jsonMap.put(properties.getProperty("ERROR","error"), "Items Added Must Be Less Than " + maxQuantity);
                    break;
                }
              
                request.setAttribute("add_product_id",product.getString("productID"));
                request.setAttribute("quantity", product.getString("quantity"));
                request.setAttribute("frequency",product.getString("frequency"));
                ShoppingCartEvents.addToCart(request, response);  
            }
            
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + "items " + items + " "  + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable To Add To Cart");
        }
        finally{
            ProductPromoWorker.doPromotions(cart, dispatcher);
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * update cart, one item at a time
     * @param callbackFunction
     * @param quantityText -- product quantity
     * @param cartItemIndex -- cart item to update
     * @return
     */
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response modifyCart(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("quantityText") String quantityText,  @QueryParam("cartItemIndex") String cartItemIndex){
        super.initRequestAndDelegator();
        try{
           // updateCart will retrieve the quantityText and cartItemIndex via the request object
            String result = null;//CartEvents.updateCart(request, response);
            if(UtilValidate.isNotEmpty(result) && result.equals("error")){
                jsonMap.put(properties.getProperty("ERROR","error"),  " Unable Modify Cart");
            }
            else{
                jsonMap.put(properties.getProperty("SUCCESS","success"), "Successfully Added To Cart cartItemIndex " + cartItemIndex + " quantity " + quantityText);
            }
            
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + "quantity " + quantityText + " cartItemIndex " + cartItemIndex + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable Modify Cart");
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * update product frequency 
     * @param callbackFunction
     * @param frequency
     * @param cartItemIndex -- cart item to update
     * @return
     */
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/frequency")
    public Response modifyFrequency(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("frequency") String frequency,  @QueryParam("cartItemIndex") String cartItemIndex){
        super.initRequestAndDelegator();
        try{
            // changeFrequency will use the frequency and cartItemIndex through the request object
            String result = null;//CartEvents.changeFrequency(request, response);
            if(UtilValidate.isNotEmpty(result) && result.equals("error")){
                jsonMap.put(properties.getProperty("ERROR","error"),  " Unable Modify Cart");
            }
            else{
                jsonMap.put(properties.getProperty("SUCCESS","success"), "Successfully Updated Frequency  cartItemIndex " + cartItemIndex + " frequency " + frequency);
            }
            
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " frequency " + frequency + " cartItemIndex " + cartItemIndex + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable Modify Cart");
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/frequencies")
    public Response modifyFrequencies(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("frequency") String frequency){
        super.initRequestAndDelegator();
        try{
            /// changeFrequency will use the frequency and cartItemIndex through the request object
            String result = null;//CartEvents.setEmptyCartItemFrequencies(request, response);
            if(UtilValidate.isNotEmpty(result) && result.equals("error")){
                jsonMap.put(properties.getProperty("ERROR","error"),  " Unable Modify Cart");
            }
            else{
                jsonMap.put(properties.getProperty("SUCCESS","success"), "Successfully Updated Frequency frequency " + frequency);
            }
            
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " frequency " + frequency + " " + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable Modify Cart");
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    
    
    
    
   /**
    * insert promotions and gift card
    * @param callbackFunction
    * @param promotion
    * @return
    */
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/promotion")
    public Response addPromotionOrGiftCard(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("promotion")  String promotion){
        super.initRequestAndDelegator();
        try{
            HttpSession session = request.getSession();
            cart = ShoppingCartEvents.getCartObject(request);
            String giftCardNumber = (String)session.getAttribute("giftCardNumber");
            promotion = promotion.toLowerCase().trim();
            Debug.log("promo entered " + cart.getProductPromoCodesEntered().size());
            if (UtilValidate.isNotEmpty(giftCardNumber) && giftCardNumber.equalsIgnoreCase(promotion) || cart.getProductPromoCodesEntered().size() > 0){
                jsonMap.put(properties.getProperty("ERROR","error"), "Only One Gift Card Per Order");
            }
            else{
                jsonMap.put(properties.getProperty("SUCCESS","success"), "Gift Card / Promotion " + promotion + " Added Successfully");
               
                GenericValue finAccount = FinAccountHelper.getFinAccountFromCode(promotion, delegator);
                if (UtilValidate.isNotEmpty(finAccount) && !"FNACT_CANCELLED".equals(finAccount.getString("statusId")) && !"FNACT_EXPIRE".equals(finAccount.getString("statusId"))) {
                    String finAccountId = finAccount.getString("finAccountId");
                    GenericValue systemUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
                    
                    Map<String, Object> finAccountContext = FastMap.newInstance();
                    finAccountContext.put("finAccountId", finAccountId);
                    finAccountContext.put("userLogin", systemUserLogin);
                    Map<String, Object> finAccountResult = dispatcher.runSync("checkFinAccountBalance", finAccountContext);
                    if (!ServiceUtil.isSuccess(finAccountResult)) {
                        jsonMap.clear();
                        jsonMap.put(properties.getProperty("ERROR","error"), "Unable To Find Gift Card " + promotion);
                    }
                    else{
                        // valid gift card
                        BigDecimal availableBalance = (BigDecimal)finAccountResult.get("availableBalance");
                        session.setAttribute("giftCardFinAccountId", finAccountId);
                        session.setAttribute("giftCardNumber", promotion);
                        session.setAttribute("giftCardBalance", availableBalance);
                        session.setAttribute("giftCardName", finAccount.getString("finAccountName"));
                    }
                }
                else{
                    // promotions 
                    
                    String error = cart.addProductPromoCode(promotion, dispatcher);
                    ProductPromoWorker.doPromotions(cart, dispatcher);
                    Debug.log("adding coupong " + error);
                    if(error != null){
                        jsonMap.clear();
                        jsonMap.put(properties.getProperty("ERROR","error"), "Unable To Add Promotion " + promotion + " " + error);
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " promotion " + promotion +  e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable To Add Promotion " + promotion);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    /**
     * remove gift card or promotion 
     * @param callbackFunction
     * @param promotion
     * @return
     */
    
  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update/promotion")
    public Response removePromotionOrGiftCard(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("promotion")  String promotion){
        super.initRequestAndDelegator();
        try{
            HttpSession session = request.getSession();
            cart = ShoppingCartEvents.getCartObject(request);
            String giftCardNumber = (String)session.getAttribute("giftCardNumber");
            promotion = promotion.trim();
            if (UtilValidate.isNotEmpty(giftCardNumber) && promotion.equalsIgnoreCase(giftCardNumber)){
                session.removeAttribute("giftCardFinAccountId");
                session.removeAttribute("giftCardNumber");
                session.removeAttribute("giftCardBalance");
                session.removeAttribute("giftCardName");
            }
            else{
                // check for promo
                Set<String> list = cart.getProductPromoCodesEntered();
                
                if(list.contains(promotion)){
                    list.remove(promotion);
                }
                else{
                    jsonMap.put(properties.getProperty("ERROR","error"), "Unable To Find Promotion " + promotion);
                }
            }
            ProductPromoWorker.doPromotions(cart, dispatcher);
           
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " promotion " + promotion +  e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable To Update Promotion " + promotion);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    

    /**
     * retrieve the cart content
     * @param callbackFunction
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public Response getProductsFromCart(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        cart = ShoppingCartEvents.getCartObject(request);
        FastList<Object> list = FastList.newInstance();
        FastMap<Object,Object> map = null;
        GenericValue product = null;
        for(GenericValue value : cart.getAdjustments()){
            for(String key : value.keySet()){
                Debug.log(" cart adjustment key " + key + " " + value.getString(key));
            }
        }
        try{
            List<ShoppingCartItem> cartList = cart.items();
            BigDecimal totalItemAdjustments = BigDecimal.ZERO;
            for (ShoppingCartItem cartItem : cartList) {
               
                //BigDecimal adjustmentAmount = BigDecimal.ZERO;
                map = FastMap.newInstance();
                map.put("productID", cartItem.getProductId());
                map.put("cartItemIndex", cartList.indexOf(cartItem));
                map.put("quantity", cartItem.getQuantity());
//                map.put("frequency", cartItem.getFrequency());
                map.put("basePrice", cartItem.getBasePrice());
                map.put("displayPrice", cartItem.getDisplayPrice());
                map.put("subTotal", cartItem.getItemSubTotal());
                map.put("specialPromoPrice", cartItem.getSpecialPromoPrice());
                map.put("name", cartItem.getName());
                
                product = cartItem.getProduct(); 
                List<GenericValue> featureList = product.getRelatedCache("ProductFeatureAppl");
                featureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "PRODUCT_VARIATION"),featureList);
                GenericValue feature = null;
                if(UtilValidate.isNotEmpty(featureList)){
                    feature = EntityUtil.getFirst(featureList);
                }
                else{
                    featureList = product.getRelatedCache("ProductFeatureAppl");
                    featureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "STAND_ALONE"),featureList);
                    feature = EntityUtil.getFirst(featureList);
                }
                map.put("feature",feature.getString("description"));
                product = cartItem.getParentProduct();
                product = product != null ? product : cartItem.getProduct();
                map.put("brandName", product.getString("brandName"));
                map.put("productName", product.getString("productName"));
                map.put("smallImageURL", product.getString("smallImageUrl"));
                map.put("parentProductID", product.getString("productId"));
                
                for(GenericValue attribute : product.getRelatedCache("ProductAttribute")){
                    map.put(attribute.getString("attrName"), attribute.getString("attrValue"));
                }
                List<GenericValue> adjustmentList = cartItem.getAdjustments();
                for (GenericValue adjustment : adjustmentList) {
                    for (String key : adjustment.keySet()) {
                        Debug.log(cartItem.getProductId() + " -- cartItem "+ " key " + key + " " + adjustment.getString(key));
                    }
                }
                map.put("adjustments", cartItem.getOtherAdjustments());
                totalItemAdjustments = totalItemAdjustments.add(cartItem.getOtherAdjustments());
                list.add(map);
            }
            Object [] codes = cart.getProductPromoCodesEntered().toArray();
            List<Object> promotionList = FastList.newInstance();
            for(Object obj : codes){
                map = FastMap.newInstance();
                GenericValue productPromoCode = delegator.findByPrimaryKeyCache("ProductPromoCode", UtilMisc.toMap("productPromoCodeId", obj));
                GenericValue productPromo = productPromoCode.getRelatedOneCache("ProductPromo");
                map.put("code", obj);
                map.put("amount",  cart.getProductPromoUseTotalDiscount(productPromo.getString("productPromoId")));
                map.put("description",  productPromo.getString("promoText"));
                map.put("productPromoCode",  productPromo.getString("productPromoId"));
                promotionList.add(map);
            }
            HttpSession session = request.getSession();
            if(session.getAttribute("giftCardFinAccountId") != null){
                // gift card exists
                map = FastMap.newInstance();
                map.put("code", session.getAttribute("giftCardNumber"));
                map.put("amount", session.getAttribute("giftCardBalance"));
                map.put("description", session.getAttribute("giftCardName"));
                map.put("productPromoCode", session.getAttribute("giftCardFinAccountId"));
                promotionList.add(map);
                jsonMap.put("giftCardAmount", session.getAttribute("giftCardBalance"));
            }
            
            jsonMap.put("promotions", promotionList);
            jsonMap.put("cartAdjustments",cart.getOrderOtherAdjustmentTotal());
            jsonMap.put("itemAdjustments",totalItemAdjustments);
            //jsonMap.put("quantity", list.size());
            jsonMap.put("quantity", cart.getTotalQuantity());
            jsonMap.put("saleTax", cart.getTotalSalesTax());
            jsonMap.put("shippingTax", cart.getTotalShipping());
            jsonMap.put("shippingAmount", cart.getTotalShipping());
            jsonMap.put("total", cart.getGrandTotal());
            jsonMap.put("subTotal", cart.getSubTotal());
            jsonMap.put("items",  list);
        }
        catch (Exception e) {
            Debug.logError(e, e.getClass().getName()  + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"), "Unable To Retrieve Items From Cart");
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    } 
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tax")
    public Response calculateOrderTax(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("shippingContactMechID") String shippingContactMechID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            cart = ShoppingCartEvents.getCartObject(request);
            //cart.setShippingContactMechId(shippingContactMechID);
            Map<String,Object> results = dispatcher.runSync("calculateOrderTax", UtilMisc.toMap("cart", cart,"userLogin",userLogin));
            if(ServiceUtil.isSuccess(results)){
                jsonMap.put("tax", results.get("tax"));
            }
            else{
                jsonMap.put(ERROR, "Unable To Calculate Tax");
            }
            
        } catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " Unable To Calculate Tax" + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " Unable Modify Cart");
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
   
}