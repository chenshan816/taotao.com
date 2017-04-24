package com.taotao.portal.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
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
	
	@Override
	public String creareOrder(Order order,HttpServletRequest request) {
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
			Integer orderId = (Integer) result.getData();
			return orderId.toString();
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
