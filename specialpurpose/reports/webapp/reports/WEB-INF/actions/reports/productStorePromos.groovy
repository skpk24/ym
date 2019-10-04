import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


String productStoreId = "";

productStoreId = parameters.get("productStoreId");
if(productStoreId == null)
	productStoreId = session.getAttribute("productStoreId");

List storePromos = new ArrayList();
storePromos = delegator.findByAnd("ProductStorePromoAppl",UtilMisc.toMap("productStoreId",productStoreId));
//println("productStoreId = "+productStoreId);

if(storePromos != null && storePromos.size()>0){
	List promoIds = EntityUtil.getFieldListFromEntityList(storePromos, "productPromoId", true);
	context.productPromos = delegator.findByCondition("ProductPromo",EntityCondition.makeCondition("productPromoId", EntityOperator.IN ,promoIds),null,null);
}