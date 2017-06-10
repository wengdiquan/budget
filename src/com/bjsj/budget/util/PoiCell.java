package com.bjsj.budget.util;
/**
 *=======================================================================
 * Overview       :   excel导出支持类 定义了行、列、数据、样式
 *
* Business Rules : 
 *
 * Comments       : 
 *=======================================================================
 *CHG NO  DATE        PROGRAMMER	DESCRIPTION
 * ----------- 	-------------  	----------------------	---------------------------------------
 * 	01		2015-10-22	那光升	  		新建
 *========================================================================
*/
import org.apache.poi.ss.usermodel.CellStyle;

public class PoiCell {
	private int row;    //行号
	private int column; // 列号
	private String data; // 数据
	private CellStyle style;
	
	public PoiCell(){	
	}
	public PoiCell(int column,String data){
		this.column = column;
		this.data = data;
	}
	public PoiCell(int column,String data,CellStyle style){
		this.column = column;
		this.data = data;
		this.style = style;
	}
	public PoiCell(int row,int column,String data){
		this.row = row;
		this.column = column;
		this.data = data;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public CellStyle getStyle() {
		return style;
	}
	public void setStyle(CellStyle style) {
		this.style = style;
	}

}
