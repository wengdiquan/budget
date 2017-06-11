package com.bjsj.budget.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.constant.Constant;
import com.bjsj.budget.model.LookValue;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.LookValueService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;

@Controller
@RequestMapping(value="/lookvalue")
public class LookValueController {
	
	@Autowired
	private LookValueService lookValueService;
	
	
	/**
	 * 查询 字典类型
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryLookTypeInfo")
	@ResponseBody
	public ListView queryLookTypeInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = lookValueService.queryLookTypeInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	
	/**
	 * 查询 字典值
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryLookValueInfo")
	@ResponseBody
	public ListView queryLookValueInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		String type = request.getParameter("type");
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		queryMap.put("listCode", Constant.COST_CODE);
		PageObject pageObj = lookValueService.queryLookValueInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	@RequestMapping(value = "/saveOrUpdateValue")
	@ResponseBody
	public String saveOrUpdateValue (HttpServletRequest request,
			HttpServletResponse response){
		LookValue lv = new LookValue();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), lv);
			
			if("edit".equals(request.getParameter("cmd"))){
				lookValueService.updateValue(lv);
			}else{
				lookValueService.insertValue(lv);
			}
			
			
		}catch (Exception ex) {
			return "{success:false,msg:" + ex.getMessage() + "}";
		}
		return "{success:true}";
	}
}
