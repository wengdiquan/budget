package com.bjsj.budget.controller;

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

import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.Project;
import com.bjsj.budget.model.ReportModel;
import com.bjsj.budget.service.ProjectService;
import com.bjsj.budget.util.TransforUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value="/project")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	/*@RequestMapping("/getProjectTreeList")
	@ResponseBody
	public List<JSONObject> getProjectTreeList(HttpServletRequest request, HttpServletResponse response){
		
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		if("click".equals(queryMap.get("type"))){
			//查询下级菜单
			List<Project> projectPList = projectService.getLowerProjectTreeList(queryMap);
			
			if(projectPList.isEmpty()){
				return resultList;
			}
			
			String iconClsStr = "icon-room";
			if(projectPList.get(0).getLevel() == 3){
				iconClsStr = "icon-wood";
			}
			JSONObject jsonP = new JSONObject();
			JSONArray sjsonArray = new JSONArray();
			for(Project project: projectPList){
				JSONObject jsonS = new JSONObject();
				jsonS.element("id", project.getProjectId());
				jsonS.element("text", project.getProjectName());
				jsonS.element("leaf", false);
				jsonS.element("iconCls", iconClsStr);
				jsonS.element("expanded", true);
				jsonS.element("level", project.getLevel());
				queryMap.put("parentId", project.getProjectId() + "");
				List<Project> bprojectList = projectService.getLowerProjectTreeList(queryMap);
				JSONArray bjsonArray = new JSONArray();
				for(Project bproject: bprojectList){
					JSONObject jsonB = new JSONObject();
					jsonB.element("id", bproject.getProjectId());
					jsonB.element("text", bproject.getProjectName());
					jsonB.element("leaf", false);
					jsonB.element("iconCls", "icon-wood");
					jsonB.element("expanded", true);
					jsonB.element("level", bproject.getLevel());
					bjsonArray.add(jsonB);
				}
				if(!bjsonArray.isEmpty()){
					jsonS.element("children", bjsonArray);
				}
				sjsonArray.add(jsonS);
			}
			if(!sjsonArray.isEmpty()){
				jsonP.element("children", sjsonArray);
			}
			
			resultList.add(jsonP);
			
		}else{
			//查询该项目或子节点
			Project projectP = projectService.getProjectRoot(queryMap);
			JSONObject jsonP = new JSONObject();
			jsonP.element("id", projectP.getProjectId());
			jsonP.element("text", projectP.getProjectName());
			jsonP.element("leaf", false);
			jsonP.element("iconCls", "icon-buliding");
			jsonP.element("expanded", true);
			jsonP.element("level", projectP.getLevel());
			queryMap.put("parentId", projectP.getProjectId() + "");
			List<Project> sprojectList = projectService.getLowerProjectTreeList(queryMap);
			JSONArray sjsonArray = new JSONArray();
			for(Project sproject: sprojectList){
				JSONObject jsonS = new JSONObject();
				jsonS.element("id", sproject.getProjectId());
				jsonS.element("text", sproject.getProjectName());
				jsonS.element("leaf", false);
				jsonS.element("iconCls", "icon-room");
				jsonS.element("expanded", true);
				jsonS.element("level", sproject.getLevel());
				
				queryMap.put("parentId", sproject.getProjectId() + "");
				List<Project> bprojectList = projectService.getLowerProjectTreeList(queryMap);
				JSONArray bjsonArray = new JSONArray();
				for(Project bproject: bprojectList){
					JSONObject jsonB = new JSONObject();
					jsonB.element("id", bproject.getProjectId());
					jsonB.element("text", bproject.getProjectName());
					jsonB.element("leaf", false);
					jsonB.element("iconCls", "icon-wood");
					jsonB.element("expanded", true);
					jsonB.element("level", bproject.getLevel());
					bjsonArray.add(jsonB);
				}
				if(!bjsonArray.isEmpty()){
					jsonS.element("children", bjsonArray);
				}
				sjsonArray.add(jsonS);
			}
			if(!sjsonArray.isEmpty()){
				jsonP.element("children", sjsonArray);
			}
			
			resultList.add(jsonP);
		}
		
		return resultList;
	}*/
	
	/**
	 * 请求别的系统，获取项目信息
	 * @return
	 */
	@RequestMapping("/getProjectTreeList")
	@ResponseBody
	public List<JSONObject> getProjectTreeList(HttpServletRequest request, HttpServletResponse response){
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		if("init".equals(queryMap.get("source"))){
			List<Project> otherProjectList = new ArrayList<Project>();
			Project p1 = new Project();
			p1.setProjectId(1);
			p1.setProjectName("项目1");
			
			Project p2 = new Project();
			p2.setProjectId(2);
			p2.setProjectName("项目2");
			
			otherProjectList.add(p1);
			otherProjectList.add(p2);
			
			for(Project pproject: otherProjectList){
				JSONObject jsonp = new JSONObject();
				jsonp.element("id", pproject.getProjectId());
				jsonp.element("text", pproject.getProjectName());
				jsonp.element("leaf", false);
				jsonp.element("iconCls", "icon-buliding");
				jsonp.element("expanded", true);
				jsonp.element("level", pproject.getLevel());
				
				queryMap.put("parentId", pproject.getProjectId() + "");
				List<Project> sprojectList = projectService.getLowerProjectTreeList(queryMap);
				JSONArray sjsonArray = new JSONArray();
				for(Project sproject: sprojectList){
					JSONObject jsonS = new JSONObject();
					jsonS.element("id", sproject.getProjectId());
					jsonS.element("text", sproject.getProjectName());
					jsonS.element("leaf", false);
					jsonS.element("iconCls", "icon-room");
					jsonS.element("expanded", true);
					jsonS.element("level", sproject.getLevel());
					
					queryMap.put("parentId", sproject.getProjectId() + "");
					List<Project> bprojectList = projectService.getLowerProjectTreeList(queryMap);
					JSONArray bjsonArray = new JSONArray();
					for(Project bproject: bprojectList){
						JSONObject jsonB = new JSONObject();
						jsonB.element("id", bproject.getProjectId());
						jsonB.element("text", bproject.getProjectName());
						jsonB.element("leaf", true);
						jsonB.element("iconCls", "icon-wood");
						jsonB.element("expanded", false);
						jsonB.element("level", bproject.getLevel());
						bjsonArray.add(jsonB);
					}
					jsonS.element("children", bjsonArray);
					sjsonArray.add(jsonS);
				}
				jsonp.element("children", sjsonArray);
				
				resultList.add(jsonp);
			}
		}
		
		return resultList;
	}
	
	@RequestMapping(value = "/saveOrUpdateProject")
	@ResponseBody
	public String saveOrUpdateProject (HttpServletRequest request,
			HttpServletResponse response){
		
		Project project = new Project();
		
		try
		{
			TransforUtil.transFromMapToBean(request.getParameterMap(), project);
			
			if("edit".equals(request.getParameter("cmd"))){
				projectService.updateProject(project);
			}else{
				projectService.insertProject(project);
			}
			
		}catch (Exception ex) {
			ex.printStackTrace();
			return "{success:false,msg:'错误:" + ex.getMessage() + "'}";
		}
		return "{success:true, data:'" + project.getProjectId() +"'}";
	}
	
	/**
	 * 请求别的系统，获取项目信息
	 * @return
	 */
	@RequestMapping("/getProjectSum")
	@ResponseBody
	public List<JSONObject> getProjectSum(HttpServletRequest request, HttpServletResponse response){
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		if("init".equals(queryMap.get("source"))){
			List<Project> otherProjectList = new ArrayList<Project>();
			Project p1 = new Project();
			p1.setProjectId(1);
			p1.setProjectName("项目1");
			
			Project p2 = new Project();
			p2.setProjectId(2);
			p2.setProjectName("项目2");
			
			otherProjectList.add(p1);
			otherProjectList.add(p2);
			
			for(Project pproject: otherProjectList){
				JSONObject jsonp = new JSONObject();
				jsonp.element("id", pproject.getProjectId());
				jsonp.element("text", pproject.getProjectName());
				jsonp.element("leaf", false);
				jsonp.element("iconCls", "icon-buliding");
				jsonp.element("expanded", true);
				jsonp.element("level", pproject.getLevel());
				
				queryMap.put("parentId", pproject.getProjectId() + "");
				List<Project> sprojectList = projectService.getLowerProjectTreeList(queryMap);
				JSONArray sjsonArray = new JSONArray();
				for(Project sproject: sprojectList){
					JSONObject jsonS = new JSONObject();
					jsonS.element("id", sproject.getProjectId());
					jsonS.element("text", sproject.getProjectName());
					jsonS.element("leaf", false);
					jsonS.element("iconCls", "icon-room");
					jsonS.element("expanded", true);
					jsonS.element("level", sproject.getLevel());
					
					queryMap.put("parentId", sproject.getProjectId() + "");
					List<Project> bprojectList = projectService.getLowerProjectTreeList(queryMap);
					JSONArray bjsonArray = new JSONArray();
					for(Project bproject: bprojectList){
						JSONObject jsonB = new JSONObject();
						jsonB.element("id", bproject.getProjectId());
						jsonB.element("text", bproject.getProjectName());
						jsonB.element("leaf", true);
						jsonB.element("iconCls", "icon-wood");
						jsonB.element("expanded", false);
						jsonB.element("level", bproject.getLevel());
						bjsonArray.add(jsonB);
					}
					jsonS.element("children", bjsonArray);
					sjsonArray.add(jsonS);
				}
				jsonp.element("children", sjsonArray);
				
				resultList.add(jsonp);
			}
		}
		
		return resultList;
	}
	
	/**
	 * 查询 汇总数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getSumList")
	@ResponseBody
	public List getSumList(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = TransforUtil.transRMapToMap(request.getParameterMap());
		List<ReportModel> CMList = new ArrayList();
		if(queryMap.get("projectId") != null && queryMap.get("projectId") != ""){
			CMList = projectService.getSumList(queryMap);
		}
		
		return CMList;
	}
}
