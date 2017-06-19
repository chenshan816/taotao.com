package com.taotao.portal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbCart;

public interface CartService {
	
	TaotaoResult addCartItem(long itemId,int num,HttpServletRequest request,HttpServletResponse response,int type);
	
	List<TbCart> getCartItemList(HttpServletRequest request);

	void deleteCartItem(Long itemId, HttpServletRequest request,
			HttpServletResponse response);
}
