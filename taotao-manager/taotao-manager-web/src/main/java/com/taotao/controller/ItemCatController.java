package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUITreeNode;
import com.taotao.service.ItemCatService;

/**
 * 商品类型查询
 * @author dell
 *
 */
@Controller
@RequestMapping("/item/cat")
public class ItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	@RequestMapping("/list")
	@ResponseBody
	private List<EUITreeNode> getItemCatList(@RequestParam(value="id", defaultValue="0")Long parentId){
		return itemCatService.getCatList(parentId);
	}
	
}
