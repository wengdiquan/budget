package com.bjsj.budget.service;

import java.util.Map;

import com.bjsj.budget.model.UnitProject;
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
	
	/**
	 * 修改数据
	 * @param queryMap
	 */
	void changeSeq(Map<String, String> queryMap);
	
	/**
	 * 更新子目字段
	 * @param queryMap
	 */
	void updateItemAndDetail(Map<String, String> queryMap);
	
	/**
	 * 刷新当前行
	 * @param queryMap
	 */
	UnitProject getItemById(Map<String, String> queryMap);
	
	/**
	 * 新增详细值
	 * @param queryMap
	 */
	void insertDetail(Map<String, String> queryMap);
	
	/**
	 * 删除详细值
	 * @param queryMap
	 */
	void deleteBitProjectDetail(Map<String, String> queryMap);
	
	/**
	 * 更新运材安的值
	 * @param queryMap
	 */
	void updateDetailAndItem(Map<String, String> queryMap);
}
