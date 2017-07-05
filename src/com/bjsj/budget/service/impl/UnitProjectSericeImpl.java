package com.bjsj.budget.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.config.yaml.YamlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.constant.Constant;
import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.dao.UnitProjectDao;
import com.bjsj.budget.dao.UnitProjectDetailDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.UnitProject;
import com.bjsj.budget.model.UnitProjectDetail;
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
		
		String times = queryMap.get("times");
		
		UnitProject u = new UnitProject();
		//定额类型
		if("DING".equals(type)){
			//修改
			//根据id 查询编码
			CategoryModelModel cgmModel = categoryModelDao.selectCMByPrimaryKey(queryMap);
			if("1".equals(times)){
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
			u.setContent(1d);//含量
			u.setDtgcl(1d);//单体工程量为默认为1
			
			List<CategoryModelYCAModel> ycaModelList = categoryModelDao.getDetailYCA(cgmModel.getId());
			//含税单价
			BigDecimal taxSinglePrice =  this.getTaxSinglePriceUp(ycaModelList); 
			
			//不含税单价
			BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
			
			u.setSinglePrice(this.toMoney(singlePrice)); //不含税单价
			u.setTaxSinglePrice(this.toMoney(taxSinglePrice)); //含税单价
			u.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, 1d))); //不含税合价
			u.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, 1d))); //含税合价
			u.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice))); //综合单价 == 含税单价 + 利润（目前没有）
			u.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), 1d))); //综合合价 = 综合单价 × 数量
			u.setRemark(null);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setLookValueId(null);
			
			if("1".equals(times)){
				//更新定额(第一次是更新)
				unitProjectDao.updateByPrimaryKey(u);
			}else{
				//保存定额
				unitProjectDao.insert(u);
			}
			
			int index = 1;
			if("1".equals(times)){
				//更新定额(第一次是更新)
				queryMap.put("unitProjectId", u.getId() + "");
				unitProjectDetailDao.deleteByUnitProjectId(queryMap);
			}
			
			for(CategoryModelYCAModel ycaModel:ycaModelList){
				UnitProjectDetail detail = new UnitProjectDetail();
				//明细信息
				detail.setCode(ycaModel.getCode());
				detail.setType(ycaModel.getLookTypeName().substring(0, 1)); //类别
				detail.setName(ycaModel.getName());
				detail.setTypeInfo(null);
				detail.setUnit(ycaModel.getUnit()); //单位
				detail.setContent(ycaModel.getContent()); //含量
				detail.setNoTaxPrice(ycaModel.getNoPrice()); //不含税单价
				detail.setTaxPrice(ycaModel.getPrice());  //含税单价
				
				//修改数量
				detail.setAmount(this.multiply(1d, detail.getContent()).doubleValue()); //数量
				detail.setSingleSumPrice(this.toMoney(this.singleSumPriceDown(ycaModel.getNoPrice(), detail.getAmount())));  //不含税合价
				detail.setTaxSingleSumPrice(this.toMoney(this.singleTaxSumPriceDown(ycaModel.getPrice(), detail.getAmount()))); //含税合价
				
				detail.setOrigCount(detail.getAmount());// 原始数量
				detail.setLookValueId(ycaModel.getLookValueId());
				detail.setSeq(index++);
				detail.setUnitprojectId(u.getId()); 
				detail.setIsSuppleCost(0);
				detail.setIsSupType(0);
				//保存详细数据
				unitProjectDetailDao.insert(detail);
			}
		}else if("YCA".equals(type)){
			//运材安新增(替换)
			CategoryModelYCAModel ycaModel = categoryModelDao.selectByPrimaryKey(queryMap);
			
			if("1".equals(times)){
				//查询出来seq。然后删除调用，在新增一条
				u = unitProjectDao.getUnitProjectById(queryMap);
				unitProjectDetailDao.deleteByUnitProjectId(queryMap);
			}else{
				//新增一条新的（在最后面新增）
				Integer maxSeq = unitProjectDao.getMaxSeqByMap(queryMap);
				u.setSeq(maxSeq + 1); //排序
			}
			
			u.setCode(ycaModel.getCode());
			u.setType(ycaModel.getLookTypeName().substring(0, 1));
			u.setName(ycaModel.getName());
			u.setUnit(ycaModel.getUnit());
			u.setContent(0d);//含量
			u.setDtgcl(0d);
			u.setSinglePrice(BigDecimal.valueOf(ycaModel.getPrice())
					.divide(BigDecimal.valueOf(1 + Constant.PER_CENT), 5, RoundingMode.HALF_DOWN)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); //不含税单价
			u.setTaxSinglePrice(ycaModel.getPrice()); //含税单价
			u.setSingleSumPrice(0d); //不含税合价
			u.setTaxSingleSumPrice(0d); //含税合价
			u.setPrice(ycaModel.getPrice()); //综合单价
			u.setSumPrice(0d); //综合合价
			u.setRemark(null);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setLookValueId(ycaModel.getLookValueId());
			if("1".equals(times)){
				//更新定额(第一次是更新)
				unitProjectDao.updateByPrimaryKey(u);
			}else{
				//保存定额
				unitProjectDao.insert(u);
			}
		}
	}
	




	@Override
	public void insertBlankItem(Map<String, String> queryMap) {
		
		//判断是否需要刷顺序
		String needOrderFlag = queryMap.get("needOrderFlag");
		
		//查询所有单位工程下子目
		List<UnitProject> itemInfoList = unitProjectDao.getBitProjectItemInfo(queryMap);
		if("Y".equals(needOrderFlag)){
			int pos = Integer.parseInt(queryMap.get("pos")); //选择的是第几行 (0开始)
			UnitProject u = new UnitProject();
			u.setDtgcl(1d);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			if(itemInfoList.isEmpty()){
				u.setSeq(1);
			}else{
				u.setSeq(itemInfoList.get(pos).getSeq());
			}
			
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
			if(itemInfoList.isEmpty()){
				u.setSeq(1);
			}else{
				u.setSeq(itemInfoList.get(itemInfoList.size() - 1).getSeq() + 1);
			}
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
		List<UnitProjectDetail>  detailInfoList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
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
	
	@Override
	public void changeSeq(Map<String, String> queryMap) {
		
		queryMap.put("unitProjectId", queryMap.get("opearId"));
		UnitProject operUP = unitProjectDao.getUnitProjectById(queryMap);
		
		queryMap.put("unitProjectId", queryMap.get("overId"));
		UnitProject overUP = unitProjectDao.getUnitProjectById(queryMap);
		
		Integer temp = operUP.getSeq();
		operUP.setSeq(overUP.getSeq());
		overUP.setSeq(temp);
		
		unitProjectDao.updateByPrimaryKey(overUP);
		unitProjectDao.updateByPrimaryKey(operUP);
	}
	
	/**
	 * 修改子目和明细
	 */
	@Override
	public void updateItemAndDetail(Map<String, String> queryMap) {
		
		String unitProjectId = queryMap.get("unitProjectId");
		UnitProject unitProjectDB = unitProjectDao.getUnitProjectById(queryMap);
		
		Double dtglcDB = unitProjectDB.getDtgcl(); //初始工程量
		
		Double changeDB  = dtglcDB; 
		if(this.doubleCompare(dtglcDB, 0d)){
			changeDB = 1d;
		}
		
		String name = queryMap.get("name");
		String value = queryMap.get("value");
		
		//工程量（含税合价|不含税合价|综合单价|综合合价） 修改含量
		if("dtgcl".equals(name) || "content".equals(name)){
			
			Double v = Double.parseDouble(value);
			
			if(this.doubleCompare(v, unitProjectDB.getDtgcl())){
				return;
			}
			
			unitProjectDB.setContent(v); //含量
			unitProjectDB.setDtgcl(v); //工程量
			
			//倍数
			BigDecimal mutiDecimal = this.divive(v, changeDB); 
			
			//修改的是定额
			if(StringUtil.isEmpty(unitProjectDB.getLookValueId())){
				
				/**
				 * 含税合价(子目下详细值)
				 */
				List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
				
				/**
				 * 更新详细值（数量变更）
				 */
				for(UnitProjectDetail detail : detailList){
					detail.setAmount(this.multiply(detail.getContent(),v).doubleValue());  //数量 = 含量×工程量
					//修改不含税/含税合价
					detail.setSingleSumPrice(this.toMoney(this.multiply(detail.getAmount(), detail.getNoTaxPrice())));
					detail.setTaxSingleSumPrice(this.toMoney(this.multiply(detail.getAmount(), detail.getTaxPrice())));
					unitProjectDetailDao.updateByPrimaryKey(detail);
				}
				
				//含税单价
				BigDecimal taxSinglePrice = this.taxSinglePriceUp(detailList);
				
				//不含税单价
				BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
				
				unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));
				unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
				
				//不含税合价
				unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
				//含税合价
				unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
				
				//综合单价
				unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
				
				//综合合价
				unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
				
				unitProjectDao.updateByPrimaryKey(unitProjectDB);
			}else{
				
				//获取原始的detail
				CategoryModelYCAModel ycaModel = categoryModelDao.getDetailInfoByCode(unitProjectDB.getCode());
				
				BigDecimal  taxSinglePrice = BigDecimal.valueOf(unitProjectDB.getTaxSinglePrice());
				//等于1的话
				if(this.doubleCompare(v, 1d)){
					//不含税合价
					unitProjectDB.setTaxSingleSumPrice(this.toMoney(taxSinglePrice));
					//含税合价
					unitProjectDB.setSingleSumPrice(this.toMoney(this.getSinglePriceUp(taxSinglePrice)));
				
				}else{
					//不含税合价
					unitProjectDB.setSingleSumPrice(this.toMoney(BigDecimal.valueOf(unitProjectDB.getSingleSumPrice()).multiply(mutiDecimal)));
					//含税合价
					unitProjectDB.setTaxSingleSumPrice(this.toMoney(BigDecimal.valueOf(unitProjectDB.getTaxSingleSumPrice()).multiply(mutiDecimal)));
				}
				
					
				//综合单价(不变)
				//综合合价
				unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), v)));
				unitProjectDao.updateByPrimaryKey(unitProjectDB);
			}
		}
		
		//修改含税单价（==不含税单价= (含税单价 / 1 + 0.17)）
		if("taxSinglePrice".equals(name)){
			
			Double v = Double.parseDouble(value);
		
			if(this.doubleCompare(v, unitProjectDB.getTaxSinglePrice())){
				return; //没有修改
			}
			
			
			if(StringUtil.isEmpty(unitProjectDB.getLookValueId())){
				//计算运材安的比例
				List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
				
				BigDecimal ybc = BigDecimal.ZERO; //运输费
				BigDecimal cbc = BigDecimal.ZERO; //材料费
				BigDecimal abc = BigDecimal.ZERO; //安装费
				
				//差值(之前含税单价×含量)
				BigDecimal  subV = BigDecimal.ZERO; //差值
				
				for(UnitProjectDetail detail : detailList){
					
					if(detail.getIsSuppleCost()  == 1){
						continue;
					}
					subV = subV.add(this.multiply(detail.getContent(), detail.getTaxPrice()));
					if("运".equals(detail.getType())){
						ybc = ybc.add(BigDecimal.valueOf(detail.getTaxSingleSumPrice()));
					}if("材".equals(detail.getType())){
						cbc = cbc.add(BigDecimal.valueOf(detail.getTaxSingleSumPrice()));
					}if("安".equals(detail.getType())){
						abc = abc.add(BigDecimal.valueOf(detail.getTaxSingleSumPrice()));
					}
				}
				
				subV = BigDecimal.valueOf(v).subtract(subV);
				
				BigDecimal total = ybc.add(cbc).add(abc); 
				
				/**
				 * 是否含有补充费用。
				 */
				UnitProjectDetail yDetail = new UnitProjectDetail();
				UnitProjectDetail cDetail = new UnitProjectDetail();
				UnitProjectDetail aDetail = new UnitProjectDetail();
				
				for(UnitProjectDetail detail : detailList){
					if(detail.getIsSuppleCost()  == 1 && "YSFTZ".equals(detail.getCode())){
						yDetail = detail;
						continue;
					}
					
					if(detail.getIsSuppleCost()  == 1 && "CLFTZ".equals(detail.getCode())){
						cDetail = detail;
						continue;
					}
					
					if(detail.getIsSuppleCost()  == 1 && "AZFTZ".equals(detail.getCode())){
						aDetail = detail;
					}
				}
				
				//新增
				ybc = ybc.divide(total, 5, RoundingMode.HALF_DOWN).multiply(subV);
				
				if(StringUtil.isEmpty(yDetail.getCode())){
					if(!this.doubleCompare(ybc.doubleValue(), 0d)){
						yDetail.setCode("YSFTZ");
						yDetail.setType("运"); //列别
						yDetail.setName("运输费调整");
						yDetail.setTypeInfo(null);
						yDetail.setUnit("元");
						if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
							yDetail.setContent(ybc.doubleValue());
							yDetail.setAmount(0d);
						}else {
							yDetail.setContent(this.divive(ybc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
							yDetail.setAmount(ybc.doubleValue()); //数量
						}
					
						yDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), yDetail.getAmount())));  //不含税合价
						yDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, yDetail.getAmount()))); //含税合价
						yDetail.setNoTaxPrice(this.toMoney(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)))); //不含税单价
						yDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
						yDetail.setOrigCount(0d);// 原始数量
						yDetail.setLookValueId(null); //调整的lookValue
						yDetail.setSeq(detailList.size() + 1);
						yDetail.setUnitprojectId(unitProjectDB.getId()); 
						yDetail.setIsSuppleCost(1); //是否为费用调整
						yDetail.setIsSupType(0); //是否补充费用
						unitProjectDetailDao.insert(yDetail);
					}
					
				//修改
				}else{
					if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
						yDetail.setContent(ybc.doubleValue());
						yDetail.setAmount(0d);
					}else {
						yDetail.setContent(this.divive(ybc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
						yDetail.setAmount(ybc.doubleValue()); //数量
					}
				
					yDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), yDetail.getAmount())));  //不含税合价
					yDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, yDetail.getAmount()))); //含税合价
					yDetail.setNoTaxPrice(this.toMoney(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)))); //不含税单价
					yDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
					unitProjectDetailDao.updateByPrimaryKey(yDetail);
				}
				
				cbc = cbc.divide(total, 5, RoundingMode.HALF_DOWN).multiply(subV);
				if(StringUtil.isEmpty(cDetail.getCode())){
					if(!this.doubleCompare(cbc.doubleValue(), 0d)){
						cDetail.setCode("CLFTZ");
						cDetail.setType("材"); //列别
						cDetail.setName("材料费调整");
						cDetail.setTypeInfo(null);
						cDetail.setUnit("元");
						if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
							cDetail.setContent(cbc.doubleValue());
							cDetail.setAmount(0d);
						}else{
							cDetail.setContent(this.divive(cbc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
							cDetail.setAmount(cbc.doubleValue()); //数量
						}
					
						cDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), cDetail.getAmount())));  //不含税合价
						cDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, cDetail.getAmount()))); //含税合价
						cDetail.setNoTaxPrice(this.toMoney(this.getSinglePriceUp(BigDecimal.valueOf(1d)))); //不含税单价
						cDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
						cDetail.setOrigCount(0d);// 原始数量
						cDetail.setLookValueId(null); //调整的lookValue
						cDetail.setSeq(detailList.size() + 2);
						cDetail.setUnitprojectId(unitProjectDB.getId()); 
						cDetail.setIsSuppleCost(1); //是否为费用调整
						cDetail.setIsSupType(0); //是否补充费用
						unitProjectDetailDao.insert(cDetail);
					}
				//修改
				}else{
					
					if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
						cDetail.setContent(cbc.doubleValue());
						cDetail.setAmount(0d);
					}else{
						cDetail.setContent(this.divive(cbc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
						cDetail.setAmount(cbc.doubleValue()); //数量
					}
					cDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), cDetail.getAmount())));  //不含税合价
					cDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, cDetail.getAmount()))); //含税合价
					cDetail.setNoTaxPrice(this.toMoney(this.getSinglePriceUp(BigDecimal.valueOf(1d)))); //不含税单价
					cDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
					unitProjectDetailDao.updateByPrimaryKey(cDetail);
				}
				
				//安装费
				abc = abc.divide(total,5, RoundingMode.HALF_DOWN).multiply(subV);
				if(StringUtil.isEmpty(aDetail.getCode())){
					if(!this.doubleCompare(abc.doubleValue(), 0d)){
						aDetail.setCode("AZFTZ");
						aDetail.setType("安"); //列别
						aDetail.setName("安装费调整");
						aDetail.setTypeInfo(null);
						aDetail.setUnit("元");
						if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
							aDetail.setContent(abc.doubleValue());
							aDetail.setAmount(0d);
						}else{
							aDetail.setContent(this.divive(abc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
							aDetail.setAmount(abc.doubleValue()); //数量
						}

						aDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), aDetail.getAmount())));  //不含税合价
						aDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, aDetail.getAmount()))); //含税合价
						aDetail.setNoTaxPrice(this.toMoney(this.getSinglePriceUp(BigDecimal.valueOf(1d)))); //不含税单价
						aDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
						aDetail.setOrigCount(aDetail.getAmount());// 原始数量
						aDetail.setLookValueId(null); //调整的lookValue
						aDetail.setSeq(detailList.size() + 3);
						aDetail.setUnitprojectId(unitProjectDB.getId()); 
						aDetail.setIsSuppleCost(1); //是否为费用调整
						aDetail.setIsSupType(0); //是否补充费用
						unitProjectDetailDao.insert(aDetail);
					}
				//修改
				}else{
					if(this.doubleCompare(unitProjectDB.getDtgcl(), 0d)){
						aDetail.setContent(abc.doubleValue());
						aDetail.setAmount(0d);
					}else{
						aDetail.setContent(this.divive(abc.doubleValue(), unitProjectDB.getDtgcl()).doubleValue()); //含量
						aDetail.setAmount(abc.doubleValue()); //数量
					}

					aDetail.setSingleSumPrice(this.toMoney(this.multiply(this.getNoTaxPriceDown(BigDecimal.valueOf(1d)).doubleValue(), aDetail.getAmount())));  //不含税合价
					aDetail.setTaxSingleSumPrice(this.toMoney(this.multiply(1d, aDetail.getAmount()))); //含税合价
					aDetail.setNoTaxPrice(this.toMoney(this.getSinglePriceUp(BigDecimal.valueOf(1d)))); //不含税单价
					aDetail.setTaxPrice(this.toMoney(BigDecimal.valueOf(1d)));  //含税单价
					
					unitProjectDetailDao.updateByPrimaryKey(aDetail);
				}
				
				//查询详细值（影响不含税价）
				BigDecimal taxSinglePrice = BigDecimal.valueOf(v);
				BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
				
				//含税单价
				unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));
				
				//不含税单价
				unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
				
				//不含税合价
				unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
				
				//含税合价
				unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
				
				//综合单价
				unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
				
				//综合合价
				unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
				
				unitProjectDao.updateByPrimaryKey(unitProjectDB);
				
			}else{
				
				Double gcl = unitProjectDB.getDtgcl();
				
				BigDecimal taxSinglePrice = BigDecimal.valueOf(v);
				//不含税单价
				BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
				
				//含税单价
				unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));
				unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
				
				//不含税合价
				unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, gcl)));
				
				//含税合价
				unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, gcl)));
				
				//综合单价(
				unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
				
				//综合合计
				unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), gcl)));
				
				//修改类别为换
				unitProjectDB.setType("换");
				unitProjectDao.updateByPrimaryKey(unitProjectDB);
			}
		}
		
		//修改备注 或者 修改名称
		if("name".equals(name)){
			unitProjectDB.setName(value);
			unitProjectDao.updateByPrimaryKey(unitProjectDB);
		}
		if("remark".equals(name)){
			unitProjectDB.setRemark(value);
			unitProjectDao.updateByPrimaryKey(unitProjectDB);
		}
	}
	
	@Override
	public UnitProject getItemById(Map<String, String> queryMap) {
		List<UnitProject> itemInfoList = unitProjectDao.getBitProjectItemInfo(queryMap);
		return itemInfoList.get(0);
	}
	
	@Override
	public void insertDetail(Map<String, String> queryMap) {
		String className = queryMap.get("classType");
		String type = queryMap.get("type");
		
		String typeName = "";
		if("安".equals(type)){
			typeName = "安装";
		}else if("材".equals(type)){
			typeName = "材料";
		}else if("运".equals(type)){
			typeName = "运输";
		}
		
		//获取最大值（已经使用的最大值）
		queryMap.put("code", "");
		Integer nextValue = unitProjectDetailDao.getNextValue(queryMap);
		
		Format f1 = new DecimalFormat("000");
		String nextStr = f1.format(nextValue);
		
		//新增
		if("add".equals(className)){
			UnitProjectDetail upd = new UnitProjectDetail();
			upd.setUnitprojectId(Integer.parseInt(queryMap.get("unitProjectId")));
			upd.setCode("补充" + typeName + nextStr);
			upd.setType(type);
			upd.setName(null);
			upd.setTypeInfo(null);
			upd.setUnit("工日");
			upd.setContent(0d);
			upd.setAmount(0d);
			upd.setSingleSumPrice(0d);
			upd.setTaxSingleSumPrice(0d);
			upd.setNoTaxPrice(0d);
			upd.setTaxPrice(0d);
			upd.setOrigCount(0d);
			upd.setIsSuppleCost(0);
			upd.setIsSupType(1);
			List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
			if("defaultV".equals(queryMap.get("specialD"))){
				upd.setSeq(1);
			}else{
				upd.setSeq(detailList.get(detailList.size() -1).getSeq() + 1);
			}
			
			unitProjectDetailDao.insert(upd);
		}
		
		//插入 暂不支持 TODO
		if("insert".equals(className)){
			UnitProjectDetail upd = new UnitProjectDetail();
			upd.setUnitprojectId(Integer.parseInt(queryMap.get("unitProjectId")));
			upd.setCode("补充" + typeName + nextStr);
			upd.setType(type);
			List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
			upd.setSeq(detailList.get(detailList.size() -1).getSeq() + 1);
			unitProjectDetailDao.insert(upd);
		}
	}
	
	/**
	 * 删除运财安
	 */
	@Override
	public void deleteBitProjectDetail(Map<String, String> queryMap) {
		
		//删除详细值
		unitProjectDetailDao.deleteByPrimaryKey(Integer.parseInt(queryMap.get("unitProjectDetailId")));
		
		UnitProject unitProjectDB = unitProjectDao.getUnitProjectById(queryMap);
		
		//修改子目
		queryMap.remove("unitProjectDetailId");
		List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
		
		//含税单价
		BigDecimal taxSinglePrice = this.taxSinglePriceUp(detailList);
		//不含税单价
		BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
		
		unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));;
		unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
		
		
		//不含税合价
		unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
		
		//含税合价
		unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
		
		//综合单价
		unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
		
		//综合合价
		unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
		
		unitProjectDao.updateByPrimaryKey(unitProjectDB);
		
		
		
	}
	
	/**
	 * 修改明细和子目
	 */
	@Override
	public void updateDetailAndItem(Map<String, String> queryMap) throws Exception{
		String unitProjectId = queryMap.get("unitProjectId");
		UnitProject unitProjectDB = unitProjectDao.getUnitProjectById(queryMap);
		
		UnitProjectDetail detailDB = unitProjectDetailDao.getUnitProjectDetailById(queryMap);
		
		String name = queryMap.get("name");
		String value = queryMap.get("value");
		
		//修改类型
		if("name".equals(name)){
			if(!detailDB.equals(name)){
				detailDB.setName(value);
				unitProjectDetailDao.updateByPrimaryKey(detailDB);
			}
			return;
		}
		
		//修改规格及型号
		if("typeInfo".equals(name)){
			detailDB.setTypeInfo(value);
			unitProjectDetailDao.updateByPrimaryKey(detailDB);
			return;
		}
		
		//修改含量
		if("content".equals(name)){
			
			Double v = Double.parseDouble(value);
			
			if(this.doubleCompare(v, detailDB.getContent())){
				return;
			}
			
			Double contentDB = detailDB.getContent();
			Double changeDB = contentDB;
			if(this.doubleCompare(contentDB, 0d)){
				changeDB = 1d;
			}
			
			//倍数
			BigDecimal mutiDecimal = this.divive(v, changeDB); 
			
			detailDB.setContent(v);
			
			//数量修改
			detailDB.setAmount(this.multiply(v, unitProjectDB.getDtgcl()).doubleValue());
			
			Double noTaxPrice = detailDB.getNoTaxPrice();
			Double taxPrice =detailDB.getTaxPrice();
			//修改不含税合价
			detailDB.setSingleSumPrice(this.toMoney(this.multiply(detailDB.getSingleSumPrice(), mutiDecimal.doubleValue())));
			//修改含税合价
			detailDB.setTaxSingleSumPrice(this.toMoney(this.multiply(detailDB.getTaxSingleSumPrice(), mutiDecimal.doubleValue())));
			unitProjectDetailDao.updateByPrimaryKey(detailDB);
			
			//修改子目
			queryMap.remove("unitProjectDetailId");
			List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);
			
			//含税单价
			BigDecimal taxSinglePrice = this.taxSinglePriceUp(detailList);
			//不含税单价
			BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
			
			unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));;
			unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
			
			//不含税合价
			unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
			
			//含税合价
			unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
			
			//综合单价
			unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
			
			//综合合价
			unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
			
			unitProjectDao.updateByPrimaryKey(unitProjectDB);
			
			return;
		}
		
		//修改数量
		if("amount".equals(name)){
			
			Double v = Double.parseDouble(value);
			
			if(this.doubleCompare(v, detailDB.getAmount())){
				return;
			}
			
			Double amountDB = detailDB.getAmount();
			Double changeDB = amountDB;
			if(this.doubleCompare(amountDB, 0d)){
				changeDB = 1d;
			}
			
			//倍数
			BigDecimal mutiDecimal = this.divive(v, changeDB); 
			
			//修改含量（数量除了工程量）
			detailDB.setContent(this.divive(v, unitProjectDB.getDtgcl()).doubleValue());
			
			//修改数量
			detailDB.setAmount(v);
			
			Double noTaxPrice = detailDB.getNoTaxPrice();
			Double taxPrice = detailDB.getTaxPrice();
			//修改不含税合价
			detailDB.setSingleSumPrice(this.toMoney(this.multiply(detailDB.getSingleSumPrice(), mutiDecimal.doubleValue())));
			//修改含税合价
			detailDB.setTaxSingleSumPrice(this.toMoney(this.multiply(detailDB.getTaxSingleSumPrice(), mutiDecimal.doubleValue())));
			unitProjectDetailDao.updateByPrimaryKey(detailDB);
			
			//修改子目
			queryMap.remove("unitProjectDetailId");
			List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);

			//含税单价
			BigDecimal taxSinglePrice = this.taxSinglePriceUp(detailList);
			//不含税单价
			BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
			
			unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));;
			unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
			
			//不含税合价
			unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
			
			//含税合价
			unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
			
			//综合单价
			unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
			
			//综合合价
			unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
			unitProjectDao.updateByPrimaryKey(unitProjectDB);
		
			return;
		}
		
		//修改市场价
		if("taxPrice".equals(name)){
			
			Double v = Double.parseDouble(value);
			
			if(this.doubleCompare(v, detailDB.getTaxPrice())){
				return;
			}
			
			//修改编号
			if(detailDB.getCode().indexOf("@") != -1){
				queryMap.put("code", detailDB.getCode().substring(0, detailDB.getCode().indexOf("@")));
			}else{
				queryMap.put("code", detailDB.getCode());
				Integer count = unitProjectDetailDao.getNextValue(queryMap);
				detailDB.setCode(detailDB.getCode() + "@" + count);
			}
			
			//含税单价
			BigDecimal taxPrice = BigDecimal.valueOf(v);
			detailDB.setTaxPrice(this.toMoney(taxPrice));
			
			//不含税单价
			BigDecimal noTaxPrice = this.getNoTaxPriceDown(taxPrice);
			detailDB.setNoTaxPrice(this.toMoney(noTaxPrice));
			
			//含税合价
			detailDB.setTaxSingleSumPrice(this.toMoney(this.multiply(taxPrice.doubleValue(), detailDB.getAmount())));
			
			//不含税合价
			detailDB.setSingleSumPrice(this.toMoney(this.multiply(noTaxPrice.doubleValue(), detailDB.getAmount())));
			
			unitProjectDetailDao.updateByPrimaryKey(detailDB);
			
			//修改子目
			queryMap.remove("unitProjectDetailId");
			List<UnitProjectDetail> detailList = unitProjectDetailDao.getBitProjectDetailInfo(queryMap);

			//含税单价
			BigDecimal taxSinglePrice = this.taxSinglePriceUp(detailList);
			//不含税单价
			BigDecimal singlePrice = this.getSinglePriceUp(taxSinglePrice);
			
			unitProjectDB.setTaxSinglePrice(this.toMoney(taxSinglePrice));;
			unitProjectDB.setSinglePrice(this.toMoney(singlePrice));
			
			//不含税合价
			unitProjectDB.setSingleSumPrice(this.toMoney(this.getSingleSumPriceUp(singlePrice, unitProjectDB.getDtgcl())));
			
			//含税合价
			unitProjectDB.setTaxSingleSumPrice(this.toMoney(this.getTaxSingleSumPriceUp(taxSinglePrice, unitProjectDB.getDtgcl())));
			
			//综合单价
			unitProjectDB.setPrice(this.toMoney(this.getPriceUp(taxSinglePrice)));
			
			//综合合价
			unitProjectDB.setSumPrice(this.toMoney(this.getSumPriceUp(this.getPriceUp(taxSinglePrice), unitProjectDB.getDtgcl())));
			unitProjectDao.updateByPrimaryKey(unitProjectDB);
			return;
		}
	}
	
	/**
	 * 不含税价改变  bitProjectId, code, value 
	 * 
	 *  bitProjectId  单位工程id（可为空）
	 *  unitProjectId  子目id（可为空）
	 *  unitProjectDetailId 详细Id （可为空）
	 *  value 值 
	 * @param unitProjectId
	 */
	public void changeMarkMoney(Map<String, String> queryMap){
		
		
	}
	
	
	
	/**
	 * 上面板含税单价
	 * @param ycaModelList
	 */
	private BigDecimal getTaxSinglePriceUp(List<CategoryModelYCAModel> ycaModelList) {
		BigDecimal taxSinglePrice = BigDecimal.valueOf(0d);
		for(CategoryModelYCAModel ycaModel : ycaModelList){
			taxSinglePrice = taxSinglePrice.add(this.multiply(ycaModel.getContent(), ycaModel.getPrice()));
		}
		return taxSinglePrice;
	}
	
	
	/**
	 * 上面版 不含税合价 （不含税单价 × 工程量）
	 * @param singlePrice
	 * @param gcl
	 * @return
	 */
	public BigDecimal getSingleSumPriceUp(BigDecimal singlePrice, Double gcl){
		return singlePrice.multiply(BigDecimal.valueOf(gcl));
	}
	
	/**
	 * 上面版 含税合价 （含税单价 × 工程量）
	 * @param taxSinglePrice
	 * @param gcl
	 * @return
	 */
	public BigDecimal getTaxSingleSumPriceUp(BigDecimal taxSinglePrice, Double gcl){
		return taxSinglePrice.multiply(BigDecimal.valueOf(gcl));
	}
	
	
	/**
	 * 上面版 不含税单价
	 */
	private BigDecimal getSinglePriceUp(BigDecimal taxSinglePrice){
		return taxSinglePrice.divide(BigDecimal.valueOf(1 + Constant.PER_CENT), 5, RoundingMode.HALF_DOWN);
	}
	
	/**
	 * 下面版， 不含税合价
	 * @param taxPrice
	 * @return
	 */
	private BigDecimal getNoTaxPriceDown(BigDecimal taxPrice){
		return taxPrice.divide(BigDecimal.valueOf(1 + Constant.PER_CENT), 5, RoundingMode.HALF_DOWN);
	}
	
	
	/**
	 * 上面版综合单价  （含税单价  + 利润（目前不存在））
	 * @param taxSinglePrice
	 * @return
	 */
	private BigDecimal getPriceUp(BigDecimal taxSinglePrice){
		return taxSinglePrice;
	}
	
	/**
	 * 上面版综合合价(综合单价 * 工程量)
	 */
	private BigDecimal getSumPriceUp(BigDecimal price, Double gcl){
		return price.multiply(BigDecimal.valueOf(gcl));
	}

	
	/**
	 * 两个数相除
	 * @param v1
	 * @param v2
	 * @return
	 */
	private BigDecimal divive(Double v1, Double v2){
		return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), 5, RoundingMode.HALF_DOWN); 
	}
	
	/**
	 * 四舍五入转换为double
	 * @param v
	 * @return
	 */
	private Double toMoney(BigDecimal v){
		return v.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	private boolean doubleCompare(Double v1, Double v2){
		return Double.doubleToLongBits(v1) == Double.doubleToLongBits(v2);  
	}
	
	private BigDecimal multiply(Double v1, Double v2){
		return BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2)); 
	}
	
	/**
	 * 下面版 不含税合价 (不含税 单价 × 数量)
	 * @param noPrice
	 * @param amount
	 * @return
	 */
	private BigDecimal singleSumPriceDown(Double noPrice, Double amount) {
		return BigDecimal.valueOf(noPrice).multiply(BigDecimal.valueOf(amount));
	}

	/**
	 * 下面版  含税合价 (含税单价 × 数量)
	 * @param price
	 * @param amount
	 * @return
	 */
	private BigDecimal singleTaxSumPriceDown(Double price, Double amount) {
		return BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(amount));
	}
	
	/**
	 * 上面版 含税单价 （已经填写的下面版的值）
	 * @param detailList
	 * @return
	 */
	private BigDecimal taxSinglePriceUp(List<UnitProjectDetail> detailList) {
		
		BigDecimal taxSingelPrice = BigDecimal.ZERO;
		
		/**
		 * 含量  × 市场价
		 */
		for(UnitProjectDetail detail : detailList){
			taxSingelPrice = taxSingelPrice.add(this.multiply(detail.getContent(), detail.getTaxPrice()));
		}
		return taxSingelPrice;
	}
	
	@Override
	public UnitProjectDetail getDetailById(Map<String, String> queryMap) {
		return unitProjectDetailDao.getUnitProjectDetailById(queryMap);
	}
	
	@Override
	public void changeDetailSeq(Map<String, String> queryMap) {
		
		queryMap.put("unitProjectDetailId", queryMap.get("opearId"));
		UnitProjectDetail operUP = unitProjectDetailDao.getUnitProjectDetailById(queryMap);
		
		queryMap.put("unitProjectDetailId", queryMap.get("overId"));
		UnitProjectDetail overUP = unitProjectDetailDao.getUnitProjectDetailById(queryMap);
		
		Integer temp = operUP.getSeq();
		operUP.setSeq(overUP.getSeq());
		overUP.setSeq(temp);
		
		unitProjectDetailDao.updateByPrimaryKey(overUP);
		unitProjectDetailDao.updateByPrimaryKey(operUP);
	}
}
