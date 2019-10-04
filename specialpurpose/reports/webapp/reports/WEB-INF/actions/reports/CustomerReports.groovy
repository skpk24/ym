import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.order.report.Report;

delegator = request.getAttribute("delegator");

security = request.getAttribute("security");

// create the fromDate for calendar

fromCal = Calendar.getInstance();

fromCal.setTime(new java.util.Date());

//fromCal.set(Calendar.DAY_OF_WEEK, fromCal.getActualMinimum(Calendar.DAY_OF_WEEK));

fromCal.set(Calendar.DATE, fromCal.getActualMinimum(Calendar.DATE));
fromCal.set(Calendar.HOUR_OF_DAY, fromCal.getActualMinimum(Calendar.HOUR_OF_DAY));
fromCal.set(Calendar.MINUTE, fromCal.getActualMinimum(Calendar.MINUTE));
fromCal.set(Calendar.SECOND, fromCal.getActualMinimum(Calendar.SECOND));
fromCal.set(Calendar.MILLISECOND, fromCal.getActualMinimum(Calendar.MILLISECOND));
fromTs = new Timestamp(fromCal.getTimeInMillis());
fromStr = fromTs.toString();
fromStr = fromStr.substring(0, fromStr.indexOf('.'));
context.put("fromDateStr", fromStr);

// create the thruDate for calendar
toCal = Calendar.getInstance();
toCal.setTime(new java.util.Date());
//toCal.set(Calendar.DAY_OF_WEEK, toCal.getActualMaximum(Calendar.DAY_OF_WEEK));
toCal.set(Calendar.HOUR_OF_DAY, toCal.getActualMaximum(Calendar.HOUR_OF_DAY));
toCal.set(Calendar.MINUTE, toCal.getActualMaximum(Calendar.MINUTE));
toCal.set(Calendar.SECOND, toCal.getActualMaximum(Calendar.SECOND));
toCal.set(Calendar.MILLISECOND, toCal.getActualMaximum(Calendar.MILLISECOND));
toTs = new Timestamp(toCal.getTimeInMillis());
toStr = toTs.toString();
context.put("thruDateStr", toStr);

delegator = request.getAttribute("delegator");
security = request.getAttribute("security");
String fromDate = parameters.get("minDate");
String thruDate = parameters.get("maxDate");
String viewReport = parameters.get("viewReport");
if(viewReport != null && viewReport.equalsIgnoreCase("true"))
	context.put("viewReport",viewReport);
if(fromDate != null)
	context.put("fromDateStr", fromDate);
if(thruDate != null)
	context.put("thruDateStr", thruDate);

