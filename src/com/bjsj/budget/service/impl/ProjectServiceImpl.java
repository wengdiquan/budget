package com.bjsj.budget.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.CategoryModelDao;
import com.bjsj.budget.dao.ProjectDao;
import com.bjsj.budget.model.CategoryModelModel;
import com.bjsj.budget.model.CategoryModelYCAModel;
import com.bjsj.budget.model.Project;
import com.bjsj.budget.model.ReportModel;
import com.bjsj.budget.model.UnitProject;
import com.bjsj.budget.model.UnitProjectDetail;
import com.bjsj.budget.service.ProjectService;

@Service("projectServiceImpl")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectDao projectDao;
	
	@Autowired
	private CategoryModelDao categoryModelDao;
	
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
	
	//新增单项工程
	@Override
	public void insertBitProject(Map<String, String> queryMap) throws Exception {
		
		String type = queryMap.get("type");
		
		//定额类型
		if("DING".equals(type)){
			
			//根据id 查询编码
			CategoryModelModel cgmModel = categoryModelDao.selectCMByPrimaryKey(queryMap);
			
			UnitProject u = new UnitProject();
			u.setCode(cgmModel.getCode());
			u.setType("定");
			u.setType(cgmModel.getName());
			u.setUnit(cgmModel.getUnit());
			u.setContent(null);//含量
			u.setDtgcl(1d);//单体工程量为默认为1
			
			//根据定额ID,查询下面的运材安
			List<CategoryModelYCAModel> ycaModelList = categoryModelDao.getDetailYCA(cgmModel.getId());
			
			double singlePrice = 0; //单价（//TODO）
			List<UnitProjectDetail> detailList = new ArrayList<UnitProjectDetail>();
			for(CategoryModelYCAModel ycaModel:ycaModelList){
				UnitProjectDetail detail = new UnitProjectDetail();
				
				//明细信息
				detail.setUnitprojectId(Integer.parseInt(queryMap.get("bitProjectId")));
				detail.setCode(ycaModel.getCode());
				//detail.setType();
				detail.setName(ycaModel.getName());
				
				
				
				
				
				singlePrice += ycaModel.getNoPrice() * ycaModel.getContent();
				detailList.add(detail);
			}
			u.setSinglePrice(singlePrice); //单价
			u.setSingleSumPrice(singlePrice * 1); //合价
			u.setPrice(cgmModel.getTransportFee() + cgmModel.getMaterialFee() + cgmModel.getInstallationFee()); //综合单价
			u.setSumPrice(u.getPrice() * 1); //综合合价
			u.setRemark(null);
			u.setBitProjectId(Integer.parseInt(queryMap.get("bitProjectId")));
			u.setSourceType("DING");
			
			
			
		}	
	}
}
