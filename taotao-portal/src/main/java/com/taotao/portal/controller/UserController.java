package com.taotao.portal.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.Utils.CookieUtils;
import com.taotao.portal.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request,HttpServletResponse response){
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		if(StringUtils.isBlank(token)){
			return "redirect: ";
		}
		userService.logout(token);
		//删除购物车的cookie信息
		CookieUtils.deleteCookie(request, response, "TT_CART");
		return "redirect: ";
	}
}
