package com.bjsj.budget.service;

import java.util.Map;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface YCATotalService {
	/**
	 * 查询YCA 汇总
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryYCATotalInfo(Map<String, String> queryMap, PageInfo pageInfo);

}
