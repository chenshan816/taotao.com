package com.taotao.portal.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemDesc;
import com.taotao.portal.pojo.ItemInfo;


public interface ItemService {
	
	ItemInfo getItemById(long itemId);
	
	String getItemDescById(long itemId);
	
	String getItemParamById(long itemId);
}
