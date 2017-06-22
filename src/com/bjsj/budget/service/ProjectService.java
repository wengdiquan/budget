package com.bjsj.budget.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.Project;
import com.bjsj.budget.model.ReportModel;

public interface ProjectService {
	
	/**
	 * 更新项目
	 * @param project
	 */
	void updateProject(Project project) throws Exception;
	
	/**
	 * 新增项目
	 * @param project
	 */
	void insertProject(Project project) throws Exception;
	
	/**
	 * 查询该条信息
	 * @param queryMap
	 * @return 
	 */
	Project getProjectRoot(Map<String, String> queryMap);
	
	/**
	 * 查询下级菜单
	 * @param queryMap
	 * @return
	 */
	List<Project> getLowerProjectTreeList(Map<String, String> queryMap);
	
	/**
	 * 项目，单位工程  汇总数据
	 * @param queryMap
	 * @return
	 */
	List<ReportModel> getSumList(@Param("map")Map<String, String> queryMap);
	
}
