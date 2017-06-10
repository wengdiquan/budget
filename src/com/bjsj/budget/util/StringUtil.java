package com.bjsj.budget.util;
/**
 * 
 * 字段串处理的工具类
 * @author 宁业春
 *
 */
public class StringUtil {

	/**
	 * 校验字符串是否为空
	 * @param o 传入的校验对象
	 * @return 校验结果 true表示传入的对象为空或空字符或只带空格的字符串，false表示字符串不为空
	 */
	public static Boolean isEmpty(Object o){
		return (o==null||"".equals(o.toString().trim()))?true:false;
	}
	
	/**
	 * 如果字符串为null的话就转换为""，保证字符串不为null
	 * @param str 传入的字符串
	 * @return 处理后的字符串
	 */
	public static String nullToEmpty(Object str){
		return str ==null?"":str.toString();
	}
	
	/**
	 * 删除异常信息上的异常字符串
	 * @param msg 异常信息
	 * @return 删除后结果
	 */
	public static String deleteExceptionString(String msg) {
		String deleteString = "java.lang.Exception:";
		if (msg != null) {
			return msg.replace(deleteString, "");
		} else {
			return msg;
		}
	}
}
