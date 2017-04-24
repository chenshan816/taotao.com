package com.taotao.portal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.taotao.portal.pojo.Order;

public interface OrderService {
	
	String creareOrder(Order order,HttpServletRequest request);
	
	List<Order> getOrderByUserID(HttpServletRequest request);
}
