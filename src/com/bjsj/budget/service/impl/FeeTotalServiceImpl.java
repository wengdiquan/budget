package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bjsj.budget.dao.FeeTotalDao;
import com.bjsj.budget.model.FeeTotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.FeeTotalService;
@Service("feeTotalServiceImpl")
public class FeeTotalServiceImpl implements FeeTotalService{
	@Autowired
	private FeeTotalDao feeTotalDao;
	
	@Override
	public PageObject queryFeeTotalInfo(Map<String, String> queryMap,PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<FeeTotalModel> orderChangeHeaders = feeTotalDao.queryFeeTotalInfo(queryMap, pageInfo);
		pageObj.setRecords(feeTotalDao.getFeeTotalInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public void updateValue(FeeTotalModel record) throws Exception {
		feeTotalDao.updateByPrimaryKey(record);
	}

}
