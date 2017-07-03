package com.bjsj.budget.service;

import java.util.Map;
import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface YCATotalService {
	/**
	 * 查询YCA 汇总
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryYCATotalInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 修改YCA 汇总
	 * @param record
	 * @throws Exception
	 */
	public void updateValue(YCATotalModel record) throws Exception;
	
	/** 
	 * 修改YCA 汇总  
	 * 金额修改
	 * @param record
	 * @throws Exception
	 */
	public void updateAmountValue(Map<String,String[]> map) throws Exception;

}
