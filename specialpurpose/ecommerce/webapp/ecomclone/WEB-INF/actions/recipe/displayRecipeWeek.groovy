import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;

Map commandList=[:];
UserLogin = session.getAttribute("userLogin");
recipe = org.ofbiz.recipes.RecipeEvents.recipeOfTheWeekObject(request, response);
context.recipe = recipe;
	if(UtilValidate.isEmpty(recipe))
	{
		return "error";
	}
		
			gv = delegator.findByPrimaryKey("RecipeWeek", UtilMisc.toMap("recipeManagementId",recipe.getString("recipeManagementId")));
			
context.recipeWeek=gv;
if(UtilValidate.isEmpty(gv))
	{
		return "error";
	}
gv1 = delegator.findByPrimaryKey("UserLogin", [userLoginId : recipe.createdBy]);
if(UtilValidate.isNotEmpty(gv1))
{
	gv2=delegator.findByPrimaryKey("Person", [partyId : gv1.partyId]);
	if(UtilValidate.isNotEmpty(gv1))
	{
		if(UtilValidate.isNotEmpty(gv2.firstName) && UtilValidate.isNotEmpty(gv2.lastName))
		name=gv2.firstName+" "+gv2.lastName;
		else
			name=gv2.firstName;
		context.name=name;
	}
}

if(UtilValidate.isNotEmpty(UserLogin))
{

userGv=delegator.findByPrimaryKey("Person", [partyId : UserLogin.partyId]);
if(UtilValidate.isNotEmpty(userGv))
{
	if(UtilValidate.isNotEmpty(userGv.firstName) && UtilValidate.isNotEmpty(userGv.lastName))
	loginName=userGv.firstName+" "+userGv.lastName;
	else
loginName=userGv.firstName;
	context.loginName=loginName;
}
}

managementId = delegator.findList("RecipeWeek", EntityCondition.makeCondition("recipeManagementId",gv.recipeManagementId), null, null, null, false);
if(UtilValidate.isNotEmpty(managementId))
{
	gv4=EntityUtil.getFirst(managementId);
	List condn=[];
	condn.add(EntityCondition.makeCondition("recipeManagementId",gv4.recipeManagementId));
	condn.add(EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,null));
	condn.add(EntityCondition.makeCondition("statusId","RECP_COMM_APPROVED"));
	condn.add(EntityCondition.makeCondition("type","RECIPE_COMM_TYPE_BLO"));
	
	List<GenericValue> commentsList=delegator.findList("RecipeComments", EntityCondition.makeCondition(condn,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
context.recipeCommentList=commentsList;
	
}

context.userLogin = request.getSession().getAttribute("userLogin");



