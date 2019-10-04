
productCategoryId = context.productCategoryId
context.catList = org.ofbiz.product.category.CategoryWorker.getCategoryName123(request,delegator,productCategoryId);
context.childCatList = org.ofbiz.product.category.CategoryWorker.getRelatedCategoriesRet(request, "subCatLists1", productCategoryId, true);

java.util.List filterKeys = new java.util.ArrayList();
filterKeys.add("Less than Rs 20 ");
filterKeys.add("Rs 21 to 50 ");
filterKeys.add("Rs 51 to 100 ");
filterKeys.add("Rs 101 to 200 ");
filterKeys.add("Rs 201 to 500 ");
filterKeys.add("More than Rs 501 ");


context.filterKeys = filterKeys;
