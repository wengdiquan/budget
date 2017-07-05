package com.bjsj.budget.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.constant.Constant;
import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.dao.LookValueDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.CategoryModelService;
import com.bjsj.budget.util.NumberUtils;
import com.bjsj.budget.util.TransforUtil;
@Service("categoryModelServiceImpl")
public class CategoryModelServiceImpl implements CategoryModelService{
	@Autowired
	private CategoryModelDao categoryModelDao;
	@Autowired
	private LookValueDao lookValueDao;
	@Override
	public List<CategoryModelModel> getCategoryModelList(
			Map<String, String> queryMap) {
		return categoryModelDao.getCategoryModelList(queryMap);
	}

	@Override
	public PageObject queryDetailCategoryModel(Map<String, String> queryMap,
			PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		if("onelevel".equals(queryMap.get("param2"))){
			List<CategoryModelModel> orderChangeHeaders = categoryModelDao.queryDetailCategoryModelOne(queryMap, pageInfo);
			pageObj.setRecords(categoryModelDao.getDetailCategoryModelCountOne(queryMap));
			pageObj.setRows(orderChangeHeaders);
		}else{
			List<CategoryModelModel> orderChangeHeaders = categoryModelDao.queryDetailCategoryModel(queryMap, pageInfo);
			pageObj.setRecords(categoryModelDao.getDetailCategoryModelCount(queryMap));
			pageObj.setRows(orderChangeHeaders);
		}
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
		TransforUtil.transFromMapToBean(map, record);
		
		Double db = record.getPrice()/(1+record.getRate()/100);
		record.setNoPrice(NumberUtils.degree(db));
		db = record.getNoPrice() * record.getAmount();
		record.setSumNoPrice(NumberUtils.degree(db));
		db = record.getPrice() * record.getAmount();
		record.setSumPrice(NumberUtils.degree(db));
		
		categoryModelDao.insertCMYCA(record);
		
		Map queryMap = new HashMap();
		queryMap.put("id", record.getUnitProject_Id());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		
		LookValue lvVO = lookValueDao.selectByPrimaryKey(record.getLookValueId());
		
		if(Constant.YSFLAG == lvVO.getLookTypeId()){
			cmVO.setTransportFee(NumberUtils.degree(cmVO.getTransportFee() + record.getSumNoPrice()));
		}else if(Constant.CLFLAG == lvVO.getLookTypeId()){
			cmVO.setMaterialFee(NumberUtils.degree(cmVO.getMaterialFee() + record.getSumNoPrice()));
		}else if(Constant.AZFLAG == lvVO.getLookTypeId()){
			cmVO.setInstallationFee(NumberUtils.degree(cmVO.getInstallationFee() + record.getSumNoPrice()));
		}
		categoryModelDao.updateCMByPrimaryKey(cmVO);
		
	}

	@Override
	public void updateValue(Map<String, String[]> map) throws Exception {
		
		CategoryModelYCAModel record = new CategoryModelYCAModel();
		
		TransforUtil.transFromMapToBean(map, record);
		
		if(Objects.isNull(record.getLookTypeId())){
			record.setLookValueId(Integer.parseInt(map.get("lookValueId")[0]));
		}
		
		Map queryMap = new HashMap();
		queryMap.put("id", record.getId());
		CategoryModelYCAModel tmVO = categoryModelDao.selectByPrimaryKey(queryMap);
		
		Double db = record.getPrice()/(1+record.getRate()/100);
		record.setNoPrice(NumberUtils.degree(db));
		db = record.getNoPrice() * record.getAmount();
		record.setSumNoPrice(NumberUtils.degree(db));
		db = record.getPrice() * record.getAmount();
		record.setSumPrice(NumberUtils.degree(db));
		
		categoryModelDao.updateByPrimaryKey(record);
		
		queryMap.put("id", record.getUnitProject_Id());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		
		LookValue lvVO = lookValueDao.selectByPrimaryKey(record.getLookValueId());
		
		if(tmVO.getPrice() != record.getPrice()){
			record.setId(null);
			categoryModelDao.insertCMYCASpecial(record);
			//汇总应该包括 新增和改动两部分
			if(Constant.YSFLAG == lvVO.getLookTypeId()){
				cmVO.setTransportFee(cmVO.getTransportFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.CLFLAG == lvVO.getLookTypeId()){
				cmVO.setMaterialFee(cmVO.getMaterialFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.AZFLAG == lvVO.getLookTypeId()){
				cmVO.setInstallationFee(cmVO.getInstallationFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}
			categoryModelDao.updateCMByPrimaryKey(cmVO);
		}else{
			
			if(Constant.YSFLAG == lvVO.getLookTypeId()){
				cmVO.setTransportFee(cmVO.getTransportFee() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.CLFLAG == lvVO.getLookTypeId()){
				cmVO.setMaterialFee(cmVO.getMaterialFee() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.AZFLAG == lvVO.getLookTypeId()){
				cmVO.setInstallationFee(cmVO.getInstallationFee() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}
			categoryModelDao.updateCMByPrimaryKey(cmVO);
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

	@Override
	public void insertCM(Map<String, String[]> map) throws Exception {
		CategoryModelModel record = new CategoryModelModel();
		TransforUtil.transFromMapToBean(map, record);
		
		categoryModelDao.insertCM(record);
	}

	@Override
	public void insertChapter(CategoryModelModel record) throws Exception {
		categoryModelDao.insertChapter(record);		
	}
}
