package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.LookValueDao;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.LookValueService;

@Service("lookValueServiceImpl")
public class LookValueServiceImpl implements LookValueService {

	@Autowired
	private LookValueDao lookValueDao;

	@Override
	public PageObject queryLookValueInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<LookValue> orderChangeHeaders = lookValueDao.queryLookValueInfo(queryMap, pageInfo);
		pageObj.setRecords(lookValueDao.getLookValueInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}
	
	
	@Override
	public PageObject queryLookTypeInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<Map> orderChangeHeaders = lookValueDao.queryLookTypeInfo(queryMap, pageInfo);
		pageObj.setRecords(lookValueDao.getLookTypeInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}
	
	@Override
	public void updateValue(LookValue lv) throws Exception {
		lookValueDao.updateByPrimaryKey(lv);
	}
	
	@Override
	public void insertValue(LookValue lv) throws Exception {
		
		//查询是否存在
		int lookValueCount = lookValueDao.getLookValueCountAdd(lv);
		
		if(lookValueCount > 0){
			throw new Exception("费用代码或名称存在无法新增"); 
		}
		lookValueDao.insert(lv);
	}
	
}
