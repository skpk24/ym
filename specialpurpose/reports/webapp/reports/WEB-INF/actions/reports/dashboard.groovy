import org.ofbiz.accounting.Reports;

imagelocation = Reports.viewDailyDashboard(delegator, session);
imagelocation1 = Reports.viewWeeklyDashboard(delegator, session);
imagelocation2 = Reports.viewMonthlyDashboard(delegator, session);
imagelocation3 = Reports.viewQuaterlyDashboard(delegator, session);
imagelocation4 = Reports.viewSalesDaily(delegator, session);
imagelocation5 = Reports.viewSalesWeekly(delegator, session);
imagelocation6 = Reports.viewSalesMonthly(delegator, session);
imagelocation7 = Reports.viewSalesQuarterly(delegator, session);
imagelocation8 = Reports.viewPurchaseDaily(delegator, session);
imagelocation9 = Reports.viewPurchaseWeekly(delegator, session);
imagelocation10 = Reports.viewPurchaseMonthly(delegator, session);
imagelocation11 = Reports.viewPurchaseQuarterly(delegator, session);
imagelocation12 = Reports.viewCreditDaily(delegator, session);
imagelocation13 = Reports.viewCustomerDaily(delegator, session);

imagelocation14 = Reports.viewCustomerWeekly(delegator, session);
imagelocation15 = Reports.viewCustomerMonthly(delegator, session);
imagelocation16 = Reports.viewCustomerQuarterly(delegator, session);
imagelocation17 = Reports.viewDamageDaily(delegator, session);
imagelocation18 = Reports.viewWastageWeekly(delegator, session);
imagelocation19 = Reports.viewWastageMonthly(delegator, session);
imagelocation20 = Reports.viewWastageQuarterly(delegator, session);

imagelocation22 = Reports.viewCreditWeekly(delegator, session);
imagelocation23 = Reports.viewCreditMonthly(delegator, session);
imagelocation24 = Reports.viewCreditQuarterly(delegator, session);







context.imagelocation = imagelocation
context.imagelocation1 = imagelocation1
context.imagelocation2 = imagelocation2
context.imagelocation3 = imagelocation3
context.imagelocation4 = imagelocation4
context.imagelocation5 = imagelocation5
context.imagelocation6 = imagelocation6
context.imagelocation7 = imagelocation7
context.imagelocation8 = imagelocation8
context.imagelocation9 = imagelocation9
context.imagelocation10 = imagelocation10
context.imagelocation11 = imagelocation11
context.imagelocation12 = imagelocation12
context.imagelocation13 = imagelocation13
context.imagelocation14 = imagelocation14
context.imagelocation15 = imagelocation15
context.imagelocation16 = imagelocation16
context.imagelocation17 = imagelocation17
context.imagelocation18 = imagelocation18
context.imagelocation19 = imagelocation19
context.imagelocation20 = imagelocation20

context.imagelocation22 = imagelocation22
context.imagelocation23 = imagelocation23
context.imagelocation24 = imagelocation24
print "\n\n\n\n radha \n\n\n\n"
print "\n\n\n ############################ groovy imagelocation = "+imagelocation
print "\n\n\n ############################ session value = "+session.getAttribute("imagelocation");
