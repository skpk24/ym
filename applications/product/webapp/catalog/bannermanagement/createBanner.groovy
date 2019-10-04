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



			PrintWriter out = response.getWriter();
			String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/banners";

			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());
			
			
			String productStoreId = mr.getParameter("productStoreId");
			String catalogId = mr.getParameter("catalogId");
			String fromDate = mr.getParameter("fromDate");
			String thruDate = mr.getParameter("thruDate");
			String bannerLinkUrl = mr.getParameter("bannerLinkUrl");
			String sequenceNum = mr.getParameter("sequenceNum");
			String position = mr.getParameter("position");
			
			String groupName = mr.getParameter("groupName");

			String categoryName = mr.getParameter("categoryName");

			
			
			
			if(UtilValidate.isEmpty(fromDate)) fromDate = UtilDateTime.nowTimestamp().toString();
			if(UtilValidate.isEmpty(thruDate)) thruDate = null;
			if(UtilValidate.isEmpty(sequenceNum)) sequenceNum = "1";
			if(UtilValidate.isEmpty(position)) position = "HOME";
			
			GenericValue  bannerManagement = delegator.makeValue("BannerManagement");
			
			bannerManagement.put("bannerManagementId", delegator.getNextSeqId("BannerManagement"));
			bannerManagement.put("productStoreId",productStoreId);
			bannerManagement.put("catalogId",catalogId);
			bannerManagement.put("position",position);
			bannerManagement.put("fromDate",Timestamp.valueOf(fromDate));
			bannerManagement.put("thruDate",thruDate);
			bannerManagement.put("sequenceNum",Long.parseLong(sequenceNum));
			
			bannerManagement.put("groupName",groupName);
			bannerManagement.put("categoryName",categoryName);
			
			bannerManagement.put("bannerLinkUrl",bannerLinkUrl);
			bannerManagement.put("bannerImageUrl","/images/banners/"+fileName);
			
			delegator.create(bannerManagement);
		
			request.setAttribute("productStoreId",productStoreId);
			
		return "success";