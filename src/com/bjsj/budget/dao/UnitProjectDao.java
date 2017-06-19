package com.bjsj.budget.dao;

import com.bjsj.budget.model.UnitProject;

public interface UnitProjectDao extends BaseDao {

	int insert(UnitProject record);

	int updateByPrimaryKey(UnitProject record);
}