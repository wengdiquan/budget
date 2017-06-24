package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;

import com.bjsj.budget.model.UnitProject;

public interface UnitProjectDao extends BaseDao {

	int insert(UnitProject record);

	int updateByPrimaryKey(UnitProject record);
	
	/**
	 * 获取最大的序号
	 * @param queryMap
	 * @return
	 */
	Integer getMaxSeqByMap(Map<String, String> queryMap);
	
	/**
	 * 获取单位工程的子目信息
	 * @param queryMap
	 * @return
	 */
	List<UnitProject> getBitProjectItemInfo(Map<String, String> queryMap);
	
	/**
	 * 根据主键，删除子目
	 * @param queryMap
	 */
	void deleteByUnitProjectId(Map<String, String> queryMap);
	
	/**
	 * 根据ID 查询出来记录
	 * @param queryMap
	 * @return
	 */
	UnitProject getUnitProjectById(Map<String, String> queryMap);

	
	
}