List registrations = new ArrayList();
// Get orders from DB ...
if(viewReport != null)
		try {

			List dateCondiList = new ArrayList();
			if((ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)) != null)
			dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			if((ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)) != null)
			dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.NOT_EQUAL, null));
			EntityConditionList dateCondition = null;
				if(dateCondiList.size()>0)
				dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

			List mainExprs = new ArrayList();
			if(dateCondition != null)
			mainExprs.add(dateCondition);
			mainExprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
			EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
			try{
			//registrations = delegator.findByCondition("PartyAndUserLoginAndPerson",mainCondition,null,UtilMisc.toList("createdDate DESC") );
			registrations = delegator.findByCondition("Person",null,null,null );
			}catch(Exception e){
				e.printStackTrace();
			}
			List registrationsList = new ArrayList();
			Iterator registrationsItr = registrations.iterator();
			while(registrationsItr.hasNext()) {
				GenericValue gv = (GenericValue) registrationsItr.next();
				if(Report.hasPartyRoles(gv.getString("partyId"), "VISITOR", request) || Report.hasPartyRoles(gv.getString("partyId"), "SPONSOR", request)){
				}else{
					Map partyDetailsMap =  (Map)org.ofbiz.party.party.PartyHelper.getShippingContactdeatils(delegator, gv.getString("partyId"));
				    Map resistration = new HashMap();
				    resistration.put("createdDate", gv.getString("createdDate"));
				    resistration.put("partyId", gv.getString("partyId"));
				    
				    if(gv.getString("lastName")!=null)
				    resistration.put("PartyName", gv.getString("firstName") + " " + gv.getString("lastName"));
				    else
				    	 resistration.put("PartyName", gv.getString("firstName"));
				    resistration.put("userLoginId", gv.getString("userLoginId"));
				    String telephoneHome = "", emailId = "", postalAddress = "";
				    if(partyDetailsMap != null){
				    	if(partyDetailsMap.get("telephoneHome") != null){
				    		if(partyDetailsMap.telephoneHome.countryCode != null && !partyDetailsMap.telephoneHome.countryCode.equals(""))
				    			telephoneHome = telephoneHome+partyDetailsMap.telephoneHome.countryCode;
				    		if(partyDetailsMap.telephoneHome.areaCode != null && !partyDetailsMap.telephoneHome.areaCode.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneHome.areaCode;
				    		if(partyDetailsMap.telephoneHome.contactNumber != null && !partyDetailsMap.telephoneHome.contactNumber.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneHome.contactNumber;
				    	}
				    	if(partyDetailsMap.get("telephoneWork")!=null && (telephoneHome.length()==0)){
				    		if(partyDetailsMap.telephoneWork.countryCode != null && !partyDetailsMap.telephoneWork.countryCode.equals(""))
				    			telephoneHome = telephoneHome+partyDetailsMap.telephoneWork.countryCode;
				    		if(partyDetailsMap.telephoneWork.areaCode != null && !partyDetailsMap.telephoneWork.areaCode.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneWork.areaCode;
				    		if(partyDetailsMap.telephoneWork.contactNumber != null && !partyDetailsMap.telephoneWork.contactNumber.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneWork.contactNumber;				    		
				    	}
				    	if(partyDetailsMap.get("telephoneMobile")!=null && (telephoneHome.length()==0)){
				    		if(partyDetailsMap.telephoneMobile.countryCode != null && !partyDetailsMap.telephoneMobile.countryCode.equals(""))
				    			telephoneHome = telephoneHome+partyDetailsMap.telephoneMobile.countryCode;
				    		if(partyDetailsMap.telephoneMobile.areaCode != null && !partyDetailsMap.telephoneMobile.areaCode.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneMobile.areaCode;
				    		if(partyDetailsMap.telephoneMobile.contactNumber != null && !partyDetailsMap.telephoneMobile.contactNumber.equals(""))
				    			telephoneHome = telephoneHome + "-" + partyDetailsMap.telephoneMobile.contactNumber;				    		
				    	}
				    	emailId = partyDetailsMap.get("primaryEmail");
				    	if(partyDetailsMap.get("postalShippingAddress") != null){
				    		if(partyDetailsMap.postalShippingAddress.address1 != null)
				    		postalAddress = postalAddress + partyDetailsMap.postalShippingAddress.address1;
				    		if(partyDetailsMap.postalShippingAddress.address2 != null)
				    		postalAddress = postalAddress + " " + partyDetailsMap.postalShippingAddress.address2;
				    		if(partyDetailsMap.postalShippingAddress.city != null)
				    		postalAddress = postalAddress + " " + partyDetailsMap.postalShippingAddress.city;
				    		if(partyDetailsMap.postalShippingAddress.countryGeoId != null)
				    		postalAddress = postalAddress + " " + partyDetailsMap.postalShippingAddress.countryGeoId;
				    	}
				    	resistration.put("phoneNumber", telephoneHome);
		    			resistration.put("emailId", emailId);
						resistration.put("postalAddress", postalAddress);
				}
				    registrationsList.add(resistration);
			}
			registrations = registrationsList;
			}
			context.put("registrations", registrations);
		} catch (Exception gee) {
			gee.printStackTrace();
			print("CustomerReports.groovy:143:Info: Unable to find registrations report." + gee);
		}
		if( fromDate!= null && fromDate.length()>0){
			context.put("fromDate", fromDate.substring(0, fromDate.lastIndexOf(" ")) );
			context.put("fromDateStr", fromDate);
			}
		if( thruDate!= null && thruDate.length()>0){
			context.put("thruDate", thruDate.substring(0,thruDate.lastIndexOf(" ")));
			context.put("thruDateStr", thruDate);
			}
	//pagination
if(registrations != null)
	listSize = registrations.size();
else listSize = 0;
viewSize = parameters.get("VIEW_SIZE");
viewIndex = parameters.get("VIEW_INDEX");
// set the default view size
defaultViewSize = request.getAttribute("defaultViewSize");
if (defaultViewSize == null) {
    defaultViewSize = new java.lang.Integer(10);
}

if(viewSize == null) viewSize = defaultViewSize;
else viewSize = Integer.parseInt(viewSize);
if(viewIndex == null) viewIndex = 1;
else viewIndex = Integer.parseInt(viewIndex);
	int lowIndex = (viewIndex-1) * viewSize + 1;
	int highIndex = (lowIndex + viewSize) -1;
	if (listSize < highIndex) highIndex = listSize;

context.put("viewIndex", viewIndex);
context.put("viewSize", viewSize);
context.put("lowIndex", lowIndex);
context.put("highIndex", highIndex);
context.put("listSize", listSize);

