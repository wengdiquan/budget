package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;

import com.bjsj.budget.model.UnitProjectDetail;

public interface UnitProjectDetailDao extends BaseDao {

	int deleteByPrimaryKey(Integer id);

	int insert(UnitProjectDetail record);

	UnitProjectDetail selectByPrimaryKey(Integer id);

	int updateByPrimaryKey(UnitProjectDetail record);

	/**
	 * 获取单位工程的子目的运材安详细信息
	 * @param queryMap
	 * @return
	 */
	List<UnitProjectDetail> getBitProjectDetailInfo(Map<String, String> queryMap);
	
	/**
	 * 根据子目删除所有details
	 * @param queryMap
	 */
	void deleteByUnitProjectId(Map<String, String> queryMap);}