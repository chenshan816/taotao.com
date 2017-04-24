package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.CookieUtils;
import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;

/**
 * 购物车service
 * @author cs
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Value("${ITEM_BASEINFO_URL}")
	private String ITEM_BASEINFO_URL;
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	
	/**
	 * 添加购物车商品
	 * 其中1表示增加
	 * 2表示直接就是数量
	 */
	@Override
	public TaotaoResult addCartItem(long itemId, int num,HttpServletRequest request,HttpServletResponse response,int type) {
		//判断cookie中是否已经存在
		CartItem cartItem = null;
		//取购物车商品列表
		List<CartItem> cartItemlist = getCartItemList(request);
		//判断商品列表中是否已经存在此商品
		for(CartItem item : cartItemlist){
			if(item.getId() == itemId){
				cartItem = item;
				if(type == 1)
					cartItem.setNum(item.getNum()+num);
				else if(type == 2)
					cartItem.setNum(num);
				break;
			}
		}
		if(cartItem == null){
			cartItem = new CartItem();
			//根据商品id查询商品基本信息
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_BASEINFO_URL+itemId);
			//转换为java对象
			TaotaoResult result = TaotaoResult.formatToPojo(json, TbItem.class);
			
			if(result.getStatus() == 200){
				TbItem item = (TbItem) result.getData();
				//转换为精简的pojo
				cartItem.setId(item.getId());
				cartItem.setTitle(item.getTitle());
				cartItem.setPrice(item.getPrice());
				cartItem.setImage(item.getImage()==null?"":item.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItemlist.add(cartItem);
			}
		}
		//将购物车添加到cookie中
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cartItemlist),true);
		return TaotaoResult.ok();
	}
	/**
	 * 删除cookie中购物车中的商品
	 */
	@Override
	public void deleteCartItem(Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		//取购物车商品列表
		List<CartItem> cartItemlist = getCartItemList(request);
		//判断商品列表中是否已经存在此商品
		for(CartItem item : cartItemlist){
			if(item.getId() == itemId){
				cartItemlist.remove(item);
				break;
			}
		}
		//将购物车添加到cookie中
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cartItemlist),true);
	}

	/**
	 * 获取cookie中购物车的商品列表
	 */
	private List<CartItem> getCartItemList(HttpServletRequest request){
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART",true);
		List<CartItem> list = null;
		if(StringUtils.isBlank(cartJson)){
			list = new ArrayList<CartItem>();
		}else{
			//将json数据转换为列表
			list = JsonUtils.jsonToList(cartJson, CartItem.class);
		}
		return list;
	}
	@Override
	public List<CartItem> getCartItemList(
			HttpServletRequest request, HttpServletResponse response) {
		List<CartItem> list = getCartItemList(request);
		return list;
	}

}
