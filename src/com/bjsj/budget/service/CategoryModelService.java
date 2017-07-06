package com.bjsj.budget.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface CategoryModelService {
	
	/**
	 * 查询左侧 模块  树状数据
	 * @param queryMap
	 * @return
	 */
	public List<CategoryModelModel> getCategoryModelList(Map<String, String> queryMap);
	
	/**
	 * 查询右侧 上部分 序里的数据
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryDetailCategoryModel(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询右侧下方部分 运材安数据
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryDetailYCA(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 新增模块 YCA
	 * @param ycaModelId 
	 * @param record
	 */
	public void insertValue(Map<String, String[]> map, String ycaModelId) throws Exception;
	
	/**
	 * 新增模块 
	 * @param record
	 */
	public void insertCM(Map<String, String[]> map) throws Exception;
	
	/**
	 * 新增 章或者节
	 * @param record
	 */
	public void insertChapter(CategoryModelModel record) throws Exception;
	
	/**
	 * 修改 模块 YCA
	 * @param record
	 * @throws Exception
	 */
	public void updateValue(Map<String, String[]> map) throws Exception;
	
	/**
	 * 修改 模块 
	 * @param record
	 * @throws Exception
	 */
	public void updateValueCM(CategoryModelModel record) throws Exception;
	
	public CategoryModelYCAModel selectByPrimaryKey(Map<String, String> queryMap);
	
}
