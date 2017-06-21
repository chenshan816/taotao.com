package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbCart;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbUser;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.OrderService;

/**
 * 提交订单Service
 * @author cs
 *
 */
@Service
public class OrderServiceImpl implements OrderService{
	
	@Value("${ORDER_BASE_URL}")
	private String ORDER_BASE_URL;
	@Value("${ORDER_CREATE_URL}")
	private String ORDER_CREATE_URL;
	@Value("${ORDER_LIST_URL}")
	private String ORDER_LIST_URL;
	@Value("${CART_INFO_LIST}")
	private String CART_INFO_LIST;
	@Value("${PAY_URL}")
	private String PAY_URL;
	
	@Override
	public String[] creareOrder(Order order,HttpServletRequest request) {
		//获取用户信息
		//在拦截器中将用户信息放入request中
		TbUser userInfo = (TbUser) request.getAttribute("userInfo");
		//放入order中
		order.setUserId(userInfo.getId());
		order.setBuyerNick(userInfo.getUsername());
		//调用taotao-order服务
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL+ORDER_CREATE_URL, JsonUtils.objectToJson(order));
		//将json转换为taotao-result
		TaotaoResult result = TaotaoResult.format(json);
		if(result.getStatus() == 200){
			String orderId = (String) result.getData();
			//将选中的物品从购物车中删除
			//将订单对象和购物车对象进行转化
			List<TbCart> cartList = changeOrderItemToCartItem(order.getOrderItems());
			HttpClientUtil.doPostJson(ORDER_BASE_URL+CART_INFO_LIST+"delete_list/"+userInfo.getId(), JsonUtils.objectToJson(cartList));
			//获取支付码
			String qrUrl = getPayImage(orderId);
			return new String[]{orderId,qrUrl};
		}
		return null;	
	}
	
	private List<TbCart> changeOrderItemToCartItem(List<TbOrderItem> orderItems) {
		List<TbCart> cartList =  new ArrayList<TbCart>();
		if(orderItems == null || orderItems.size() <=0)
			return cartList;
		TbOrderItem orderItem = null;
		for(int i=0;i<orderItems.size();i++){
			TbCart cart = new TbCart();
			orderItem = orderItems.get(i);
			cart.setId(Long.parseLong(orderItem.getItemId()));
			cartList.add(cart);
		}
		return cartList;
	}

	public String getPayImage(String orderId){
		String json = HttpClientUtil.doGet(ORDER_BASE_URL+PAY_URL+orderId+"/pay");
		TaotaoResult result = TaotaoResult.format(json);
		if(result.getStatus() == 200){
			Map<String,String> resultMap = (Map<String,String>) result.getData();
			//1.获取qrUrl
			return resultMap.get("qrUrl");
		}
		return null;
	}

	@Override
	public List<Order> getOrderByUserID(HttpServletRequest request) {
		//获取用户信息
		//在拦截器中将用户信息放入request中
		TbUser userInfo = (TbUser) request.getAttribute("userInfo");
		String url = ORDER_BASE_URL+ORDER_LIST_URL+userInfo.getId()+"/1/5";
		String json = HttpClientUtil.doGet(url);
		//将json转换为taotao-result
		TaotaoResult result = TaotaoResult.formatToList(json, Order.class);
		if(result.getStatus() == 200){
			List<Order> orderList = (List<Order>) result.getData();
			return orderList;
		}
		return null;
	}
}
