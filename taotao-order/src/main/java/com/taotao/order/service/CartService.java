package com.taotao.order.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.CartInfo;

public interface CartService {
	
	TaotaoResult addCartItem(Long itemId,Integer num,CartInfo cartInfo,Integer type);
	
	TaotaoResult getCartItemList(CartInfo cartInfo);

	TaotaoResult deleteCartItem(Long itemId, CartInfo cartInfo);
}
