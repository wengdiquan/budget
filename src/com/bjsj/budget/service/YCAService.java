package com.bjsj.budget.service;

import java.util.Map;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface YCAService {
	/**
	 * 查询yCA
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryYCAInfo(Map<String, String> queryMap, PageInfo pageInfo);

	/**
	 * 新增YCA
	 * @param record
	 */
	public void insertValue(YCAModel record) throws Exception;
	/**
	 * 修改YCA
	 * @param record
	 * @throws Exception
	 */
	public void updateValue(YCAModel record) throws Exception;
}
