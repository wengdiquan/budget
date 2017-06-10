package com.bjsj.budget.service.impl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.omg.CORBA.StringHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bjsj.budget.dao.ScheduleForMpsDao;
import com.bjsj.budget.model.ScheduleConfgExcelForMpsBean;
import com.bjsj.budget.model.ScheduleForMpsBean;
import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.ScheduleForMpsService;
import com.bjsj.budget.util.FileConfig;
import com.bjsj.budget.util.FileUtil;
import com.bjsj.budget.util.PoiExcel2k3Helper;
import com.bjsj.budget.util.PoiExcel2k7Helper;
import com.bjsj.budget.util.PoiExcelHelper;
import com.bjsj.budget.util.TimeUtil;

@Service("scheduleForMpsServiceImpl")
public class ScheduleForMpsServiceImpl implements ScheduleForMpsService {

	private static String COL_TYPE_DETAIL = "DETAIL";

	@Autowired
	private ScheduleForMpsDao scheduleForMapDao;

	@Override
	public void getCurrHeaderId(Map<String, Object> queryMap) {
		queryMap.put("newFlag", "N");
		scheduleForMapDao.getCurrHeaderId(queryMap);
	}

	@Override
	public PageObject queryScheduleInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<ScheduleForMpsBean> orderChangeHeaders = scheduleForMapDao.queryScheduleInfo(queryMap, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getScheduleInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public PageObject queryRelationScheduleInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<ScheduleForMpsBean> orderChangeHeaders = scheduleForMapDao.queryRelationScheduleInfo(queryMap, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getRelationScheduleInfoCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public List<Map> findLookupValues(Map<String, Object> queryMap) {
		String classType = queryMap.get("classType") + "";
		List<Map> resultMap = null;

		if ("status".equals(classType)) {
			resultMap = scheduleForMapDao.getStatus(queryMap);
		}

		if ("template".equals(classType)) {
			resultMap = scheduleForMapDao.getTemplates(queryMap);
		}

		if ("templateSetup".equals(classType)) {
			resultMap = scheduleForMapDao.getTemplatesSetup(queryMap);
			if (resultMap == null || resultMap.size() == 0) {
				resultMap = scheduleForMapDao.getTemplates(queryMap);
			}
		}

		if ("versionNumberD".equals(classType)) {
			resultMap = scheduleForMapDao.getVersionInfo(queryMap);
		}

		if ("roleNameD".equals(classType)) {
			resultMap = scheduleForMapDao.getRoleNameInfo(queryMap);
		}

		if ("submitStatusD".equals(classType)) {
			resultMap = scheduleForMapDao.getStatusInfo(queryMap);
		}

		if ("organizeQ".equals(classType)) {
			resultMap = scheduleForMapDao.getOrganizeInfo(queryMap);
		}

		if ("factoryQ".equals(classType)) {
			resultMap = scheduleForMapDao.getFactoryInfo(queryMap);
		}

		if ("demandNameQ".equals(classType)) {
			resultMap = scheduleForMapDao.getDemandNameInfo(queryMap);
		}

		if ("demandTypeQ".equals(classType)) {
			resultMap = scheduleForMapDao.getDemandTypeInfo(queryMap);
		}

		if ("demandCodeQ".equals(classType)) {
			resultMap = scheduleForMapDao.getDemandCodeInfo(queryMap);
		}

		if ("useTypeQ".equals(classType)) {
			resultMap = scheduleForMapDao.getUserTypeInfo(queryMap);
		}

		if ("dataTypeQ".equals(classType)) {
			resultMap = scheduleForMapDao.getDateTypeInfo(queryMap);
		}

		return resultMap;
	}

	@Override
	public void freshMpsData(Map<String, String> queryMap) {
		scheduleForMapDao.freshMpsData(queryMap);
	}

	@Override
	public List<Map> getCopySetup(Map<String, Object> queryMap) {
		return scheduleForMapDao.getCopySetup(queryMap);
	}

	@Override
	public void copy(Map<String, String> queryMap) {
		scheduleForMapDao.copy(queryMap);
	}

	@Override
	public String getDefaultTemplateByRoleId(Map<String, Object> queryMap) {
		return scheduleForMapDao.getDefaultTemplateByRoleId(queryMap) + "";
	}

	@Override
	public void down(Map<String, String> queryMap) throws Exception {
		// 1. 生成数据
		scheduleForMapDao.genMps(queryMap);
		if ("s".equals(queryMap.get("returnStatus")) || "S".equals(queryMap.get("returnStatus"))) {
		} else {
			throw new Exception("调用存储过程：mps_schedule_pkg.Gen_Mps时失败：" + queryMap.get("returnMessage"));
		}

		// 2. 读取Excel配置信息
		List<ScheduleConfgExcelForMpsBean> excelConfigList = scheduleForMapDao.getExcelConfig(queryMap);
		if (excelConfigList.isEmpty()) {
			throw new Exception("根据HeaderId[" + queryMap.get("headerId") + "]查询mps_header_templet时数据为空，请先配置下载模板");
		}

		String baseTable = excelConfigList.get(0).getBaseTable();
		if ("".equals(baseTable) || baseTable == null) {
			throw new Exception("根据HeaderId[" + queryMap.get("headerId") + "]查询mps_header_templet时基础表为空");
		}

		// 获取数据类型
		List<HashMap<String, String>> dataTypeMaplist = scheduleForMapDao
				.getTableColType(excelConfigList.get(0).getBaseTable());

		// key->字段， value->类型
		HashMap<String, String> dataTypeMap = new HashMap<String, String>();
		for (HashMap<String, String> dataMap : dataTypeMaplist) {
			dataTypeMap.put(dataMap.get("NAME"), dataMap.get("TYPE"));
		}

		// 基础表头
		List<String> baseHeader = new ArrayList<String>();

		// 年份合并Map
		Map<String, List<Integer>> yearCalendarMap = new LinkedHashMap<String, List<Integer>>();
		List<String> colCodeList = new ArrayList<String>();
		// 日期值
		List<String> dataValueList = new ArrayList<String>();
		// showValue
		List<String> showValueList = new ArrayList<String>();
		// 是否可以编辑
		List<String> updateFlagList = new ArrayList<String>();
		// 基础数据Col
		// List<String> baseShowValueList = new ArrayList<String>();
		// 基础LiNE数据
		List<String> baseLineList = new ArrayList<String>();
		// 基础TABLE信息
		List<String> baseTableList = new ArrayList<String>();

		int firstDetailColTypeIndex = -1;
		boolean markFirstDetailColType = true;
		for (int i = 0; i < excelConfigList.size(); i++) {
			ScheduleConfgExcelForMpsBean excelBean = excelConfigList.get(i);
			baseHeader.add(excelBean.getColTitle2());
			colCodeList.add(excelBean.getColCode());
			dataValueList.add(excelBean.getColTitle3());
			showValueList.add(excelBean.getShowValue());

			if ("LINE".equalsIgnoreCase(excelBean.getColType())) {
				baseLineList.add(excelBean.getShowValue());
			}

			if ("TABLE".equalsIgnoreCase(excelBean.getShowType())) {
				baseTableList.add(excelBean.getShowValue());
			}

			updateFlagList.add(excelBean.getUpdateFlag());

			if (markFirstDetailColType && COL_TYPE_DETAIL.equalsIgnoreCase(excelBean.getColType())) {
				firstDetailColTypeIndex = i;
				markFirstDetailColType = false;
			}

			if (yearCalendarMap.get(excelBean.getColTitle1()) == null) {
				List<Integer> tempList = new ArrayList<Integer>();
				tempList.add(excelBean.getColSeq().intValue());
				yearCalendarMap.put(excelBean.getColTitle1(), tempList);
			} else {
				yearCalendarMap.get(excelBean.getColTitle1()).add(excelBean.getColSeq().intValue());
			}
		}

		// 查询明显行数据
		String selectLineSql = this.selectLineSql(baseTableList, baseTable, queryMap, dataTypeMap);

		List<Map<String, String>> lineDbMapList = null;
		try {
			lineDbMapList = scheduleForMapDao.getDownData(selectLineSql);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("在查询数据时失败，失败行SQL:" + selectLineSql);
		}

		/*
		 * if (lineDbMapList == null || lineDbMapList.isEmpty()) { throw new
		 * Exception("根据HeaderId[" + queryMap.get("headerId") + "]查询"+ baseTable
		 * +"数据为空"); }
		 */

		// 查询详细数据
		String selectDetailSql = this.selectDetailSql(baseTableList, baseTable, queryMap, dataTypeMap);
		List<Map<String, String>> detailDbMapList = null;
		try {
			detailDbMapList = scheduleForMapDao.getDownData(selectDetailSql);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("在查询数据时失败，失败行SQL:" + selectDetailSql);
		}

		List<Map<String, String>> dbMapList = new ArrayList<Map<String, String>>();
		for (Map<String, String> lineMap : lineDbMapList) {

			String line = lineMap.get("LINE_ID");

			boolean isExistInDeatil = false;
			for (Map<String, String> detailMap : detailDbMapList) {
				// 同一行

				if (line.equals(detailMap.get("LINE_ID").toString())) {
					isExistInDeatil = true;
					Map<String, String> tempMap = new HashMap<String, String>();
					tempMap.putAll(lineMap);
					tempMap.put("CAL", detailMap.get("CAL"));
					tempMap.put("SHOW_TYPE", detailMap.get("SHOW_TYPE"));
					tempMap.put("COL_TITLE2", detailMap.get("COL_TITLE2"));
					tempMap.put("QTY", detailMap.get("QTY"));
					tempMap.put("DETAIL_COLOR", detailMap.get("DETAIL_COLOR"));
					tempMap.put("DETAIL_TAG", detailMap.get("DETAIL_TAG"));
					dbMapList.add(tempMap);
				}
			}

			// detail中不存在数据
			if (!isExistInDeatil) {
				dbMapList.add(lineMap);
			}
		}

		// 查询数据类型
		List<HashMap<String, String>> dbMapColTypeList = scheduleForMapDao.getTableColType(baseTable);

		// 处理数据
		List<List<String>> resultDateList = new ArrayList<List<String>>();
		List<List<String>> colorResultDataList = new ArrayList<List<String>>();
		HashMap<String, String> colorLineAndDetailMap = new HashMap<String, String>();
		// TODO 处理SHOWTYPE Y_CAL， N_CAL
		List<List<String>> showTypeList = new ArrayList<List<String>>();
		this.genderDownData(resultDateList, dbMapList, baseLineList, baseHeader, colorResultDataList,
				colorLineAndDetailMap, firstDetailColTypeIndex, showTypeList);

		// 生成文件
		String pathName = FileConfig.PATH + "mps/" + TimeUtil.getCurTimeToFormat("yyyyMMdd") + "/";
		String fileName = "MPS_" + queryMap.get("roleName") + "_" + TimeUtil.getCurTimeToFormat("yyyyMMddHHmmss")
				+ ".xls";
		try {
			this.genderDownFile(queryMap, pathName, fileName, baseHeader, firstDetailColTypeIndex, yearCalendarMap,
					dataValueList, colCodeList, showValueList, updateFlagList, resultDateList, baseLineList,
					colorResultDataList, colorLineAndDetailMap, showTypeList, dbMapColTypeList);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}

		/**
		 * 是否更新MPS
		 */
		if ("Y".equals(queryMap.get("isUpdate"))) {
			scheduleForMapDao.freshMpsData(queryMap);
		}

		queryMap.put("url", pathName + fileName);
	}

	/**
	 * 拼接详细SQL
	 * 
	 * @param baseShowValueList
	 * @param dataTypeMap
	 *            数据类型
	 * @param string
	 */
	private String selectDetailSql(List<String> baseShowValueList, String tableName, Map<String, String> queryMap,
			HashMap<String, String> dataTypeMap) {
		StringBuffer sb = new StringBuffer();

		sb.append(" select ");
		sb.append(" to_char(md.line_id) line_id").append(", ");
		sb.append(" md.cal ").append(", ");
		sb.append(" decode(md.SHOW_TYPE, 'CAL', 'Y', 'N') SHOW_TYPE").append(", ");
		sb.append(" md.COL_TITLE2 ").append(", ");
		sb.append(" to_char(md.qty) qty").append(", ");
		sb.append(" clv2.meaning  detail_color, ");
		sb.append(" clv2.tag detail_tag ");
		sb.append(" from ").append("Mps_Details_v").append(" md ");
		sb.append(" left join Com_Lookup_Values clv2 on md.color = clv2.lookup_code and clv2.type_id = 8 ");
		sb.append(" where md.header_id = ").append(queryMap.get("headerId"));

		return sb.toString();
	}

	/**
	 * 拼接查询行SQL
	 * 
	 * @param baseShowValueList
	 * @param dataTypeMap
	 *            数据类型
	 * @param string
	 */
	private String selectLineSql(List<String> baseShowValueList, String tableName, Map<String, String> queryMap,
			HashMap<String, String> dataTypeMap) {

		StringBuffer sb = new StringBuffer();

		sb.append(" select ");
		sb.append(" to_char(ml.line_id) line_id, ");
		for (String col : baseShowValueList) {
			if ("DATE".equals(dataTypeMap.get(col))) {
				sb.append(" to_char(ml." + col + ", 'yyyy/mm/dd hh24:mi:ss')").append(col).append(" , ");
			} else {
				sb.append(" to_char(ml." + col + ") ").append(col).append(" ,");
			}
		}
		sb.append(" clv1.meaning  line_color, ");
		sb.append(" clv1.tag line_tag ");
		sb.append(" from ").append(tableName).append(" ml ");
		sb.append(" left join Com_Lookup_Values clv1 on clv1.lookup_code = ml.line_color and clv1.type_id = 8");
		sb.append(" where ml.header_id = ").append(queryMap.get("headerId"));
		sb.append(" and ml.Enable_Flag = 'Y' ");
		sb.append(" order by ml.line_no ");

		return sb.toString();
	}

	/**
	 * 处理db中的数据
	 * 
	 * @param resultDateList
	 *            处理后的数据
	 * @param dbMapList
	 *            原始数据
	 * @param baseShowValueList
	 *            showValue 为 TABLE的值
	 * @param baseHeader
	 *            表头
	 * @param colorResultDataList
	 *            颜色列
	 * @param showTypeList
	 */
	private void genderDownData(List<List<String>> resultDateList, List<Map<String, String>> dbMapList,
			List<String> baseShowValueList, List<String> baseHeader, List<List<String>> colorResultDataList,
			HashMap<String, String> colorLineAndDetailMap, int firstFunctionCol, List<List<String>> showTypeList) {
		// 键位line_id
		Map<String, List<String>> tempMapForDB = new HashMap<String, List<String>>();
		Map<String, List<String>> showTypeMapForDB = new HashMap<String, List<String>>();
		Map<String, List<String>> colorMapForDB = new HashMap<String, List<String>>();

		for (Map<String, String> dbMap : dbMapList) {
			String lineId = dbMap.get("LINE_ID");
			if (tempMapForDB.containsKey(lineId)) {
				List<String> tempList = tempMapForDB.get(lineId);
				List<String> colorList = colorMapForDB.get(lineId);

				List<String> showTypeOldList = showTypeMapForDB.get(lineId);

				// 日期数据
				for (int j = baseShowValueList.size(); j < baseHeader.size(); j++) {
					if (baseHeader.get(j).equals(dbMap.get("COL_TITLE2"))) {
						// show_type 数据
						showTypeOldList.set(j, dbMap.get("SHOW_TYPE") + "_" + dbMap.get("CAL"));

						if ("AAA".equals(tempList.get(j))) {
							tempList.set(j, dbMap.get("QTY"));
						} else {
							tempList.set(j,
									(Integer.valueOf(dbMap.get("QTY")) + Integer.valueOf(tempList.get(j))) + "");
						}
					}
				}

				// 颜色字段
				for (int j = firstFunctionCol; j < baseHeader.size(); j++) {
					if (baseHeader.get(j).equals(dbMap.get("COL_TITLE2"))) {
						if (colorList.get((j - firstFunctionCol + 1) * 2) == null) {
							colorList.set((j - firstFunctionCol + 1) * 2, dbMap.get("DETAIL_COLOR"));
							colorList.set((j - firstFunctionCol + 1) * 2 + 1, dbMap.get("DETAIL_TAG"));
						}
					}
				}
				if (dbMap.get("LINE_COLOR") != null) {
					colorLineAndDetailMap.put(dbMap.get("LINE_COLOR"), dbMap.get("LINE_TAG"));
				}

				if (dbMap.get("DETAIL_COLOR") != null) {
					colorLineAndDetailMap.put(dbMap.get("DETAIL_COLOR"), dbMap.get("DETAIL_TAG"));
				}

			} else {
				List<String> newList = new ArrayList<String>();
				List<String> colorNewList = new ArrayList<String>();

				// ShowType行值
				List<String> showTypeNewList = new ArrayList<String>();

				for (int i = 0; i < baseShowValueList.size(); i++) {
					// 基础字段
					newList.add(dbMap.get(baseShowValueList.get(i)));

					// show_type 字段
					showTypeNewList.add("");
				}

				// 日期号段(和公式)
				for (int j = baseShowValueList.size(); j < baseHeader.size(); j++) {
					if (baseHeader.get(j).equals(dbMap.get("COL_TITLE2"))) {
						showTypeNewList.add(dbMap.get("SHOW_TYPE") + "_" + dbMap.get("CAL"));
						newList.add(dbMap.get("QTY"));
					} else {
						newList.add("AAA");
						showTypeNewList.add("");
					}
				}
				resultDateList.add(newList);
				tempMapForDB.put(lineId, newList);

				// show_type 值
				showTypeList.add(showTypeNewList);
				showTypeMapForDB.put(lineId, showTypeNewList);

				// 颜色字段
				colorNewList.add(dbMap.get("LINE_COLOR"));
				colorNewList.add(dbMap.get("LINE_TAG"));
				for (int j = firstFunctionCol; j < baseHeader.size(); j++) {
					if (baseHeader.get(j).equals(dbMap.get("COL_TITLE2"))) {
						colorNewList.add(dbMap.get("DETAIL_COLOR"));
						colorNewList.add(dbMap.get("DETAIL_TAG"));
					} else {
						colorNewList.add(null);
						colorNewList.add(null);
					}
				}

				if (dbMap.get("LINE_COLOR") != null) {
					colorLineAndDetailMap.put(dbMap.get("LINE_COLOR"), dbMap.get("LINE_TAG"));
				}

				if (dbMap.get("DETAIL_COLOR") != null) {
					colorLineAndDetailMap.put(dbMap.get("DETAIL_COLOR"), dbMap.get("DETAIL_TAG"));
				}

				colorResultDataList.add(colorNewList);
				colorMapForDB.put(lineId, colorNewList);
			}
		}
	}

	/**
	 * 生成文件
	 * 
	 * @param queryMap
	 * @param pathAndFileName
	 * @param fileName
	 * @param baseHeader
	 * @param firstDetailColType
	 * @param yearCalendarMap
	 * @param dataValueList
	 * @param colCodeList
	 * @param showValueList
	 * @param updateFlagList
	 * @param resultDateList
	 * @param colorResultDataList
	 *            颜色列
	 * @param colorLineAndDetailMap
	 * @param showTypeList
	 * @param dbMapColTypeList
	 *            基础数据的数量类型
	 * @throws Exception
	 */
	private void genderDownFile(Map<String, String> queryMap, String pathName, String fileName, List<String> baseHeader,
			int firstDetailCol, Map<String, List<Integer>> yearCalendarMap, List<String> dataValueList,
			List<String> colCodeList, List<String> showValueList, List<String> updateFlagList,
			List<List<String>> resultDateList, List<String> baseShowValueList, List<List<String>> colorResultDataList,
			HashMap<String, String> colorLineAndDetailMap, List<List<String>> showTypeList,
			List<HashMap<String, String>> dbMapColTypeList) throws Exception {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFDataFormat format = wb.createDataFormat();
		// 特殊字段
		HSSFCellStyle headerStyle = wb.createCellStyle();
		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
		headerFont.setFontName("仿宋");
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
		this.setColor(wb, headerStyle, "ADD8E6", (short) 17);

		HSSFCellStyle headerLineStyle = wb.createCellStyle();
		headerLineStyle.setFont(headerFont);
		headerLineStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);// 居左
		this.setColor(wb, headerLineStyle, "ADD8E6", (short) 17);

		HSSFFont textFont = wb.createFont();
		textFont.setFontName("Arial Unicode MS");

		// 处理行和明细的颜色
		HashMap<String, CellStyle> lineAndDetailColorMap = this.processLineAndDetailColorMap(wb, colorLineAndDetailMap);

		// 数据（数字，不变色）
		HSSFCellStyle dataMarkerNumStyle = wb.createCellStyle();
		dataMarkerNumStyle.setFont(textFont);
		dataMarkerNumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		this.setColor(wb, dataMarkerNumStyle, "FFFFFF", (short) 13);

		// 数据（数字，变色）
		HSSFCellStyle dataMarkerNumColorStyle = wb.createCellStyle();
		dataMarkerNumColorStyle.setFont(textFont);
		dataMarkerNumColorStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		this.setColor(wb, dataMarkerNumColorStyle, "D1D1D1", (short) 14);

		// 制灰公式
		HSSFCellStyle dataColorStyle = wb.createCellStyle();
		dataColorStyle.setFont(textFont);
		this.setColor(wb, dataColorStyle, "D1D1D1", (short) 11);

		// 数据（字符串，不变色）
		HSSFCellStyle dataMarkerStrStyle = wb.createCellStyle();
		dataMarkerStrStyle.setFont(textFont);
		dataMarkerStrStyle.setDataFormat(format.getFormat("@"));
		this.setColor(wb, dataMarkerStrStyle, "FFFFFF", (short) 15);

		// 数据（字符串，变色）
		HSSFCellStyle dataMarkerStrColorStyle = wb.createCellStyle();
		dataMarkerStrColorStyle.setFont(textFont);
		dataMarkerStrColorStyle.setDataFormat(format.getFormat("@"));
		this.setColor(wb, dataMarkerStrColorStyle, "D1D1D1", (short) 16);

		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setFontName("Arial Unicode MS");
		style.setFont(font);
		style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
		style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
		style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
		style.setBorderRight(CellStyle.BORDER_THIN);// 右边框

		Sheet sheet = wb.createSheet("mps");

		// 头信息
		Row firstRow = sheet.createRow(0);
		Cell firstCell1 = firstRow.createCell(0);
		firstCell1.setCellValue("排程版本:");
		firstCell1.setCellStyle(style);
		Cell firstCell2 = firstRow.createCell(1);
		firstCell2.setCellValue(queryMap.get("versionNo"));
		firstCell2.setCellStyle(style);
		// 年月
		Cell yearCell = firstRow.createCell(firstDetailCol - 1);
		yearCell.setCellValue("年月");
		yearCell.setCellStyle(headerStyle);

		// 年月数据
		for (Entry<String, List<Integer>> entry : yearCalendarMap.entrySet()) {

			if (entry.getKey() == null) {
				continue;
			}

			List<Integer> tempList = entry.getValue();
			Integer first = tempList.get(0) - 1;
			Cell tempCell = firstRow.createCell(first);
			tempCell.setCellValue(entry.getKey());
			tempCell.setCellStyle(headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, first, tempList.get(tempList.size() - 1) - 1));
		}

		Row secondRow = sheet.createRow(1);
		Cell secondCell1 = secondRow.createCell(0);
		secondCell1.setCellValue("模板名称:");
		secondCell1.setCellStyle(style);
		Cell secondCell2 = secondRow.createCell(1);
		secondCell2.setCellValue(queryMap.get("templateName"));
		secondCell2.setCellStyle(style);

		// 日期
		Cell dateCell = secondRow.createCell(firstDetailCol - 1);
		dateCell.setCellValue("日期");
		dateCell.setCellStyle(headerStyle);

		for (int i = firstDetailCol; i < dataValueList.size(); i++) {
			Cell tempCell = secondRow.createCell(i);
			tempCell.setCellValue(dataValueList.get(i));
			tempCell.setCellStyle(headerStyle);
		}

		Row thirdRow = sheet.createRow(2);
		Cell thirdCell1 = thirdRow.createCell(0);
		thirdCell1.setCellValue("版本时间:");
		thirdCell1.setCellStyle(style);
		Cell thirdCell2 = thirdRow.createCell(1);
		thirdCell2.setCellValue(queryMap.get("versionDate"));
		thirdCell2.setCellStyle(style);

		// 数据汇总
		for (int i = firstDetailCol; i < colCodeList.size(); i++) {
			Cell tempCell = thirdRow.createCell(i);
			tempCell.setCellFormula("SUBTOTAL(9, " + colCodeList.get(i) + "5" + ":" + colCodeList.get(i)
					+ (resultDateList.size() + 4) + ")");
		}

		// 表头
		Row fourRow = sheet.createRow(3);
		for (int c = 0; c < baseHeader.size(); c++) {
			Cell cell = fourRow.createCell(c);
			cell.setCellValue(baseHeader.get(c));
			cell.setCellStyle(headerLineStyle);
		}

		// 修改为可筛选
		sheet.setAutoFilter(CellRangeAddress.valueOf("A4:" + this.excelColIndexToStr(baseHeader.size()) + "4"));

		// 填充数据
		CellStyle lineCs = null;
		CellStyle detailCs = null;
		for (int r = 0; r < resultDateList.size(); r++) {
			Row row = sheet.createRow(r + 4);
			List<String> lineList = resultDateList.get(r);
			List<String> showTypeLineList = showTypeList.get(r);
			for (int c = 0; c < lineList.size(); c++) {
				Cell cell = row.createCell(c);
				String showType = showTypeLineList.get(c);
				if (!"".equals(showTypeLineList.get(c)) && showType.startsWith("Y")) {
					String cal = showType.substring(showType.indexOf("_") + 1);
					if (!"null".equals(cal)) {
						cell.setCellStyle(dataColorStyle);
						cell.setCellFormula(cal);
					} else {
						cell.setCellStyle(dataMarkerNumStyle);
					}
				} else if (showValueList.get(c).contains("@")) {
					cell.setCellStyle(dataColorStyle);
					cell.setCellFormula(showValueList.get(c).replace("@", (r + 5) + ""));
				} else {
					if ("AAA".equals(lineList.get(c))) {
						cell.setCellStyle(dataMarkerNumStyle);
						// cell.setCellValue("");
					} else {
						String s = lineList.get(c);
						cell.setCellValue(s);
						if (c < baseShowValueList.size() && !this.isNumberCol(c, baseShowValueList, dbMapColTypeList)) { // -8的目的是，可以是数字在Excel居右显示
							if ("N".equals(updateFlagList.get(c))) {
								cell.setCellStyle(dataMarkerStrColorStyle);
							} else {
								cell.setCellStyle(dataMarkerStrStyle);
							}
						} else {
							try {
								if ("N".equals(updateFlagList.get(c))) {
									cell.setCellStyle(dataMarkerNumColorStyle);
								} else {
									cell.setCellStyle(dataMarkerNumStyle);
								}
								cell.setCellValue(Integer.parseInt(s));
							} catch (Exception ex) {
								if ("N".equals(updateFlagList.get(c))) {
									cell.setCellStyle(dataMarkerStrColorStyle);
								} else {
									cell.setCellStyle(dataMarkerStrStyle);
								}
							}
						}
					}
				}

				lineCs = this.getLineStyle(wb, colorResultDataList.get(r), lineAndDetailColorMap);
				if (lineCs != null) {
					cell.setCellStyle(lineCs);
				}

				// 明细数据才需要处理
				if (c >= firstDetailCol) {
					detailCs = this.getDetailStyle(wb, c, colorResultDataList.get(r), firstDetailCol,
							lineAndDetailColorMap);
					if (detailCs != null) {
						cell.setCellStyle(detailCs);
					}
				}
			}
		}

		// 自动数据宽度
		for (int c = 0; c < baseHeader.size(); c++) {
			sheet.autoSizeColumn(c, true);
			/*
			 * if(c < firstDetailColType - 1){ sheet.setColumnWidth(c, 4200);
			 * }else{ sheet.setColumnWidth(c, 2800); }
			 */
		}

		FileUtil.createDir(pathName);
		FileUtil.createFile(pathName + fileName);
		FileOutputStream outStream = new FileOutputStream(pathName + fileName);
		wb.write(outStream);
		wb.close();
	}

