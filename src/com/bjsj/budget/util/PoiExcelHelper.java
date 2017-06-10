package com.bjsj.budget.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.StringUtil;

/**
 * Excel统一POI处理类（针对2003以前和2007以后两种格式的兼容处理）
 * 
 * @author chengesheng
 * @date 2012-5-3 下午03:10:23
 * @note PoiHelper
 */
public abstract class PoiExcelHelper {
	public static final String SEPARATOR = ",";
	public static final String CONNECTOR = "-";

	/** 获取sheet列表，子类必须实现 */
	public abstract ArrayList<String> getSheetList(String filePath);

	/** 读取Excel文件数据 
	 * @throws MyException */
	public ArrayList<ArrayList<String>> readExcel(String filePath,
			int sheetIndex) throws MyException {
		return readExcel(filePath, sheetIndex, "1-", "1-");
	}

	/** 读取Excel文件数据 
	 * @throws MyException */
	public ArrayList<ArrayList<String>> readExcel(String filePath,
			int sheetIndex, String rows) throws MyException {
		return readExcel(filePath, sheetIndex, rows, "1-");
	}

	/** 读取Excel文件数据 */
	public ArrayList<ArrayList<String>> readExcel(String filePath,
			int sheetIndex, String[] columns) {
		return readExcel(filePath, sheetIndex, "1-", columns);
	}

	/** 读取Excel文件数据，子类必须实现 */
	public abstract ArrayList<ArrayList<String>> readExcel (String filePath,
			int sheetIndex, String rows, String columns) throws MyException ;

	/** 读取Excel文件数据 */
	public ArrayList<ArrayList<String>> readExcel(String filePath,
			int sheetIndex, String rows, String[] columns) {
		int[] cols = getColumnNumber(columns);

		return readExcel(filePath, sheetIndex, rows, cols);
	}

	/** 读取Excel文件数据，子类必须实现 */
	public abstract ArrayList<ArrayList<String>> readExcel(String filePath,
			int sheetIndex, String rows, int[] cols);

