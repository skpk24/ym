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

package com.ilinks.restful.customer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * For customer contact displays in front end.
 */
public class ContactHelper {

    public static final String module = ContactHelper.class.getName();
    
    /**
     * Format output string for a credit card information.
     * 
     * @param creditCard
     * @return
     */
    public static String formatCreditCard(GenericValue creditCard) {
    	return formatCreditCard(creditCard, false);
    }
    
    public static String formatCreditCard(GenericValue creditCard, int division) {
    	return formatCreditCard(creditCard, false, division, null);
    }
    
    public static String formatCreditCard(GenericValue creditCard, int division, String replacement) {
    	return formatCreditCard(creditCard, false, division, replacement);
    }
    
    public static String formatCreditCard(GenericValue creditCard, boolean shortCardNumber, int division, String replacement) {
    	return formatCreditCard(creditCard, shortCardNumber, false, division, replacement);
    }
    
    public static String formatCreditCard(GenericValue creditCard, boolean shortCardNumber) {
    	return formatCreditCard(creditCard, shortCardNumber, false);
    }
    
    public static String formatCreditCard(GenericValue creditCard, boolean shortCardNumber, boolean showExpireDate) {
    	return formatCreditCard(creditCard, shortCardNumber, showExpireDate, 4, null);
    }
    
