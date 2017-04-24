package com.taotao.portal.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Value("${SSO_DOMAIN_BASE_URL}")
	public String SSO_DOMAIN_BASE_URL;
	@Value("${SSO_USER_TOKRN_URL}")
	private String SSO_USER_TOKRN_URL;
	@Value("${SSO_PAGE_LOGIN_URL}")
	public String SSO_PAGE_LOGIN_URL;
	@Value("${SSO_USER_LOGOUT}")
	private String SSO_USER_LOGOUT;
	
	/**
	 * 根据token调用sso系统获取用户信息
	 */
	@Override
	public TbUser getUserInfoByToken(String token) {
		String json = HttpClientUtil.doGet(SSO_DOMAIN_BASE_URL+SSO_USER_TOKRN_URL+token);
		TaotaoResult result = TaotaoResult.formatToPojo(json, TbUser.class);
		if(result.getStatus() == 200){
			return (TbUser) result.getData();
		}
		return null;
	}

	@Override
	public void logout(String token) {
		HttpClientUtil.doGet(SSO_DOMAIN_BASE_URL+SSO_USER_LOGOUT+token);
	}

}
