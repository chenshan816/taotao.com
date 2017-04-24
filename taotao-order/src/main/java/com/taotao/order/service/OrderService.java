package com.taotao.order.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

public interface OrderService {
	
	TaotaoResult createOrder(TbOrder order,List<TbOrderItem> itemList,TbOrderShipping orderShipping);
	
	TaotaoResult getOrderByOrderId(Long orderId);
	
	TaotaoResult getOrderByUserId(Long UserId,int page,int count);
	
	TaotaoResult updateStatus(TbOrder order);
}
