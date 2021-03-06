package com.bjsj.budget.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.model.YCATotalModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.YCATotalService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;
@Controller
@RequestMapping(value="/ycatotal")
public class YCATotalController {
	@Autowired
	private YCATotalService yCATotalService;
	
	/**
	 * 查询 yca 汇总
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryYCATotalInfo")
	@ResponseBody
	public ListView queryYCATotalInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = yCATotalService.queryYCATotalInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	@RequestMapping(value = "/saveOrUpdateValue")
	@ResponseBody
	public String saveOrUpdateValue (HttpServletRequest request,HttpServletResponse response){
		YCATotalModel record = new YCATotalModel();
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
			String fieldName = queryMap.get("fieldName");
			String value = queryMap.get("value");
			if("name".equals(fieldName)){
				record.setName(value);
				yCATotalService.updateValue(record);
			}
			if("tax_Price".equals(fieldName)){
				yCATotalService.updateAmountValue(request.getParameterMap());
			}
		}catch (Exception ex) {
			return "{success:false,msg:" + ex.getMessage() + "}";
		}
		return "{success:true}";
	}
	
	@RequestMapping(value = "/updateValue")
	@ResponseBody
	public String updateValue (HttpServletRequest request,HttpServletResponse response){
		try{
			yCATotalService.updateAmountValue(request.getParameterMap());
		}catch (Exception ex) {
			return "{success:false,msg:" + ex.getMessage() + "}";
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
