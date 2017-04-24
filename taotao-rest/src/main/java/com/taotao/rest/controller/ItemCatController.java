package com.taotao.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.Utils.JsonUtils;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;

/**
 * 分类管理
 * @author cs
 *
 */
@Controller
public class ItemCatController {
	@Autowired
	ItemCatService itemCatService;
	
	@RequestMapping(value="/itemcat/all",produces=MediaType.APPLICATION_JSON_VALUE+";charset=utf-8")
	@ResponseBody
	public String getItemCatList(String callback){
		CatResult catResult = itemCatService.getItemCatList();
		//把pojo转成字符串，再加上callback
		String json = JsonUtils.objectToJson(catResult);
		String result = callback+"("+json+");";
		return result;
	}
}
