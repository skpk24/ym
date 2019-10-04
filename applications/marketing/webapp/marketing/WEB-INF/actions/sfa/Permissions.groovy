import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.workeffort.workeffort.*;
import org.ofbiz.base.util.*;




       securityGrp = delegator.findList("UserLoginSecurityGroup",
                                              EntityCondition.makeCondition([EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"),
                                              EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.userLoginId)], EntityOperator.AND), 
										      null, null, null, false);
		
	  if(!UtilValidate.isEmpty(securityGrp)) context.permission = "Y"		
	  
	  
	  //Recently view by logged in user
	  
	  						  
userLoginId =  userLogin.userLoginId ;

lastViewed = new ArrayList();
lastViewed = delegator.findList("ServerHit",
	EntityCondition.makeCondition([EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS,  userLoginId ),
	EntityCondition.makeCondition("referrerUrl", EntityOperator.NOT_LIKE, "%logout%")], EntityOperator.AND),
	null, null, null, false);
urlList = []
listSize = lastViewed.size() ;
int i = 0 ;
if(lastViewed.size()>10){
   while(true){
	   listSize = listSize-1;
	   i = i + 1 ;
		urls = [:] ;
		String s = lastViewed.get(listSize).contentId ;
		s = s.substring(s.indexOf('.')+1, s.length())
		urls.put("requestUrl",lastViewed.get(listSize).requestUrl);
		urls.put("contentId", s );
		urlList.add(urls);
		if(i>=10) break ;
	   }
   context.urlList = urlList ;
}else{
context.urlList = lastViewed ;
}
context.userLoginId = userLoginId ;

partyAndUserLogin = EntityUtil.getFirst(delegator.findList("PartyAndUserLogin",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false)) ;



facilityId = parameters.get("facilityId");
fixedAssetId = parameters.get("fixedAssetId");
partyId = parameters.get("partyId");
workEffortTypeId = parameters.get("workEffortTypeId");
calendarType = parameters.calendarType;
start = nowTimestamp.clone();

eventsParam = "";
if (facilityId != null) {
	eventsParam = "facilityId=" + facilityId;
}
if (fixedAssetId != null) {
	eventsParam = "fixedAssetId=" + fixedAssetId;
}
if (partyId != null) {
	eventsParam = "partyId=" + partyId;
}
if (workEffortTypeId != null) {
	eventsParam = "workEffortTypeId=" + workEffortTypeId;
}

Map serviceCtx = UtilMisc.toMap("userLogin", userLogin, "start", start, "numPeriods", 1, "periodType", Calendar.DATE);
serviceCtx.putAll(UtilMisc.toMap("partyId", partyId, "facilityId", facilityId, "fixedAssetId", fixedAssetId, "workEffortTypeId", workEffortTypeId, "calendarType", calendarType, "locale", locale, "timeZone", timeZone));

Map result = dispatcher.runSync("getWorkEffortEventsByPeriod",serviceCtx);


previousParams = session.getAttribute("_PREVIOUS_PARAMS_");

view = delegator.findList("ServerHit",null,null,null,null,false);


context.put("days", result.get("periods"));
context.put("start", start);
context.put("eventsParam", eventsParam);


									 
											 