    public static String formatCreditCard(GenericValue creditCard, boolean shortCardNumber, boolean showExpireDate, int division, String replacement) {
        StringBuilder result = new StringBuilder(16);

        String cardType = creditCard.getString("cardType");
        if (UtilValidate.isNotEmpty(cardType)) {
    		GenericValue cardNameValue;
			try {
				cardNameValue = EntityUtil.getFirst(creditCard.getDelegator().findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "CREDIT_CARD_TYPE", 
				        "enumCode", cardType)));
	            if (UtilValidate.isNotEmpty(cardNameValue) && UtilValidate.isNotEmpty(cardNameValue.getString("description"))) {
	                cardType = cardNameValue.getString("description");
	            }
			} catch (GenericEntityException e) {
				// do nothing
			}
            result.append(cardType);
        }
        String companyNameOnCard = creditCard.getString("companyNameOnCard");
        if (UtilValidate.isEmpty(cardType)) {
        	result.append(companyNameOnCard);
        }
        
        if (division < 3) division = 3;
        
        String cardNumber = creditCard.getString("cardNumber");
        if (cardNumber != null) {
        	if (shortCardNumber && cardNumber.length() > division) {
                result.append(": ***").append(cardNumber.substring(cardNumber.length() - division));
            } else {
                result.append(": ");
                for (int i = 0, j = cardNumber.length() - ((int) (cardNumber.length() / division)) * division; i<cardNumber.length(); i++, j--) {
                	if (j <= 0) {
                		j = division;
                		result.append(" ");
                	}
                	if (UtilValidate.isNotEmpty(replacement) && i < (cardNumber.length() - division)) {
                        result.append(replacement);
                	} else {
                        result.append(cardNumber.charAt(i));
                	}
                }
            }
        }
        
        String expireDate = creditCard.getString("expireDate");
        if (showExpireDate && UtilValidate.isNotEmpty(expireDate)) {
            result.append(" (Expr:").append(expireDate).append(")");
        }
        return result.toString();
    }
    
    /**
     * Format a card number to output.
     * 
     * @param creditCard
     * @return
     */
    public static String formatCardNumber(GenericValue creditCard) {
    	return formatCardNumber(creditCard, 4);
    }

    public static String formatCardNumber(GenericValue creditCard, int division) {
        StringBuilder result = new StringBuilder(16);

        if (division < 3) division = 3;
        
        String cardNumber = creditCard.getString("cardNumber");
        if (cardNumber != null) {
            for (int i = 0, j = cardNumber.length() - ((int) (cardNumber.length() / division)) * division; i<cardNumber.length(); i++, j--) {
            	if (j <= 0) {
            		j = division;
            		result.append(" ");
            	}
                result.append(cardNumber.charAt(i));
            }
        }
        return result.toString();
    }

    /**
     * Format output string for Person. 
     * 
     * @param person
     * @param locale
     * @return
     */
    public static String formatPersonName(Object person, Locale locale) {
    	return formatPersonName(person, locale, "PetBestUiLabels", "MyAccountUserNamePattern", true);
    }

    public static String formatPersonName(Object person, Locale locale, String resource, String namePattern) {
    	return formatPersonName(person, locale, resource, namePattern, true);
    }

    public static String formatPersonName(Object person, Locale locale, String resource, String namePattern, boolean includeMiddle) {
        StringBuilder result = new StringBuilder(8);
    	if (person == null) {
    		return "";
    	}
    	String firstName = getValue(person, "firstName");
       	String middleName = getValue(person, "middleName");
    	String lastName = getValue(person, "lastName");

        String name = UtilProperties.getMessage(resource, namePattern,
                new Object[] {firstName, middleName, lastName}, locale);
        result.append(name.replaceAll("  ", " "));

    	return result.toString();
    }
    
    /**
     * Format a telecom number to output
     * 
     * @param telecomNumber
     * @param locale
     * @return String
     */
    public static String formatTelecomNumber(Object telecomNumber, Locale locale) {
    	return formatTelecomNumber(telecomNumber, null, locale);
    }

    public static String formatTelecomNumber(Object telecomNumber, Object partyContactMech, Locale locale) {
    	return formatTelecomNumber(telecomNumber, partyContactMech, locale, "PetBestUiLabels", "MyAccountTelecomNumberUSPattern", false);
    }

    public static String formatTelecomNumber(Object telecomNumber, Locale locale, String resource, String telecomPattern, boolean includeExt) {
    	return formatTelecomNumber(telecomNumber, null, locale, resource, telecomPattern, false);
    }
    
    public static String formatTelecomNumber(Object telecomNumber, Object partyContactMech, Locale locale, String resource, String telecomPattern, boolean includeExt) {
    	if (telecomNumber == null) {
    		return "";
    	}
    	String countryCode = getValue(telecomNumber, "countryCode", "000");
    	String areaCode = getValue(telecomNumber, "areaCode", "000");
    	String contactNumber = getValue(telecomNumber, "contactNumber", "000-0000");
    	if (countryCode.equals("000") && areaCode.equals("000") && contactNumber.equals("000-0000")) {
    		return "";
    	}
    	// for US style phone number
    	if (contactNumber.length() == 7) {
    		contactNumber = contactNumber.substring(0, 3) + "-" + contactNumber.substring(3);
    	}

    	String extension = getValue(partyContactMech, "extension");
    	if (UtilValidate.isNotEmpty(extension) && includeExt) {
    		contactNumber += " " + UtilProperties.getMessage("PartyUiLabels", "PartyContactExt", locale) + " " + extension;
    	}
    	
        return UtilProperties.getMessage(resource, telecomPattern,
                new Object[] {countryCode, areaCode, contactNumber}, locale);
    }
    
    private static String getValue(Object person, String fieldName) {
    	return getValue(person, fieldName, "");
    }

    private static String getValue(Object person, String fieldName, String defaultValue) {
    	String result = "";
    	if (person instanceof GenericValue) {
    		result = ((GenericValue) person).getString(fieldName);
    	} else if (person instanceof Map<?, ?>) {
    		result = (String) ((Map<?, ?>) person).get(fieldName);
    	}
    	if (UtilValidate.isEmpty(result)) {
    		result = defaultValue;
    	}
    	return result;
    }

    /**
     * Get a telecom number linked to a postal address.
     * 
     * @param delegator
     * @param postalAddress
     * @param partyId
     * @return a TelecomNumber
     * @throws GenericEntityException
     */
    public static GenericValue getBillingAddrPhone(GenericDelegator delegator, GenericValue postalAddress, String partyId) throws GenericEntityException {
    	return getPostalAddressPhone(delegator, postalAddress, "PHONE_BILLING", partyId);
    }
    
    public static GenericValue getPostalAddressPhone(GenericDelegator delegator, GenericValue postalAddress, String contactMechPurposeTypeId, String partyId) throws GenericEntityException {
    	if (UtilValidate.isEmpty(postalAddress) || UtilValidate.isEmpty(partyId)) {
    		return null;
    	}

    	if (!postalAddress.getEntityName().equals("PostalAddress")) {
    		return null;
    	}
    	
		GenericValue contactMech = postalAddress.getRelatedOne("ContactMech");
		if (UtilValidate.isEmpty(contactMech)) {
			return null;
		}
		
		List<GenericValue> toContactMechs = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMech.getString("contactMechId")));
		if (UtilValidate.isEmpty(toContactMechs)) {
			return null;
		}
		
		for (GenericValue toContactMech : toContactMechs) {
			if (UtilValidate.isEmpty(toContactMech)) {
				continue;
			}
			GenericValue telecomNumber = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", toContactMech.getString("contactMechIdTo")));
			if (UtilValidate.isEmpty(telecomNumber)) {
				continue;
			}
			List<GenericValue> partyTelecomNumbers = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("contactMechId", telecomNumber.getString("contactMechId"), "partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId));
			partyTelecomNumbers = EntityUtil.filterByDate(partyTelecomNumbers, true);
			// make sure the telecom number belongs to the party and is PHONE_BILLING
			if (UtilValidate.isEmpty(partyTelecomNumbers) || !hasValidPartyContactMech(partyTelecomNumbers.get(0))) {
				continue;
			}
			return telecomNumber;
		}
        return null;
    }

    /**
     * Get billing address of a credit card.
     * 
     * @param delegator
     * @param creditCard
     * @param partyId
     * @return a PostalAddress
     * @throws GenericEntityException
     */
    public static GenericValue getCreditCardBillingAddress(GenericDelegator delegator, GenericValue creditCard, String partyId) throws GenericEntityException {
    	if (UtilValidate.isEmpty(creditCard) || UtilValidate.isEmpty(partyId)) {
    		return null;
    	}
    	
    	if (!creditCard.getEntityName().equals("CreditCard")) {
    		return null;
    	}
    	
		GenericValue billingAddress = creditCard.getRelatedOne("PostalAddress");
		if (UtilValidate.isEmpty(billingAddress)) {
			return null;
		}
		
		List<GenericValue> partyBillingAddrs = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("contactMechId", billingAddress.getString("contactMechId"), "partyId", partyId, "contactMechPurposeTypeId", "BILLING_LOCATION"));
		partyBillingAddrs = EntityUtil.filterByDate(partyBillingAddrs, true);
		// make sure the billing address belongs to the party and is BILLING_LOCATION
		if (UtilValidate.isEmpty(partyBillingAddrs) || !hasValidPartyContactMech(partyBillingAddrs.get(0))) {
	        return null;
		}
		return billingAddress;
    }

    /**
     * Check whether a PartyContactMechPurpose has valid PartyContactMech.
     * 
     * @param delegator
     * @param purpose
     * @return true if any valid PartyContactMech exists; else false.
     * @throws GenericEntityException
     */
    public static boolean hasValidPartyContactMech(GenericValue purpose) throws GenericEntityException {
    	if (UtilValidate.isEmpty(purpose)) {
    		return false;
    	}
    	if (!purpose.getEntityName().equals("PartyContactMechPurpose")) {
    		return false;
    	}
    	List<GenericValue> result = purpose.getRelated("PartyContactMech");
    	result = EntityUtil.filterByDate(result, true);
    	if (UtilValidate.isEmpty(result)) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Get attributes' values from ContactMechAttribute.
     * 
     * @param delegator
     * @param postalAddress
     * @param attributes
     * @return Map contains the attribute names as keys and values
     * @throws GenericEntityException
     */
    public static Map<String, String> getContactMechAttributes(GenericDelegator delegator, GenericValue postalAddress, List<String> attributes) throws GenericEntityException {
    	Map<String, String> result = FastMap.newInstance();
    	if (UtilValidate.isEmpty(postalAddress)) {
    		return result;
    	}
    	if (!postalAddress.getEntityName().equals("PostalAddress")) {
    		return result;
    	}
    	if (UtilValidate.isEmpty(attributes)) {
    		return result;
    	}
    	String contactMechId = postalAddress.getString("contactMechId");
    	// get attributes from ContactMechAttribute
    	for (String attrName : attributes) {
    		if (UtilValidate.isEmpty(attrName)) {
    			continue;
    		}
        	GenericValue value = delegator.findByPrimaryKey("ContactMechAttribute", UtilMisc.toMap("contactMechId", contactMechId,
                    "attrName", attrName));
            if (UtilValidate.isNotEmpty(value)) {
                result.put(attrName, value.getString("attrValue"));
            }
    	}
    	return result;
    }
}
