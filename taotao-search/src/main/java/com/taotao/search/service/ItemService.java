package com.taotao.search.service;

import com.taotao.common.pojo.Item;
import com.taotao.common.pojo.TaotaoResult;

public interface ItemService {
	TaotaoResult importAllItems();
	TaotaoResult importItem(Long itemId);
}
