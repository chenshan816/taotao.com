package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.portal.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService{

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	
	@Override
	public String getContentList(String url) {
		//从配置文件中获取contentCategoryId
		//调用rest服务获取内容列表--get格式
		String result = HttpClientUtil.doGet(REST_BASE_URL+url);
		try{
			//将json格式转换为TaotaoResult对象
			TaotaoResult taotaoResult = TaotaoResult.formatToList(result, TbContent.class);
			List<TbContent> list = (List<TbContent>) taotaoResult.getData();
			//将数据转换为前台需要的json格式
			List<Map> resultList = new ArrayList<>();
			for(TbContent content:list){
				Map map = new HashMap<>();
				map.put("src", content.getPic());
				map.put("height", 240);
				map.put("width", 670);
				map.put("srcB", content.getPic2());
				map.put("widthB", 550);
				map.put("heightB", 240);
				map.put("href", content.getUrl());
				map.put("alt", content.getSubTitle());
				resultList.add(map);
			}
			return JsonUtils.objectToJson(resultList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
