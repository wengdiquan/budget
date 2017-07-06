package com.bjsj.budget.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class NumberUtils {
	
	/**
	 * 转换为金钱
	 * @param param
	 * @return
	 */
	public static double degree(double param){
		if(Objects.isNull(param)){
			return 0;
		}
		return  BigDecimal.valueOf(param).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 相乘
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double multiply(Double v1, Double v2){
		return BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2)).doubleValue(); 
	}
	
	/**
	 * 相除
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double divive(Double v1, Double v2){
		return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), 5, RoundingMode.HALF_DOWN).doubleValue(); 
	}

}
