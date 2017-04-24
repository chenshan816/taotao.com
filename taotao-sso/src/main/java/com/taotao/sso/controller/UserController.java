package com.taotao.sso.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jdi.Method;
import com.taotao.common.Utils.ExceptionUtil;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

/**
 * 单点登录用户controller
 * @author cs
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/check/{param}/{type}")
	@ResponseBody
	public Object checkData(@PathVariable String param,@PathVariable Integer type,String callback){
		TaotaoResult result = null;
		//参数有效性校验
		if(StringUtils.isBlank(param)){
			result = TaotaoResult.build(400, "校验内容不能为空");
		}
		if(type == null){
			result = TaotaoResult.build(400, "校验内容不能为空");
		}
		if(null != result){
			if(null != callback){
				MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
				mappingJacksonValue.setJsonpFunction(callback);
				return mappingJacksonValue;
			}else{
				return result;
			}
		}
		//调用服务
		try {
			//防止乱码
			param = new String(param.getBytes("iso8859-1"),"utf-8");
			result = userService.checkData(param, type);
		} catch (Exception e) {
			result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}

	@RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	public Object register(TbUser user,String callback){
		TaotaoResult result = null;
		//调用服务
		try {
			result = userService.createUser(user);
		} catch (Exception e) {
			result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}

	@RequestMapping(value="login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult userLogin(String username, String password,
			HttpServletRequest request,HttpServletResponse response){
		TaotaoResult result = userService.userLogin(username, password,request,response);
		return result;
	}

	@RequestMapping("/token/{token}")
	@ResponseBody
	public Object getUserInfo(@PathVariable String token,String callback){
		TaotaoResult result = null;
		//调用服务
		try {
			result = userService.getUserInfoByToken(token);
		} catch (Exception e) {
			result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}

	@RequestMapping("/logout/{token}")
	@ResponseBody
	public Object userLogout(@PathVariable String token,String callback){
		TaotaoResult result = null;
		//调用服务
		try {
			result = userService.userLogout(token);
		} catch (Exception e) {
			result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}

}
