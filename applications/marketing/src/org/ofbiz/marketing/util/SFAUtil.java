package org.ofbiz.marketing.util;

import java.math.BigDecimal;
import java.util.List;
import org.ofbiz.base.util.*;

public class SFAUtil {

	/**
	 * 
	 * @param list
	 * @return sum of elements of a list
	 */
	public static BigDecimal sumArrayListElements(List<BigDecimal> list) {
		BigDecimal sum = BigDecimal.ZERO;
		if (!UtilValidate.isEmpty(list)) {
			for (BigDecimal i : list) {
				if (!UtilValidate.isEmpty(i)) {
					sum = sum.add(i);
				}
			}
		}
		return sum;
	}
	
	
	
}
