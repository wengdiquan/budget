package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.page.PageInfo;

public interface CategoryModelDao extends BaseDao{
	
		List<CategoryModelModel> getCategoryModelList(@Param("map")Map<String, String> queryMap);
	
		List<CategoryModelModel> queryDetailCategoryModel(@Param("map") Map<String, String> queryMap, @Param("page") PageInfo pageInfo);
		int getDetailCategoryModelCount(@Param("map") Map<String, String> queryMap);
		
		List<CategoryModelYCAModel> queryDetailYCA(@Param("map") Map<String, String> queryMap, @Param("page") PageInfo pageInfo);
		int getDetailYCACount(@Param("map") Map<String, String> queryMap);
		
		
		
		int insertCMYCA(CategoryModelYCAModel record);
		//int getYCACount(YCAModel record);
		int updateByPrimaryKey(CategoryModelYCAModel record);
		
		int updateCMByPrimaryKey(CategoryModelModel record);
		
		CategoryModelYCAModel selectByPrimaryKey(@Param("map") Map<String, String> queryMap);
		
		CategoryModelModel selectCMByPrimaryKey(@Param("map") Map<String, String> queryMap);
		
		//List<HashMap> getLookTypeList(@Param("map")Map<String, String> queryMap);

}
