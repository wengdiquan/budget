package com.bjsj.budget.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.model.UnitProject;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.UnitProjectService;
import com.bjsj.budget.util.ListView;
import com.bjsj.budget.util.TransforUtil;

import net.sf.json.JSONArray;
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
	public JSONObject getBitProjectItemInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		PageInfo pageInfo = new PageInfo();
		
		PageObject pageObj = unitProjectService.getBitProjectItemInfo(queryMap, pageInfo);
		
		UnitProject root = new UnitProject();
		
		JSONObject rootJ = new JSONObject();
		rootJ.element("name", "整个项目");
		//不含税合价
		rootJ.element("singlePrice", this.getTotalPrice(1, (List<UnitProject>)pageObj.getRows()));
		//含税合价
		rootJ.element("taxSinglePrice", this.getTotalPrice(2, (List<UnitProject>)pageObj.getRows()));
		//不含税综合合价
		rootJ.element("price", this.getTotalPrice(3, (List<UnitProject>)pageObj.getRows()));
		//含税综合合价
		rootJ.element("sumPrice", this.getTotalPrice(4, (List<UnitProject>)pageObj.getRows()));
		rootJ.element("leaf", false);	
		rootJ.element("expanded", true);
		rootJ.element("iconCls", "no-icon");
		
		JSONArray jsonA = new JSONArray();
		for(UnitProject u : (List<UnitProject>) pageObj.getRows()){
			JSONObject json = JSONObject.fromObject(u);
			json.element("iconCls", "no-icon");
			json.element("leaf", true);	
			json.element("expanded", false);
			jsonA.add(json);
		}
		
		rootJ.element("children", jsonA);
		
		JSONArray resultA = new JSONArray();
		resultA.add(rootJ);
		JSONObject result = new JSONObject();
		result.element("children", resultA);
		return result;
	}
	
	/**
	 * 获取价格
	 * @param i
	 * @param rows
	 * @return
	 */
	private Double getTotalPrice(int i, List<UnitProject> unitProjectList) {
		
		BigDecimal decimal = BigDecimal.valueOf(0);
		if(i == 1){
			for(UnitProject u : unitProjectList){
				if(!Objects.isNull(u.getSinglePrice())){
					decimal = decimal.add(BigDecimal.valueOf(u.getSinglePrice()));
				}
			}
		}
		
		if(i == 2){
			for(UnitProject u : unitProjectList){
				if(!Objects.isNull(u.getTaxSinglePrice())){
					decimal = decimal.add(BigDecimal.valueOf(u.getTaxSinglePrice()));
				}
			}
		}
		
		if(i == 3){
			for(UnitProject u : unitProjectList){
				if(!Objects.isNull(u.getPrice())){
					decimal = decimal.add(BigDecimal.valueOf(u.getPrice()));
				}
			}
		}
		
		if(i == 4){
			for(UnitProject u : unitProjectList){
				if(!Objects.isNull(u.getSumPrice())){
					decimal = decimal.add(BigDecimal.valueOf(u.getSumPrice()));
				}
			}
		}
		
		return decimal.doubleValue();
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
	
	/**
	 * 改变顺序
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/changeSeq")
	@ResponseBody
	public JSONObject changeSeq(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		try{
			unitProjectService.changeSeq(queryMap);
		}catch (Exception ex) {
			ex.printStackTrace();
			json.put("success", false);
			json.put("msg", "出错:" + ex.getMessage());
		}
		return json;
	}
	
	/**
	 * 更新字段
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updateItemAndDetail")
	@ResponseBody
	public JSONObject updateItemAndDetail(HttpServletRequest request, HttpServletResponse response){
		// 查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		try{
			unitProjectService.updateItemAndDetail(queryMap);
		}catch (Exception ex) {
			ex.printStackTrace();
			json.put("success", false);
			json.put("msg", "出错:" + ex.getMessage());
		}
		return json;
	}
	
}
