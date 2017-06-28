package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.bjsj.budget.model.FeeTotalModel;
import com.bjsj.budget.page.PageInfo;

public interface FeeTotalDao extends BaseDao{
	List<FeeTotalModel> queryFeeTotalInfo(@Param("map") Map<String, String> queryMap, @Param("page") PageInfo pageInfo);
	int getFeeTotalInfoCount(@Param("map") Map<String, String> queryMap);
	
	int updateByPrimaryKey(FeeTotalModel record);
}
