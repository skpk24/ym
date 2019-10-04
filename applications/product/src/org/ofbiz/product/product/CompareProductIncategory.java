package org.ofbiz.product.product;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;

import org.ofbiz.service.DispatchContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.entity.GenericDelegator;
public class CompareProductIncategory {
	
	public static String getCategoryDetail(HttpServletRequest request , HttpServletResponse response)
	{
		try
		{
		PrintWriter out=response.getWriter();
	
		String catalogId = CatalogWorker.getCurrentCatalogId(request);
		GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
		Set fieldToSelect =new HashSet();
		fieldToSelect.add("productCategoryId");
		List orderBy=new ArrayList();
		orderBy.add("productCategoryId");
		String optionValue = "<option>Select</option>";
		try
		{
			List conditionList =new ArrayList();
			String whereString = "prod_catalog_id = '"+catalogId+"' AND product_category_id IS NOT NULL";
			EntityWhereString ew = EntityWhereString.makeConditionWhere(whereString);
			//conditionList.add(EntityCondition.makeCondition("prodCatalogId",EntityOperator.EQUALS,catalogId));
			//conditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.NOT_EQUAL ,null));
			//EntityCondition conditon = ew.freeze();
			
			List <GenericValue>cateogryList=delegator.findList("ProdCatalogCategory",EntityCondition.makeCondition("prodCatalogId",EntityOperator.EQUALS,catalogId) ,fieldToSelect, null, null, true);
			List categoryIds =new ArrayList();
			for (GenericValue gv : cateogryList)
			{
				if(gv.get("productCategoryId")!=null ||gv.get("productCategoryId")!="" )
				{
					categoryIds.add(gv.get("productCategoryId"));
				}
			}       
			
			List productCatRollup =delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition("parentProductCategoryId",EntityOperator.IN,categoryIds),fieldToSelect, orderBy, null, true);
			List prodCatRollupList =EntityUtil.getFieldListFromEntityList(productCatRollup, "productCategoryId", true);
			prodCatRollupList.addAll(categoryIds);
			List productCategory=delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId",EntityComparisonOperator.IN,prodCatRollupList),fieldToSelect, orderBy, null, true);
			List productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
			orderBy=new ArrayList();
			orderBy.add("categoryName");
			List <GenericValue>categoryAndNameList=(List <GenericValue>)delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryId",EntityComparisonOperator.IN,productCategoryIds),null, orderBy, null, true);
			
			for(Object genericObject : categoryAndNameList)
			{   
				GenericValue genericVal=(GenericValue)genericObject;
				optionValue = optionValue + "<option value='"+genericVal.get("productCategoryId")+"'>"+genericVal.get("categoryName")+"</option>";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("\n\n\n\\n request has been called  and catalog id ="+catalogId);
		
		request.setAttribute("optionValue", optionValue);
	
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String getProductForcategory(HttpServletRequest request , HttpServletResponse response)
	{
		try
		{
				PrintWriter out=response.getWriter();
			    String categoryId=request.getParameter("categoryId");
			    
			    //System.out.println("\n\n \n  in getProductForcategory and category id = "+categoryId);
				GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
				Set fieldToSelect =new HashSet(); 
				fieldToSelect.add("productId");
				List productCategoryMember=delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId",EntityComparisonOperator.EQUALS,categoryId),fieldToSelect, null, null, true);
				List productIds =EntityUtil.getFieldListFromEntityList(productCategoryMember, "productId", true);
				String optionValue="<option>Select</option>";
				if(productIds.size()<=0)
				{  
					//System.out.println("\n\n \n  in getProductForcategory error part = ");
					optionValue="NO PRODUCT";
					
				}
				else
				{
					List productDetail = delegator.findList("Product", EntityCondition.makeCondition("productId",EntityComparisonOperator.IN,productIds),null, null, null, true);
					
					for(Object genericObject : productDetail)
					{   
						GenericValue genericVal=(GenericValue)genericObject;
						optionValue = optionValue + "<option value='"+genericVal.get("productId")+"'>"+genericVal.get("productName")+"</option>";
					}
					 //System.out.println("\n\n \n  in getProductForcategory and optionValue  = "+optionValue);
					 
				}
				out.println(optionValue);
				
		}
		 catch(Exception exception)
		 {
			 exception.printStackTrace();
		 }
		  return "success";
}
	public static String checkForProductCompareAllow(HttpServletRequest request)
	{
		try
		{
			String catalogId = CatalogWorker.getCurrentCatalogId(request);
			GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
			Set fieldToSelect =new HashSet();
			fieldToSelect.add("allowProductCompare");
			GenericValue catalogGv=delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId",catalogId));
			String check= (String)catalogGv.get("allowProductCompare");
			if(check!=null)
			{
				if("Y".equalsIgnoreCase(check))
				{
					return "Y";
				}
				else
				{
					return "N";
				}
			 }
			else
			{
				return "N";
			}
			
			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			return "N";
		}
	}
}