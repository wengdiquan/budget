package com.bjsj.budget.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;

public interface YCATotalDao extends BaseDao{
	List<YCATotalModel> queryYCATotalInfo(@Param("map") Map<String, String> queryMap, @Param("page") PageInfo pageInfo);
	int getYCATotalInfoCount(@Param("map") Map<String, String> queryMap);
}
