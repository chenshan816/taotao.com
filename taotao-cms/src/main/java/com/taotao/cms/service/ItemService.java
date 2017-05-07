package com.taotao.cms.service;

import com.taotao.common.pojo.TaotaoResult;


public interface ItemService {
	
	TaotaoResult getItemBaseInfo(long id);
	
	TaotaoResult getItemDesc(long id);
	
	TaotaoResult getItemParam(long itemId);
}
