package com.bjsj.budget.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.StringHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bjsj.budget.page.PageInfo;
import com.bjsj.budget.page.PageObject;
import com.bjsj.budget.service.ScheduleForMpsService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * 排除管理
 * @author WB20160742
 *
 */
@Controller
@RequestMapping(value="/schedule")
public class ScheduleForMpsController {
	
	@Autowired
	private ScheduleForMpsService scheduleForMpsService;
	
	/**
	 * 获取最新排程Header
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getCurrHeaderId")
	@ResponseBody
	public JsonObject getCurrHeaderId(HttpServletRequest request,
			HttpServletResponse response){
		JsonObject object = new JsonObject();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		
		try{
			scheduleForMpsService.getCurrHeaderId(queryMap);
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			object.addProperty("message",queryMap.get("returnMessage") + "");
			object.addProperty("currHeaderId", queryMap.get("currHeaderId") + "");
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	
	/**
	 * 查询主排程|关联排程
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryScheduleInfo")
	@ResponseBody
	public JsonObject queryScheduleInfo(HttpServletRequest request,
			HttpServletResponse response){
		//查询条件
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		queryMap.put("versionDateS", request.getParameter("versionDateS"));
		queryMap.put("versionDateE", request.getParameter("versionDateE"));
		queryMap.put("status", request.getParameter("status"));
		queryMap.put("versionNo", request.getParameter("versionNo"));
		queryMap.put("currHeaderId", request.getParameter("currHeaderId"));
		queryMap.put("enableFlag", request.getParameter("enableFlag"));
		
		String type = request.getParameter("type");
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
		
		PageObject pageObj = null;
		if("main".equalsIgnoreCase(type)){
			pageObj	= scheduleForMpsService.queryScheduleInfo(queryMap, pageInfo);
		}
		
		if("relation".equalsIgnoreCase(type)){
			queryMap.put("headerId", request.getParameter("headerId"));
			pageObj	= scheduleForMpsService.queryRelationScheduleInfo(queryMap, pageInfo);
		}
		
		JsonObject resultJson = new JsonObject();
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(pageObj.getRows());
		resultJson.add("scheduleInfoList", jsonElement);
		resultJson.addProperty("totalCount", pageObj.getRecords());

		return resultJson;
	}
	
	/**
	 * 刷新数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/freshMpsData")
	@ResponseBody
	public JsonObject freshMpsData(HttpServletRequest request,
			HttpServletResponse response){
		
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		queryMap.put("headerId", request.getParameter("headerId"));
		
		try{
			scheduleForMpsService.freshMpsData(queryMap);
			
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","刷新成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	/**
	 * 查询复制选项
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getCopySetup")
	@ResponseBody
	public JsonObject getCopySetup(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		JsonObject object = new JsonObject();
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(scheduleForMpsService.getCopySetup(queryMap));
		object.add("copySetupInfo", jsonElement);
		return object;
	}
	
	/**
	 * 复制数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/copy")
	@ResponseBody
	public JsonObject copy(HttpServletRequest request,
			HttpServletResponse response){
		
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		
		queryMap.put("headerId", request.getParameter("headerId"));
		queryMap.put("mpsType", request.getParameter("mpsType"));
		try{
			scheduleForMpsService.copy(queryMap);
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","复制成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	/**
	 * 查询快表数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/findLookupValues")
	@ResponseBody
	public JsonObject findLookupValues(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		queryMap.put("classType", request.getParameter("classType"));
		
		queryMap.put("scheduleTypeValues", request.getParameter("scheduleTypeValues"));
		queryMap.put("organized", request.getParameter("organized"));
		queryMap.put("demandCode", request.getParameter("demandCode"));
		
		JsonObject object = new JsonObject();
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(scheduleForMpsService.findLookupValues(queryMap));
		object.add("fcstLookupValues", jsonElement);
		return object;
	}
	
	/**
	 * 获取默认的模板（根据角色查询）
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getDefaultTemplateByRoleId")
	@ResponseBody
	public JsonObject getDefaultTemplateByRoleId(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		JsonObject object = new JsonObject();
		object.addProperty("templateId", scheduleForMpsService.getDefaultTemplateByRoleId(queryMap));
		return object;
	}
	
	/**
	 * 下载
	 */
	@RequestMapping(value = "/down")
	@ResponseBody
	public JsonObject down(HttpServletRequest request,
			HttpServletResponse response){
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		queryMap.put("roleName", request.getSession().getAttribute("roleName").toString());
		queryMap.put("headerId", request.getParameter("headerId"));
		queryMap.put("batchId", request.getParameter("batchId"));
		queryMap.put("templateId", request.getParameter("templateId"));
		queryMap.put("templateName", request.getParameter("templateName"));
		queryMap.put("versionNo", request.getParameter("versionNo"));
		queryMap.put("versionDate", request.getParameter("versionDate"));
		queryMap.put("realPath", request.getSession().getServletContext().getRealPath("/"));
		
		queryMap.put("isUpdate", request.getParameter("isUpdate"));
		
		try{
			scheduleForMpsService.down(queryMap);
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			object.addProperty("message",queryMap.get("returnMessage") + "");
			object.addProperty("url", queryMap.get("url"));
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}	
	
	/**
	 * 上传
	 * @param file
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importSchedule")
	@ResponseBody
	public ResponseEntity<String> importSchedule(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		StringHolder sh = new StringHolder();
		sh.value = "";
		
		HttpHeaders responseHeaders = new HttpHeaders();
		MediaType mediaType = new MediaType("text", "html", Charset.forName("UTF-8"));
        responseHeaders.setContentType(mediaType);
		
		try{
			scheduleForMpsService.importSchedule(file, queryMap, sh);
		}
		catch(Exception ex){
			ex.printStackTrace();
			sh.value = ex.getMessage();
		}
		
		String msg = "";
		msg = sh.value;
		
		String json = "{\"success\":true,\"message\":\""+msg+ "\"}" ;
		
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
	}
	
	/**
	 * down 文件
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/downFile")
	@ResponseBody
	public void downFile(HttpServletRequest request,
			HttpServletResponse response) {
		String path = request.getParameter("path");
		
		String flag = request.getParameter("flag");
		
        // path是指欲下载的文件的路径。
        File file = new File(path);
        // 取得文件名。
        String filename = file.getName();

        // 以流的形式下载文件。
        InputStream fis;		
		try {
			fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ new String(filename.getBytes("gb2312"),
									"ISO8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
		}finally{
			if("S".equals(flag)){
				file.delete();
			}
		}
	}
	
	/**
	 * 检查要下载的源文件是否存在
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/checkFile")
	@ResponseBody
	public JsonObject checkFile(HttpServletRequest request,
			HttpServletResponse response) {
        String path = request.getParameter("path");
        // path是指欲下载的文件的路径。
        File file = new File(path);
        JsonObject object = new JsonObject();
        object.addProperty("exist", file.exists());
		return object;
	}
	
	/**
	 * 更新备注
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updateRemarkByHeaderId")
	@ResponseBody
	public JsonObject updateRemarkByHeaderId(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("headerId",  request.getParameter("headerId"));
		map.put("remark", request.getParameter("remark"));
		JsonObject object = new JsonObject();
		
		
		try{
			scheduleForMpsService.updateRemarkByHeaderId(map);	
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(map.get("returnStatus"))||"S".equals(map.get("returnStatus"))){
			object.addProperty("status","S");
			if(null == map.get("returnMessage")){
				object.addProperty("message","更新成功");
			}else{
				object.addProperty("message",map.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",map.get("returnStatus") + "");
	    	object.addProperty("message",map.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	
	/**
	 * 提交数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/submit")
	@ResponseBody
	public JsonObject submit(HttpServletRequest request,
			HttpServletResponse response){
		
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		
		queryMap.put("headerId", request.getParameter("headerId"));
		
		try{
			scheduleForMpsService.submit(queryMap);
			
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","提交成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	
	/**
	 * 催办
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/urge")
	@ResponseBody
	public JsonObject urge(HttpServletRequest request,
			HttpServletResponse response){
		
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		
		queryMap.put("headerId", request.getParameter("headerId"));
		
		try{
			scheduleForMpsService.urge(queryMap);
			
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","催办成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	/**
	 * 获取关联料号，和仅显示MPS
	 */
	@RequestMapping(value = "/getRelationAndMpsFlag")
	@ResponseBody
	public JsonObject getRelationAndMpsFlag(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roleId", request.getSession().getAttribute("roleId").toString());
		queryMap.put("templateId", request.getParameter("templateId"));
		JsonObject object = new JsonObject();
		HashMap<String, String> relationAndMpsFlagMap = scheduleForMpsService.getRelationAndMpsFlag(queryMap);
		
		if(relationAndMpsFlagMap != null){
			object.addProperty("relationFlag", relationAndMpsFlagMap.get("RELATIONFLAG"));
			object.addProperty("mpsFlag", relationAndMpsFlagMap.get("MPSFLAG"));
		}
		
		return object;
	}
	
	/**
	 * 查询设置信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryProfile")
	@ResponseBody
	public JsonObject queryProfile(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		// 获取查询条件
		map.put("roleId", (String)request.getSession().getAttribute("roleId"));
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentResult(Integer.parseInt(request.getParameter("start")));
		pageInfo.setShowCount(Integer.parseInt(request.getParameter("limit")));
	
		PageObject pageObj = scheduleForMpsService.queryProfile(map, pageInfo);
		JsonObject object = new JsonObject();
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(pageObj.getRows());
		object.add("setupList", jsonElement);
		object.addProperty("totalCount", pageObj.getRecords());
		return object;
	}
	
	/**
	 * 修改设置信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updateProfile")
	@ResponseBody
	public JsonObject updateProfile(HttpServletRequest request,
			HttpServletResponse response){
		JsonObject object = new JsonObject();
		
		String seqNo = request.getParameter("seqNo");   
		String copy = request.getParameter("copy"); 
		String display = request.getParameter("display");
		String dataType = request.getParameter("dataType");
	
		String templateId = request.getParameter("templateId");  
		String relationItemFlag = request.getParameter("relationItemFlag");  
		String mpsOnlyFlag = request.getParameter("mpsOnlyFlag"); 
		
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("templateId", templateId);
		queryMap.put("relationItemFlag", relationItemFlag);
		queryMap.put("mpsOnlyFlag", mpsOnlyFlag);
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String seqNos[] = seqNo.split(",");
		String copys[] = copy.split(",");
		String displays[] = display.split(",");
		String dataTypes[] = dataType.split(",");
		for(int i = 0;i < displays.length;i ++){
			//再按&分隔
			Map<String, String> map = new HashMap<String, String>();
			map.put("displayFlag", displays[i]);         
			map.put("copyFlag",copys[i]);  
			map.put("seqNo",seqNos[i]);
			map.put("dataType", dataTypes[i]);
			list.add(map);
		}
		
		try{
			scheduleForMpsService.updateSetup(queryMap, list);
			object.addProperty("status", "s");
			
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","修改设置成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
			
		}catch(Exception ex){
			object.addProperty("status", "U");
			object.addProperty("msg", ex.getMessage());
		}
		
		return object;
	}
	
	/**
	 * 验证
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validate")
	@ResponseBody
	public JsonObject validate(HttpServletRequest request, HttpServletResponse response){
		JsonObject object = new JsonObject();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("headerId", request.getParameter("headerId"));
		
		try{
			scheduleForMpsService.validate(queryMap);
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			if(null == queryMap.get("returnMessage")){
				object.addProperty("message","验证成功");
			}else{
				object.addProperty("message",queryMap.get("returnMessage") + "");	
			}
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
	
	/**
	 * 点击设置时调用存储过程
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/loadSetUp")
	@ResponseBody
	public JsonObject loadSetUp(HttpServletRequest request,
			HttpServletResponse response){
		JsonObject object = new JsonObject();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		
		try{
			scheduleForMpsService.loadSetUp(queryMap);
		}catch(Exception ex){
			ex.printStackTrace();
			object.addProperty("status", "U");
			object.addProperty("message", "程序执行抛出异常，异常原因:" + ex.getMessage());
			return object;
		}

		if("s".equals(queryMap.get("returnStatus"))||"S".equals(queryMap.get("returnStatus"))){
			object.addProperty("status","S");
			object.addProperty("message",queryMap.get("returnMessage") + "");
	    }else{
	    	object.addProperty("status",queryMap.get("returnStatus") + "");
	    	object.addProperty("message",queryMap.get("returnMessage") + "");
	    }
		
		return object;
	}
}