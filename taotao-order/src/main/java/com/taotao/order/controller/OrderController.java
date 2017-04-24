package com.taotao.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.Utils.ExceptionUtil;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;


/**
 * 订单controller 
 * @author cs
 *
 */
@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	//使用RequestBody接收json字符串
	@RequestMapping("/create")
	@ResponseBody
	public TaotaoResult createOrder(@RequestBody Order order){
		try{
			TaotaoResult result = orderService.createOrder(order, order.getOrderItems(), order.getOrderShipping());
			return result;
		}catch(Exception e){
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	@RequestMapping("/info/{orderId}")
	@ResponseBody
	public TaotaoResult getOrderByOrderId(@PathVariable Long orderId){
		TaotaoResult result = orderService.getOrderByOrderId(orderId);
		return result;
	}
	
	@RequestMapping("/list/{userId}/{page}/{count}")
	@ResponseBody
	public TaotaoResult getOrderByUserId(@PathVariable("userId") Long userId,
					@PathVariable("page") Integer page,@PathVariable("count") Integer count){
		TaotaoResult result = orderService.getOrderByUserId(userId,page,count);
		return result;
	}
	
	@RequestMapping(value="/changeStatus",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult getOrderByUserId(TbOrder order){
		TaotaoResult result = orderService.updateStatus(order);
		return result;
	}
}
