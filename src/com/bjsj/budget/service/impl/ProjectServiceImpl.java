package com.bjsj.budget.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.ProjectDao;
import com.bjsj.budget.model.Project;
import com.bjsj.budget.model.ReportModel;
import com.bjsj.budget.service.ProjectService;

@Service("projectServiceImpl")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectDao projectDao;
	
	@Override
	public void updateProject(Project project) throws Exception {
		
		
		/***
		 * 先删除单位工程信息 TODO
		 */
		
		
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("parentId", project.getProjectId() + "");
		
		List<Project> childProjectList = getLowerProjectTreeList(queryMap);
		for(Project p : childProjectList){
			projectDao.deleteByPrimaryKey(p.getProjectId());
		}
		projectDao.deleteByPrimaryKey(project.getProjectId());
	}

	@Override
	public void insertProject(Project project) throws Exception {
		
		Map<String,Object> queryMap = new HashMap<String, Object>();
		
		//如果level 为1 的话，表示是项目，不需要检查
		//如果level 为2的话，表示是单项工程，需要检查
		//如果level 为3的话，表示是单位工程，需要检查
		if(1 != project.getLevel()){
			queryMap.put("isChild", "Y");
			queryMap.put("projectName", project.getProjectName());
			queryMap.put("level", project.getLevel());
			queryMap.put("parentId", project.getParentId());
			
			int countProject = projectDao.getCountProjectByMap(queryMap);
			if(countProject > 1){
				throw new Exception("名称已经存在，无法新增"); 
			}
		}
		projectDao.insert(project);
	}
	
	@Override
	public Project getProjectRoot(Map<String, String> queryMap) {
		return projectDao.selectByMap(queryMap);
	}
	
	@Override
	public List<Project> getLowerProjectTreeList(Map<String, String> queryMap) {
		return projectDao.getLowerProjectTreeList(queryMap);
	}

	@Override
	public List<ReportModel> getSumList(@Param("map") Map<String, String> queryMap) {
		if("1".endsWith(queryMap.get("pro"))){
			return projectDao.getSumList(queryMap);
		}else{
			return projectDao.getSumListUnit(queryMap);
		}
		
	}
}
