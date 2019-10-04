// println "\n\n #################  website id  "+context.webSiteId
List list =delegator.findByAnd("ThemeAttribute", ["componentId":context.webSiteId]);
//println "\n\n ############## "+list
Iterator iterator = list.iterator();
Map colorMap = new HashMap();
while(iterator.hasNext())
 {
   GenericValue themeAttr = (GenericValue)iterator.next();
   if("maintabover".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("maintabover", themeAttr.get("attrValue"));
	  continue;
	}
	 if("maintabovercolor".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("maintabovercolor", themeAttr.get("attrValue"));
	  continue;
	}
	 if("mainnavigationsecondbg".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("mainnavigationsecondbg", themeAttr.get("attrValue"));
	  continue;
	}
	
	 if("maintaboversecondcolor".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("maintaboversecondcolor", themeAttr.get("attrValue"));
	  continue;
	}
	 if("mainnavigationthirdbg".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("mainnavigationthirdbg", themeAttr.get("attrValue"));
	  continue;
	}
	
	 if("maintabovercolor".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("maintabovercolor", themeAttr.get("attrValue"));
	  continue;
	}
	 if("mainnavigationbg".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("mainnavigationbg", themeAttr.get("attrValue"));
	  continue;
	}
	
	 if("screenlettitleimge".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("screenlettitleimge", themeAttr.get("attrValue"));
	  continue;
	}
	 if("screenlettitlecolor".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("screenlettitlecolor", themeAttr.get("attrValue"));
	  continue;
	}
	 if("screenletborder".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("screenletborder", themeAttr.get("attrValue"));
	  continue;
	}
	 if("basictablebg".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("basictablebg", themeAttr.get("attrValue"));
	  continue;
	}
	 if("basictablecolor".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("basictablecolor", themeAttr.get("attrValue"));
	  continue;
	}
	 if("basictablealtrow".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("basictablealtrow", themeAttr.get("attrValue"));
	  continue;
	}
	if("basictablesingle".equals(themeAttr.get("attrName")))
	{
	  colorMap.put("basictablesingle", themeAttr.get("attrValue"));
	  continue;
	}
 
}
//println "\n\n ###################### colorMap = "+colorMap
context.colorValue = colorMap;
