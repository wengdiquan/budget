package com.bjsj.budget.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bjsj.budget.model.ScheduleConfgExcelForMpsBean;
import com.bjsj.budget.model.ScheduleForMpsBean;
import com.bjsj.budget.page.PageInfo;

public interface ScheduleForMpsDao extends BaseDao {
	
	/**
	 * 获取主排程
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	List<ScheduleForMpsBean> queryScheduleInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 满足主排程的数量
	 * @param queryMap
	 * @return
	 */
	int getScheduleInfoCount(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 获取关联排程
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	List<ScheduleForMpsBean> queryRelationScheduleInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);

	/**
	 * 满足关联排程的数量
	 * @param queryMap
	 * @return
	 */
	int getRelationScheduleInfoCount(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 获取最新的排程
	 * @param queryMap
	 */
	void getCurrHeaderId(Map queryMap);
	
	/**
	 * 获取状态
	 * @param queryMap
	 * @return
	 */
	List<Map> getStatus(Map<String, Object> queryMap);
	
	/**
	 * 刷新数据
	 * @param queryMap
	 */
	void freshMpsData(Map<String, String> queryMap);
	
	/**
	 * 获取 复制配置选项
	 * @param queryMap
	 * @return
	 */
	List<Map> getCopySetup(@Param("map")Map<String, Object> queryMap);
	
	/**
	 * 复制
	 * @param queryMap
	 */
	void copy(Map<String, String> queryMap);
	
	/**
	 * 下载时->生成数据
	 * @param queryMap
	 */
	void genMps(Map<String, String> queryMap);

	/**
	 * 获取Template 
	 * @param queryMap
	 * @return
	 */
	List<Map> getTemplates(@Param("map")Map<String, Object> queryMap);
	
	/**
	 * 获取默认模板
	 * @param queryMap
	 * @return
	 */
	int getDefaultTemplateByRoleId(@Param("map")Map<String, Object> queryMap);
	
	/**
	 * 下载时获取Excel配置
	 * @param queryMap
	 * @return 
	 */
	List<ScheduleConfgExcelForMpsBean> getExcelConfig(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 下载时，获取数据
	 * @param queryMap
	 * @return
	 */
	List<Map<String, String>> getDownData(@Param("sql")String sql);
	
	/**
	 * 清空临时数据
	 * @param string
	 */
	void truncateTemp(@Param("str")String string);
	
	/**
	 * 根据版本号获取Header信息
	 */
	List<ScheduleForMpsBean> getScheduleInfoByMap(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 更新源信息
	 * @param queryMap
	 */
	void updateHeaderUrl(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 批量行新增
	 * @param sql
	 */
	void batchInsert(@Param("sql")String sql);
	
	/**
	 * 上传完毕之后，调用存储过程
	 * @param qMap
	 */
	void updateMps(Map<String, Object> qMap);

	/**
	 * 更新备注
	 * @param map
	 */
	void updateRemarkByHeaderId(Map<String, String> map);
	
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
	 * 获取设置模板
	 * @param queryMap
	 * @return
	 */
	List<Map> getTemplatesSetup(Map<String, Object> queryMap);
	
	/**
	 *  获取关联料号，和仅显示MPS
	 * @param queryMap
	 * @return
	 */
	HashMap<String, String> getRelationAndMpsFlag(Map<String, Object> queryMap);
	
	/**
	 * 设置信息
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	List<?> queryProfile(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 设置的数量
	 * @param map
	 * @return
	 */
	int getProfileCount(Map<String, String> map);
	
	/**
	 * 更新设置
	 * @param map
	 */
	void updateSetup(Map<String, String> map);
	
	/**
	 * 更新设置中的模板
	 * @param queryMap
	 */
	void updateTemplateSetup(Map<String, String> queryMap);
	
	/**
	 * 获取表名所有列的数据类型
	 * @param tableName
	 */
	List<HashMap<String, String>> getTableColType(String tableName);
	
	/**
	 * 验证
	 * @param queryMap
	 */
	void validate(Map<String, String> queryMap);
	
	/**
	 * 点击设置时，先出发该方法
	 * @param queryMap
	 */
	void loadSetUp(Map<String, Object> queryMap);
	
	/**
	 * 查询版本号
	 * @param queryMap
	 * @return
	 */
	List<Map> getVersionInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询版本号
	 * @param queryMap
	 * @return
	 */
	List<Map> getRoleNameInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询状态
	 * @param queryMap
	 * @return
	 */
	List<Map> getStatusInfo(Map<String, Object> queryMap);

	/**
	 * 查询下载数据（提交状态）
	 * @param jsonparams
	 * @return
	 */
	List<Map<String, String>> downSubMitStatusInfo(Map<String, String> jsonparams);
	
	/**
	 * 查询组织
	 * @param queryMap
	 * @return
	 */
	List<Map> getOrganizeInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询工厂
	 * @param queryMap
	 * @return
	 */
	List<Map> getFactoryInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询计划名称
	 * @param queryMap
	 * @return
	 */
	List<Map> getDemandNameInfo(@Param("map")Map<String, Object> queryMap);
	
	/**
	 * 查询计划类型
	 * @param queryMap
	 * @return
	 */
	List<Map> getDemandTypeInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询计划类别
	 * @param queryMap
	 * @return
	 */
	List<Map> getDemandCodeInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询用途
	 * @param queryMap
	 * @return
	 */
	List<Map> getUserTypeInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询数据类型
	 * @param queryMap
	 * @return
	 */
	List<Map> getDateTypeInfo(Map<String, Object> queryMap);
	
	/**
	 * 查询纵向数据
	 * @param jsonparams
	 * @return
	 */
	List<Map<String, String>> getDemandDataZ(Map<String, String> jsonparams);
	
	/**
	 * 横向数据
	 * @param jsonparams
	 * @return
	 */
	List<Map<String, String>> getDemandDataH(Map<String, String> jsonparams);
	
	/**
	 * 查询提交状态
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	List<Map<String, String>> querySubMitStatusInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 查询提交状态的数量
	 * @param queryMap
	 * @return
	 */
	int getSubMitStatusCount(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 查询需求
	 * @param queryMap
	 * @param pageInfo
	 * @return
	 */
	List<Map<String, String>> queryRequireDataInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 查询需求数量
	 * @param queryMap
	 * @return
	 */
	int getRequireDataCount(@Param("map")Map<String, String> queryMap);
	
	/**
	 * 查询活动历史
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	List<?> queryHistoryAction(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 查询活动历史数量
	 * @param map
	 * @return
	 */
	int getHistoryActionCount(@Param("map")Map<String, String> map);
	
	/**
	 * 查询排程消息
	 * @param map
	 * @param pageInfo
	 * @return
	 */
	List<?> queryScheduleMsgInfo(@Param("map")Map<String, String> queryMap, @Param("page")PageInfo pageInfo);
	
	/**
	 * 查询小时数量
	 * @param map
	 * @return
	 */
	int getScheduleMsgInfoCount(@Param("map")Map<String, String> map);
}
