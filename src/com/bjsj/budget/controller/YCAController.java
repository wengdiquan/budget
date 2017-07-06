package com.bjsj.budget.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.YCAService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.NumberUtils;
import com.bjsj.budget.util.TransforUtil;
@Controller
@RequestMapping(value="/yca")
public class YCAController {
	@Autowired
	private YCAService yCAService;
	
	/**
	 * 查询 yca
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryYCAInfo")
	@ResponseBody
	public ListView queryYCAInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = yCAService.queryYCAInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	@RequestMapping(value = "/saveOrUpdateValue")
	@ResponseBody
	public JSONObject saveOrUpdateValue (HttpServletRequest request,
			HttpServletResponse response){
		YCAModel record = new YCAModel();
		JSONObject resultJson = new JSONObject();
		resultJson.put("success", true);
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			
			if("edit".equals(request.getParameter("cmd"))){
				record.setNoPrice(NumberUtils.degree(NumberUtils.divive(record.getPrice(), 1 + NumberUtils.divive(record.getRate(), 100d))));
				yCAService.updateValue(record);
			}else{
				Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
				List<LookValue> lookValue = yCAService.getLookValueList(queryMap);
				if(lookValue.size() > 0){
					LookValue tt = lookValue.get(0);
					String code = record.getCode();
					record.setType(TransforUtil.transYCAByType(tt.getLookTypeId()));
					record.setCode(tt.getLookvalueCode() + "-" + code);
					record.setNoPrice(NumberUtils.degree(NumberUtils.divive(record.getPrice(), 1 + NumberUtils.divive(record.getRate(), 100d))));
					yCAService.insertValue(record);
				}
			}
		}catch (Exception ex) {
			resultJson.put("success", false);
			resultJson.put("msg", ex.getMessage());
		}
		return resultJson;
	}
	/**
	 * 查询 字典类型
	 * @param request
	 * @param response
	 * @return
	 */
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
	}
}
