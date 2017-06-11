package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface LookValueDao extends BaseDao {

	List<LookValue> queryLookValueInfo(@Param("map") Map<String, String> queryMap,
			@Param("page") PageInfo pageInfo);
	
	int getLookValueInfoCount(@Param("map") Map<String, String> queryMap);
	
	List<Map> queryLookTypeInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	int getLookTypeInfoCount(@Param("map") Map<String, String> queryMap);
	
	int deleteByPrimaryKey(Integer lookvalueId);

	int insert(LookValue record);

	LookValue selectByPrimaryKey(Integer lookvalueId);

	int updateByPrimaryKey(LookValue record);
	
	/**
	 * 字典值新增的时候是否存在
	 * @param lv
	 * @return
	 */
	int getLookValueCountAdd(LookValue lv);
}