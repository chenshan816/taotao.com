package com.taotao.order.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;

public interface CartService {
	
	TaotaoResult addCartItem(Long itemId,Integer num,CartInfo cartInfo,Integer type);
	
	TaotaoResult getCartItemList(CartInfo cartInfo);

	TaotaoResult deleteCartItem(Long itemId, CartInfo cartInfo);

	TaotaoResult deleteCartItemByOrder(Long userId, List<TbCart> itemList);
}
