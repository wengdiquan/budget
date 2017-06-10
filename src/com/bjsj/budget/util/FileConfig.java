package com.bjsj.budget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 文件信息配置类
 * 
 * @author wangyuanliang
 * 
 */
public class FileConfig {
	private static FileConfig fileConfig;
	public static String PATH = "/";
	public static String DEMEND_IP = "192.168.137.177";
	public static String OA_GROUP_ID = "3583";;
	public static String OA_CUSTOMER_ADD_ID = "3623";;
	public static String OA_CUSTOMER_UPDATE_ID = "3663";;
	public static String OA_SERVICE_IP = "192.168.130.27";;

	public static FileConfig getInstance() {
		if (fileConfig == null) {
			fileConfig = new FileConfig();
		}
		return fileConfig;
	}

	public void init() {
		InputStream inputStream = this.getClass().getResourceAsStream("/application.properties");
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			DEMEND_IP = properties.getProperty("DEMEND_IP");
			OA_GROUP_ID = properties.getProperty("OA_GROUP_ID");
			OA_CUSTOMER_ADD_ID = properties.getProperty("OA_CUSTOMER_ADD_ID");
			OA_CUSTOMER_UPDATE_ID = properties.getProperty("OA_CUSTOMER_UPDATE_ID");
			OA_SERVICE_IP = properties.getProperty("OA_SERVICE_IP");
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
