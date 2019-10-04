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

package org.ofbiz.sfa.vcard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
//import javolution.util.FastMap;

import net.wimpi.pim.Pim;
import net.wimpi.pim.contact.basicimpl.AddressImpl;
import net.wimpi.pim.contact.basicimpl.EmailAddressImpl;
import net.wimpi.pim.contact.basicimpl.PhoneNumberImpl;
import net.wimpi.pim.contact.io.ContactMarshaller;
import net.wimpi.pim.contact.io.ContactUnmarshaller;
import net.wimpi.pim.contact.model.Address;
import net.wimpi.pim.contact.model.Communications;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.EmailAddress;
import net.wimpi.pim.contact.model.Organization;
import net.wimpi.pim.contact.model.OrganizationalIdentity;
import net.wimpi.pim.contact.model.PersonalIdentity;
import net.wimpi.pim.contact.model.PhoneNumber;
import net.wimpi.pim.factory.ContactIOFactory;
import net.wimpi.pim.factory.ContactModelFactory;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class VCard {
	public static final String module = VCard.class.getName();
	public static final String resourceError = "MarketingUiLabels";

	public static Map<String, Object> importVCard(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Address workAddress = null;
		String email = null;
		String phone = null;
		ByteBuffer byteBuffer = (ByteBuffer) context.get("infile");
		byte[] inputByteArray = byteBuffer.array();
		InputStream in = new ByteArrayInputStream(inputByteArray);
		String partyType = (String) context.get("partyType");
		Boolean isGroup = "PartyGroup".equals(partyType); // By default we
		//Map<String, Object> serviceCtx = FastMap.newInstance();
		Map<String, Object> serviceCtx = new HashMap<String, Object>();
		

		try {
			ContactIOFactory ciof = Pim.getContactIOFactory();
			ContactUnmarshaller unmarshaller = ciof.createContactUnmarshaller();
			Contact[] contacts = unmarshaller.unmarshallContacts(in);

			for (Contact contact : contacts) {
				PersonalIdentity pid = contact.getPersonalIdentity();
				if (!isGroup) {
					serviceCtx.put("firstName", pid.getFirstname());
					serviceCtx.put("lastName", pid.getLastname());
				}
				for (Iterator<?> iter = contact.getAddresses(); iter.hasNext();) {
					Address address = (AddressImpl) iter.next();
					if (contact.isPreferredAddress(address)) {
						workAddress = address;
						break;
					} else if (address.isWork()) {
						workAddress = address;
						break;
					} else { // for now use preferred/work address only
						continue;
					}
				}
				if (UtilValidate.isNotEmpty(workAddress)) {
					serviceCtx.put("address1", workAddress.getStreet());
					serviceCtx.put("city", workAddress.getCity());
					serviceCtx.put("postalCode", workAddress.getPostalCode());

					List<GenericValue> countryGeoList = null;
					List<GenericValue> stateGeoList = null;
					EntityCondition cond = EntityCondition.makeCondition(
							UtilMisc.toList(EntityCondition.makeCondition(
									"geoTypeId", EntityOperator.EQUALS,
									"COUNTRY"), EntityCondition.makeCondition(
									"geoName", EntityOperator.LIKE,
									workAddress.getCountry())),
							EntityOperator.AND);
					countryGeoList = delegator.findList("Geo", cond, null,
							null, null, true);
					if (!countryGeoList.isEmpty()) {
						GenericValue countryGeo = EntityUtil
								.getFirst(countryGeoList);
						serviceCtx.put("countryGeoId", countryGeo.get("geoId"));
					}

					EntityCondition condition = EntityCondition.makeCondition(
							UtilMisc.toList(EntityCondition
									.makeCondition("geoTypeId",
											EntityOperator.EQUALS, "STATE"),
									EntityCondition.makeCondition("geoName",
											EntityOperator.LIKE,
											workAddress.getRegion())),
							EntityOperator.AND);
					stateGeoList = delegator.findList("Geo", condition, null,
							null, null, true);
					if (!stateGeoList.isEmpty()) {
						GenericValue stateGeo = EntityUtil
								.getFirst(stateGeoList);
						serviceCtx.put("stateProvinceGeoId",
								stateGeo.get("geoId"));
					}
				}

				if (!isGroup) {
					Communications communications = contact.getCommunications();
					if (UtilValidate.isNotEmpty(communications)) {
						for (Iterator<?> iter = communications
								.getEmailAddresses(); iter.hasNext();) {
							EmailAddress emailAddress = (EmailAddressImpl) iter
									.next();
							if (communications
									.isPreferredEmailAddress(emailAddress)) {
								email = emailAddress.getAddress();
								break;
							} else {
								email = emailAddress.getAddress();
								break;
							}
						}
						if (UtilValidate.isNotEmpty(email)) {
							serviceCtx.put("emailAddress", email);
						}
						for (Iterator<?> iter = communications
								.getPhoneNumbers(); iter.hasNext();) {
							PhoneNumber phoneNumber = (PhoneNumberImpl) iter
									.next();
							if (phoneNumber.isPreferred()) {
								phone = phoneNumber.getNumber();
								break;
							} else if (phoneNumber.isWork()) {
								phone = phoneNumber.getNumber();
								break;
							} else { // for now use only preferred/work phone
										// numbers
								continue;
							}
						}
						if (UtilValidate.isNotEmpty(phone)) {
							String[] numberParts = phone.split("\\D");
							String telNumber = "";
							for (String number : numberParts) {
								if (number != "") {
									telNumber = telNumber + number;
								}
							}
							serviceCtx.put("areaCode",
									telNumber.substring(0, 3));
							serviceCtx.put("contactNumber",
									telNumber.substring(3));
						}
					}
				}
				OrganizationalIdentity oid = contact
						.getOrganizationalIdentity();
				// Useful when creating a contact with more than OOTB
				if (!isGroup) {
					serviceCtx.put("personalTitle", oid.getTitle());
				}

				// Needed when creating an account (a PartyGroup)
				if (isGroup) {
					// serviceCtx.put("partyRole", oid.getRole()); // not used
					// yet,maybe useful later
					if (oid.hasOrganization()) {
						Organization org = oid.getOrganization();
						serviceCtx.put("groupName", org.getName());
					}
				}

				GenericValue userLogin = (GenericValue) context
						.get("userLogin");
				serviceCtx.put("userLogin", userLogin);
				String serviceName = (String) context.get("serviceName");
				Map<String, Object> serviceContext = UtilGenerics.cast(context
						.get("serviceContext"));
				if (UtilValidate.isNotEmpty(serviceContext)) {
					for (Map.Entry<String, Object> entry : serviceContext
							.entrySet()) {
						serviceCtx.put(entry.getKey(), entry.getValue());
					}
				}
				Map<String, Object> resp = dispatcher.runSync(serviceName,
						serviceCtx);
				result.put("partyId", resp.get("partyId"));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceError, "SfaImportVCardError",
					UtilMisc.toMap("errorString", e.getMessage()), locale));
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceError, "SfaImportVCardError",
					UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		return result;
	}

	public static Map<String, Object> exportVCard(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		Locale locale = (Locale) context.get("locale");
		File file = null;
		try {
			ContactModelFactory cmf = Pim.getContactModelFactory();
			Contact contact = cmf.createContact();

			PersonalIdentity pid = cmf.createPersonalIdentity();
			String fullName = PartyHelper.getPartyName(delegator, partyId,
					false);
			String[] name = fullName.split("\\s");
			pid.setFirstname(name[0]);
			pid.setLastname(name[1]);
			contact.setPersonalIdentity(pid);

			GenericValue postalAddress = PartyWorker
					.findPartyLatestPostalAddress(partyId, delegator);
			Address address = cmf.createAddress();
			address.setStreet(postalAddress.getString("address1"));
			address.setCity(postalAddress.getString("city"));

			address.setPostalCode(postalAddress.getString("postalCode"));
			GenericValue state = postalAddress
					.getRelatedOne("StateProvinceGeo");
			if (UtilValidate.isNotEmpty(state)) {
				address.setRegion(state.getString("geoName"));
			}
			GenericValue countryGeo = postalAddress.getRelatedOne("CountryGeo");
			if (UtilValidate.isNotEmpty(countryGeo)) {
				String country = postalAddress.getRelatedOne("CountryGeo")
						.getString("geoName");
				address.setCountry(country);
				address.setWork(true); // this can be better set by checking
										// contactMechPurposeTypeId
			}
			contact.addAddress(address);

			Communications communication = cmf.createCommunications();
			contact.setCommunications(communication);

			PhoneNumber number = cmf.createPhoneNumber();
			GenericValue telecomNumber = PartyWorker
					.findPartyLatestTelecomNumber(partyId, delegator);
			if (UtilValidate.isNotEmpty(telecomNumber)) {
				number.setNumber(telecomNumber.getString("areaCode")
						+ telecomNumber.getString("contactNumber"));
				number.setWork(true); // this can be better set by checking
										// contactMechPurposeTypeId
				communication.addPhoneNumber(number);
			}
			EmailAddress email = cmf.createEmailAddress();
			GenericValue emailAddress = PartyWorker.findPartyLatestContactMech(
					partyId, "EMAIL_ADDRESS", delegator);
			if (UtilValidate.isNotEmpty(emailAddress.getString("infoString"))) {
				email.setAddress(emailAddress.getString("infoString"));
				communication.addEmailAddress(email);
			}
			ContactIOFactory ciof = Pim.getContactIOFactory();
			ContactMarshaller marshaller = ciof.createContactMarshaller();
			String saveToDirectory = UtilProperties.getPropertyValue(
					"sfa.properties", "save.outgoing.directory", "");
			if (UtilValidate.isEmpty(saveToDirectory)) {
				saveToDirectory = System.getProperty("ofbiz.home");
			}
			String saveToFilename = fullName + ".vcf";
			file = FileUtil.getFile(saveToDirectory + "/" + saveToFilename);
			FileOutputStream outputStream = new FileOutputStream(file);
			marshaller.marshallContact(outputStream, contact);
			outputStream.close();
		} catch (FileNotFoundException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceError, "SfaExportVCardErrorOpeningFile",
					UtilMisc.toMap("errorString", file.getAbsolutePath()),
					locale));
		} catch (IOException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceError, "SfaExportVCardErrorWritingFile",
					UtilMisc.toMap("errorString", file.getAbsolutePath()),
					locale));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceError, "SfaExportVCardError",
					UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> createCase(DispatchContext dctx,
			Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		// verify that a partyId of some sort was supplied
		String accountPartyId = (String) context.get("accountPartyId");
		String contactPartyId = (String) context.get("contactPartyId");

		try {
			// create the cust request
			context.put("statusId", "CRQ_SUBMITTED");
			ModelService modelService = dctx
					.getModelService("createCustRequest");
			Map<String, Object> caseParams = modelService.makeValid(context,
					"IN");

			// CustRequest.fromPartyId is not used by the CRM/SFA application,
			// which is designed to handle multiple parties
			// but we'll fill it for consistency with OFBiz and use
			// contactPartyId first then accountPartyId
			if (contactPartyId != null) {
				caseParams.put("fromPartyId", contactPartyId);
			} else {
				caseParams.put("fromPartyId", accountPartyId);
			}

			Map<String, Object> serviceResults = dispatcher.runSync(
					"createCustRequest", caseParams);
			if (ServiceUtil.isError(serviceResults)) {
				return serviceResults;
			}

			String custRequestId = (String) serviceResults.get("custRequestId");

			// create the account role if an account is supplied, but only if
			// user has CRMSFA_CREATE_CASE permission on that account
			if (accountPartyId != null) {

				serviceResults = dispatcher.runSync("createCustRequestRole",
						UtilMisc.toMap("custRequestId", custRequestId,
								"partyId", accountPartyId, "roleTypeId",
								"ACCOUNT", "userLogin", userLogin));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
			}

			// create the contact role if a contact is supplied, but only if
			// user has CRMSFA_CASE_CREATE permission on that contact
			if (contactPartyId != null) {

				serviceResults = dispatcher.runSync("createCustRequestRole",
						UtilMisc.toMap("custRequestId", custRequestId,
								"partyId", contactPartyId, "roleTypeId",
								"CONTACT", "userLogin", userLogin));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
			}

			/*
			 * String userPartyId = userLogin.getString("partyId"); if
			 * (UtilValidate.isNotEmpty(userPartyId) &&
			 * !userPartyId.equals(caseParams.get("fromPartyId"))) {
			 * CreateCustRequestRoleService createCustReqRoleSrvc = new
			 * CreateCustRequestRoleService(new User(userLogin));
			 * createCustReqRoleSrvc.setInCustRequestId(custRequestId);
			 * createCustReqRoleSrvc.setInPartyId(userPartyId);
			 * createCustReqRoleSrvc
			 * .setInRoleTypeId(RoleTypeConstants.REQ_TAKER);
			 * createCustReqRoleSrvc.runSync(new Infrastructure(dispatcher)); }
			 */
			// create the note if a note is supplied
			String note = (String) context.get("note");
			if (note != null) {
				serviceResults = dispatcher.runSync("createCustRequestNote",
						UtilMisc.toMap("custRequestId", custRequestId,
								"userLogin", userLogin));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				String noteId = (String) serviceResults.get("noteId");

				// create a note association with the account and contact
				// parties
				if (accountPartyId != null) {
					serviceResults = dispatcher.runSync("createPartyNote",
							UtilMisc.toMap("partyId", accountPartyId, "noteId",
									noteId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
				if (contactPartyId != null) {
					serviceResults = dispatcher.runSync("createPartyNote",
							UtilMisc.toMap("partyId", contactPartyId, "noteId",
									noteId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
			}

			// check if a WorkEffort Id was supplied, if so assign it to this
			// Case
			String workEffortId = (String) context.get("workEffortId");
			/*
			 * if (workEffortId != null) { serviceResults =
			 * dispatcher.runSync("crmsfa.updateActivityAssociation",
			 * UtilMisc.toMap("workEffortId", workEffortId, "custRequestId",
			 * custRequestId, "userLogin", userLogin)); if
			 * (ServiceUtil.isError(serviceResults)) { return serviceResults; }
			 * }
			 */
			// return the custRequestId
			Map<String, Object> result = ServiceUtil.returnSuccess();
			result.put("custRequestId", custRequestId);
			if (workEffortId != null) {
				result.put("workEffortId", workEffortId);
			}
			return result;
		}

		catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
		}

	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> assignToSfaLead(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		String partyIdFrom = partyId;
		String partyIdTo = (String) context.get("partyIdTo");
	
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom"); 
		if(UtilValidate.isEmpty(roleTypeIdFrom))
			roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
			
		String roleTypeIdTo = (String)context.get("roleTypeIdTo"); 
		if(UtilValidate.isEmpty(roleTypeIdTo))
			roleTypeIdTo =	"LEAD";
		
		String partyRelationshipTypeId = (String)context.get("partyRelationshipTypeId"); 
		if(UtilValidate.isEmpty(partyRelationshipTypeId))
		partyRelationshipTypeId= "LEAD_ASSIGNED";

		GenericValue partyRole = null;

		try {
			partyRole =  delegator.findOne("PartyRole", UtilMisc.toMap(
					"partyId", partyId, "roleTypeId", roleTypeIdFrom), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (partyRole == null) {
			partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap(
					"partyId", partyId, "roleTypeId", roleTypeIdFrom));
			try {
				partyRole.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!UtilValidate.isEmpty(partyRole)) {
			GenericValue gv = (partyRole);
			roleTypeIdFrom = gv.getString("roleTypeId");
		}

		// Before creating the partyRelationShip, create the partyRoles if they
		// don't exist
		GenericValue partyToRole = null;
		NumberFormat nf = NumberFormat.getInstance();
		try {
			Long a = 	 (Long) nf.parse(partyIdTo);
			partyIdTo = a.toString();
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			partyToRole = delegator.findOne("PartyRole", UtilMisc.toMap(
					"partyId", partyIdTo, "roleTypeId", roleTypeIdTo), false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (partyToRole == null) {
			partyToRole = delegator.makeValue("PartyRole", UtilMisc.toMap(
					"partyId", partyIdTo, "roleTypeId", roleTypeIdTo));
			try {
				partyToRole.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		

		// Check if there is already a partyRelationship of that type with
		// another party from the side indicated

		List<GenericValue> partyRelationShipList = null;

		String sideChecked = partyIdFrom.equals(partyId) ? "partyIdFrom"
				: "partyIdTo";
		try {
			partyRelationShipList = delegator
					.findByAnd("PartyRelationship", UtilMisc.toMap(sideChecked,
							partyId, "roleTypeIdFrom", roleTypeIdFrom,
							"roleTypeIdTo", roleTypeIdTo,
							"partyRelationshipTypeId", partyRelationshipTypeId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// We consider the last one (in time) as sole active (we try to maintain
		// a unique relationship and keep changes history)
		partyRelationShipList = EntityUtil.filterByDate(partyRelationShipList);
		GenericValue oldPartyRelationShip = EntityUtil
				.getFirst(partyRelationShipList);
		if (UtilValidate.isNotEmpty(oldPartyRelationShip)) {
			oldPartyRelationShip.setFields(UtilMisc.toMap("thruDate",
					UtilDateTime.nowTimestamp())); // Current becomes inactive
			try {
				oldPartyRelationShip.store();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Map newContext = new HashMap();

			newContext.put("partyId", partyId);
			newContext.put("partyIdFrom", partyIdFrom);
			newContext.put("roleTypeIdTo", roleTypeIdTo);
			newContext.put("partyIdTo", partyIdTo);
			newContext.put("roleTypeIdFrom", roleTypeIdFrom);
			newContext.put("fromDate", UtilDateTime.nowTimestamp());
			newContext.put("partyRelationshipTypeId", partyRelationshipTypeId);
			newContext.put("partyRelationshipTypeId", partyRelationshipTypeId);
			newContext.put("userLogin", userLogin);

			result = dispatcher.runSync("createPartyRelationship", newContext); // Create
			// new one
		} catch (GenericServiceException e) {
			Debug.logWarning(e.getMessage(), module);
			return ServiceUtil
					.returnError(UtilProperties
							.getMessage(
									resourceError,
									"partyrelationshipservices.could_not_create_party_role_write",
									UtilMisc.toMap("errorString",
											e.getMessage()), locale));
		}
		return ServiceUtil
				.returnSuccess("Selected Companies Assigned to Team Lead [ "
						+ partyIdTo + " ]");

	}

}
