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
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.model.YCAModel;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.CategoryModelService;
import com.bjsj.budget.service.YCAService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;
@Controller
@RequestMapping(value="/categorymodel")
public class CategoryModelController {
	@Autowired
	private YCAService yCAService;
	@Autowired
	private CategoryModelService categoryModelService;
	
	/**
	 * 查询 查询右侧 上部分 序里的数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryDetailCategoryModel")
	@ResponseBody
	public ListView queryDetailCategoryModel(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = categoryModelService.queryDetailCategoryModel(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	/**
	 * 查询右侧下方部分 运材安数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryDetailYCA")
	@ResponseBody
	public ListView queryDetailYCA(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = categoryModelService.queryDetailYCA(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	@RequestMapping(value = "/saveOrUpdateValue")
	@ResponseBody
	public String saveOrUpdateValue (HttpServletRequest request,
			HttpServletResponse response){
		YCAModel record = new YCAModel();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			
			if("edit".equals(request.getParameter("cmd"))){
				yCAService.updateValue(record);
			}else{
				Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
				List<LookValue> lookValue = yCAService.getLookValueList(queryMap);
				if(lookValue.size() > 0){
					LookValue tt = lookValue.get(0);
					String code = record.getCode();
					record.setCode(tt.getLookvalueCode() + "-" + code);
					yCAService.insertValue(record);
				}
			}
			
			
		}catch (Exception ex) {
			return "{success:false,msg:" + ex.getMessage() + "}";
		}
		return "{success:true}";
	}
	/**
	 * 查询 左侧树状  模块
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getCategoryModelList")
	@ResponseBody
	public List<JSONObject> getCategoryModelList(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		queryMap.put("leaf", "0");
		List<CategoryModelModel> CMList = categoryModelService.getCategoryModelList(queryMap);
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		Map<String, String> queryMap1 = new HashMap();
		for(int i = 0; i < CMList.size(); i++){
			JSONObject jsonRoot = new JSONObject();
			CategoryModelModel map = CMList.get(i);
			jsonRoot.element("id", map.getId());
			jsonRoot.element("text", map.getName());
			jsonRoot.element("leaf", false);
			jsonRoot.element("expanded", true);
			
			queryMap1.put("parentId", map.getId().toString());
			List<CategoryModelModel> CMMChild = categoryModelService.getCategoryModelList(queryMap1);
			
			JSONArray sjsonArray = new JSONArray();
			for(CategoryModelModel child : CMMChild){
				JSONObject json = new JSONObject();
				json.element("id", child.getId());
				json.element("text", child.getName());
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
