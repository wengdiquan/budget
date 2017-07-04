package com.bjsj.budget.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.UnitProjectDao;
import com.bjsj.budget.dao.YCATotalDao;
import com.bjsj.budget.model.UnitProject;
import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.YCATotalService;
import com.bjsj.budget.util.NumberUtils;
import com.bjsj.budget.util.TransforUtil;
@Service("yCATotalServiceImpl")
public class YCATotalServiceImpl implements YCATotalService{
	@Autowired
	private YCATotalDao yCATotalDao;
	@Autowired
	private UnitProjectDao unitProjectDao;
	
	@Override
	public PageObject queryYCATotalInfo(Map<String, String> queryMap,PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<YCATotalModel> orderChangeHeaders = yCATotalDao.queryYCATotalInfo(queryMap, pageInfo);
		pageObj.setRecords(yCATotalDao.getYCATotalInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public void updateValue(YCATotalModel record) throws Exception {
		yCATotalDao.updateByPrimaryKey(record);
	}

	@Override
	public void updateAmountValue(Map<String,String[]> map) throws Exception {
		YCATotalModel record = new YCATotalModel();
		TransforUtil.transFromMapToBean(map, record);
		
		Map<String, String> queryMap = TransforUtil.transRMapToMap(map);
		
		String value = queryMap.get("value");
		record.setTax_Price(Double.parseDouble(value));
		
		YCATotalModel ycamodel = yCATotalDao.getYCATotalList(queryMap).get(0);
		Double oldTax_Price = ycamodel.getTax_Price();//含税单价
		Double oldNoTax_Price = ycamodel.getNotax_Price();//不含税单价
		
		//1.按照当前单位工程下的全部编码修改，修改后的含税单价， 计算 含税合价 = 含税单价 * 数量，
		//2.核心，修改后，会导致子目里的 综合合价，综合单价 改变，只计算改变的金额，这样用原金额 + 改变的金额 就可以了
		Double Tax_Single_SumPrice = NumberUtils.degree(ycamodel.getAmount() * record.getTax_Price());
		record.setTax_Single_SumPrice(Tax_Single_SumPrice);
		Double Notax_Price = NumberUtils.degree(record.getTax_Price()/1.17);
		record.setNotax_Price(Notax_Price);
		Double Single_SumPrice = NumberUtils.degree(ycamodel.getAmount() * record.getNotax_Price());
		record.setSingle_SumPrice(Single_SumPrice);
		yCATotalDao.updateByPrimaryKey(record);
		
		//UnitProject upVO = unitProjectDao.getUnitProjectById(queryMap);
		List<UnitProject> list = yCATotalDao.getUnitProjectList(queryMap);
		for(UnitProject upVO : list){
			//含税单价= 综合单价 = sum(含税单价*含量) = (用原金额 + 改变的金额 就可以了)
			//含税合价 = 综合合价  = sum（含税单价*数量）= (用原金额 + 改变的金额 就可以了)
			//不含税单价= sum(不含税单价*含量) = (用原金额 + 改变的金额 就可以了)
			//不含税合价 = sum（不含税单价*数量）= (用原金额 + 改变的金额 就可以了)
			
			Double chajia = NumberUtils.degree(record.getTax_Price()* upVO.getContent()) - 
					NumberUtils.degree(oldTax_Price * upVO.getContent());
			Double chajiaH = NumberUtils.degree(record.getTax_Price()* ycamodel.getAmount()) - 
					NumberUtils.degree(oldTax_Price * ycamodel.getAmount());
			Double chajiaBHD = NumberUtils.degree(record.getNotax_Price()* upVO.getContent()) - 
					NumberUtils.degree(oldNoTax_Price * upVO.getContent());
			Double chajiaBHH = NumberUtils.degree(record.getNotax_Price()* ycamodel.getAmount()) - 
					NumberUtils.degree(oldNoTax_Price * ycamodel.getAmount());
			
			UnitProject finalUP = new UnitProject();
			finalUP.setId(upVO.getId());
			finalUP.setTaxSinglePrice(upVO.getTaxSinglePrice() + chajia);//含税单价
			finalUP.setPrice(upVO.getTaxSinglePrice());//综合单价
			
			finalUP.setSumPrice(upVO.getSumPrice() + chajiaH);//综合合价
			finalUP.setTaxSingleSumPrice(upVO.getSumPrice());//含税合价
			
			finalUP.setSinglePrice(upVO.getSinglePrice() + chajiaBHD);// 不含税单价
			finalUP.setSingleSumPrice(upVO.getSingleSumPrice() + chajiaBHH);// 不含税合价
			
			yCATotalDao.updateUnitByPrimaryKey(finalUP);
		}
		
	}

}
