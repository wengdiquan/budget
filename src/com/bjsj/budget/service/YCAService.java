package com.bjsj.budget.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.Project;
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
	
	
	public List<LookValue> getLookValueList(Map<String, String> queryMap);
	
	public List<HashMap> getLookTypeList(Map<String, String> queryMap);
}
