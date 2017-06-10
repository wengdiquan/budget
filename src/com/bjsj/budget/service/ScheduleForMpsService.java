package com.bjsj.budget.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.StringHolder;
import org.springframework.web.multipart.MultipartFile;

import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;

public interface ScheduleForMpsService {
	
	/**
	 * 查询主排程
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	PageObject queryScheduleInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询关联排程
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	PageObject queryRelationScheduleInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 获取最新的排程单号
	 * @param queryMap
	 */
	void getCurrHeaderId(Map<String, Object> queryMap);
	
	/**
	 * 查询快表
	 * @param queryMap
	 * @return
	 */
	List<Map> findLookupValues(Map<String, Object> queryMap);
	
	/**
	 * 刷新数据
	 * @param queryMap
	 */
	void freshMpsData(Map<String, String> queryMap);

	/**
	 * 复制设置
	 * @param queryMap
	 * @return
	 */
	List<Map> getCopySetup(Map<String, Object> queryMap);

	/**
	 * 复制
	 * @param queryMap
	 */
	void copy(Map<String, String> queryMap);
	
	/**
	 * 下载
	 * @param queryMap
	 */
	void down(Map<String, String> queryMap) throws Exception;
	
	/**
	 * 获取默认的模板（根据角色查询）
	 * @param queryMap
	 * @return
	 */
	String getDefaultTemplateByRoleId(Map<String, Object> queryMap);
	
	/**
	 * 上传
	 * @param file
	 * @param queryMap
	 * @param sh
	 * @throws Exception 
	 */
	void importSchedule(MultipartFile file, Map<String, String> queryMap, StringHolder sh) throws Exception;
	
	/**
	 * 更新备注
	 * @param map
	 * @param sh
	 */
	void updateRemarkByHeaderId(Map<String, String> map) throws Exception;
	
	/**
	 * 提交
	 * @param queryMap
	 */
	void submit(Map<String, String> queryMap);

	/**
	 * 催办
	 * @param queryMap
	 */
	void urge(Map<String, String> queryMap);
	
	/**
	 * 获取关联料号，和仅显示MPS
	 * @param queryMap
	 * @return
	 */
	HashMap<String, String> getRelationAndMpsFlag(Map<String, Object> queryMap);
	
	/**
	 * 查询设置信息
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	PageObject queryProfile(Map<String, String> map, PageInfo pageInfo);
	
	/**
	 * 更新设置信息
	 * @param queryMap 
	 * @param list
	 * @throws Exception 
	 */
	void updateSetup(Map<String, String> queryMap, List<Map<String, String>> list) throws Exception;
	
	/**
	 * 验证
	 * @param queryMap
	 * @throws Exception 
	 */
	void validate(Map<String, String> queryMap) throws Exception;
	
	/**
	 * 点击设置时，触发
	 * @param queryMap
	 */
	void loadSetUp(Map<String, Object> queryMap);
	
	/**
	 * 下载提交状态
	 * @param jsonparams
	 * @throws Exception 
	 */
	void downSubMitStatusInfo(Map<String, String> jsonparams) throws Exception;
	
	/**
	 * 下载需求数据
	 * @param jsonparams
	 * @throws Exception 
	 */
	void downDemandDataInfo(Map<String, String> jsonparams) throws Exception;
	
	/***
	 * 查询提交状态
	 * @param jsonparams
	 * @param pageInfo
	 * @return
	 */
	PageObject querySubMitStatusInfo(Map<String, String> jsonparams, PageInfo pageInfo);
	
	/**
	 * 查询需求数量
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	PageObject queryRequireDataInfo(Map<String, String> queryMap, PageInfo pageInfo);
	
	/**
	 * 查询活动历史
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	PageObject queryHistoryAction(Map<String, String> map, PageInfo pageInfo);
	
	/**
	 * 查询排程消息
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	PageObject queryScheduleMsgInfo(Map<String, String> map, PageInfo pageInfo);
}
