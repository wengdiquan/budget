package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.CategoryModelService;
@Service("categoryModelServiceImpl")
public class CategoryModelServiceImpl implements CategoryModelService{
	@Autowired
	private CategoryModelDao categoryModelDao;
	
	@Override
	public List<CategoryModelModel> getCategoryModelList(
			Map<String, String> queryMap) {
		return categoryModelDao.getCategoryModelList(queryMap);
	}

	@Override
	public PageObject queryDetailCategoryModel(Map<String, String> queryMap,
			PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<CategoryModelModel> orderChangeHeaders = categoryModelDao.queryDetailCategoryModel(queryMap, pageInfo);
		pageObj.setRecords(categoryModelDao.getDetailCategoryModelCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public PageObject queryDetailYCA(Map<String, String> queryMap,
			PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<CategoryModelYCAModel> orderChangeHeaders = categoryModelDao.queryDetailYCA(queryMap, pageInfo);
		pageObj.setRecords(categoryModelDao.getDetailYCACount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}
}
