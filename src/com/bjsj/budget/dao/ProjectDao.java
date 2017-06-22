package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.Project;
import com.bjsj.budget.model.ReportModel;

public interface ProjectDao extends BaseDao {

	int deleteByPrimaryKey(Integer projectId);

	int insert(Project record);

	Project selectByMap(Map<String, String> queryMap);

	int updateByPrimaryKey(Project record);
	
	/**
	 * 查询项目
	 * @param queryMap
	 * @return
	 */
	int getCountProjectByMap(Map<String, Object> queryMap);
	
	/**
	 * 查询下级菜单
	 * @param queryMap
	 * @return
	 */
	List<Project> getLowerProjectTreeList(Map<String, String> queryMap);
	
	/**
	 * 项目  汇总数据
	 * @param queryMap
	 * @return
	 */
	List<ReportModel> getSumList(@Param("map")Map<String, String> queryMap);
	
	
	/**
	 * 单项工程  汇总数据
	 * @param queryMap
	 * @return
	 */
	List<ReportModel> getSumListUnit(@Param("map")Map<String, String> queryMap);
}