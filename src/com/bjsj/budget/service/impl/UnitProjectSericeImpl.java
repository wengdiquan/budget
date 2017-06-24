package com.bjsj.budget.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.dao.UnitProjectDao;
import com.bjsj.budget.dao.UnitProjectDetailDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.UnitProject;
import com.bjsj.budget.model.UnitProjectDetail;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.UnitProjectService;
import com.bjsj.budget.util.StringUtil;

@Service("unitProjectSericeImpl")
public class UnitProjectSericeImpl implements UnitProjectService {
	
	@Autowired
	private CategoryModelDao categoryModelDao;
	
	@Autowired
	private UnitProjectDao unitProjectDao;
	
	@Autowired
	private UnitProjectDetailDao unitProjectDetailDao;
	
	//新增单项工程
	@Override
	public void insertItem(Map<String, String> queryMap) throws Exception {
		
		String type = queryMap.get("type");
		
		String unitProjectId = queryMap.get("unitProjectId");
		String unitProjectCode = queryMap.get("unitProjectCode");
		String times = queryMap.get("times");
		
		UnitProject u = new UnitProject();
		//定额类型
		if("DING".equals(type)){
			//修改
			//根据id 查询编码
			CategoryModelModel cgmModel = categoryModelDao.selectCMByPrimaryKey(queryMap);
			if(StringUtil.isEmpty(unitProjectCode)){
				//查询出来seq。然后删除调用，在新增一条
				u = unitProjectDao.getUnitProjectById(queryMap);
				
			}else{
				//新增一条新的（在最后面新增）
				Integer maxSeq = unitProjectDao.getMaxSeqByMap(queryMap);
				u.setSeq(maxSeq + 1); //排序
			}
			
			u.setCode(cgmModel.getCode());
			u.setType("定");
			u.setName(cgmModel.getName());
			u.setUnit(cgmModel.getUnit());
			u.setContent(null);//含量
			u.setDtgcl(1d);//单体工程量为默认为1
			//根据定额ID,查询下面的运材安
			List<CategoryModelYCAModel> ycaModelList = categoryModelDao.getDetailYCA(cgmModel.getId());
			double singlePrice = 0; //不含税单价
			double taxSinglePrice = 0; //含税单价
			for(CategoryModelYCAModel ycaModel:ycaModelList){
				singlePrice += ycaModel.getNoPrice() * ycaModel.getContent();
				taxSinglePrice += ycaModel.getPrice() * ycaModel.getContent(); 
			}
			u.setSinglePrice(singlePrice); //不含税单价
			u.setTaxSinglePrice(taxSinglePrice); //含税单价
			u.setSingleSumPrice(singlePrice * 1); //不含税合价
			u.setTaxSingleSumPrice(taxSinglePrice * 1); //不含税合价
			u.setPrice(cgmModel.getTransportFee() + cgmModel.getMaterialFee() + cgmModel.getInstallationFee()); //综合单价
			u.setSumPrice(u.getPrice() * 1); //综合合价
			u.setRemark(null);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setSourceType(null);
			
			if("1".equals(times)){
				//更新定额(第一次是更新)
				unitProjectDao.updateByPrimaryKey(u);
			}else{
				//保存定额
				unitProjectDao.insert(u);
			}
			
			int index = 1;
			for(CategoryModelYCAModel ycaModel:ycaModelList){
				UnitProjectDetail detail = new UnitProjectDetail();
				//明细信息
				detail.setCode(ycaModel.getCode());
				detail.setType(ycaModel.getLookTypeName().substring(0, 1)); //列别
				detail.setName(ycaModel.getName());
				detail.setTypeInfo(null);
				detail.setUnit(ycaModel.getUnit());
				detail.setContent(ycaModel.getContent()); //含量
				detail.setSingleSumPrice(ycaModel.getNoPrice() * ycaModel.getAmount());  //不含税合价
				detail.setTaxSingleSumPrice(ycaModel.getPrice() * ycaModel.getAmount()); //含税合价
				detail.setNoTaxPrice(ycaModel.getNoPrice()); //不含税单价
				detail.setTaxPrice(ycaModel.getPrice());  //含税单价
				detail.setAmount(ycaModel.getAmount()); //数量
				detail.setOrigCount(ycaModel.getAmount());// 原始数量
				detail.setLookValueId(ycaModel.getLookValueId());
				detail.setSeq(index++);
				detail.setUnitprojectId(u.getId()); 
				unitProjectDetailDao.insert(detail);
			}
		}	
	}
	
	@Override
	public void insertBlankItem(Map<String, String> queryMap) {
		
		//判断是否需要刷顺序
		String needOrderFlag = queryMap.get("needOrderFlag");
		int pos = Integer.parseInt(queryMap.get("pos")); //选择的是第几行 (0开始)
		
		//查询所有单位工程下子目
		List<UnitProject> itemInfoList = unitProjectDao.getBitProjectItemInfo(queryMap);
		if("Y".equals(needOrderFlag)){
			UnitProject u = new UnitProject();
			u.setDtgcl(1d);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setSeq(itemInfoList.get(pos).getSeq());
			
			unitProjectDao.insert(u);
			//刷新后面的所有List
			UnitProject up = null;
			for(int i = pos; i < itemInfoList.size(); i++){
				up = itemInfoList.get(i);
				up.setSeq(up.getSeq() + 1);
				unitProjectDao.updateByPrimaryKey(up);
			}
			
		}else{
			UnitProject u = new UnitProject();
			u.setDtgcl(1d);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setSeq(itemInfoList.get(itemInfoList.size() - 1).getSeq() + 1);
			unitProjectDao.insert(u);
		}
	}
	
	@Override
	public PageObject getBitProjectItemInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<UnitProject> itemInfoList = unitProjectDao.getBitProjectItemInfo(queryMap);
		pageObj.setRecords(itemInfoList.size());
		pageObj.setRows(itemInfoList);
		return pageObj;
	}
	
	@Override
	public PageObject getBitProjectDetailInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<UnitProject>  detailInfoList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
		pageObj.setRecords(detailInfoList.size());
		pageObj.setRows(detailInfoList);
		return pageObj;
	}
	
	/**
	 * 根据子目删除信息
	 * @param queryMap
	 */
	@Override
	public void deleteBitProjectItem(Map<String, String> queryMap) throws Exception{
		//删除details
		unitProjectDetailDao.deleteByUnitProjectId(queryMap);
		unitProjectDao.deleteByUnitProjectId(queryMap);
	}
}
