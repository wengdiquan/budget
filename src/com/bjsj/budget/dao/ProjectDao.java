package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;

import com.bjsj.budget.model.Project;

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
}