	/**
	 * 判断改列是否为数字类型(用于Excel转换
	 * 
	 * @param c
	 * @param baseShowValueList
	 * @param dbMapColTypeList
	 * @return
	 */
	private boolean isNumberCol(int c, List<String> baseShowValueList, List<HashMap<String, String>> dbMapColTypeList) {

		String baseName = baseShowValueList.get(c);

		for (HashMap<String, String> map : dbMapColTypeList) {
			if (map.get("NAME").equalsIgnoreCase(baseName)) {
				return map.get("TYPE").equals("NUMBER");
			}
		}
		return false;
	}

	/**
	 * 获取列的明细的样式
	 * 
	 * @param wb
	 * @param c
	 * @param list
	 * @param size
	 * @param lineAndDetailColorMap
	 * @return
	 */
	private CellStyle getDetailStyle(HSSFWorkbook wb, int c, List<String> list, int firstDetailCol,
			HashMap<String, CellStyle> lineAndDetailColorMap) {

		List<String> colorList = new ArrayList<String>();
		List<String> tagList = new ArrayList<String>();

		for (int i = 2; i < list.size(); i++) {
			if (i % 2 == 0) {
				colorList.add(list.get(i));
			} else {
				tagList.add(list.get(i));
			}
		}
		int intex = c - firstDetailCol;
		String s0 = colorList.get(intex);
		String s1 = tagList.get(intex);
		CellStyle style = null;
		if (s0 != null) {
			style = lineAndDetailColorMap.get(s0);
			this.setColor(wb, style, s0, (short) Short.valueOf(s1));
		}

		return style;
	}

