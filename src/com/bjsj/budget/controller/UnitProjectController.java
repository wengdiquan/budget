package com.bjsj.budget.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.UnitProjectService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value="/unitproject")
public class UnitProjectController {
	
	@Autowired
	private UnitProjectService unitProjectService;
	
	/**
	 * 新增单位工程数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/insertItem")
	@ResponseBody
	public JSONObject insertItem(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		try{
			unitProjectService.insertItem(queryMap);
		}catch (Exception ex) {
			ex.printStackTrace();
			json.put("success", false);
			json.put("msg", "出错:" + ex.getMessage());
		}
		return json;
	}
	
	/**
	 * 新建空的单位工程
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/insertBlankItem")
	@ResponseBody
	public JSONObject insertBlankItem(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		try{
			unitProjectService.insertBlankItem(queryMap);
		}catch (Exception ex) {
			ex.printStackTrace();
			json.put("success", false);
			json.put("msg", "出错:" + ex.getMessage());
		}
		return json;
	}
	
	/**
	 * 单位工程子目信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getBitProjectItemInfo")
	@ResponseBody
	public ListView getBitProjectItemInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = unitProjectService.getBitProjectItemInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	/**
	 * 查询单位工程的子目详细信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getBitProjectDetailInfo")
	@ResponseBody
	public ListView getBitProjectDetailInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = unitProjectService.getBitProjectDetailInfo(queryMap, pageInfo);
		
		ListView typeList = new ListView();
		typeList.setData(pageObj.getRows());
		typeList.setTotalRecord(pageObj.getRecords());
		return typeList;
	}
	
	/**
	 * 删除单位工程下的子目
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteBitProjectItem")
	@ResponseBody
	public JSONObject deleteBitProjectItem(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		try{
			unitProjectService.deleteBitProjectItem(queryMap);
		}catch (Exception ex) {
			ex.printStackTrace();
			json.put("success", false);
			json.put("msg", "出错:" + ex.getMessage());
		}
		return json;
	}
}
