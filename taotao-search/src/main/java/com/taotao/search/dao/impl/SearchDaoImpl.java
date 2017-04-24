package com.taotao.search.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.Item;
import com.taotao.common.pojo.SearchResult;
import com.taotao.search.dao.SearchDao;
/**
 * 商品搜索dao
 * @author cs
 *
 */
@Repository
public class SearchDaoImpl implements SearchDao{

	@Autowired
	private SolrServer solrServe;
	
	@Override
	public SearchResult seatch(SolrQuery query) throws Exception {
		
		//返回值
		SearchResult result = new SearchResult();
		//根据查询条件查询索引库
		QueryResponse response = solrServe.query(query);
		//取出查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		result.setRecordCount(solrDocumentList.getNumFound());
		List<Item> itemList = new ArrayList<Item>();
		//高亮显示
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		for(SolrDocument solrDocument : solrDocumentList){
			Item item = new Item();
			item.setId(Long.parseLong((String)solrDocument.get("id")));
			//高亮显示结果
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if(list != null && list.size()>0){
				title =list.get(0);
			}else{
				title = (String) solrDocument.get("item_title");
			}
			item.setTitle(title);
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((long)solrDocument.get("item_price"));
			item.setTitle((String) solrDocument.get("item_title"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			itemList.add(item);	
		}
		result.setItemList(itemList);
		return result;
	}

}
