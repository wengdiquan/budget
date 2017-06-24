package com.bjsj.budget.service;

import java.util.Map;

import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface UnitProjectService {
	
	/**
	 * 新增单位i工程的子目
	 * @param queryMap
	 */
	void insertItem(Map<String, String> queryMap) throws Exception;
	
	PageObject getBitProjectItemInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询子目的运材安详细信息
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	PageObject getBitProjectDetailInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 新增空的单位工程的单项工程
	 * @param queryMap
	 */
	void insertBlankItem(Map<String, String> queryMap);

	/**
	 * 删除单位工程的子目
	 * @param queryMap
	 * @throws Exception 
	 */
	void deleteBitProjectItem(Map<String, String> queryMap) throws Exception;
}
