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
	void deleteByUnitProjectId(Map<String, String> queryMap);
	
	/**
	 * 根据Id 获取 明细
	 * @param queryMap
	 * @return
	 */
	UnitProjectDetail getUnitProjectDetailById(Map<String, String> queryMap);
	
	/**
	 * 获取编号
	 * @param queryMap
	 * @return 
	 */
	Integer getNextValue(Map<String, String> queryMap);
	
	/**
	 * 查询最大到Seq
	 * @param queryMap
	 * @return
	 */
	Integer getMaxSeqByMap(Map<String, String> queryMap);}