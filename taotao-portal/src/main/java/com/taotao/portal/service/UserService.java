package com.taotao.portal.service;

import com.taotao.pojo.TbUser;

public interface UserService {
	
	public TbUser getUserInfoByToken(String token);
	
	public void	logout(String token);
}
