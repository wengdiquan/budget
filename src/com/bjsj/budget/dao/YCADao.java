package com.bjsj.budget.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;

public interface YCADao extends BaseDao{
	List<YCAModel> queryYCAInfo(@Param("map") Map<String, String> queryMap, @Param("page") PageInfo pageInfo);
	int getYCAInfoCount(@Param("map") Map<String, String> queryMap);
	int insertYCA(YCAModel record);
	int getYCACount(YCAModel record);
	int updateByPrimaryKey(YCAModel record);
	List<LookValue> getLookValueList(@Param("map")Map<String, String> queryMap);
	List<HashMap> getLookTypeList(@Param("map")Map<String, String> queryMap);
}
