package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemParam;
import com.taotao.service.ItemParamService;

/**
 * 规格参数管理
 * @author cs
 *
 */
@Controller
public class ItemParamController {
	
	@Autowired
	private ItemParamService itemParamService;
	/**
	 * 商品列表信息
	 * 返回格式：{total:”2”,rows:[{“id”:”1”,”name”:”张三”},{“id”:”2”,”name”:”李四”}]}
	 */
	@RequestMapping("/item/param/list")
	@ResponseBody
	public EUIDateGridResult getItemList(Integer page,Integer rows){
		return itemParamService.getItemParamList(page, rows);
	}
	
	/**
	 * 商品规格参数初始化
	 */
	@RequestMapping("item/query/{itemCatId}")
	@ResponseBody
	public TaotaoResult getItemParam(@PathVariable Long itemCatId){
		return itemParamService.getItemParamByCid(itemCatId);
	}
	
	/**
	 * 商品规格分组保存
	 */
	@RequestMapping(value="item/param/save/{itemCatId}",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult itemParamSave(@PathVariable Long itemCatId,String paramData){
		TbItemParam itemParam = new TbItemParam();
		itemParam.setItemCatId(itemCatId);
		itemParam.setParamData(paramData);
		return itemParamService.itemParamSave(itemParam);
	}
}
