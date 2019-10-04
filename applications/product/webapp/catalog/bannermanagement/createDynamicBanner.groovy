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
			String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/dynamicBanners";

			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());
			 
			   
	        String imageName = mr.getParameter("imageName");
			String imageContent = mr.getParameter("imageContent");
			String imageType = mr.getParameter("imageType");
			String imageLinkUrl = mr.getParameter("imageLinkUrl");
			String imageSequenceNum = mr.getParameter("imageSequenceNum"); 
				
			if(UtilValidate.isEmpty(imageSequenceNum)) imageSequenceNum = "1";
			if(UtilValidate.isEmpty(imageType)) imageType = "BANNER"; 
			 
			try{
				GenericValue  bannerManagement = delegator.makeValue("DynamicBannerManagement");
					bannerManagement.put("bannerManagementId", delegator.getNextSeqId("DynamicBannerManagement"));
					bannerManagement.put("imageName",imageName);
					bannerManagement.put("imageContent",imageContent);
					bannerManagement.put("imageType",imageType);
					bannerManagement.put("imagePath","/images/dynamicBanners/"+fileName);
					bannerManagement.put("imageLinkUrl",imageLinkUrl);
					bannerManagement.put("imageSequenceNum",Long.parseLong(imageSequenceNum));
				delegator.create(bannerManagement); 
				 
			}catch (Exception e) {
				e.printStackTrace();
			}
		return "success";