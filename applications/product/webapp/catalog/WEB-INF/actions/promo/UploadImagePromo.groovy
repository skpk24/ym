import java.util.*;
import java.sql.*;
import java.io.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import org.ofbiz.widget.html.*;


nowTimestamp = UtilDateTime.nowTimestamp();
context.put("nowTimestamp", nowTimestamp);
promodetails=null;
String productPromoId = request.getParameter("productPromoId");
if(productPromoId != null){
         promodetails = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
         context.put("firstImageUrl",promodetails.get("firstImageUrl"));
}
String nowTimestampString = nowTimestamp.toString();
context.put("nowTimestampString", nowTimestampString);


imageFilenameFormat = UtilProperties.getPropertyValue('catalog', 'image.filename.format');
imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), context);

imageUrlPrefix = UtilProperties.getPropertyValue('catalog', 'image.url.prefix');

context.imageFilenameFormat = imageFilenameFormat;
context.imageServerPath = imageServerPath;
context.imageUrlPrefix = imageUrlPrefix;

//FlexibleStringExpander filenameExpander = new FlexibleStringExpander(imageFilenameFormat);
//context.put("imageNamePromo", imageUrlPrefix + "/" + filenameExpander.expandString(UtilMisc.toMap("location","promo","type", "medium", "id", productPromoId)));

filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
context.put("imageNameFirst", imageUrlPrefix + "/" + filenameExpander.expandString(UtilMisc.toMap("location","promo", "type", "first", "id", productPromoId)));
context.put("imageNameSecond", imageUrlPrefix + "/" + filenameExpander.expandString(UtilMisc.toMap("location","promo","type", "second", "id", productPromoId)));
context.put("imageNameThird", imageUrlPrefix + "/" + filenameExpander.expandString(UtilMisc.toMap("location","promo","type", "third", "id", productPromoId)));




//Uploading Images
Object forLock = new Object();
String contentType = null;
String fileType = request.getParameter("fname");

if(fileType != null)
{ 
    if("first".equals(fileType))
    {
        context.put("imageNamePromo1",  productPromoId);
    }    
    if("second".equals(fileType))
    {
        context.put("imageNamePromo2",  productPromoId);
    }    
    if("third".equals(fileType))
    {
        context.put("imageNamePromo3",  productPromoId);
    }    
    if("fourth".equals(fileType))
    {
        context.put("imageNamePromo4",  productPromoId);
    }    
}

if(fileType!=null ){
	
	String fileLocation = filenameExpander.expandString(UtilMisc.toMap("location", "promo","type", fileType,"id", productPromoId));
	String filePathPrefix = "";
	String filenameToUse = fileLocation;
	if (fileLocation.lastIndexOf("/") != -1) {
		filePathPrefix = fileLocation.substring(0, fileLocation.lastIndexOf("/") + 1); // adding 1 to include the trailing slash
		filenameToUse = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
	}
	
	if (contentType != null && (i1 = contentType.indexOf("boundary=")) != -1) {
	   contentType = contentType.substring(i1 + 9);
	   contentType = "--" + contentType;
	}

	String defaultFileName = filenameToUse + "_temp";
	HttpRequestFileUpload uploadObject = new HttpRequestFileUpload();
	uploadObject.setOverrideFilename(defaultFileName);
	uploadObject.setSavePath(imageServerPath + "/" + filePathPrefix);
	uploadObject.doUpload(request);
	String clientFileName = uploadObject.getFilename();
	if (clientFileName != null) context.put("clientFileName", clientFileName);
	if (clientFileName != null && clientFileName.length() > 0) {
	if (clientFileName.lastIndexOf(".") > 0 && clientFileName.lastIndexOf(".") < clientFileName.length()) {
		filenameToUse += clientFileName.substring(clientFileName.lastIndexOf("."));
	} else {
		filenameToUse += ".jpg";
	}
		
	context.put("clientFileName", clientFileName);        
	context.put("filenameToUse", filenameToUse);
			
	String characterEncoding = request.getCharacterEncoding();
	String imageUrl = imageUrlPrefix + "/" + filePathPrefix + java.net.URLEncoder.encode(filenameToUse, characterEncoding);
			
	try {
	   File file = new File(imageServerPath + "/" + filePathPrefix, defaultFileName);
	   File file1 = new File(imageServerPath + "/" + filePathPrefix, filenameToUse);
	   try {
		   file1.delete();
	   } catch(Exception e) { 
	   }
	   file.renameTo(file1);
	} catch(Exception e) { 
	   e.printStackTrace();
	}
		
	if(imageUrl != null && imageUrl.length() > 0) {
	   context.put("imageUrl", imageUrl);
	   promodetails.set(fileType + "ImageUrl", imageUrl);
	   promodetails.store();
	}
	}

}