	/** 读取Excel文件内容 
	 * @throws MyException 
	 * @throws NumberFormatException */
	protected ArrayList<ArrayList<String>> readExcel(Sheet sheet, String rows,
			int[] cols) throws NumberFormatException, MyException {
		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		// 处理行信息，并逐行列块读取数据
		String[] rowList = rows.split(SEPARATOR);
		for (String rowStr : rowList) {
			if (rowStr.contains(CONNECTOR)) {
				String[] rowArr = rowStr.trim().split(CONNECTOR);
				int start = Integer.parseInt(rowArr[0]) - 1;
				int end;
				if (rowArr.length == 1) {
					end = getValidExcelRowNum(sheet);//sheet.getLastRowNum();
				} else {
					end = Integer.parseInt(rowArr[1].trim()) - 1;
				}
				dataList.addAll(getRowsValue(sheet, start, end, cols));
			} else {
				dataList.add(getRowValue(sheet, Integer.parseInt(rowStr) - 1,
						cols));
			}
		}
		return dataList;
	}
	/**
	 * 获取excel有效行，去除空行
	 * @param sheet
	 * @return
	 */
		private Integer getValidExcelRowNum(Sheet sheet){
			CellReference cellReference = new CellReference("A4");
			for (int i = cellReference.getRow()-1; i <= sheet.getLastRowNum();) {  
	            Row r = sheet.getRow(i);  
	            if(r == null){  
	                // 如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动  
	                sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);  
	                continue;  
	            }  
	            Boolean flag = false;  
	            for(Cell c:r){  
	                if(c.getCellType() != Cell.CELL_TYPE_BLANK){  
	                    flag = true;  
	                    break;  
	                }  
	            }  
	            if(flag){  
	                i++;  
	                continue;  
	            }else{//如果是空白行（即可能没有数据，但是有一定格式）  
	                if(i == sheet.getLastRowNum())//如果到了最后一行，直接将那一行remove掉  
	                    sheet.removeRow(r);  
	                else//如果还没到最后一行，则数据往上移一行  
	                    sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);  
	            }  
	        }  
						
						
			return sheet.getLastRowNum();
		}
	/** 获取连续行、列数据 */
	protected ArrayList<ArrayList<String>> getRowsValue(Sheet sheet,
			int startRow, int endRow, int startCol, int endCol) {
		if (endRow < startRow || endCol < startCol) {
			return null;
		}

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		for (int i = startRow; i <= endRow; i++) {
			data.add(getRowValue(sheet, i, startCol, endCol));
		}
		return data;
	}

	/** 获取连续行、不连续列数据 
	 * @throws MyException */
	private ArrayList<ArrayList<String>> getRowsValue(Sheet sheet,
			int startRow, int endRow, int[] cols) throws MyException {
		if (endRow < startRow) {
			return null;
		}

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		for (int i = startRow; i <= endRow; i++) {
			data.add(getRowValue(sheet, i, cols));
		}
		return data;
	}

	/** 获取行连续列数据 */
	private ArrayList<String> getRowValue(Sheet sheet, int rowIndex,
			int startCol, int endCol) {
		if (endCol < startCol) {
			return null;
		}

		Row row = sheet.getRow(rowIndex);
		ArrayList<String> rowData = new ArrayList<String>();
		for (int i = startCol; i <= endCol; i++) {
			rowData.add(getCellValue(row, i));
		}
		return rowData;
	}

	/** 获取行不连续列数据 
	 * @throws MyException */
	private ArrayList<String> getRowValue(Sheet sheet, int rowIndex, int[] cols) throws MyException {
		Row row = sheet.getRow(rowIndex);
		ArrayList<String> rowData = new ArrayList<String>();
		for (int colIndex : cols) {
			try {
				rowData.add(getCellValue(row, colIndex));
			} catch (Exception e) {
				throw new MyException("第"+(rowIndex+1)+"行,第"+(colIndex+1)+"列数据读取失败");
			}
		}
		return rowData;
	}

	/**
	 * 获取单元格内容
	 * 
	 * @param row
	 * @param column
	 *            a excel column string like 'A', 'C' or "AA".
	 * @return
	 */
	protected String getCellValue(Row row, String column) {
		return getCellValue(row, getColumnNumber(column));
	}

	/**
	 * 获取单元格内容
	 * 
	 * @param row
	 * @param col
	 *            a excel column index from 0 to 65535
	 * @return
	 */
	private String getCellValue(Row row, int col) {
		if (row == null) {
			return "";
		}
		Cell cell = row.getCell(col);
		return getCellValue(cell);
	}

	/**
	 * 获取单元格内容
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellValue(Cell cell) {
		// if (cell == null) {
		// return "";
		// }
		//
		// String value = cell.toString().trim();
		// try {
		// // This step is used to prevent Integer string being output with
		// // '.0'.
		// Float.parseFloat(value);
		// value=value.replaceAll("\\.0$", "");
		// value=value.replaceAll("\\.0+$", "");
		// return value;
		// } catch (NumberFormatException ex) {
		// return value;
		// }

		String value = null;

		if (cell != null) {
			switch (cell.getCellType()) {

			case HSSFCell.CELL_TYPE_FORMULA:
				// cell.getCellFormula();
				try {
					
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// modify by fangjing 20130605
						if (cell.getCellStyle().getDataFormat() == 185
								|| cell.getCellStyle().getDataFormat() == 177
								|| cell.getCellStyle().getDataFormat() == 184
								|| cell.getCellStyle().getDataFormat() == 20) {
							String sDate = cell.getNumericCellValue() + "";
							if (StringUtil.getEncodedSize(sDate) > 0) {
								Date date = HSSFDateUtil.getJavaDate(cell
										.getNumericCellValue());
								return new java.text.SimpleDateFormat("yyyy-MM-dd")
										.format(date);
							}
							return "";
						} else {
							double d = cell.getNumericCellValue();
							Date date = HSSFDateUtil.getJavaDate(d);
							SimpleDateFormat dformat = new SimpleDateFormat(
									"yyyy-MM-dd");
							value = dformat.format(date);
						}
					}else{
						value = String.valueOf(cell.getNumericCellValue());	
					}
					
				} catch (IllegalStateException e) {
					value = String.valueOf(cell.getRichStringCellValue());
				}
				break;

			case HSSFCell.CELL_TYPE_NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// modify by fangjing 20130605
					if (cell.getCellStyle().getDataFormat() == 185
							|| cell.getCellStyle().getDataFormat() == 177
							|| cell.getCellStyle().getDataFormat() == 184
							|| cell.getCellStyle().getDataFormat() == 20) {
						String sDate = cell.getNumericCellValue() + "";
						if (StringUtil.getEncodedSize(sDate) > 0) {
							Date date = HSSFDateUtil.getJavaDate(cell
									.getNumericCellValue());
							return new java.text.SimpleDateFormat("yyyy-MM-dd")
									.format(date);
						}
						return "";
					} else {
						double d = cell.getNumericCellValue();
						Date date = HSSFDateUtil.getJavaDate(d);
						SimpleDateFormat dformat = new SimpleDateFormat(
								"yyyy-MM-dd");
						value = dformat.format(date);
					}
				} else {
					if(cell.toString().matches("^([0-9]{1,}[.][0-9]*)$")){
                    	String[] cellVal = cell.toString().split("\\.");
                    	int decimalDigit = cellVal[1].length();
                    	if(decimalDigit > 1){
                    		BigDecimal bd = new BigDecimal(cell.getNumericCellValue()); 
                    		double doubleVal = bd.setScale(decimalDigit = decimalDigit > 8 ? 8 : decimalDigit, BigDecimal.ROUND_HALF_UP).doubleValue();
                    		value = String.valueOf(doubleVal);
                    	} else {
	                    	NumberFormat nf = NumberFormat.getInstance();
		                    nf.setGroupingUsed(false);//true时的格式：1,234,567,890
		                    value= nf.format(cell.getNumericCellValue());//数值类型的数据为double，所以需要转换一下 
                    	}
                    	//System.out.println("IF===CELL_TYPE_NUMERIC IF"+ value);
                    } else {
                    	NumberFormat nf = NumberFormat.getInstance();
	                    nf.setGroupingUsed(false);//true时的格式：1,234,567,890
	                    value= nf.format(cell.getNumericCellValue());//数值类型的数据为double，所以需要转换一下 
	                   // System.out.println("ELSE===CELL_TYPE_NUMERIC ELSE"+ value);
                    }
				}
				break;

			case HSSFCell.CELL_TYPE_STRING:
				value = String.valueOf(cell.getRichStringCellValue());
				break;

			case HSSFCell.CELL_TYPE_BOOLEAN:
				System.out.println("====CELL_TYPE_BOOLEAN"
						+ cell.getBooleanCellValue());
				value = String.valueOf(cell.getBooleanCellValue());
				break;
			}
		}

		return value;
	}

	/**
	 * Change excel column letter to integer number
	 * 
	 * @param columns
	 *            column letter of excel file, like A,B,AA,AB
	 * @return
	 */
	private int[] getColumnNumber(String[] columns) {
		int[] cols = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			cols[i] = getColumnNumber(columns[i]);
		}
		return cols;
	}

	/**
	 * Change excel column letter to integer number
	 * 
	 * @param column
	 *            column letter of excel file, like A,B,AA,AB
	 * @return
	 */
	private int getColumnNumber(String column) {
		int length = column.length();
		short result = 0;
		for (int i = 0; i < length; i++) {
			char letter = column.toUpperCase().charAt(i);
			int value = letter - 'A' + 1;
			result += value * Math.pow(26, length - i - 1);
		}
		return result - 1;
	}

	/**
	 * Change excel column string to integer number array
	 * 
	 * @param sheet
	 *            excel sheet
	 * @param columns
	 *            column letter of excel file, like A,B,AA,AB
	 * @return
	 */
	protected int[] getColumnNumber(Sheet sheet, String columns) {
		// 拆分后的列为动态，采用List暂存
		ArrayList<Integer> result = new ArrayList<Integer>();
		String[] colList = columns.split(SEPARATOR);
		for (String colStr : colList) {
			if (colStr.contains(CONNECTOR)) {
				String[] colArr = colStr.trim().split(CONNECTOR);
				int start = Integer.parseInt(colArr[0]) - 1;
				int end;
				if (colArr.length == 1) {
					end = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum() - 1;
				} else {
					end = Integer.parseInt(colArr[1].trim()) - 1;
				}
				for (int i = start; i <= end; i++) {
					result.add(i);
				}
			} else {
				result.add(Integer.parseInt(colStr) - 1);
			}
		}

		// 将List转换为数组
		int len = result.size();
		int[] cols = new int[len];
		for (int i = 0; i < len; i++) {
			cols[i] = result.get(i).intValue();
		}

		return cols;
	}
}
