package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.CookieUtils;
import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.CartService;

/**
 * 购物车service
 * 
 * @author cs
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Value("${ORDER_BASE_URL}")
	private String ORDER_BASE_URL;
	@Value("${CART_INFO_LIST}")
	private String CART_INFO_LIST;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * 添加购物车商品 其中1表示增加 2表示直接就是数量
	 */
	@Override
	public TaotaoResult addCartItem(long itemId, int num,
			HttpServletRequest request, HttpServletResponse response, int type) {
		CartInfo cartInfo = getCartInfo(request);
		// 调用taotao-order服务
		String json = "";
		if (2 == type) {
			json = HttpClientUtil.doPostJson(ORDER_BASE_URL + CART_INFO_LIST
					+ "update/" + itemId + "/" + num,
					JsonUtils.objectToJson(cartInfo));
		} else if (1 == type) {
			json = HttpClientUtil.doPostJson(ORDER_BASE_URL + CART_INFO_LIST
					+ "add/" + itemId+"/"+num, JsonUtils.objectToJson(cartInfo));
		}
		// 将json转换为taotao-result
		TaotaoResult result = TaotaoResult.formatToPojo(json, CartInfo.class);
		// 如果没有登录则需要重新将信息重新放入cookie中
		if (result.getStatus() == 200) {
			if (cartInfo.getUser() == null) {
				cartInfo = (CartInfo) result.getData();
				CookieUtils.setCookie(request, response, "TT_CART", JsonUtils
						.objectToJson(cartInfo.getCookieCartItemList()), true);
			} else {
				CookieUtils.deleteCookie(request, response, "TT_CART");
			}

		}
		return result;
	}

	/**
	 * 删除cookie中购物车中的商品
	 */
	@Override
	public void deleteCartItem(Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		CartInfo cartInfo = getCartInfo(request);
		// 调用taotao-order服务
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL + CART_INFO_LIST
				+ "delete/" + itemId, JsonUtils.objectToJson(cartInfo));
		// 将json转换为taotao-result
		TaotaoResult result = TaotaoResult.formatToPojo(json, CartInfo.class);
		// 如果没有登录则需要重新将信息重新放入cookie中
		if (result.getStatus() == 200) {
			if (cartInfo.getUser() == null) {
				cartInfo = (CartInfo) result.getData();
				CookieUtils.setCookie(request, response, "TT_CART", JsonUtils
						.objectToJson(cartInfo.getCookieCartItemList()), true);
			} else {
				CookieUtils.deleteCookie(request, response, "TT_CART");
			}

		}
	}

	@Override
	public List<TbCart> getCartItemList(HttpServletRequest request) {
		CartInfo cartInfo = getCartInfo(request);
		return getCartItemList(cartInfo);
	}

	/**
	 * 获取cookie中购物车的商品列表
	 */
	private List<TbCart> getCookieCartItemList(HttpServletRequest request) {
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		List<TbCart> list = null;
		if (StringUtils.isBlank(cartJson)) {
			list = new ArrayList<TbCart>();
		} else {
			// 将json数据转换为列表
			list = JsonUtils.jsonToList(cartJson, TbCart.class);
		}
		return list;
	}

	/**
	 * 从数据库获取数据
	 * 
	 * @param request
	 * @return
	 */
	private List<TbCart> getCartItemList(CartInfo cartInfo) {
		// 调用taotao-order服务
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL + CART_INFO_LIST
				+ "cart", JsonUtils.objectToJson(cartInfo));
		// 将json转换为taotao-result
		TaotaoResult result = TaotaoResult.formatToList(json, TbCart.class);
		return (List<TbCart>) result.getData();
	}

	private CartInfo getCartInfo(HttpServletRequest request) {
		// 判断是否登录
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		// 2.根据token换取用户信息，调用sso系统接口
		TbUser userInfo = userService.getUserInfoByToken(token);
		List<TbCart> cartItemList = null;
		if (userInfo == null) {
			// 获取Cookie中的信息
			cartItemList = getCookieCartItemList(request);
		} else {
			// 从数据库获取
			cartItemList = getCartItemList(new CartInfo(userInfo, null));
		}
		// 封装一个对象进行转发
		CartInfo cartInfo = new CartInfo(userInfo, cartItemList);
		return cartInfo;
	}
}