	/**
	 * 处理行和明显的颜色集合
	 * 
	 * @param wb
	 * @param textFont
	 * @param i
	 * @param colorLineAndDetailMap
	 * @return
	 */
	private HashMap<String, CellStyle> processLineAndDetailColorMap(HSSFWorkbook wb,
			HashMap<String, String> colorLineAndDetailMap) {
		HashMap<String, CellStyle> lineAndDetailColorMap = new HashMap<String, CellStyle>();
		for (String key : colorLineAndDetailMap.keySet()) {
			HSSFCellStyle style = wb.createCellStyle();
			HSSFFont textFont = wb.createFont();
			textFont.setFontName("Arial Unicode MS");
			style.setFont(textFont);
			this.setColor(wb, style, key, Short.valueOf(colorLineAndDetailMap.get(key)));
			lineAndDetailColorMap.put(key, style);
		}
		return lineAndDetailColorMap;
	}

	/**
	 * 获取行的样式
	 * 
	 * @param wb
	 * @param col
	 * @param row
	 * @param list
	 * @param size
	 * @return
	 */
	private CellStyle getLineStyle(HSSFWorkbook wb, List<String> colorList,
			HashMap<String, CellStyle> lineAndDetailColorMap) {
		String s0 = colorList.get(0);
		CellStyle style = null;
		if (s0 != null) {
			style = lineAndDetailColorMap.get(s0);
			this.setColor(wb, style, s0, (short) Short.valueOf(colorList.get(1)));
		}

		return style;
	}

