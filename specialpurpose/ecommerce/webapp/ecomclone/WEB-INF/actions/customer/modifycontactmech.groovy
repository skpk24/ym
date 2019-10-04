import java.lang.*;
import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.accounting.payment.PaymentWorker;


//partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, userLogin.partyId, true);


if(!UtilValidate.isEmpty(request.getParameter("emailcontactMechId")))
{
emailcontact=request.getParameter("emailcontactMechId");
postalcontactMechId=request.getParameter("addressContactMech");
telecontactMechId=request.getParameter("telecontactMechId");


shipAddress = delegator.findByPrimaryKey("PostalAddress", [contactMechId :postalcontactMechId]);
telePhone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : telecontactMechId]);
email = delegator.findByPrimaryKey("ContactMech", [contactMechId : emailcontact]);


context.emailid=emailcontact
context.postalid=postalcontactMechId
context.teleid=telecontactMechId
context.contactMech=email;
context.telecomNumberData=telePhone;
context.postalAddressData=shipAddress;
}


if(!UtilValidate.isEmpty(request.getParameter("emailId")))
	
{
	if(!UtilValidate.isEmpty(request.getParameter("postalId")))
	{
	String contactMechId = request.getParameter("postalId");
	
	String toName = request.getParameter("toName");
	String address1 = request.getParameter("address1");
	String address2 = request.getParameter("address2");
	String area = request.getParameter("area");
	String directions = request.getParameter("directions");
	String city = request.getParameter("city");
	String postalCode = request.getParameter("postalCode");
	String countryGeoId = request.getParameter("countryGeoId");
	String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
	
	if(UtilValidate.isEmpty(stateProvinceGeoId)){
		stateProvinceGeoId = null;
	}

	gv = delegator.findByPrimaryKey("PostalAddress", [contactMechId :contactMechId]);
	if(!UtilValidate.isEmpty(gv))
	{
	gv.set("toName", toName);
	gv.set("address1", address1);
	gv.set("address2", address2);
	gv.set("area", area);
	gv.set("directions", directions);
	gv.set("city", city);
	gv.set("postalCode", postalCode);
	gv.set("countryGeoId", countryGeoId);
	gv.set("stateProvinceGeoId", stateProvinceGeoId);
	gv.store();
	}
	context.postalid=contactMechId
	}
	
	
	if(!UtilValidate.isEmpty(request.getParameter("emailId")))
	{
	
	String contactMechId = request.getParameter("emailId");
	String emailAddress = request.getParameter("emailAddress");
	
	

	gv = delegator.findByPrimaryKey("ContactMech", [contactMechId :contactMechId]);
	if(!UtilValidate.isEmpty(gv))
	{
	gv.set("infoString", emailAddress);

	gv.store();
	}
	context.contactMech=contactMechId;
	}
	if(!UtilValidate.isEmpty(request.getParameter("teleId")))
	{
	String contactMechId = request.getParameter("teleId");
	String contactNumber = request.getParameter("contactNumber");
	
	

	gv = delegator.findByPrimaryKey("TelecomNumber", [contactMechId :contactMechId]);
	if(!UtilValidate.isEmpty(gv))
	{
	gv.set("contactNumber", contactNumber);

	gv.store();
	}
	context.teleid=contactMechId
	}
	shipAddress = delegator.findByPrimaryKey("PostalAddress", [contactMechId :context.postalid]);
	telePhone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : context.teleid]);
	email = delegator.findByPrimaryKey("ContactMech", [contactMechId : context.contactMech]);
	
	context.contactMech=email;
	context.telecomNumberData=telePhone;
	context.postalAddressData=shipAddress;

	

}

if(UtilValidate.isNotEmpty(request.getParameter("emailcontactMechId")))
{

partyBillValue  = delegator.findList("PartyContactMech", EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, postalcontactMechId), null, null, null, false);

gv=EntityUtil.getFirst(partyBillValue);

if(UtilValidate.isNotEmpty(gv))
{
	Timestamp d1=UtilDateTime.nowTimestamp();
	exprList1 = [];
	expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,gv.partyId);
	
	exprList1.add(expr);
	
	expr = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
exprList1.add(expr);
topCond1 = EntityCondition.makeCondition(exprList1, EntityOperator.AND);

conId = delegator.findList("PartyContactMech",topCond1 , null, null, null, false);
ConList=EntityUtil.getFieldListFromEntityList(conId, "contactMechId", true);

exprList = [];
expr = EntityCondition.makeCondition("contactMechId", EntityOperator.IN,ConList);

exprList.add(expr);
expr = EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS");
exprList.add(expr);

topCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

contactList=delegator.findList("ContactMech",topCond , null, null, null, false);


if(contactList.size()<=1)
flag1="false";
else
flag1="true";

}
else
flag1="false";

context.flag1=flag1;

}

return "success";







