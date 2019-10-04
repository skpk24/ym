package com.ilinks.restful.post;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.ServiceUtil;
public class UIPagination {

	static public Map<String, Object> paginate(EntityListIterator objectList, int viewIndex, int viewSize, String resource, String module) {

		Map<String, Object> paginateResult = ServiceUtil.returnSuccess();
		List<GenericValue> recordList = null;
		int listSize = 0;
		int lowIndex = 0;
		int highIndex = 0;
		int indexNumbers = 0;
		try {
			if (objectList != null) {
				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;
				recordList = objectList.getPartialList(lowIndex, viewSize);
				objectList.last();
				listSize = objectList.currentIndex();
				if (highIndex > listSize) {
					highIndex = listSize;
				}
				if (viewSize > 0) {
					if (listSize % viewSize == 0) {
						indexNumbers = (listSize / viewSize) - 1;
					}else {
						indexNumbers = listSize / viewSize;
					}
				}else {
					indexNumbers = 0;
				}
				// close the list iterator
				objectList.close();

			}
		} catch (GenericEntityException e) {
			paginateResult.put("_ERROR_MESSAGE_", "Error! Could not retrieve the records");
			Debug.logError(e,e.getMessage(), module);
		}
		if (recordList == null) {
			recordList = FastList.newInstance();
		}
		
		paginateResult.put("lowIndex", lowIndex);
		paginateResult.put("highIndex", highIndex);
		paginateResult.put("recordList", recordList);
		paginateResult.put("listSize", listSize);
		paginateResult.put("indexNumbers", indexNumbers);
		return paginateResult;

	}

}
