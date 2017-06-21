package com.taotao.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.service.CartService;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;

/**
 * 购物车controller
 * 
 * @author cs
 *
 */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private CartService cartService;

	@RequestMapping(value = "/add/{itemId}/{num}", method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult addCartItem(@RequestBody CartInfo cartInfo,
			@PathVariable("itemId") Long itemId,
			@PathVariable("num") Integer num) {
		TaotaoResult result = cartService.addCartItem(itemId, num, cartInfo, 1);
		return result;
	}

	@RequestMapping(value = "/cart")
	@ResponseBody
	public TaotaoResult getCartItem(@RequestBody CartInfo cartInfo) {
		return cartService.getCartItemList(cartInfo);
	}

	@RequestMapping("/update/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateItemNum(@PathVariable("itemId") Long itemId,
			@PathVariable("num") Integer num, @RequestBody CartInfo cartInfo) {
		return cartService.addCartItem(itemId, num, cartInfo, 2);
	}

	@RequestMapping("/delete/{itemId}")
	@ResponseBody
	public TaotaoResult deleteCartItem(@PathVariable Long itemId,
			@RequestBody CartInfo cartInfo) {
		return cartService.deleteCartItem(itemId, cartInfo);
	}
	
	@RequestMapping("/delete_list/{userId}")
	@ResponseBody
	public TaotaoResult deleteCartItemByOrderId(@PathVariable Long userId,@RequestBody List<TbCart> itemList) {
		return cartService.deleteCartItemByOrder(userId,itemList);
	}
}