	private CellStyle setColor(HSSFWorkbook wb, CellStyle style, String color, short index) {
		// 设置边框
		style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
		style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
		style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
		style.setBorderRight(CellStyle.BORDER_THIN);// 右边框
		if (color != "" && color != null) {
			style.setFillForegroundColor(index);
			// 转为RGB码
			int r = Integer.parseInt((color.substring(0, 2)), 16); // 转为16进制
			int g = Integer.parseInt((color.substring(2, 4)), 16);
			int b = Integer.parseInt((color.substring(4, 6)), 16);
			// 自定义cell颜色
			HSSFPalette palette = wb.getCustomPalette();
			palette.setColorAtIndex(index, (byte) r, (byte) g, (byte) b);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}

		return style;
	}

	/**
	 * 上传
	 * 
	 * @throws Exception
	 */
	@Override
	public void importSchedule(MultipartFile file, Map<String, String> queryMap, StringHolder sh) throws Exception {

		// 判断是否是Excel文件
		String fileName = file.getOriginalFilename();
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			throw new Exception("您要上传的文件类型不符合要求");
		}

		// 选择Excel读取器
		PoiExcelHelper helper;
		if (fileName.indexOf(".xlsx") != -1) {
			helper = new PoiExcel2k7Helper();
		} else {
			helper = new PoiExcel2k3Helper();
		}

		// 备份文件
		String dirfileName = FileUtil.backImportFile(file, fileName, "mpsback");

