package com.taotao.order.dao;

import java.util.List;

import com.taotao.order.pojo.Order;


public interface OrderDao {
	
	List<Order> getOrderByUserId(Long userId);
	Order getOrderByOrderId(Long orderId);
}
