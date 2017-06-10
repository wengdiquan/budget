package com.bjsj.budget.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjsj.budget.constant.Constant;

/**
 * 请求页面控制层
 * @author TrueBt
 *
 */
@Controller
public class HomeController {
	
	@RequestMapping("/home")
	public String home(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//模拟用户登录,用于和其他平台结合
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put("userName", "小明");
		userMap.put("userId", "1");
		request.getSession().setAttribute(Constant.SESSION_SYS_USER, userMap);
		return "back/main";
		
		/*if (request.getSession().getAttribute(Constant.SESSION_SYS_USER) == null) {
			return "";
		} else {
			return "back/main";
		}*/
	}
	
	
	
}
