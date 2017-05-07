package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemParam;
import com.taotao.service.ItemService;


@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	/**
	 * 商品列表信息
	 * 返回格式：{total:”2”,rows:[{“id”:”1”,”name”:”张三”},{“id”:”2”,”name”:”李四”}]}
	 */
	@RequestMapping("/item/list")
	@ResponseBody
	public EUIDateGridResult getItemList(Integer page,Integer rows){
		return itemService.getItemList(page, rows);
	}
	
	/**
	 * 商品保存
	 */
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult itemSave(TbItem item,String desc,String itemParams){
		TaotaoResult result = itemService.createItem(item,desc,itemParams);
		//itemService.updateSolrService((long)result.getData());
		return result;
	}
	/**
	 * 获取商品描述
	 */
	@RequestMapping(value="/item/desc/{id}")
	@ResponseBody
	public TaotaoResult itemDesc(@PathVariable Long id){
		return itemService.itemDesc(id);
	}
	/**
	 * 更新商品
	 */
	@RequestMapping(value="/item/update",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult itemUpdate(TbItem item,String desc,String paramData){
		TaotaoResult result = itemService.updateItem(item,desc,paramData);
		//itemService.updateSolrService(item.getId());
		return result;
	}
}
