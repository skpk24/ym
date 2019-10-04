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
import org.ofbiz.entity.condition.*;

Date date = new Date();

Integer intDay = new Integer(date.getDate());
Integer intMonth = new Integer(date.getMonth()+1);
Integer intYear = new Integer(date.getYear()+1900);
String fromDate = null;
String thruDate  = null;
String  day = intDay.toString();
String  month = intMonth.toString();
String  year = intYear.toString();
	
if(fromDate == null)
	fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
if(thruDate == null)
	thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";

List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();


dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.GREATER_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
EntityCondition entityCondition = EntityCondition.makeCondition(dateCondiList);
attenList = delegator.findList("EmplAttendanceRecord", entityCondition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
context.attenList=attenList;










