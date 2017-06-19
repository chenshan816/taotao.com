package com.taotao.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.pojo.TbCart;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.CartService;
import com.taotao.portal.service.OrderService;

/**
 * 前台页面订单Controller
 * @author dell
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	@RequestMapping("/order-cart")
	public String showOrderCart(HttpServletRequest request,HttpServletResponse response,Model model){
		//获取购物车商品列表
		List<TbCart> cartList = cartService.getCartItemList(request);
		//传递给页面
		model.addAttribute("cartList", cartList);
		return "order-cart";
	}
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public String createOrder(Order order,HttpServletRequest request,Model model){
		try {
			String orderId = orderService.creareOrder(order,request);
			//信息回显
			model.addAttribute("orderId", orderId);
			model.addAttribute("payment", order.getPayment());
			model.addAttribute("date", new DateTime().plusDays(3).toString("yyyy-MM-dd"));
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "订单生成出错，请稍后重试");
			return "error/exception";
		}
	}
	
	@RequestMapping("/myorders")
	public String showMyOrder(HttpServletRequest request,Model model){
		List<Order> orderList = orderService.getOrderByUserID(request);
		model.addAttribute("orderList", orderList);
		return "my-orders";
	}
	
	@RequestMapping("/comment/{orderId}")
	public String showComment(@PathVariable Long orderId){
		return "my-order-comment";
	}
}
