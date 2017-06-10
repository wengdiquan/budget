package com.bjsj.budget.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

public class TimeUtil {

	/**
	 * 获取当前时间:format
	 */
	public static String getCurTimeToFormat(String format){
		  DateFormat dateFormat = new SimpleDateFormat(format);
		  String str = dateFormat.format(new Date());
		  return str;
	}
	public static DateFormat  getDateFormatDay(){
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	public static DateFormat  getDateFormatTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取当前时间:format
	 */
	public static String getDefaultFormatCurTime(){
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String str = dateFormat.format(new Date());
		  return str;
	}

	/**
	 * 获取当前日期的前后i天的日期
	 * @param i
	 * @return
	 */
	public static String getDate(int i){
		
		Date date=new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,i);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		 
		return dateString;
	}
	
	/**
	 * 获取某日期的前后i天的日期
	 * @param i
	 * @return
	 */
	public static String getDate(String dateStr,int i){
		String dateString = "";
		try{
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE,i);//把日期往后增加一天.整数往后推,负数往前移动
			date=calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			dateString = formatter.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dateString;
	}
	
	/**
     * 时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return long 
     */

	public static long getDaySub(String beginDateStr,String endDateStr)
    {
        long day=0;
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");    
        Date beginDate;
        Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);    
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);    
        } catch (Exception e)
        {
            e.printStackTrace();
        }   
        return day;
    }
	/**
	 * 根据  interval 间隔 获取 时间段集合
	 * @param startDate
	 * @param endDate
	 * @param interval
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String,String>> getDateIntervalList(String startDate, String endDate,int interval) throws Exception{
		List<Map<String,String>> resList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			resList = new ArrayList<Map<String,String>>();
			
			Date date1 = sdf.parse(startDate);
			Date date2 = sdf.parse(endDate);
			
			Calendar startCld = Calendar.getInstance();
			startCld.setTime(date1);
			
			Calendar endCld = Calendar.getInstance();
			endCld.setTime(date2);
			
			Calendar curMaxDate = Calendar.getInstance();
			
			Map<String,String> map = null;
			while(!startCld.getTime().after(date2)){//起始段小于结束日期
				map = new HashMap<String,String>();

				map.put("startDate", sdf.format(startCld.getTime()));
				
				Calendar endDay = Calendar.getInstance();
				endDay.setTime(startCld.getTime());
				endDay.add(Calendar.MONTH,interval-1);
				endDay.set(Calendar.DAY_OF_MONTH, endDay.getActualMaximum(Calendar.DAY_OF_MONTH));
				if(!endDay.getTime().after(date2)){
					map.put("endDate", sdf.format(endDay.getTime()));
					curMaxDate.setTime(endDay.getTime());
				}else{
					map.put("endDate", sdf.format(date2));
					curMaxDate.setTime(date2);
				}
				
				resList.add(map);
				startCld.setTime(curMaxDate.getTime());
				startCld.add(Calendar.DAY_OF_MONTH, 1);// 日+1
			}
			
		} catch (Exception e) {
			return null;
		}
		return resList;
	}
	
	public static String getTimeByLong(Long timeLong){
		Date date = new Date(timeLong);
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
}
