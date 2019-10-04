Please Wait ......
<form method="post" action="/ordermgr/control/listOrders" id="manageOrder" name="manageOrder">
    <input type="hidden" name="orderStatusId" value="ORDER_APPROVED" id="manageOrder_orderStatusId"/>
    <input type="hidden" name="viewSize" value="10" id="manageOrder_viewSize"/>
    <input type="hidden" name="viewIndex" value="1" id="manageOrder_viewIndex"/>
    <input type="hidden" name="lookupFlag" value="Y" id="manageOrder_lookupFlag"/>
    <input type="hidden" name="hideFields" value="Y" id="manageOrder_hideFields"/>
    <input type="hidden" name="showAll" value="Y" id="manageOrder_showAll"/>
    <input type="hidden" name="noConditionFind" value="Y" id="manageOrder_noConditionFind"/>
</form>



<script type='text/javascript'> 
window.onload = function(){
	document.manageOrder.submit(); 
};
</script>