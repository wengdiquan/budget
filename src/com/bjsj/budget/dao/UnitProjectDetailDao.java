package com.bjsj.budget.dao;

import com.bjsj.budget.model.UnitProjectDetail;

public interface UnitProjectDetailDao extends BaseDao {

	int deleteByPrimaryKey(Integer id);

	int insert(UnitProjectDetail record);

	UnitProjectDetail selectByPrimaryKey(Integer id);

	int updateByPrimaryKey(UnitProjectDetail record);
}