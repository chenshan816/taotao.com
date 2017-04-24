package com.taotao.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taotao.order.pojo.Order;

public interface OrderMapper {
	
	Order getOrderByOrderId(@Param("order_id") long orderId);
	
	List<Order> getOrderByUserId(@Param("user_id") long userId);
}
