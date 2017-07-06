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
import com.bjsj.budget.dao.YCADao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.YCAModel;
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
	@Autowired
	private YCADao yCADao;
	
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
	public void insertValue(Map<String, String[]> map, String ycaModelId) throws Exception {
		CategoryModelYCAModel record = new CategoryModelYCAModel();
		TransforUtil.transFromMapToBean(map, record);
		
		Map<String, Object> qMap = new HashMap<String, Object>();
		qMap.put("ycaModelId", ycaModelId);
		YCAModel ycaModel = yCADao.getYCAModelById(qMap);
		record.setCode(ycaModel.getCode());
		record.setName(ycaModel.getName());
		record.setUnit(ycaModel.getUnit());
		record.setRate(ycaModel.getRate());
		record.setNoPrice(NumberUtils.degree(ycaModel.getNoPrice())); //不含税单价
		record.setPrice(NumberUtils.degree(ycaModel.getPrice())); //含税单价
		record.setAmount(0d);
		record.setSumNoPrice(NumberUtils.degree(NumberUtils.multiply(ycaModel.getNoPrice(), record.getAmount()))); 
		record.setSumPrice(NumberUtils.degree(ycaModel.getPrice() * record.getAmount()));
		record.setLookValueId(ycaModel.getLookvalue_id());
		categoryModelDao.insertCMYCA(record);
		
		Map queryMap = new HashMap();
		queryMap.put("id", record.getUnitProject_Id());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		
		LookValue lvVO = lookValueDao.selectByPrimaryKey(record.getLookValueId());
		
		
		//含税单价
		cmVO.setPrice(NumberUtils.degree(NumberUtils.ifNull2Zero(cmVO.getPrice()) + NumberUtils.multiply(NumberUtils.ifNull2Zero(ycaModel.getPrice()), record.getContent())));
		
		//不含税单价
		cmVO.setNoPrice(NumberUtils.degree(NumberUtils.ifNull2Zero(cmVO.getNoPrice()) + NumberUtils.multiply(NumberUtils.ifNull2Zero(ycaModel.getNoPrice()), record.getContent())));

		
		//这些值应该都是0
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
		Map queryMap = new HashMap();
		queryMap.put("id", record.getId());
		CategoryModelYCAModel recordDB = categoryModelDao.selectByPrimaryKey(queryMap);
		
		//数量为0
		recordDB.setSumNoPrice(NumberUtils.degree(NumberUtils.multiply(recordDB.getNoPrice(), NumberUtils.ifNull2Zero(record.getAmount())))); 
		recordDB.setSumPrice(NumberUtils.degree(recordDB.getPrice() * NumberUtils.ifNull2Zero(record.getAmount())));
		
		Double contentUsed =  recordDB.getContent();
		
		recordDB.setContent(record.getContent());
		
		categoryModelDao.updateByPrimaryKey(recordDB);
		
		queryMap.put("id", record.getUnitProject_Id());
		CategoryModelModel cmVO = categoryModelDao.selectCMByPrimaryKey(queryMap);
		LookValue lvVO = lookValueDao.selectByPrimaryKey(recordDB.getLookValueId());
		
		/*if(recordDB.getPrice() != record.getPrice()){
			record.setId(null);
			//categoryModelDao.insertCMYCASpecial(record);
			//汇总应该包括 新增和改动两部分
			if(Constant.YSFLAG == lvVO.getLookTypeId()){
				cmVO.setTransportFee(cmVO.getTransportFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.CLFLAG == lvVO.getLookTypeId()){
				cmVO.setMaterialFee(cmVO.getMaterialFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}else if(Constant.AZFLAG == lvVO.getLookTypeId()){
				cmVO.setInstallationFee(cmVO.getInstallationFee() + record.getSumNoPrice() + (record.getSumNoPrice()-tmVO.getSumNoPrice()));
			}
			categoryModelDao.updateCMByPrimaryKey(cmVO);
		}else{*/
			
		
		//含税单价
		cmVO.setPrice(NumberUtils.degree(NumberUtils.ifNull2Zero(cmVO.getPrice()) + NumberUtils.multiply(NumberUtils.ifNull2Zero(recordDB.getPrice()), record.getContent() - contentUsed)));
		
		//不含税单价
		cmVO.setNoPrice(NumberUtils.degree(NumberUtils.ifNull2Zero(cmVO.getNoPrice()) + NumberUtils.multiply(NumberUtils.ifNull2Zero(recordDB.getNoPrice()), record.getContent() - contentUsed)));

		
		if(Constant.YSFLAG == lvVO.getLookTypeId()){
			cmVO.setTransportFee(NumberUtils.ifNull2Zero(cmVO.getTransportFee()) + (NumberUtils.ifNull2Zero(record.getSumNoPrice())-NumberUtils.ifNull2Zero((recordDB.getSumNoPrice()))));
		}else if(Constant.CLFLAG == lvVO.getLookTypeId()){
			cmVO.setMaterialFee(NumberUtils.ifNull2Zero(cmVO.getMaterialFee()) + NumberUtils.ifNull2Zero(((record.getSumNoPrice())- NumberUtils.ifNull2Zero(recordDB.getSumNoPrice()))));
		}else if(Constant.AZFLAG == lvVO.getLookTypeId()){
			cmVO.setInstallationFee(NumberUtils.ifNull2Zero(cmVO.getInstallationFee()) + NumberUtils.ifNull2Zero((record.getSumNoPrice())-NumberUtils.ifNull2Zero((recordDB.getSumNoPrice()))));
		}
		categoryModelDao.updateCMByPrimaryKey(cmVO);
		
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
