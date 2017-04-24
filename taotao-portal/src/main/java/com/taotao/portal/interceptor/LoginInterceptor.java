package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.Utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.impl.UserServiceImpl;


public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private UserServiceImpl userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		//下订单之前要求用户必须登录
		//true表示执行，false表示不执行该视图
		//1.判断用户是否登录，从cookie中取出token
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		//2.根据token换取用户信息，调用sso系统接口
		TbUser userInfo = userService.getUserInfoByToken(token);
		//3.如果未登录则跳转到登录页面，并且将url带入以便登录之后进行跳转
		if(null == userInfo){
			response.sendRedirect(userService.SSO_DOMAIN_BASE_URL+userService.SSO_PAGE_LOGIN_URL
					+"?redirect="+request.getRequestURL());
			return false;
		}else{
			//4.取到则进行放行，继续进行页面跳转
			//将用户信息加入request中
			request.setAttribute("userInfo", userInfo);
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
