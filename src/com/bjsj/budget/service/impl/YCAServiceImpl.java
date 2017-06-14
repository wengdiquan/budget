package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bjsj.budget.dao.YCADao;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.YCAService;
@Service("yCAServiceImpl")
public class YCAServiceImpl implements YCAService{
	@Autowired
	private YCADao yCADao;
	
	@Override
	public PageObject queryYCAInfo(Map<String, String> queryMap,PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<YCAModel> orderChangeHeaders = yCADao.queryYCAInfo(queryMap, pageInfo);
		pageObj.setRecords(yCADao.getYCAInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public void insertValue(YCAModel record) throws Exception {
		//查询是否存在
		int lookValueCount = yCADao.getYCACount(record);
		
		if(lookValueCount > 0){
			throw new Exception("费用代码存在无法新增"); 
		}
		yCADao.insertYCA(record);
	}
	@Override
	public void updateValue(YCAModel record) throws Exception {
		yCADao.updateByPrimaryKey(record);
	}

}
