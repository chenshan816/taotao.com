package com.taotao.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import com.taotao.common.pojo.SearchResult;

public interface SearchDao {
	
	SearchResult seatch(SolrQuery query) throws Exception;
}
