package com.bjsj.budget.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	public String saveOrUpdateValue (HttpServletRequest request,
			HttpServletResponse response){
		YCAModel record = new YCAModel();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), record);
			
			if("edit".equals(request.getParameter("cmd"))){
				yCAService.updateValue(record);
			}else{
				yCAService.insertValue(record);
			}
			
			
		}catch (Exception ex) {
			return "{success:false,msg:" + ex.getMessage() + "}";
		}
		return "{success:true}";
	}
}
