package com.taotao.service;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemParam;

public interface ItemService {

	TbItem getItemById(long itemId);
	
	EUIDateGridResult getItemList(int page,int rows);
	
	TaotaoResult itemDesc(long id);
	
	TaotaoResult updateItem(TbItem item,String desc,String paramDatam);

	TaotaoResult createItem(TbItem item, String desc, String paramData);
	
	TaotaoResult updateSolrService(long itemId);
}
