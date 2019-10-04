/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.ofbiz.party.party;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;

/**
 * PartyHelper
 */
public class PartyHelper {

    public static final String module = PartyHelper.class.getName();

    public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        GenericValue partyObject = null;
        try {
            partyObject = delegator.findByPrimaryKey("PartyNameView", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }

    public static String getPartyName(GenericValue partyObject, boolean lastNameFirst) {
        if (partyObject == null) {
            return "";
        }
        if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
            return formatPartyNameObject(partyObject, lastNameFirst);
        } else {
            String partyId = null;
            try {
                partyId = partyObject.getString("partyId");
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Party object does not contain a party ID", module);
            }

            if (partyId == null) {
                Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), module);
                return "";
            } else {
                return getPartyName(partyObject.getDelegator(), partyId, lastNameFirst);
            }
        }
    }

    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst) {
                if (UtilFormatOut.checkNull(partyValue.getString("lastName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
                    if (partyValue.getString("firstName") != null) {
                        result.append(", ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
            } else {
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("middleName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }
    
    public static Map  getShippingContactdeatils(GenericDelegator delegator, String partyId)
	{
		// 1. Get Party Contact Details - EMAIL_ADDRESS, TELECOM_NUMBER, POSTAL_ADDRESS
		Map partyDetailsMap = new HashMap();
		try 
		{
			//partyDetailsMap.put("person", delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId)));
			List partyContactMechList = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId));
			if (partyContactMechList != null && partyContactMechList.size() > 0) 
			{
				Iterator partyContactMechListIterator = partyContactMechList.iterator();
				GenericValue contactMechGV = null;
				while (partyContactMechListIterator.hasNext()) 
				{
					contactMechGV = ((GenericValue)partyContactMechListIterator.next()).getRelatedOne("ContactMech");
					processContactDetails(delegator, partyDetailsMap, (String)contactMechGV.get("contactMechTypeId"), contactMechGV);
				}
			}
		}
		catch (GenericEntityException geeX) 
		{
			Debug.logError(geeX, "Failed to get PartyContactMech details for partyId:[" + partyId+ "] with message:[" + geeX.getMessage() +"]", module);
		}
		return partyDetailsMap;
	}  
    
	private static void processContactDetails(GenericDelegator delegator, Map partyDetailsMap, String contactMechTypeId, GenericValue contactMechGV) 
	{
		try
		{
			String contactMechId=(String)contactMechGV.get("contactMechId");
			if ("TELECOM_NUMBER".equalsIgnoreCase(contactMechTypeId)) 
			{
				GenericValue userTelephoneGV =delegator.findByPrimaryKey("TelecomNumber",UtilMisc.toMap("contactMechId",contactMechGV.get("contactMechId")));
	
				List userTelephoneContactMechPurposeLst =delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechId",contactMechGV.get("contactMechId")));
				String contactMechPurpose="";
				if(userTelephoneContactMechPurposeLst.size()>0)
		      	{
					GenericValue userTelephoneContactMechPurposeGv=(GenericValue)userTelephoneContactMechPurposeLst.get(0);
					if(userTelephoneContactMechPurposeGv!=null)
				 	{
						contactMechPurpose=(String)userTelephoneContactMechPurposeGv.get("contactMechPurposeTypeId");
						if(contactMechPurpose!=null && "PHONE_WORK".equalsIgnoreCase(contactMechPurpose))
						{
							partyDetailsMap.put("telephoneWork",userTelephoneGV);
							partyDetailsMap.put("telephoneWorkContactMechId",contactMechId);
						}
						if(contactMechPurpose!=null && "PHONE_HOME".equalsIgnoreCase(contactMechPurpose))
						{
							partyDetailsMap.put("telephoneHome",userTelephoneGV);
							partyDetailsMap.put("telephoneHomeContactMechId",contactMechId);
						}
						if(contactMechPurpose!=null && "PHONE_MOBILE".equalsIgnoreCase(contactMechPurpose))
						{
							partyDetailsMap.put("telephoneMobile",userTelephoneGV);
							partyDetailsMap.put("telephoneMobileContactMechId",contactMechId);
						}
						if(contactMechPurpose!=null && "FAX_NUMBER".equalsIgnoreCase(contactMechPurpose))
						{
							partyDetailsMap.put("telephoneFax",userTelephoneGV);
							partyDetailsMap.put("telephoneFaxsContactMechId",contactMechId);
						}
				 	}
		      	}
				Debug.logInfo("####### end of telocme  ", module);
			}	// if end of TELECOM
			else if ("POSTAL_ADDRESS".equalsIgnoreCase(contactMechTypeId)) 
			{
				
				GenericValue userPostalGV = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId",contactMechGV.get("contactMechId")));	
				List userPostalContactMechPurposeLst =delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechId",contactMechGV.get("contactMechId")));
				String contactMechPurpose="";
				if(!(userPostalContactMechPurposeLst.size()<=0))
				{
					GenericValue userPostalContactMechPurposeGv=(GenericValue)userPostalContactMechPurposeLst.get(0);
					if(userPostalContactMechPurposeGv!=null)
					{
						contactMechPurpose =(String)userPostalContactMechPurposeGv.get("contactMechPurposeTypeId");
						if("SHIPPING_LOCATION".equalsIgnoreCase(contactMechPurpose))
						{
							partyDetailsMap.put("postalShippingAddress",userPostalGV);
							partyDetailsMap.put("postalShippingAddressContactMechId",contactMechId);
						}
					  if("PRIMARY_LOCATION".equalsIgnoreCase(contactMechPurpose))
					  {
						partyDetailsMap.put("postalPrimaryAddress",userPostalGV);
						partyDetailsMap.put("postalPrimaryAddressContactMechId",contactMechId);
					  }
				  }
			  }
			}// if end of POSTAL_ADDRESS
		// if end of TELECOM
	  	else if ("EMAIL_ADDRESS".equalsIgnoreCase(contactMechTypeId)) 
	  	{
			List userEmailMechPurposeLst =delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechId",contactMechGV.get("contactMechId")));
			String contactMechPurpose="";
			if(!(userEmailMechPurposeLst.size()<=0))
			{
				GenericValue userEmailMechPurposeGv=(GenericValue)userEmailMechPurposeLst.get(0);
				if(userEmailMechPurposeGv!=null)
				{
					contactMechPurpose =(String)userEmailMechPurposeGv.get("contactMechPurposeTypeId");
					if("PRIMARY_EMAIL".equalsIgnoreCase(contactMechPurpose))
					{
						partyDetailsMap.put("primaryEmail",contactMechGV.getString("infoString"));
						partyDetailsMap.put("primaryEmailContactMechId",contactMechId);
					}
				 }
			  }
  			}// if end of POSTAL_ADDRESS
		}
		catch (GenericEntityException geeX)
		{
			Debug.logError(geeX, "Failed to get contact details for contactMechType:[" + contactMechTypeId+ "] && contactMechId:["+ contactMechGV.get("contactMechId")+ "with message:[" + geeX.getMessage() +"]", module);
		}
	}
    
    
}
