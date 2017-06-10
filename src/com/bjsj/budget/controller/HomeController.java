package com.bjsj.budget.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjsj.budget.constant.Constant;
import com.bjsj.budget.model.Authority;
import com.bjsj.budget.service.AuthorityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 请求页面控制层
 * @author TrueBt
 *
 */
@Controller
public class HomeController {
		
	
	@Autowired
	private AuthorityService authorityService;
	
	
	@RequestMapping("/home")
	public String home(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//模拟用户登录,用于和其他平台结合
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put("userName", "小明");
		userMap.put("userId", "1");
		request.getSession().setAttribute(Constant.SESSION_SYS_USER, userMap);
		return "back/main";
		
		/*if (request.getSession().getAttribute(Constant.SESSION_SYS_USER) == null) {
			return "";
		} else {
			return "back/main";
		}*/
	}
	
	
	@RequestMapping("/menu")
	@ResponseBody
	public List<JSONObject> menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("parentId", null);
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		List<Authority> firstMenuList =  authorityService.getMenuInfo(queryMap);
		Authority authority = null;
		for (int i = 0; i < firstMenuList.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			authority = firstMenuList.get(i);
			jsonObject.element("id", authority.getId());
			jsonObject.element("sortOrder", authority.getSortOrder());
			jsonObject.element("menuCode", authority.getMenuCode());
			jsonObject.element("text", authority.getMenuName());
			jsonObject.element("menuConfig", authority.getMenuConfig());
			jsonObject.element("expanded", authority.getExpanded());
			jsonObject.element("checked", authority.getChecked());
			jsonObject.element("leaf", authority.getLeaf());
			jsonObject.element("url", authority.getUrl());
			jsonObject.element("iconCls", authority.getIconCls());
			
			JSONArray jsonArray = new JSONArray();
			queryMap.put("parentId", authority.getId());
			List<Authority> childrenMenuList  = authorityService.getMenuInfo(queryMap);
			for (int j = 0; j < childrenMenuList.size(); j++) {
				JSONObject childrenJsonObject = new JSONObject();
				authority = childrenMenuList.get(j);
				childrenJsonObject.element("id", authority.getId());
				childrenJsonObject.element("sortOrder", authority.getSortOrder());
				childrenJsonObject.element("menuCode", authority.getMenuCode());
				childrenJsonObject.element("text", authority.getMenuName());
				childrenJsonObject.element("menuConfig", authority.getMenuConfig());
				childrenJsonObject.element("expanded", authority.getExpanded());
				childrenJsonObject.element("checked", authority.getChecked());
				childrenJsonObject.element("leaf", authority.getLeaf());
				childrenJsonObject.element("url", authority.getUrl());
				childrenJsonObject.element("iconCls", authority.getIconCls());
				jsonArray.add(childrenJsonObject);
			}
			jsonObject.element("children", jsonArray);
			resultList.add(jsonObject);
		}
		
	/*	JsonObject resultJson = new JsonObject();
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(resultList);
		resultJson.add("children", jsonElement);*/
		return resultList;
	}
	
	
}
