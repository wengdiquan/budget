package com.bjsj.budget.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.constant.Constant;
import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.CategoryModelService;
import com.bjsj.budget.util.NumberUtils;
import com.bjsj.budget.util.TransforUtil;
@Service("categoryModelServiceImpl")
public class CategoryModelServiceImpl implements CategoryModelService{
	@Autowired
	private CategoryModelDao categoryModelDao;
	
	@Override
	public List<CategoryModelModel> getCategoryModelList(
			Map<String, String> queryMap) {
		return categoryModelDao.getCategoryModelList(queryMap);
	}

	@Override
	public PageObject queryDetailCategoryModel(Map<String, String> queryMap,
			PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<CategoryModelModel> orderChangeHeaders = categoryModelDao.queryDetailCategoryModel(queryMap, pageInfo);
		pageObj.setRecords(categoryModelDao.getDetailCategoryModelCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public PageObject queryDetailYCA(Map<String, String> queryMap,
			PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<CategoryModelYCAModel> orderChangeHeaders = categoryModelDao.queryDetailYCA(queryMap, pageInfo);
		pageObj.setRecords(categoryModelDao.getDetailYCACount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public void insertValue(Map<String, String[]> map) throws Exception {
		CategoryModelYCAModel record = new CategoryModelYCAModel();
		CategoryModelModel cm = new CategoryModelModel();
		TransforUtil.transFromMapToBean(map, record);
		TransforUtil.transFromMapToBean(map, cm);
		
		Double db = record.getPrice()/(1+record.getRate()/100);
		record.setNoPrice(NumberUtils.degree(db));
		db = record.getNoPrice() * record.getAmount();
		record.setSumNoPrice(NumberUtils.degree(db));
		db = record.getPrice() * record.getAmount();
		record.setSumPrice(NumberUtils.degree(db));
		
		categoryModelDao.insertCMYCA(record);
		
		Map queryMap = new HashMap();
		queryMap.put("id", record.getId());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		if(Constant.YSFLAG.equals(record.getFlag())){
			cm.setTransportFee(cmVO.getTransportFee() + record.getSumNoPrice());
		}else if(Constant.CLFLAG.equals(record.getFlag())){
			cm.setMaterialFee(cmVO.getMaterialFee() + record.getSumNoPrice());
		}else if(Constant.AZFLAG.equals(record.getFlag())){
			cm.setInstallationFee(cmVO.getInstallationFee() + record.getSumNoPrice());
		}
		categoryModelDao.updateCMByPrimaryKey(cm);
		
	}

	@Override
	public void updateValue(Map<String, String[]> map) throws Exception {
		
		CategoryModelYCAModel record = new CategoryModelYCAModel();
		CategoryModelModel cm = new CategoryModelModel();
		
		TransforUtil.transFromMapToBean(map, record);
		TransforUtil.transFromMapToBean(map, cm);
		
		Map queryMap = new HashMap();
		queryMap.put("id", record.getId());
		CategoryModelYCAModel tmVO = categoryModelDao.selectByPrimaryKey(queryMap);
		double oldP = tmVO.getPrice();
		double newP = record.getPrice();
		
		Double db = record.getPrice()/(1+record.getRate()/100);
		record.setNoPrice(NumberUtils.degree(db));
		db = record.getNoPrice() * record.getAmount();
		record.setSumNoPrice(NumberUtils.degree(db));
		db = record.getPrice() * record.getAmount();
		record.setSumPrice(NumberUtils.degree(db));
		
		categoryModelDao.updateByPrimaryKey(record);
		
		queryMap.put("id", record.getId());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		
		if(tmVO.getPrice() != record.getPrice()){
			String tmp = record.getCode();
			record.setCode(tmp + "@");
			record.setId(null);
			categoryModelDao.insertCMYCA(record);
			
			if(Constant.YSFLAG.equals(record.getFlag())){
				cm.setTransportFee(cmVO.getTransportFee() + record.getSumNoPrice());
			}else if(Constant.CLFLAG.equals(record.getFlag())){
				cm.setMaterialFee(cmVO.getMaterialFee() + record.getSumNoPrice());
			}else if(Constant.AZFLAG.equals(record.getFlag())){
				cm.setInstallationFee(cmVO.getInstallationFee() + record.getSumNoPrice());
			}
			categoryModelDao.updateCMByPrimaryKey(cm);
		}else{
			
			if(Constant.YSFLAG.equals(record.getFlag())){
				cm.setTransportFee(cmVO.getTransportFee() + (newP-oldP));
			}else if(Constant.CLFLAG.equals(record.getFlag())){
				cm.setMaterialFee(cmVO.getMaterialFee() + (newP-oldP));
			}else if(Constant.AZFLAG.equals(record.getFlag())){
				cm.setInstallationFee(cmVO.getInstallationFee() + (newP-oldP));
			}
			categoryModelDao.updateCMByPrimaryKey(cm);
		}
		
	}

	@Override
	public CategoryModelYCAModel selectByPrimaryKey(Map<String, String> queryMap) {
		return categoryModelDao.selectByPrimaryKey(queryMap);
	}

	@Override
	public void updateValueCM(CategoryModelModel record) throws Exception {
		categoryModelDao.updateCMByPrimaryKey(record);
	}
}
