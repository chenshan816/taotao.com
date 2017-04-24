package com.taotao.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.portal.service.ContentService;

/**
 * 首页展示
 * @author cs
 *
 */
@Controller
public class IndexController {
	
	@Autowired
	private ContentService contentService;
	@Value("${REST_INDEX_AD_URL}")
	private String REST_INDEX_AD_URL;
	@Value("${REST_INDEX_AD_SMALL_URL}")
	private String REST_INDEX_AD_SMALL_URL;
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		String ad1Json = contentService.getContentList(REST_INDEX_AD_URL);
		String ad2Json = contentService.getContentList(REST_INDEX_AD_SMALL_URL);
		model.addAttribute("ad1", ad1Json);
		model.addAttribute("ad2", ad2Json);
		return "index";
	}
}
