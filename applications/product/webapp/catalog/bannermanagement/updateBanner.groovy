
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.geo.GeoWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.oreilly.servlet.MultipartRequest;

	try {
			
			String productStoreId = request.getParameter("productStoreId");
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String bannerLinkUrl = request.getParameter("bannerLinkUrl");
			String sequenceNum = request.getParameter("sequenceNum");
			String position = request.getParameter("position");
			String bannerImageUrl = request.getParameter("bannerImageUrl");
			String groupName = request.getParameter("groupName");
			String categoryName = request.getParameter("categoryName");
			
 			 
			if(UtilValidate.isEmpty(fromDate)) fromDate = UtilDateTime.nowTimestamp().toString();
			if(UtilValidate.isEmpty(sequenceNum)) sequenceNum = "1";
			if(UtilValidate.isEmpty(position)) position = "home";
			
			
				GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
				
				String bannerManagementId = request.getParameter("bannerManagementId");
				if(UtilValidate.isEmpty(bannerManagementId))return "error";
				
				
				GenericValue bannerManagement = delegator.findOne("BannerManagement", UtilMisc.toMap("bannerManagementId",bannerManagementId), false);
				
				if(UtilValidate.isNotEmpty(productStoreId))
					bannerManagement.put("productStoreId", productStoreId);
				if(UtilValidate.isNotEmpty(fromDate))
					bannerManagement.put("fromDate", Timestamp.valueOf(fromDate));
				if(UtilValidate.isEmpty(thruDate))
					bannerManagement.put("thruDate", null);
				else
					bannerManagement.put("thruDate", Timestamp.valueOf(thruDate));
					
				//if(UtilValidate.isNotEmpty(bannerLinkUrl))
					bannerManagement.put("bannerLinkUrl", bannerLinkUrl);
					
				if(UtilValidate.isNotEmpty(bannerImageUrl))
					bannerManagement.put("bannerImageUrl", bannerImageUrl);
				
				if(UtilValidate.isNotEmpty(sequenceNum) && UtilValidate.isPositiveInteger(sequenceNum))
					bannerManagement.put("sequenceNum", Long.parseLong(sequenceNum));
				
				if(UtilValidate.isNotEmpty(position))
					bannerManagement.put("position", position);
				
				if(UtilValidate.isNotEmpty(groupName))
					bannerManagement.put("groupName", groupName);
				
				if(UtilValidate.isNotEmpty(categoryName))
					bannerManagement.put("categoryName", categoryName);
				
				bannerManagement.store();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	  
		return "success";
	