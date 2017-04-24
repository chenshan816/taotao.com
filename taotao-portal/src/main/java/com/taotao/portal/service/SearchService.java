package com.taotao.portal.service;

import com.taotao.common.pojo.SearchResult;


public interface SearchService {
	
	SearchResult getSearchList(String queryString,int page);
}
