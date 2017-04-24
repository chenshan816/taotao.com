package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;

/**
 * 内容管理表现层
 * @author cs
 *
 */
@Controller
public class ContentController {
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EUIDateGridResult getContentList(Integer page,Integer rows,Long categoryId){
		return contentService.getContentListByCategoryId(page, rows, categoryId);
	}
	
	/*
	 * 内容保存
	 */
	@RequestMapping("/content/save")
	@ResponseBody
	public TaotaoResult saveContent(TbContent content){
		return contentService.saveContent(content);
	}
	
	/*
	 * 内容修改
	 */
	@RequestMapping("/content/edit")
	@ResponseBody
	public TaotaoResult updateContent(TbContent content){
		return contentService.updateContent(content);
	}
}
