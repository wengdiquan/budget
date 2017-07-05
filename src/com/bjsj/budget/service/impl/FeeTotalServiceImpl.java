package com.bjsj.budget.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bjsj.budget.dao.FeeTotalDao;
import com.bjsj.budget.model.FeeTemplateModel;
import com.bjsj.budget.model.FeeTotalModel;
import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.FeeTotalService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
	
	@Override
	public void updateValueRadix(FeeTotalModel record) throws Exception {
		//to-do  tm_unitproject_detail  判断编码，最后一个编码
		//可以删光 计算基数，基数说明 和 金额跟着改
		if(record.getCalculatedRadix()!= null && !"".equals(record.getCalculatedRadix())){
			String caRa = record.getCalculatedRadix();
			if(caRa.endsWith("+")){
				throw new Exception("非法表达式：" + caRa); 
			}
			String[] strA = record.getCalculatedRadix().split("\\+");
			String radixRemark = "";
			Double amount = (double) 0;
			YCATotalModel model = new YCATotalModel();
			HashMap map = new HashMap();
			map.put("bitProjectId", record.getBitProjectId());
			for(int i = 0; i<strA.length; i++){
				if("".equals(strA[i]) || strA[i] == null){
					continue;
				}
				map.put("code", strA[i]);
				model = feeTotalDao.selectByPrimaryKeyYCA(map);
				if(model == null){
					throw new Exception("非法表达式：" + caRa); 
				}
				radixRemark = radixRemark + "+" + model.getName();
				amount = amount + model.getTax_Single_SumPrice();
			}
			record.setRadixRemark(radixRemark.substring(1));
			record.setAmount(amount);
		}else{
			record.setCalculatedRadix("");
			record.setRadixRemark("");
			record.setAmount((double)0);
		}
		feeTotalDao.updateByPrimaryKey(record);
	}

	@Override
	public void insertValue(Map<String,String[]> map) throws Exception {
		String temList = map.get("Temvo")[0];
		String tempalteName = map.get("name")[0];
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("name", tempalteName);
		FeeTemplateModel tmpvo = feeTotalDao.selectByPrimaryKey(hm);
		if(tmpvo != null){
			throw new Exception("模板名称存在，请重新输入"); 
		}
		
		FeeTemplateModel temVO = new FeeTemplateModel();
		temVO.setName(tempalteName);
		temVO.setId(null);
		feeTotalDao.insertTemplateTable(temVO);
		tmpvo = feeTotalDao.selectByPrimaryKey(hm);
		
		JSONArray jsonArray=JSONArray.fromObject(temList);
		for(int i = 0; i < jsonArray.size(); i++){
			Object o=jsonArray.get(i);
			JSONObject jsonObject=JSONObject.fromObject(o);
			FeeTotalModel vo=(FeeTotalModel)JSONObject.toBean(jsonObject, FeeTotalModel.class);
			vo.setId(null);
			vo.setTempletId(tmpvo.getId());
			feeTotalDao.insertTemplate(vo);
		}
		
	}

	@Override
	public PageObject queryFeeTemplate(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<FeeTemplateModel> orderChangeHeaders = feeTotalDao.queryFeeTemplate(queryMap, pageInfo);
		pageObj.setRecords(feeTotalDao.getFeeTemplateCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public void insertValueNull(FeeTotalModel record) throws Exception {
		record.setId(null);
		feeTotalDao.insertTemplate(record);
	}

}
