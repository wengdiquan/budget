package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bjsj.budget.dao.YCATotalDao;
import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.YCATotalService;
@Service("yCATotalServiceImpl")
public class YCATotalServiceImpl implements YCATotalService{
	@Autowired
	private YCATotalDao yCATotalDao;
	
	@Override
	public PageObject queryYCATotalInfo(Map<String, String> queryMap,PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<YCATotalModel> orderChangeHeaders = yCATotalDao.queryYCATotalInfo(queryMap, pageInfo);
		pageObj.setRecords(yCATotalDao.getYCATotalInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

}
