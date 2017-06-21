package com.taotao.order.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.constants.Const;
import com.taotao.order.service.PayService;
import com.taotao.order.websocket.socket.SystemWebSocketHandler;

/**
 * 支付宝支付接口
 * 
 * @author cs
 *
 */
@Controller
@RequestMapping("/order/pay")
public class PayController {

	@Autowired
	private PayService payService;

	/**
	 * 支付方法
	 * 
	 * @param orderNo
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{orderNo}/pay")
	@ResponseBody
	public TaotaoResult pay(@PathVariable String orderNo, HttpServletRequest request) {
		String path = request.getSession().getServletContext().getRealPath("upload");
		return payService.pay( orderNo,path);
	}

	/**
	 * 回调方法
	 */
	@RequestMapping("/alipay_callback")
	@ResponseBody
	public Object alipayCallback(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		// 回调的签名sign，状态sign_type和参数
		// 验证是否为支付宝发，同时避免重复通知
		params.remove("sign_type");
		try {
			boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(params,
					Configs.getAlipayPublicKey(), "utf-8",
					Configs.getSignType());
			if (!rsaCheckV2) {
				SystemWebSocketHandler.sendMsg(new TextMessage(Const.AlipayCallback.RESPONSE_FAILED.getBytes()),params.get("out_trade_no"));
				return TaotaoResult.build(400, "验证错误");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		TaotaoResult result = payService.aliCallback(params);
		if(result.getStatus() == 200){
			SystemWebSocketHandler.sendMsg(new TextMessage(Const.AlipayCallback.RESPONSE_SUCCESS.getBytes()),params.get("out_trade_no"));
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		SystemWebSocketHandler.sendMsg(new TextMessage(Const.AlipayCallback.RESPONSE_FAILED.getBytes()),params.get("out_trade_no"));
		return Const.AlipayCallback.RESPONSE_FAILED;
	}
	
	/**
	 * 支付方法
	 * 
	 * @param orderNo
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{orderNo}/query_order_pay_status")
	@ResponseBody
	public TaotaoResult queryPayStatus(@PathVariable String orderNo) {
		return payService.queryOrderPayStatus(orderNo);
	}
}
