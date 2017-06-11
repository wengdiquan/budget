package com.bjsj.budget.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 转换工具类
 * 
 * @author 宁业春
 * 
 */
public class TransforUtil {

	/**
	 * 将数据从map自动提取到bean
	 * @param map map数组，系统只会取第一个
	 * @param dest 目标对象
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 */
	public static Object transFromMapToBean(Map<String, String[]> map,
			Object dest) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException {

		if (dest == null || map == null) {
			return null;
		}
		Set<String> keySet = map.keySet();
		// Field[] fields = dest.getClass().getDeclaredFields();
		// Field[] fields = dest.getClass().getFields();
		for (String key : keySet) {

			// for(Field field:fields){
			// if(key.equals(field.getName())){
			String value = null;
			if (map.get(key) != null && map.get(key)[0] != null
					&& !map.get(key)[0].isEmpty()) {
				value = map.get(key)[0];
			}
			if (value == null) {
				continue;
			}
			PropertyDescriptor pd = null;
			try {
				pd = new PropertyDescriptor(key, dest.getClass());
			} catch (Exception e) {
				continue;
			}
			Method setMethod = pd.getWriteMethod();
			if (setMethod == null) {
				continue;
			}
			String paramType = setMethod.getParameterTypes()[0].toString();

			if (paramType.equals("class java.lang.String")) {

				setMethod.invoke(dest, value);

			} else if (paramType.equals("class java.util.Date")) {
				if (10 == value.length()) {
					setMethod.invoke(dest, TimeUtil.getDateFormatDay().parse(value));
				}
				if (19 == value.length()) {
					value = value.replace("T", " ");
					setMethod.invoke(dest, TimeUtil.getDateFormatTime().parse(value));
				}

			} else if (paramType.equals("class java.lang.Float")
					|| paramType.equals("float")) {

				setMethod.invoke(dest, Float.parseFloat(value));

			} else if (paramType.equals("class java.lang.Long")
					|| paramType.equals("long")) {

				setMethod.invoke(dest, Long.parseLong(value));

			} else if (paramType.equals("class java.lang.Double")
					|| paramType.equals("double")) {

				setMethod.invoke(dest, Double.parseDouble(value));

			} else if (paramType.equals("class java.lang.Integer")
					|| paramType.equals("int")) {

				setMethod.invoke(dest, Integer.parseInt(value));

			} else if (paramType.equals("class java.math.BigDecimal")) {

				setMethod.invoke(dest, new BigDecimal(value));

			} else if (paramType.equals("class java.lang.Boolean")
					|| paramType.equals("boolean")) {
				setMethod.invoke(dest, Boolean.parseBoolean(value));
			}
			// }
			// }
		}
		return dest;
	}

	// 从Map<String,String[]>转换成Map<String,Object>
	public static Map<String, String> transRMapToMap(Map<String, String[]> map) {
		Map<String, String> rsMap = new HashMap<String, String>();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			rsMap.put(key, map.get(key) == null ? null : map.get(key)[0]);
		}
		return rsMap;
	}
	
	// 从Map<String,Object>转换成Map<String,String[]>
	public static Map<String, String[]> transMapToRMap(Map<String, Object> map) {
		Map<String, String[]> rsMap = new HashMap<String, String[]>();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			rsMap.put(key, map.get(key) == null?null:new String[]{map.get(key).toString()});
		}
		return rsMap;
	}
	
	/**
	 * 如果是空，那么就是N，一般用于checkbox
	 * @param value 传入的要判断的值
	 * @return Y|N
	 */
	public static String transIsEmptyToN(String value){
		return "Y".equals(value)?"Y":"N";
	}
	
	/**
	 * 从Y转换成on，一般用于checkbox
	 * @param value 传入的要判断的值
	 * @return on|""
	 */
	public static String transFromYToOn(String value){
		return "Y".equals(value)?"on":"";
	}
	
	/**
	 * 从true 或者 on 转换为Y，一般用于checkbox
	 * @param value 传入的要判断的值
	 * @return Y|N
	 */
	public static String transFromTrueToY(String value){
		return ("true".equalsIgnoreCase(value) || 
				"on".equalsIgnoreCase(value) || 
				"Y".equalsIgnoreCase(value))?"Y":"N";
	}
	
	/**
	 * 从Y 或者 on 转换为true，一般用于checkbox
	 * @param value 传入的要判断的值
	 * @return Y|N
	 */
	public static String transFromYToTrue(String value){
		return ("true".equalsIgnoreCase(value) || 
				"on".equalsIgnoreCase(value) || 
				"Y".equalsIgnoreCase(value))?"true":"false";
	}

}
