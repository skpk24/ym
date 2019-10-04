import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;


String screenName="SIGN_IN";
List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
conditionList.add(EntityCondition.makeCondition("partyId", context.userLogin.partyId));
EntityCondition entityCondition = EntityCondition.makeCondition(conditionList);

attenList = delegator.findList("EmplAttendanceRecord", entityCondition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
if(attenList!=null && attenList.size()>0){
   Gv=attenList.get(0);
if(Gv.getString("status")!=null && Gv.getString("status")=="SIGN_OUT"){
	screenName="SIGN_IN";
 }else{
	 screenName="SIGN_OUT";
 }
}
context.screenName=screenName;