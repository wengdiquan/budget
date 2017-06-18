package com.bjsj.budget.service;

import java.util.List;
import java.util.Map;
import com.bjsj.budget.model.CategoryModelModel;
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
	
}
