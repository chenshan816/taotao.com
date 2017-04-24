package com.taotao.order.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.order.dao.OrderDao;
import com.taotao.order.pojo.Order;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderExample;
import com.taotao.pojo.TbOrderExample.Criteria;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderItemExample;

@Repository
public class OrderDaoImpl implements OrderDao {
	
	@Autowired 
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Override
	public List<Order> getOrderByUserId(Long userId) {
		List<Order> orderList = new ArrayList<Order>();
		//获取订单信息
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbOrder> tbOrderList = orderMapper.selectByExample(example);
		//获取每个订单中商品信息
		for(TbOrder tbOrder : tbOrderList){
			Order order = new Order();
			//将订单基本信息存入order中
			order.setOrderId(tbOrder.getOrderId());
			order.setPayment(tbOrder.getPayment());
			order.setPaymentType(tbOrder.getPaymentType());
			order.setStatus(tbOrder.getStatus());
			order.setCreateTime(tbOrder.getCreateTime());
			order.setPostFee(tbOrder.getPostFee());
			order.setUserId(userId);
			order.setBuyerMessage(order.getBuyerMessage());
			order.setBuyerNick(order.getBuyerNick());	
			//获取商品信息
			TbOrderItemExample example1 = new TbOrderItemExample();
			com.taotao.pojo.TbOrderItemExample.Criteria criteria1 = example1.createCriteria();
			criteria1.andOrderIdEqualTo(tbOrder.getOrderId());
			List<TbOrderItem> orderItems = orderItemMapper.selectByExample(example1);
			order.setOrderItems(orderItems);
			orderList.add(order);
		}
		return orderList;
	}

	@Override
	public Order getOrderByOrderId(Long orderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
