import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.sql.Timestamp;




//set the page parameters
int viewIndex = 0;
int viewSize = 0;
int highIndex = 0;
int lowIndex = 0;
int sortResultListSize = 0;
    try {
           viewIndex = Integer.valueOf((String) parameters.get("viewIndex")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }
    try {
        viewSize = Integer.valueOf((String) parameters.get("viewSize")).intValue();
    } catch (Exception e) {
        viewSize = 10;
    }
lowIndex = (viewIndex * viewSize)+1;
highIndex = (viewIndex + 1) * viewSize; 


List contactListPartyList = new ArrayList();
int listCount = 0;
EntityListIterator eli = null;

try{
    eli = delegator.findListIteratorByCondition("ContactListParty", EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null), null, UtilMisc.toList("-fromDate"));
    
    // attempt to get the full size
    eli.last();
    listCount = eli.currentIndex();

    // get the partial list for this page
    eli.beforeFirst();
    if (listCount > viewSize.intValue()) {
    	contactListPartyList = eli.getPartialList(lowIndex, viewSize.intValue());
    } else if (listCount > 0) {
    	contactListPartyList = eli.getCompleteList();
    }
    if (highIndex > listCount) {
        highIndex = listCount;
    }
} catch (GenericEntityException e) {
    e.printStackTrace();
} finally {
    if (eli != null) {
        try {
            eli.close();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
}


//contactListPartyList = delegator.findByCondition("ContactListParty",EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null), null, UtilMisc.toList("-fromDate") );
// show all, including history, ie don't filter: contactListPartyList = EntityUtil.filterByDate(contactListPartyList, true);
//context.contactListPartyList = contactListPartyList;

List subscriptionList = new ArrayList();
Iterator itr = contactListPartyList.iterator();
while(itr.hasNext()){
	GenericValue contactListParty = itr.next();
	GenericValue contactList = (GenericValue) contactListParty.getRelatedOne("ContactList");
	GenericValue statusItem = (GenericValue) contactListParty.getRelatedOne("StatusItem");
	GenericValue emailAddress = (GenericValue) contactListParty.getRelatedOne("PreferredContactMech");
	
	String contactListName = "", status = "", emailId = "", date = null;
	
	if(contactList!= null)
	contactListName = (String) contactList.getString("contactListName");
	if(contactListParty!= null)
	date = (String) contactListParty.getString("fromDate");
	if(statusItem!= null)
	status = (String) statusItem.getString("description");
	if(emailAddress!= null)
	emailId = (String) emailAddress.getString("infoString");
	Map subscription = new HashMap();
	subscription.put("contactListName", contactListName);
	subscription.put("date", date);
	subscription.put("status", status);
	subscription.put("emailId", emailId);
	
	subscriptionList.add(subscription);
}

context.put("subscriptionList",subscriptionList);
context.put("viewIndex", viewIndex);
context.put("viewSize", viewSize);
context.put("listSize", listCount);
context.put("lowIndex", lowIndex);
context.put("highIndex", highIndex);
