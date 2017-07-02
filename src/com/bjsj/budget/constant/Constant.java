package com.bjsj.budget.constant;

public class Constant {
	public static final String SESSION_SYS_USER = "SESSION_SYS_USER";
	
	public static final String COST_CODE = "COST_CODE"; //费用代码，和数据库同步
	
	//新增项目时，将项目ID放入Session中
	public static final String PRODUCT_ID = "PRODUCT_ID";
	
	
	public static final Integer YSFLAG = 1;//运输费
	public static final Integer CLFLAG = 2;//材料费
	public static final Integer AZFLAG = 3;//安装费
	public static final Integer CSFLAG = 4;//措施费
	
	
	public static final Double PER_CENT = 0.17; //费率
}
