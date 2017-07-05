package com.bjsj.budget.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.model.FeeTotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.FeeTotalService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;

import net.sf.json.JSONArray;
@Controller
@RequestMapping(value="/feetotal")
public class FeeTotalController {
	@Autowired
	private FeeTotalService feeTotalService;
	
	/**
	 * 查询 费用 汇总
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryFeeTotalInfo")
	@ResponseBody
	public ListView queryFeeTotalInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = feeTotalService.queryFeeTotalInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	/**
	 * 查询 费用 汇总 模板
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryFeeTemplate")
	@ResponseBody
	public ListView queryFeeTemplate(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = feeTotalService.queryFeeTemplate(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	@RequestMapping(value = "/saveOrUpdateValue")
	@ResponseBody
	public String saveOrUpdateValue ( HttpServletRequest request,HttpServletResponse response){
		try
		{
			feeTotalService.insertValue(request.getParameterMap());
		}catch (Exception ex) {
			return "{success:false,msg:'" + ex.getMessage() + "'}";
		}
		return "{success:true}";
	}
	
	@RequestMapping(value = "/saveValue")
	@ResponseBody
	public String saveValue ( HttpServletRequest request,HttpServletResponse response){
		FeeTotalModel vo =new FeeTotalModel();
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(),vo);
			feeTotalService.insertValueNull(vo);
		}catch (Exception ex) {
			return "{success:false,msg:'" + ex.getMessage() + "'}";
		}
		return "{success:true}";
	}
	
	@RequestMapping(value = "/updateValue")
	@ResponseBody
	public String updateValue ( HttpServletRequest request,HttpServletResponse response){
		FeeTotalModel record = new FeeTotalModel();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
			if("editdb".equals(queryMap.get("cmd"))){
				feeTotalService.updateValue(record);
			}
			String fieldName = queryMap.get("fieldName");
			String value = queryMap.get("value");
			if("seq".equals(fieldName)){
				record.setSeq(value);
				feeTotalService.updateValue(record);
			}
			if("feeCode".equals(fieldName)){
				record.setFeeCode(value);
				feeTotalService.updateValue(record);
			}
			if("name".equals(fieldName)){
				record.setName(value);
				feeTotalService.updateValue(record);
			}
			if("calculatedRadix".equals(fieldName)){
				record.setCalculatedRadix(value);
				feeTotalService.updateValueRadix(record);
			}
			if("rate".equals(fieldName)){
				if(null == value || "".equals(value)){
					return "{success:true}";
				}
				record.setRate(Double.valueOf(value));
				feeTotalService.updateValue(record);
			}
			if("remark".equals(fieldName)){
				record.setRemark(value);
				feeTotalService.updateValue(record);
			}
			
		}catch (Exception ex) {
			return "{success:false,msg:'" + ex.getMessage() + "'}";
		}
		return "{success:true}";
	}
	
	@RequestMapping(value = "/updateValueRadix")
	@ResponseBody
	public String updateValueRadix ( HttpServletRequest request,HttpServletResponse response){
		FeeTotalModel record = new FeeTotalModel();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			feeTotalService.updateValueRadix(record);
		}catch (Exception ex) {
			return "{success:false,msg:'" + ex.getMessage() + "'}";
		}
		return "{success:true}";
	}
	
	/**//**
	 * 查询 字典类型
	 * @param request
	 * @param response
	 * @return
	 *//*
	@RequestMapping(value = "/queryTreeList")
	@ResponseBody
	public List<JSONObject> queryLookTypeInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		List<HashMap> typeList = yCAService.getLookTypeList(queryMap);
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		for(int i = 0; i < typeList.size(); i++){
			JSONObject jsonRoot = new JSONObject();
			Map map = typeList.get(i);
			jsonRoot.element("id", map.get("looktype_id"));
			jsonRoot.element("text", map.get("looktype_name"));
			jsonRoot.element("leaf", false);
			jsonRoot.element("expanded", true);
			
			queryMap.put("lookTypeId", map.get("looktype_id").toString());
			List<LookValue> valueList = yCAService.getLookValueList(queryMap);
			
			JSONArray sjsonArray = new JSONArray();
			for(LookValue lookValue : valueList){
				JSONObject json = new JSONObject();
				json.element("id", lookValue.getLookvalueId());
				json.element("text", lookValue.getLookvalueName());
				json.element("leaf", true);
				json.element("expanded", false);
				sjsonArray.add(json);
			}
			
			jsonRoot.element("children", sjsonArray);
			resultList.add(jsonRoot);
		}
		
		return resultList;
	}*/
}
