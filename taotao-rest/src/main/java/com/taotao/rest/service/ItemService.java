package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;


public interface ItemService {
	
	TaotaoResult getItemBaseInfo(long id);
	
	TaotaoResult getItemDesc(long id);
	
	TaotaoResult getItemParam(long itemId);
}
