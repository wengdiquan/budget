package com.bjsj.budget.service;

import java.util.List;
import java.util.Map;
import com.bjsj.budget.model.FeeTotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface FeeTotalService {
	/**
	 * 查询费用 汇总
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryFeeTotalInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询费用 汇总 模板
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	public PageObject queryFeeTemplate(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 修改 费用汇总
	 * @param record
	 * @throws Exception
	 */
	public void updateValue(FeeTotalModel record) throws Exception;

	/**
	 * 插入费用模板
	 * @param record
	 * @throws Exception
	 */
	public void insertValue(List<FeeTotalModel> record) throws Exception;
}
