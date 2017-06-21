package com.taotao.order.service;

import java.util.Map;

import com.taotao.common.pojo.TaotaoResult;

public interface PayService {
	TaotaoResult pay(String orderNo,String path);
	TaotaoResult aliCallback(Map<String,String> params);
	TaotaoResult queryOrderPayStatus(String orderNo);
}