		if (dirfileName != null) {
			try {

				List<ArrayList<String>> baseHeaderAndData = helper.readExcel(dirfileName, 0, "1-", "1-");

				if (baseHeaderAndData == null || baseHeaderAndData.isEmpty() || baseHeaderAndData.size() < 4) {
					throw new Exception("读取Excel时，数据为空或者不满足5行信息");
				}

				// 排除空格
				this.deleteBlankLine(baseHeaderAndData);

				// 模板名称：
				String templetName = baseHeaderAndData.get(1).get(1);
				if (templetName == null || "".equals(templetName)) {
					throw new Exception("读取Excel时，模板名称未填，请填写后上传");
				}

				// 清空line_temp, 和 detail_temp
				scheduleForMapDao.truncateTemp("mps_lines_temp");
				scheduleForMapDao.truncateTemp("mps_details_temp");

				// 调用存储过程获取Header_id
				Map<String, Object> qMap = new HashMap<String, Object>();
				qMap.put("newFlag", "Y");
				qMap.put("roleId", queryMap.get("roleId"));
				qMap.put("userId", queryMap.get("userId"));
				scheduleForMapDao.getCurrHeaderId(qMap);
				if ("s".equals(qMap.get("returnStatus")) || "S".equals(qMap.get("returnStatus"))) {
					queryMap.put("headerId", qMap.get("currHeaderId").toString());
					qMap.put("headerId", qMap.get("currHeaderId"));
				} else {
					throw new Exception("调用存储过程：mps_schedule_pkg.Gen_Mps_Headers时失败：" + qMap.get("returnMessage"));
				}

				// 根据模板名称查询这个Excel的表头信息(根据HeaderId,获取模板配置信息)
				List<ScheduleConfgExcelForMpsBean> templetExcelMpsBeanList = scheduleForMapDao.getExcelConfig(queryMap);
				if (templetExcelMpsBeanList == null || templetExcelMpsBeanList.isEmpty()) {
					throw new Exception(
							"根据HeaderId[" + queryMap.get("headerId") + "]查询mps_header_templet时数据为空，请先配置下载模板");
				}

				if (!templetName.trim().equals(templetExcelMpsBeanList.get(0).getTempletName())) {
					throw new Exception("上传的模板名称[" + templetName + "],和headerId[" + queryMap.get("headerId")
							+ "查询mps_header_templet时的模板名称不一致，请重新上传");
				}

				List<Integer> excelColList = new ArrayList<Integer>();
				List<String> excelNameList = new ArrayList<String>();
				List<String> colCodeListLine = new ArrayList<String>();
				List<String> showValueListLine = new ArrayList<String>();
				List<String> showValueForcheckDataTypeList = new ArrayList<String>();
				List<String> colTitle2ListLine = new ArrayList<String>();
				List<String> colCodeList = new ArrayList<String>();
				int firstFunctionCol = -1;
				boolean markFirstFunction = true;
				for (int i = 0; i < templetExcelMpsBeanList.size(); i++) {

					ScheduleConfgExcelForMpsBean bean = templetExcelMpsBeanList.get(i);
					// 取出update_flag 为Y的的col_seq.
					if ("Y".equalsIgnoreCase(bean.getUpdateFlag())) {
						// 行号Integer.valueOf(bean.getColSeq().toString()) - 1
						excelColList.add(i);

						// 基础数据
						if ("TABLE".equals(bean.getShowType())) {
							// 行编号
							colCodeListLine.add(bean.getColCode());
							// 数据库字段
							showValueListLine.add(bean.getShowValue());
							// ColTitl2
							colTitle2ListLine.add(bean.getColTitle2());
						}

						// 日期数据
						if ("FUNCTION".equals(bean.getShowType())) {
							if (markFirstFunction) {
								firstFunctionCol = i;
								markFirstFunction = false;
							}
						}
					}

					showValueForcheckDataTypeList.add(bean.getShowValue());

					// 行编号
					colCodeList.add(bean.getColCode());
					excelNameList.add(bean.getColTitle2());
				}

				// 比较模板表头和上传表头是否一致
				if (!this.compare(excelNameList, baseHeaderAndData.get(3))) {
					throw new Exception(
							"根据HeaderId[" + queryMap.get("headerId") + "]查询的表头信息，和上传的表头信息不匹配，无法上传，请检查是否模板一致");
				}

				// 处理数据(移除前4条)
				baseHeaderAndData.remove(0);
				baseHeaderAndData.remove(0);
				baseHeaderAndData.remove(0);
				// 获取头值
				ArrayList<String> headerList = baseHeaderAndData.remove(0);

				// 获取数据类型
				List<HashMap<String, String>> dataTypeMaplist = scheduleForMapDao
						.getTableColType(templetExcelMpsBeanList.get(0).getBaseTable());

				// key->字段， value->类型
				HashMap<String, String> dataTypeMap = new HashMap<String, String>();
				for (HashMap<String, String> dataMap : dataTypeMaplist) {
					dataTypeMap.put(dataMap.get("NAME"), dataMap.get("TYPE"));
				}

				// 校验数据是否是合法数据类型
				for (int j = 0; j < baseHeaderAndData.size(); j++) {
					// 数据行
					ArrayList<String> line = baseHeaderAndData.get(j);
					String v = null;
					String name = null;
					String checkStr = "";
					for (int i = 0; i < excelColList.size(); i++) {
						v = line.get(excelColList.get(i)); // 列值

						if (v != null && !"".equals(v)) {
							name = showValueForcheckDataTypeList.get(excelColList.get(i));
							checkStr = this.checkData(v, name, dataTypeMap, j + 5, excelColList.get(i) + 1);
						}
						if (!"".equals(checkStr)) {
							throw new Exception(checkStr);
						}
					}
				}

				List<ArrayList<String>> lineValueList = new ArrayList<ArrayList<String>>();
				List<ArrayList<String>> detailValueList = new ArrayList<ArrayList<String>>();
				for (int j = 0; j < baseHeaderAndData.size(); j++) {
					// 读取update_flag 为 Y 的列的值
					ArrayList<String> line = baseHeaderAndData.get(j);
					ArrayList<String> lineValues = new ArrayList<String>();
					lineValues.add(queryMap.get("headerId"));
					lineValues.add((j + 5) + "");
					lineValueList.add(lineValues);
					for (int i = 0; i < excelColList.size(); i++) {
						String v = line.get(excelColList.get(i));
						if (excelColList.get(i) < firstFunctionCol) {
							// 基础数据
							lineValues.add(v);
						} else {
							// 日期值
							if (v != null && !"".equals(v)) {
								ArrayList<String> detailValues = new ArrayList<String>();
								detailValues.add(queryMap.get("headerId"));
								detailValues.add((j + 5) + "");
								detailValues.add(colCodeList.get(excelColList.get(i)));
								detailValues.add(headerList.get(excelColList.get(i)));
								detailValues.add(v);
								detailValueList.add(detailValues);
							}
						}
					}
				}

				// 基础数据SQL 集合
				List<String> lineSqlList = new ArrayList<String>();
				showValueListLine.add(0, "HEADER_ID");
				showValueListLine.add(1, "LINE_NO");
				for (int i = 0; i < lineValueList.size(); i++) {
					lineSqlList.add(this
							.joinShowValueAndValueInsert(showValueListLine, lineValueList.get(i), queryMap, dataTypeMap)
							.toString());
				}

				// 日期数据SQL 集合
				List<String> detailSqlList = new ArrayList<String>();
				List<String> showValueListDetail = new ArrayList<String>();
				showValueListDetail.add("HEADER_ID");
				showValueListDetail.add("LINE_NO");
				showValueListDetail.add("COL_CODE");
				showValueListDetail.add("COL_TITLE2");
				showValueListDetail.add("QTY");
				for (int i = 0; i < detailValueList.size(); i++) {
					detailSqlList.add(this.joinShowValueAndValueInsert(showValueListDetail, detailValueList.get(i),
							queryMap, dataTypeMap).toString());
				}

				// 存储基础SQL
				List<List<String>> resultLineSQLList = this.createList(lineSqlList, 100);
				showValueListLine.add("CREATION_DATE");
				showValueListLine.add("CREATED_BY");
				showValueListLine.add("LAST_UPDATE_DATE");
				showValueListLine.add("LAST_UPDATED_BY");
				for (List<String> lineSQL : resultLineSQLList) {
					String sql = "";
					try {
						sql = this.insertSQL(showValueListLine, lineSQL, "mps_lines_temp");
						scheduleForMapDao.batchInsert(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new Exception("新增数据失败，失败SQL:" + sql);
					}
				}

				// 存储日历数据
				List<List<String>> resultDetailSQLList = this.createList(detailSqlList, 100);
				showValueListDetail.add("CREATION_DATE");
				showValueListDetail.add("CREATED_BY");
				showValueListDetail.add("LAST_UPDATE_DATE");
				showValueListDetail.add("LAST_UPDATED_BY");
				for (List<String> detailSQL : resultDetailSQLList) {
					String sql = "";
					try {
						sql = this.insertSQL(showValueListDetail, detailSQL, "Mps_Details_Temp");
						scheduleForMapDao.batchInsert(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new Exception("新增数据失败，失败SQL:" + sql);
					}
				}

				// 调用存储过程，更新插入数据
				scheduleForMapDao.updateMps(qMap);
				if ("s".equals(qMap.get("returnStatus")) || "S".equals(qMap.get("returnStatus"))) {
				} else {
					throw new Exception("调用存储过程：mps_schedule_pkg.Upload_Mps时失败：" + qMap.get("returnMessage"));
				}

				// 更新header的URI
				queryMap.put("url", dirfileName);
				scheduleForMapDao.updateHeaderUrl(queryMap);

			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception(ex.getMessage());
			}

		} else {
			throw new Exception("备份文件时出错，请联系管理员");
		}
	}

	/**
	 * 校验数据是否合法
	 * 
	 * @param v
	 * @param type
	 * @param dataTypeMap
	 * @param j
	 * @param integer
	 * @return
	 */
	private String checkData(String v, String name, HashMap<String, String> dataTypeMap, int row, Integer col) {
		String result = "";

		// 数字
		if ("MPS".equals(name) || "NUMBER".equals(dataTypeMap.get(name))) {
			if (isNotNumber(v)) {
				result = "您上传的excel第" + row + "行，第" + col + "列要求为数字格式，而您填写了[" + v + "],请确定后在尝试上传。";
			}
		}
		return result;
	}

	/**
	 * 校验是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNotNumber(String str) {

		if (str != null) {
			str = str.trim();
			return !str.matches("[-]?[0-9]*[.]?[0]*"); // [.]?[0-9]*
		}
		return false;
	}

	/**
	 * 去除Excel中读取的空白行
	 * 
	 * @param excelContentList
	 */
	private void deleteBlankLine(List<ArrayList<String>> excelContentList) {
		if (excelContentList != null && !excelContentList.isEmpty()) {
			for (Iterator<ArrayList<String>> iterList = excelContentList.iterator(); iterList.hasNext();) {
				boolean isNeedDelete = true;
				ArrayList<String> valueList = iterList.next();
				for (String str : valueList) {
					if (str != null) {
						isNeedDelete = false;
						break;
					}
				}

				if (isNeedDelete) {
					iterList.remove();
				}
			}
		}
	}

	/**
	 * 比较两个列表是否相同
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
		if (a.size() != b.size()) {
			return false;
		}

		// Collections.sort(a);
		// Collections.sort(b);
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 拼接上传的SQL
	 * 
	 * @param showValueList
	 * @param valueList
	 * @param queryMap
	 * @param dataTypeMap
	 * @return
	 */
	private StringBuffer joinShowValueAndValueInsert(List<String> showValueList, List<String> valueList,
			Map<String, String> queryMap, HashMap<String, String> dataTypeMap) {

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select ");
		// sbSql.append( queryMap.get("headerId")).append(", ");
		// sbSql.append( queryMap.get("lineNo")).append(", ");
		for (int i = 0; i < showValueList.size(); i++) {
			String s = showValueList.get(i);
			String v = valueList.get(i);
			if (v == null || "".equals(v)) {
				sbSql.append("'', ");
			} else {
				if (s.indexOf("DATE") != -1) {
					sbSql.append("to_date('" + v + "', 'yyyy/mm/dd hh24:mi:ss'), ");
				} else {
					sbSql.append("'" + v + "', ");
				}
			}
		}

		sbSql.append(" sysdate").append(", ");
		sbSql.append(queryMap.get("userId")).append(", ");
		sbSql.append(" sysdate").append(", ");
		sbSql.append(queryMap.get("userId"));
		sbSql.append(" from dual");
		return sbSql;
	}

	/**
	 * 将ShowVlue List 转换为SQL语句
	 * 
	 * @param showValueList
	 * @return
	 */
	private String insertSQL(List<String> showValueList, List<String> sqlList, String tableName) {
		StringBuffer sb = new StringBuffer();

		sb.append(" insert into ").append(tableName).append(" (");
		for (String s : showValueList) {
			sb.append(s).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		for (String sql : sqlList) {
			sb.append(sql).append(" union all");
		}
		sb.delete(sb.lastIndexOf("union all"), sb.length() - 1);

		return sb.toString();
	}

	/**
	 * 将结果集转换100个等个List
	 * 
	 * @param targe
	 * @param size
	 * @return
	 */
	public List<List<String>> createList(List<String> targe, int size) {
		List<List<String>> listArr = new ArrayList<List<String>>();
		// 获取被拆分的数组个数
		int arrSize = targe.size() % size == 0 ? targe.size() / size : targe.size() / size + 1;
		for (int i = 0; i < arrSize; i++) {
			List<String> sub = new ArrayList<String>();
			// 把指定索引数据放入到list中
			for (int j = i * size; j <= size * (i + 1) - 1; j++) {
				if (j <= targe.size() - 1) {
					sub.add(targe.get(j));
				}
			}
			listArr.add(sub);
		}
		return listArr;
	}

	/**
	 * 更新备注信息
	 * 
	 * @param cell
	 * @param hssfWorkbook
	 * @return
	 * @throws Exception
	 */
	public void updateRemarkByHeaderId(Map<String, String> map) throws Exception {
		scheduleForMapDao.updateRemarkByHeaderId(map);
	}

	@Override
	public void submit(Map<String, String> queryMap) {
		scheduleForMapDao.submit(queryMap);
	}

	@Override
	public void urge(Map<String, String> queryMap) {
		scheduleForMapDao.urge(queryMap);
	}

	@Override
	public HashMap<String, String> getRelationAndMpsFlag(Map<String, Object> queryMap) {
		return scheduleForMapDao.getRelationAndMpsFlag(queryMap);
	}

	@Override
	public PageObject queryProfile(Map<String, String> map, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<?> list = scheduleForMapDao.queryProfile(map, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getProfileCount(map));
		pageObj.setRows(list);
		return pageObj;
	}

	@Override
	public void updateSetup(Map<String, String> queryMap, List<Map<String, String>> list) throws Exception {

		if (queryMap.get("templateId") != null && !"".equals(queryMap.get("templateId"))) {
			scheduleForMapDao.updateTemplateSetup(queryMap);
		}

		for (int i = 0; i < list.size(); i++) {
			scheduleForMapDao.updateSetup(list.get(i));
		}
	}

	/**
	 * 将 数字转换为Excel中的列名
	 * 
	 * @param columnIndex
	 * @return
	 */
	private String excelColIndexToStr(int columnIndex) {
		if (columnIndex <= 0) {
			return null;
		}
		String columnStr = "";
		columnIndex--;
		do {
			if (columnStr.length() > 0) {
				columnIndex--;
			}
			columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
			columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
		} while (columnIndex > 0);
		return columnStr;
	}

	@Override
	public void validate(Map<String, String> queryMap) throws Exception {
		scheduleForMapDao.validate(queryMap);
	}

	@Override
	public void loadSetUp(Map<String, Object> queryMap) {
		scheduleForMapDao.loadSetUp(queryMap);
	}

	@Override
	public void downSubMitStatusInfo(Map<String, String> jsonparams) throws Exception {

		// 1. 生成数据
		List<Map<String, String>> dataMapList = scheduleForMapDao.downSubMitStatusInfo(jsonparams);

		// 基础表头
		List<String> headList = Arrays.asList("版本号", "版本日期", "角色名", "角色描述", "提交状态", "上传状态", "排程笔数", "排程数量", "预排数量",
				"备注", "历史标识");

		// 基础表头英文名
		List<String> headEngList = Arrays.asList("VERSION_NO", "VERSION_DATE", "ROLE_NAME", "ROLE_NAME_DES", "STATUS",
				"UPLOAD_FLAG", "CNT", "MPS_QTY", "PRE_MPS_QTY", "REMARK", "LOG_FLAG");

		// 数据类型
		List<String> headerDataTypeList = Arrays.asList("str", "str", "str", "str", "str", "str", "num", "num", "num",
				"str", "str");

		// 生成文件
		String pathName = FileConfig.PATH + "template/" + TimeUtil.getCurTimeToFormat("yyyyMMdd") + "/";
		String fileName = "提交状态_" + TimeUtil.getCurTimeToFormat("yyyyMMddHHmmss") + ".xls";

		HSSFWorkbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("提交状态");

		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setFontName("Arial Unicode MS");
		style.setFont(font);
		style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
		style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
		style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
		style.setBorderRight(CellStyle.BORDER_THIN);// 右边框

		HSSFDataFormat format = wb.createDataFormat();
		HSSFCellStyle headerStyle = wb.createCellStyle();
		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
		headerFont.setFontName("仿宋");
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
		this.setColor(wb, headerStyle, "ADD8E6", (short) 17);

		HSSFFont textFont = wb.createFont();
		textFont.setFontName("Arial Unicode MS");

		// 数据（数字，不变色）
		HSSFCellStyle dataMarkerNumStyle = wb.createCellStyle();
		dataMarkerNumStyle.setFont(textFont);
		dataMarkerNumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		this.setColor(wb, dataMarkerNumStyle, "FFFFFF", (short) 13);

		// 数据（字符串，不变色）
		HSSFCellStyle dataMarkerStrStyle = wb.createCellStyle();
		dataMarkerStrStyle.setFont(textFont);
		dataMarkerStrStyle.setDataFormat(format.getFormat("@"));
		this.setColor(wb, dataMarkerStrStyle, "FFFFFF", (short) 15);

		// head
		Row firstRow = sheet.createRow(0);
		for (int c = 0; c < headList.size(); c++) {
			Cell cell = firstRow.createCell(c);
			cell.setCellValue(headList.get(c));
			cell.setCellStyle(style);
		}

		// data
		for (int r = 0; r < dataMapList.size(); r++) {
			Row row = sheet.createRow(r + 1);

			Map<String, String> dataMap = dataMapList.get(r);
			for (int c = 0; c < headEngList.size(); c++) {
				Cell cell = row.createCell(c);

				String value = dataMap.get(headEngList.get(c));

				if ("num".equals(headerDataTypeList.get(c))) {
					if ("".equals(value) || null == value) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(Double.parseDouble(value));
					}
					cell.setCellStyle(dataMarkerNumStyle);
				} else {
					cell.setCellValue(dataMap.get(headEngList.get(c)));
					cell.setCellStyle(dataMarkerStrStyle);
				}
			}
		}

		for (int c = 0; c < headList.size(); c++) {
			sheet.autoSizeColumn(c, true);
		}

		FileUtil.createDir(pathName);
		FileUtil.createFile(pathName + fileName);
		FileOutputStream outStream = new FileOutputStream(pathName + fileName);
		wb.write(outStream);
		wb.close();

		jsonparams.put("url", pathName + fileName);
	}

	@Override
	public void downDemandDataInfo(Map<String, String> jsonparams) throws Exception {

		HSSFWorkbook wb = new HSSFWorkbook();
		// 生成文件
		String pathName = FileConfig.PATH + "template/" + TimeUtil.getCurTimeToFormat("yyyyMMdd") + "/";
		String fileName = "需求数据查询_" + TimeUtil.getCurTimeToFormat("yyyyMMddHHmmss") + ".xls";

		String[] styleArr = jsonparams.get("styleItems").split(",");

		for (String styleStr : styleArr) {
			// 纵向
			if ("Z".equals(styleStr)) {

				// 数据
				List<Map<String, String>> dataMapList = scheduleForMapDao.getDemandDataZ(jsonparams);

				// 基础表头
				List<String> headList = Arrays.asList("组织代码", "计划类型", "计划名称", "需求类别", "用途类别", "料号", "配置", "工厂", "数据类型",
						"计划日期", "在制数量", "库存数量", "计划数量", "创建日期", "更新日期");

				// 基础表头英文名
				List<String> headEngList = Arrays.asList("ORGANIZATION_CODE", "SCHEDULE_TYPE", "SCHEDULE_DESIGNATOR",
						"DEMAND_TYPE", "USED_TYPE", "ITEM_NO", "ITEM_CONF", "FACTORY", "DATA_TYPE", "SCHEDULE_DATE",
						"WIP_QTY", "ONHAND_QTY", "SCHEDULE_QUANTITY", "CREATION_DATE", "LAST_UPDATE_DATE");

				// 数据类型
				List<String> headerDataTypeList = Arrays.asList("str", "str", "str", "str", "str", "str", "str", "str",
						"str", "str", "num", "num", "num", "str", "str");

				Sheet sheet = wb.createSheet("纵向表");

				HSSFCellStyle style = wb.createCellStyle();
				HSSFFont font = wb.createFont();
				font.setFontHeightInPoints((short) 12);
				font.setFontName("Arial Unicode MS");
				style.setFont(font);
				style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
				style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
				style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
				style.setBorderRight(CellStyle.BORDER_THIN);// 右边框

				HSSFDataFormat format = wb.createDataFormat();
				HSSFCellStyle headerStyle = wb.createCellStyle();
				HSSFFont headerFont = wb.createFont();
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
				headerFont.setFontName("仿宋");
				headerStyle.setFont(headerFont);
				headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
				this.setColor(wb, headerStyle, "ADD8E6", (short) 17);

				HSSFFont textFont = wb.createFont();
				textFont.setFontName("Arial Unicode MS");

				// 数据（数字，不变色）

				HSSFCellStyle dataMarkerNumStyle = wb.createCellStyle();
				dataMarkerNumStyle.setFont(textFont);
				dataMarkerNumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
				this.setColor(wb, dataMarkerNumStyle, "FFFFFF", (short) 13);

				// 数据（字符串，不变色）
				HSSFCellStyle dataMarkerStrStyle = wb.createCellStyle();
				dataMarkerStrStyle.setFont(textFont);
				dataMarkerStrStyle.setDataFormat(format.getFormat("@"));
				this.setColor(wb, dataMarkerStrStyle, "FFFFFF", (short) 15);

				// head
				Row firstRow = sheet.createRow(0);
				for (int c = 0; c < headList.size(); c++) {
					Cell cell = firstRow.createCell(c);
					cell.setCellValue(headList.get(c));
					cell.setCellStyle(style);
				}

				// data
				for (int r = 0; r < dataMapList.size(); r++) {
					Row row = sheet.createRow(r + 1);

					Map<String, String> dataMap = dataMapList.get(r);
					for (int c = 0; c < headEngList.size(); c++) {
						Cell cell = row.createCell(c);

						if ("num".equals(headerDataTypeList.get(c))) {
							cell.setCellValue(Double.parseDouble(dataMap.get(headEngList.get(c))));
							cell.setCellStyle(dataMarkerNumStyle);
						} else {
							cell.setCellValue(dataMap.get(headEngList.get(c)));
							cell.setCellStyle(dataMarkerStrStyle);
						}
					}
				}

				for (int c = 0; c < headList.size(); c++) {
					sheet.autoSizeColumn(c, true);
				}
			}

			// 横向
			if ("H".equals(styleStr)) {

				String startDate = jsonparams.get("startDate");
				String endDate = jsonparams.get("endDate");
				jsonparams.put("startDate", "");
				jsonparams.put("endDate", "");

				// 基础表头
				List<String> headList = new ArrayList<String>(
						Arrays.asList("组织代码", "计划类型", "计划名称", "需求类别", "用途类别", "料号", "配置", "工厂", "数据类型"));

				// 基础表头英文名
				List<String> headEngList = Arrays.asList("ORGANIZATION_CODE", "SCHEDULE_TYPE", "SCHEDULE_DESIGNATOR",
						"DEMAND_TYPE", "USED_TYPE", "ITEM_NO", "ITEM_CONF", "FACTORY", "DATA_TYPE");

				// 数据类型
				List<String> headerDataTypeList = new ArrayList<String>(
						Arrays.asList("str", "str", "str", "str", "str", "str", "str", "str", "str"));

				// 数据
				List<Map<String, String>> dataMapList = scheduleForMapDao.getDemandDataH(jsonparams);

				// 最终数据
				List<List<String>> dataListList = new ArrayList<List<String>>();
				// startDate 和 and endDate 都未选
				if ("".equals(startDate) && "".equals(endDate)) {
					this.processDemandDataBothNull(dataMapList, headList, headEngList, headerDataTypeList,
							dataListList);
				}
				// startDate 不为空和 endDate 为空
				else if (!"".equals(startDate) && "".equals(endDate)) {
					this.processDemandDataEndDateIsNull(dataMapList, headList, headEngList, headerDataTypeList,
							dataListList, startDate);
				}
				// startDate 不为空和 endDate 不为空
				else if ("".equals(startDate) && !"".equals(endDate)) {
					this.processDemandDataStartDateIsNull(dataMapList, headList, headEngList, headerDataTypeList,
							dataListList, endDate);
				}
				// startDate 不为空和 endDate 不为空
				else if (!"".equals(startDate) && !"".equals(endDate)) {
					this.processDemandDataBothNotNull(dataMapList, headList, headEngList, headerDataTypeList,
							dataListList, startDate, endDate);
				}

				Sheet sheet = wb.createSheet("横向表");

				HSSFCellStyle style = wb.createCellStyle();
				HSSFFont font = wb.createFont();
				font.setFontHeightInPoints((short) 12);
				font.setFontName("Arial Unicode MS");
				style.setFont(font);
				style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
				style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
				style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
				style.setBorderRight(CellStyle.BORDER_THIN);// 右边框

				HSSFDataFormat format = wb.createDataFormat();
				HSSFCellStyle headerStyle = wb.createCellStyle();
				HSSFFont headerFont = wb.createFont();
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
				headerFont.setFontName("仿宋");
				headerStyle.setFont(headerFont);
				headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
				this.setColor(wb, headerStyle, "ADD8E6", (short) 17);

				HSSFFont textFont = wb.createFont();
				textFont.setFontName("Arial Unicode MS");

				// 数据（数字，不变色）
				HSSFCellStyle dataMarkerNumStyle = wb.createCellStyle();
				dataMarkerNumStyle.setFont(textFont);
				dataMarkerNumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
				this.setColor(wb, dataMarkerNumStyle, "FFFFFF", (short) 13);

				// 数据（字符串，不变色）
				HSSFCellStyle dataMarkerStrStyle = wb.createCellStyle();
				dataMarkerStrStyle.setFont(textFont);
				dataMarkerStrStyle.setDataFormat(format.getFormat("@"));
				this.setColor(wb, dataMarkerStrStyle, "FFFFFF", (short) 15);

				// head
				Row firstRow = sheet.createRow(0);
				for (int c = 0; c < headList.size(); c++) {
					Cell cell = firstRow.createCell(c);
					cell.setCellValue(headList.get(c));
					cell.setCellStyle(style);
				}

				// data
				for (int r = 0; r < dataListList.size(); r++) {
					Row row = sheet.createRow(r + 1);

					List<String> dataList = dataListList.get(r);
					for (int c = 0; c < headList.size(); c++) {
						Cell cell = row.createCell(c);

						if ("num".equals(headerDataTypeList.get(c))) {
							if ("@XOXO@".equals(dataList.get(c))) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(Double.parseDouble(dataList.get(c)));
							}

							cell.setCellStyle(dataMarkerNumStyle);
						} else {

							if ("@XOXO@".equals(dataList.get(c))) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dataList.get(c));
							}

							cell.setCellStyle(dataMarkerStrStyle);
						}
					}
				}

				for (int c = 0; c < headList.size(); c++) {
					sheet.autoSizeColumn(c, true);
				}
			}
		}

		FileUtil.createDir(pathName);
		FileUtil.createFile(pathName + fileName);
		FileOutputStream outStream = new FileOutputStream(pathName + fileName);
		wb.write(outStream);
		wb.close();

		jsonparams.put("url", pathName + fileName);
	}

	/**
	 * 开始日期不为空，结束日期不为空，显示过去和将来
	 * 
	 * @param dataMapList
	 * @param headList
	 * @param headEngList
	 * @param headerDataTypeList
	 * @param dataListList
	 * @param startDate
	 * @param endDate
	 */
	private void processDemandDataBothNotNull(List<Map<String, String>> dataMapList, List<String> headList,
			List<String> headEngList, List<String> headerDataTypeList, List<List<String>> dataListList,
			String startDate, String endDate) {
		Set<String> dateSet = new HashSet<String>();
		for (Map<String, String> dataMap : dataMapList) {
			dateSet.add(dataMap.get("SCHEDULE_DATE"));
		}

		String[] dateArr = dateSet.toArray(new String[] {});
		Arrays.sort(dateArr);

		List<String> futureDateList = new ArrayList<String>();
		List<String> passDateList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		for (String dateKey : dateArr) {
			if (dateKey.compareTo(endDate) > 0) {
				futureDateList.add(dateKey);
			} else if (dateKey.compareTo(startDate) < 0) {
				passDateList.add(dateKey);
			} else {
				dataList.add(dateKey);
			}
		}
		headList.add("过去");
		headList.addAll(dataList);
		headList.add("将来");
		headerDataTypeList.add("num");
		for (String s : dataList) {
			headerDataTypeList.add("num");
		}
		headerDataTypeList.add("num");

		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		// 处理数据，生成最终的数据结构
		for (Map<String, String> dataMap : dataMapList) {
			String key = "";
			for (String baseKey : headEngList) {
				key += (dataMap.get(baseKey) == null ? "" : dataMap.get(baseKey));
			}

			if (tempMap.containsKey(key)) {
				List<String> data = tempMap.get(key);

				int i = headList.indexOf(dataMap.get("SCHEDULE_DATE"));

				// 过去数据
				if (i == -1) {

					// 过去
					if (passDateList.indexOf(dataMap.get("SCHEDULE_DATE")) != -1) {
						if ("@XOXO@".equals(data.get(headList.indexOf("过去")))) {
							data.set(headList.indexOf("过去"), dataMap.get("SCHEDULE_QUANTITY"));
						} else {
							data.set(headList.indexOf("过去"), (Long.parseLong(dataMap.get("SCHEDULE_QUANTITY"))
									+ Long.parseLong(data.get(headList.indexOf("过去"))) + ""));
						}
					}

					// 将来
					if (futureDateList.indexOf(dataMap.get("SCHEDULE_DATE")) != -1) {
						if ("@XOXO@".equals(data.get(headList.indexOf("将来")))) {
							data.set(headList.indexOf("将来"), dataMap.get("SCHEDULE_QUANTITY"));
						} else {
							data.set(headList.indexOf("将来"), (Long.parseLong(dataMap.get("SCHEDULE_QUANTITY"))
									+ Long.parseLong(data.get(headList.indexOf("将来"))) + ""));
						}
					}

				} else {
					if ("@XOXO@".equals(data.get(i))) {
						data.set(i, dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.set(i,
								(Long.parseLong(dataMap.get("SCHEDULE_QUANTITY")) + Long.parseLong(data.get(i)) + ""));
					}
				}

			} else {
				List<String> data = new ArrayList<String>();

				// 基础数据
				for (String baseKey : headEngList) {
					data.add(dataMap.get(baseKey));
				}

				String scheduleDate = dataMap.get("SCHEDULE_DATE");

				// 过去数据
				if (passDateList.contains(scheduleDate)) {
					data.add(dataMap.get("SCHEDULE_QUANTITY"));
				} else {
					data.add("@XOXO@"); // 需要处理的数据（临时占位）
				}

				// 被选择的日期数据
				for (String dateKey : dataList) {
					if (dateKey.equals(scheduleDate)) {
						data.add(dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.add("@XOXO@"); // 需要处理的数据（临时占位）
					}
				}

				// 将来的数据
				if (futureDateList.contains(scheduleDate)) {
					data.add(dataMap.get("SCHEDULE_QUANTITY"));
				} else {
					data.add("@XOXO@"); // 需要处理的数据（临时占位）
				}

				tempMap.put(key, data);
				dataListList.add(data);
			}
		}
	}

	/**
	 * 开始日期为空，结束日期不为空，显示将来列
	 * 
	 * @param dataMapList
	 * @param headList
	 * @param headEngList
	 * @param headerDataTypeList
	 * @param dataListList
	 * @param endDate
	 */
	private void processDemandDataStartDateIsNull(List<Map<String, String>> dataMapList, List<String> headList,
			List<String> headEngList, List<String> headerDataTypeList, List<List<String>> dataListList,
			String endDate) {
		Set<String> dateSet = new HashSet<String>();
		for (Map<String, String> dataMap : dataMapList) {
			dateSet.add(dataMap.get("SCHEDULE_DATE"));
		}

		String[] dateArr = dateSet.toArray(new String[] {});
		Arrays.sort(dateArr);

		List<String> futureDateList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		for (String dateKey : dateArr) {
			if (dateKey.compareTo(endDate) > 0) {
				futureDateList.add(dateKey);
			} else {
				dataList.add(dateKey);
			}
		}

		headList.addAll(dataList);
		headList.add("将来");
		headerDataTypeList.add("num");
		for (String s : dataList) {
			headerDataTypeList.add("num");
		}

		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		// 处理数据，生成最终的数据结构
		for (Map<String, String> dataMap : dataMapList) {
			String key = "";
			for (String baseKey : headEngList) {
				key += (dataMap.get(baseKey) == null ? "" : dataMap.get(baseKey));
			}

			if (tempMap.containsKey(key)) {
				List<String> data = tempMap.get(key);

				int i = headList.indexOf(dataMap.get("SCHEDULE_DATE"));

				// 过去数据
				if (i == -1) {
					if ("@XOXO@".equals(data.get(headList.indexOf("将来")))) {
						data.set(headList.indexOf("将来"), dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.set(headList.indexOf("将来"), (Long.parseLong(dataMap.get("SCHEDULE_QUANTITY"))
								+ Long.parseLong(data.get(headList.indexOf("将来"))) + ""));
					}
				} else {
					if ("@XOXO@".equals(data.get(i))) {
						data.set(i, dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.set(i,
								(Long.parseLong(dataMap.get("SCHEDULE_QUANTITY")) + Long.parseLong(data.get(i)) + ""));
					}
				}

			} else {

				List<String> data = new ArrayList<String>();

				// 基础数据
				for (String baseKey : headEngList) {
					data.add(dataMap.get(baseKey));
				}

				String scheduleDate = dataMap.get("SCHEDULE_DATE");

				// 被选择的日期数据
				for (String dateKey : dataList) {
					if (dateKey.equals(scheduleDate)) {
						data.add(dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.add("@XOXO@"); // 需要处理的数据（临时占位）
					}
				}

				// 将来的数据
				if (futureDateList.contains(scheduleDate)) {
					data.add(dataMap.get("SCHEDULE_QUANTITY"));
				} else {
					data.add("@XOXO@"); // 需要处理的数据（临时占位）
				}

				tempMap.put(key, data);
				dataListList.add(data);
			}
		}
	}

	/**
	 * 开始日期不为空，结束日期为空，显示过去列
	 * 
	 * @param dataMapList
	 * @param headList
	 * @param headEngList
	 * @param headerDataTypeList
	 * @param dataListList
	 * @param startDate
	 */
	private void processDemandDataEndDateIsNull(List<Map<String, String>> dataMapList, List<String> headList,
			List<String> headEngList, List<String> headerDataTypeList, List<List<String>> dataListList,
			String startDate) {

		Set<String> dateSet = new HashSet<String>();
		for (Map<String, String> dataMap : dataMapList) {
			dateSet.add(dataMap.get("SCHEDULE_DATE"));
		}

		String[] dateArr = dateSet.toArray(new String[] {});
		Arrays.sort(dateArr);

		List<String> passDateList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		for (String dateKey : dateArr) {
			if (dateKey.compareTo(startDate) < 0) {
				passDateList.add(dateKey);
			} else {
				dataList.add(dateKey);
			}
		}

		headList.add("过去");
		headList.addAll(dataList);
		headerDataTypeList.add("num");
		for (String s : dataList) {
			headerDataTypeList.add("num");
		}

		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		// 处理数据，生成最终的数据结构
		for (Map<String, String> dataMap : dataMapList) {
			String key = "";
			for (String baseKey : headEngList) {
				key += (dataMap.get(baseKey) == null ? "" : dataMap.get(baseKey));
			}

			if (tempMap.containsKey(key)) {
				List<String> data = tempMap.get(key);

				int i = headList.indexOf(dataMap.get("SCHEDULE_DATE"));

				// 过去数据
				if (i == -1) {
					if ("@XOXO@".equals(data.get(headList.indexOf("过去")))) {
						data.set(headList.indexOf("过去"), dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.set(headList.indexOf("过去"), (Long.parseLong(dataMap.get("SCHEDULE_QUANTITY"))
								+ Long.parseLong(data.get(headList.indexOf("过去"))) + ""));
					}
				} else {
					if ("@XOXO@".equals(data.get(i))) {
						data.set(i, dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.set(i,
								(Long.parseLong(dataMap.get("SCHEDULE_QUANTITY")) + Long.parseLong(data.get(i)) + ""));
					}
				}

			} else {

				List<String> data = new ArrayList<String>();

				// 基础数据
				for (String baseKey : headEngList) {
					data.add(dataMap.get(baseKey));
				}

				String scheduleDate = dataMap.get("SCHEDULE_DATE");
				// 过去数据
				if (passDateList.contains(scheduleDate)) {
					data.add(dataMap.get("SCHEDULE_QUANTITY"));
				} else {
					data.add("@XOXO@"); // 需要处理的数据（临时占位）
				}

				// 被选择的日期数据
				for (String dateKey : dataList) {
					if (dateKey.equals(scheduleDate)) {
						data.add(dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.add("@XOXO@"); // 需要处理的数据（临时占位）
					}
				}

				tempMap.put(key, data);
				dataListList.add(data);
			}
		}
	}

	/**
	 * 开始日期和结束日期都不存在时，所有的日期都进行展示
	 * 
	 * @param dataMapList
	 * @param headList
	 * @param headEngList
	 * @param headerDataTypeList
	 * @param dataListList
	 */
	private void processDemandDataBothNull(List<Map<String, String>> dataMapList, List<String> headList,
			List<String> headEngList, List<String> headerDataTypeList, List<List<String>> dataListList) {

		Set<String> dateSet = new HashSet<String>();
		for (Map<String, String> dataMap : dataMapList) {
			dateSet.add(dataMap.get("SCHEDULE_DATE"));
		}

		String[] dateArr = dateSet.toArray(new String[] {});
		Arrays.sort(dateArr);

		headList.addAll(Arrays.asList(dateArr));

		for (String s : dateArr) {
			headerDataTypeList.add("num");
		}

		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		// 处理数据，生成最终的数据结构
		for (Map<String, String> dataMap : dataMapList) {
			String key = "";
			for (String baseKey : headEngList) {
				key += (dataMap.get(baseKey) == null ? "" : dataMap.get(baseKey));
			}

			if (tempMap.containsKey(key)) {
				List<String> data = tempMap.get(key);

				int i = headList.indexOf(dataMap.get("SCHEDULE_DATE"));

				if ("@XOXO@".equals(data.get(i))) {
					data.set(i, dataMap.get("SCHEDULE_QUANTITY"));
				} else {
					data.set(i, (Long.parseLong(dataMap.get("SCHEDULE_QUANTITY")) + Long.parseLong(data.get(i)) + ""));
				}
			} else {

				List<String> data = new ArrayList<String>();

				// 基础数据
				for (String baseKey : headEngList) {
					data.add(dataMap.get(baseKey));
				}

				// 日期数据
				for (String dateKey : dateArr) {

					if (dateKey.equals(dataMap.get("SCHEDULE_DATE"))) {
						data.add(dataMap.get("SCHEDULE_QUANTITY"));
					} else {
						data.add("@XOXO@"); // 需要处理的数据（临时占位）
					}
				}

				tempMap.put(key, data);
				dataListList.add(data);
			}
		}
	}

	@Override
	public PageObject querySubMitStatusInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<Map<String, String>> orderChangeHeaders = scheduleForMapDao.querySubMitStatusInfo(queryMap, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getSubMitStatusCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public PageObject queryRequireDataInfo(Map<String, String> queryMap, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<Map<String, String>> orderChangeHeaders = scheduleForMapDao.queryRequireDataInfo(queryMap, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getRequireDataCount(queryMap));
		pageObj.setRows(orderChangeHeaders);
		return pageObj;
	}

	@Override
	public PageObject queryHistoryAction(Map<String, String> map, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<?> list = scheduleForMapDao.queryHistoryAction(map, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getHistoryActionCount(map));
		pageObj.setRows(list);
		return pageObj;
	}

	@Override
	public PageObject queryScheduleMsgInfo(Map<String, String> map, PageInfo pageInfo) {
		PageObject pageObj = new PageObject();
		List<?> list = scheduleForMapDao.queryScheduleMsgInfo(map, pageInfo);
		pageObj.setRecords(scheduleForMapDao.getScheduleMsgInfoCount(map));
		pageObj.setRows(list);
		return pageObj;
	}
}
