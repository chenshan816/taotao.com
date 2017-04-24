package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.dao.JedisClient;
import com.taotao.order.dao.OrderDao;
import com.taotao.order.mapper.OrderMapper;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderExample;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderItemExample;
import com.taotao.pojo.TbOrderItemExample.Criteria;
import com.taotao.pojo.TbOrderShipping;

/**
 *  订单管理Service
 *  @author dell
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private OrderMapper orderMap;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ORDER_GEN_KEY}")
	private String ORDER_GEN_KEY;
	@Value("${ORDER_INIT_ID}")
	private String ORDER_INIT_ID;
	@Value("${ORDER_ITEM_GEN_KEY}")
	private String ORDER_ITEM_GEN_KEY;
	
	//订单付款状态常量
	private int NOT_PAYMENT = 1;//未付款
	private int PAID = 2;//已付款
	private int NOT_SHIPPED = 3;
	private int SHIPPED = 4;
	private int TRADE_SUCCESS = 5;
	private int TRADE_CLOSED = 6;
	
	@Override
	public TaotaoResult createOrder(TbOrder order, List<TbOrderItem> itemList,
			TbOrderShipping orderShipping) {
		//向订单表中插入记录
		//获取订单号
		String str = jedisClient.get(ORDER_GEN_KEY);
		if(StringUtils.isBlank(str)){
			jedisClient.set(ORDER_GEN_KEY, ORDER_INIT_ID);//设定初始化
		}
		long orderId = jedisClient.incr(ORDER_GEN_KEY);
		//补全pojo
		order.setOrderId(orderId+"");
		order.setStatus(NOT_PAYMENT);
		order.setCreateTime(new Date());
		order.setUpdateTime(new Date());
		//0-未评价 1-以评价
		order.setBuyerRate(0);
		//插入订单
		orderMapper.insert(order);
		//插入订单明细
		for(TbOrderItem orderItem : itemList){
			//订单明细生成
			long orderitemId = jedisClient.incr(ORDER_ITEM_GEN_KEY);
			orderItem.setId(orderitemId+"");
			orderItem.setOrderId(orderId+"");
			orderItemMapper.insert(orderItem);
		}
		//插入物流表
		orderShipping.setOrderId(orderId+"");
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		return TaotaoResult.ok(orderId);
	}
	/**
	 * 根据订单号查询商品订单
	 */
	@Override
	public TaotaoResult getOrderByOrderId(Long orderId) {
		Order order = orderMap.getOrderByOrderId(orderId);
		return TaotaoResult.ok(order);
	}
	/**
	 * 根据用户id查询商品订单
	 */
	@Override
	public TaotaoResult getOrderByUserId(Long userId, int page, int count) {
		PageHelper.startPage(page, count);
		List<Order> orderList = orderDao.getOrderByUserId(userId);
		return TaotaoResult.ok(orderList);
	}
	/**
	 * 更新商品状态
	 */
	@Override
	public TaotaoResult updateStatus(TbOrder order) {
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
		return TaotaoResult.ok();
	}

}
