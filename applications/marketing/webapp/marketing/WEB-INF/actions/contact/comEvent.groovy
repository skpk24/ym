import  org.ofbiz.marketing.marketing.AutoEmailServices;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.*;


condition=EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS");
ItemTypes = delegator.findList("ContactList",condition,null,null,null, false);
if(ItemTypes != null)
                contactListName=ItemTypes.contactListName;
                
context.contactListName=contactListName
context.ItemTypes=ItemTypes
print("the contact list\n\n\n\n\n"+ItemTypes);



ItemTypesTem = delegator.findList("MarketingTemplate",null,null,null,null, false);
if(ItemTypesTem != null)
               context.ItemTypesTem=ItemTypesTem
                
context.contactListName=contactListName
context.ItemTypes=ItemTypes

print("the contact list\n\n\n\n\n"+ItemTypes);
communicationEventId=parameters.communicationEventId
if(communicationEventId!=null)
{
context.communicationEventId=communicationEventId
              contactListId = parameters.contactListId     
      context.contactListId=contactListId
templateId=parameters.templateId
if(templateId!="0")
context.templateId=templateId
context.subject=parameters.subject
context.content=parameters.content
if(parameters.templateId!="0")
{
gvTemp=delegator.findByPrimaryKey("MarketingTemplate",UtilMisc.toMap("templateId",templateId))
if(gvTemp.get("templateData")!=null)
{
String tempContent=gvTemp.get("templateData");
context.tempContent=tempContent;
}
}
else
context.tempContent=parameters.content



gv=delegator.findByPrimaryKey("ContactList",UtilMisc.toMap("contactListId",contactListId))


    ownerParty = gv.get("ownerPartyId");
  if(ownerParty!=null)
  {
        context.contactMechIdFrom =ownerParty;
        context.partyIdFrom=ownerParty;
    }
    




}

