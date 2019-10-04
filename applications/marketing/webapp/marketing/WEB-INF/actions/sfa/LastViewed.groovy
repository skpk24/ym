import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.workeffort.workeffort.*;
import org.ofbiz.base.util.*;



//Recently view by logged in user


userLoginId =  userLogin.userLoginId ;

lastViewed = new ArrayList();
lastViewed = delegator.findList("ServerHit",
		EntityCondition.makeCondition([
			EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS,  userLoginId ),
			EntityCondition.makeCondition("referrerUrl", EntityOperator.NOT_LIKE, "%logout%")
		], EntityOperator.AND),
		null, null, null, false);
print "\n\n\n\n\n\n lastViewed " + lastViewed + "\n\n\n\n\n\n\n\n\n\n\n"
urlList = []
listSize = lastViewed.size() ;
int i = 0 ;
if(lastViewed.size()>10){
	while(true){
		listSize = listSize-1;
		i = i + 1 ;
		urls = [:] ;
		String s = lastViewed.get(listSize).contentId ;
		s = s.substring(s.indexOf('.')+1, s.length())
		urls.put("requestUrl",lastViewed.get(listSize).requestUrl);
		urls.put("contentId", s );
		urlList.add(urls);
		if(i>=10) break ;
	}
	context.urlList = urlList ;
}else{
	context.urlList = lastViewed ;
}
context.userLoginId = userLoginId ;


	
	
