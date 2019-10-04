package org.ofbiz.marketing.marketing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastList;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;

public class UploadEmailTemplateFile {
	public static final String module = UploadEmailTemplateFile.class.getName();

	public static String uploadEmailTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String serverPath = System.getProperty("ofbiz.home");
		String DESTINATION_DIR_PATH = serverPath
				+ "/framework/images/webapp/images/importFiles/emailTemplate/";
		String contactListName = request.getParameter("contactListName");
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		String contactListId = request.getParameter("contactListId");

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		if (items != null) {
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();

				String fname = item.getName();
				String ext = "";
				if (fname != null) {
					ext = fname.substring(fname.lastIndexOf('.'));
				} else
					continue;
				String value = item.getString();
				File savedFile = new File(DESTINATION_DIR_PATH, contactListId
						+ ".html");
				try {
					item.write(savedFile);
					String uplodedfile = contactListId + ext;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return "success";

	}

	public static String createEmailTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		// LocalDispatcher dispatcher = (LocalDispatcher)
		// request.getAttribute("dispatcher");

		String contactListName = request.getParameter("contactListName");
		String subject = request.getParameter("subject");
		String communicationEventTypeId = request
				.getParameter("communicationEventTypeId");
		String statusId = request.getParameter("statusId");
		String contentMimeTypeId = request.getParameter("contentMimeTypeId");
		String partyIdFrom = request.getParameter("partyIdFrom");

		GenericValue createContactList = delegator.makeValue("ContactList");
		String contactListId = delegator.getNextSeqId("ContactList");
		createContactList.put("contactListId", contactListId);
		createContactList.put("contactListTypeId", "NEWSLETTER");
		createContactList.put("contactListName", contactListName);
		createContactList.create();

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		if (items != null) {
			Iterator itr = items.iterator();
			String value = null;
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();

				String fname = item.getName();
				String ext = "";
				if (fname != null) {
					ext = fname.substring(fname.lastIndexOf('.'));
				} else
					continue;
				value = item.getString();
			}

			String communicationEventId = delegator
					.getNextSeqId("CommunicationEvent");
			GenericValue genericVal = delegator.makeValue("CommunicationEvent");
			genericVal.set("communicationEventId", contactListId);
			if (communicationEventTypeId != null
					&& !communicationEventTypeId.equals(""))
				genericVal.set("communicationEventTypeId",
						communicationEventTypeId);
			else
				genericVal.set("communicationEventTypeId",
						"EMAIL_COMMUNICATION");
			genericVal.set("statusId", "COM_PENDING");
			genericVal.set("contentMimeTypeId", "text/html");

			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("PCM", "PartyContactMech");
			dynamicViewEntity.addAlias("PCM", "partyId");
			dynamicViewEntity.addAlias("PCM", "contactMechId");

			dynamicViewEntity.addMemberEntity("CM", "ContactMech");
			dynamicViewEntity.addAlias("CM", "contactMechId");
			dynamicViewEntity.addAlias("CM", "contactMechTypeId");
			dynamicViewEntity.addAlias("CM", "infoString");

			dynamicViewEntity.addViewLink("PCM", "CM", Boolean.FALSE,
					ModelKeyMap.makeKeyMapList("contactMechId"));

			//List<EntityExpr> entityConditionList = FastList.newInstance();
			List<EntityExpr> entityConditionList = new ArrayList<EntityExpr>();
			entityConditionList.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, partyIdFrom));
			entityConditionList.add(EntityCondition
					.makeCondition("contactMechTypeId", EntityOperator.EQUALS,
							"EMAIL_ADDRESS"));
			EntityCondition whereCondition = EntityCondition.makeCondition(
					entityConditionList, EntityOperator.AND);
			EntityListIterator eli = delegator.findListIteratorByCondition(
					dynamicViewEntity, whereCondition, null, null, null, null);
			
			
			
			
			
		

			GenericValue emailAddress = null;
			while (((emailAddress = eli.next()) != null)) {
				break;
			}
			eli.close();
			String contactMechId = emailAddress.getString("contactMechId");
			if (contactListId == null || contactListId.equals("")) {
				contactMechId = "Company";
				partyIdFrom = "Company";
			}

			genericVal.set("contactMechIdFrom", contactMechId);
			genericVal.set("partyIdFrom", partyIdFrom);
			genericVal.set("subject", subject);
			genericVal.set("contactListId", contactListId);
			genericVal.set("content", value);
			genericVal.create();

			GenericValue contactList = delegator.makeValue("ContactList");
			contactList.set("contactListName", contactListName);
			contactList.set("contactListId", contactListId);
			contactList.set("contactMechTypeId", "EMAIL_ADDRESS");
			contactList.set("contactListTypeId", "NEWSLETTER");
			contactList.store();

		}

		return "success";
	}

	public static String readFileContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String contactListId = request.getParameter("contactListId");
		String serverPath = System.getProperty("ofbiz.home");
		String path = serverPath
				+ "\\framework\\images\\webapp\\images\\importFiles\\emailTemplate\\"
				+ contactListId + ".html";

		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		String content = org.ofbiz.base.util.StringUtil.wrapString(
				stringBuilder.toString()).toString();
		request.setAttribute("content", content);
		return "success";
	}

	public static String createEmailTemplateData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		String templateName = request.getParameter("contactListName");
		String subject = request.getParameter("subject");
		String communicationEventTypeId = request.getParameter("communicationEventTypeId");
		String statusId = request.getParameter("statusId");
		String contentMimeTypeId = request.getParameter("contentMimeTypeId");
		String partyIdFrom = request.getParameter("partyIdFrom");
		

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
			if (items != null) {
				Iterator itr = items.iterator();
				String value = null;
				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();

					if(item.isFormField() && item.getFieldName().equals("contactListName")){
						templateName = item.getString();
						//System.out.println("templateName"+templateName);
					}
					
					String fname = item.getName();
					java.io.InputStream  input = item.getInputStream();
					String ext = "";
					if (fname != null) {
						ext = fname.substring(fname.lastIndexOf('.'));
					} else
						continue;
					value = item.getString();
					
				}

				String templateId = delegator.getNextSeqId("MarketingTemplate");
				GenericValue genericVal = delegator.makeValue("MarketingTemplate");
				genericVal.set("templateId", templateId);
				genericVal.set("templateName", templateName);
				genericVal.set("templateData", value);
				genericVal.create();

			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", "Problem while uploading template.");
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", "Template Uploaded Successfully.");
		return "success";
	}
	public static String createEmailTemplateData2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		String templateName = request.getParameter("contactListName");
		String subject = request.getParameter("subject");
		String communicationEventTypeId = request.getParameter("communicationEventTypeId");
		String statusId = request.getParameter("statusId");
		String contentMimeTypeId = request.getParameter("contentMimeTypeId");
		String partyIdFrom = request.getParameter("partyIdFrom");
		

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
			if (items != null) {
				Iterator itr = items.iterator();
				String value = null;
				String ext = "";
				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
					
					if(item.isFormField() && item.getFieldName().equals("contactListName"))
					{
						templateName = item.getString();
						
					}
					String fname = item.getName();
					
					String Test =item.getName();
					
					if(Test != null)
					{
						String serverPath = System.getProperty("ofbiz.home");
						String PATH = serverPath
								+ "/framework/images/webapp/images/importFiles/emailTemplate/";
						
					File savedFile = new File(PATH+"marketingEmailTemplate.html");
							item.write(savedFile);
							request.setAttribute("textPath", savedFile);
					
					}
					
					java.io.InputStream  input = item.getInputStream();
					
					if (fname != null) {
						ext = fname.substring(fname.lastIndexOf('.'));
					} else
						continue;
					value = item.getString();
					//System.out.println("\n\n\n\n value="+value);
					String extension = ext.substring(ext.lastIndexOf(".") + 1, ext.length());
					
					if(extension.equals("html"))
					//if(extension == "html")
					{
						String templateId = delegator.getNextSeqId("MarketingTemplate");
						GenericValue genericVal = delegator.makeValue("MarketingTemplate");
						genericVal.set("templateId", templateId);
						genericVal.set("templateName", templateName);
						genericVal.set("templateData", value);
						genericVal.create();
						request.setAttribute("_EVENT_MESSAGE_", "Template Uploaded Successfully.");
						
						
					}
					else
					{
						request.setAttribute("_ERROR_MESSAGE_", "Template is not HTML type");
					}
					value = item.getString();
					break;
					
				}

			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", "Problem while uploading template.");
			e.printStackTrace();
		}
		return "success";
	}

	public static String createAutoMailTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String templateType = request.getParameter("templateType");
		String subject = request.getParameter("subject");
		String contentMimeTypeId = request.getParameter("contentMimeTypeId");
		String templateData = request.getParameter("templateData");

		try {

				String templateId = delegator.getNextSeqId("AutoMailTemplate");
				GenericValue genericVal = delegator.makeValue("AutoMailTemplate");
				genericVal.set("templateId", templateId);
				genericVal.set("templateType", templateType);
				genericVal.set("contentMimeTypeId", contentMimeTypeId);
				genericVal.set("subject", subject);
				genericVal.set("templateData", templateData);
				genericVal.create();

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", "Template created Successfully.");
		return "success";
	}
	
	public static String updateAutoMailTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String templateId = request.getParameter("templateId");
		String subject = request.getParameter("subject");
		String templateData = request.getParameter("templateData");
		String fromEmailId = request.getParameter("fromEmailId");
		String daysBefore = request.getParameter("daysBefore");
		
		String error = "Missing";
		
		if(subject == null || subject.length()<1){
			error = error+"\n Subject ";
		}
		if(fromEmailId == null|| fromEmailId.length()<1){
			error = error+"\n Email Id ";
		}
		if(templateData == null|| templateData.length()<1){
			error = error+"\n Template Data";
		}
		if(error.length()>7){
			request.setAttribute("_ERROR_MESSAGE_", error);
			return error;
		}

		try {
				GenericValue genericVal = delegator.findOne("AutoMailTemplate", org.ofbiz.base.util.UtilMisc.toMap("templateId", templateId), false);
				if(genericVal != null){
					genericVal.set("fromEmailId", fromEmailId);
					genericVal.set("daysBefore", daysBefore);
					genericVal.set("subject", subject);
					genericVal.set("templateData", templateData);
					genericVal.store();
				}

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", "Template updated Successfully.");
		return "success";
	}	

}