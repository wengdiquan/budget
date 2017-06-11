package com.bjsj.budget.service;

import java.util.Map;

import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface LookValueService {
	
	/**
	 * 查询字典信息
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryLookValueInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询字典类型
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryLookTypeInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 新增字典值
	 * @param lv
	 */
	public void insertValue(LookValue lv) throws Exception;
	
	/**
	 * 更新字典值
	 * @param lv
	 */
	public void updateValue(LookValue lv) throws Exception;

}
