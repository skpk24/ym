
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
			
			String bannerManagementId = request.getParameter("bannerManagementId");
			String productStoreId = request.getParameter("productStoreId");
			
			if(UtilValidate.isEmpty(bannerManagementId))return "error";
			
			
			GenericValue bannerManagement = delegator.findOne("BannerManagement", UtilMisc.toMap("bannerManagementId",bannerManagementId), false);
			
			bannerManagement.remove();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	