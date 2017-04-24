package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.Item;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.service.ItemService;

/**
 * 索引库维护
 * @author cs
 *
 */
@Controller
@RequestMapping("/manager")
public class ItemController {
	@Autowired
	private ItemService itemServer;
	/**
	 * 导入商品数据到索引库
	 */
	@RequestMapping("/importall")
	@ResponseBody
	public TaotaoResult importAllItems(){
		return itemServer.importAllItems();
	}
	/**
	 * 商品更新之后添加到索引库
	 */
	@RequestMapping("/import/{itemId}")
	@ResponseBody
	public TaotaoResult importItem(@PathVariable("itemId")Long itemId){
		return itemServer.importItem(itemId);
	}
}
