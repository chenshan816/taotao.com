package com.taotao.portal.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.portal.service.SearchService;

/**
 * 商品搜索service
 * @author dell
 *
 */
@Service
public class SearchServiceImpl implements SearchService {

	@Value("${SEARCH_BASE_URL}")
	private String SEARCH_BASE_URL;
	
	@Override
	public SearchResult getSearchList(String queryString, int page) {
		//调用taotao-search服务
		//查询参数
		Map<String,String> param = new HashMap<String,String>();
		param.put("q", queryString);
		param.put("page", page+"");
		try {
			String json = HttpClientUtil.doGet(SEARCH_BASE_URL,param);
			//字符串转换成java对象（TaotaoResult）
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, SearchResult.class);
			if(taotaoResult.getStatus() == 200){
				SearchResult result = (SearchResult) taotaoResult.getData();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
