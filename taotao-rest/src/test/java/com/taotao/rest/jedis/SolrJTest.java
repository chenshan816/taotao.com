package com.taotao.rest.jedis;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJTest {
	
	@Test
	public void addDocument() throws Exception{
		//创建连接
		SolrServer solrServer = new HttpSolrServer("http://192.168.27.130:8080/solr");
		//创建文档对象
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "test001");
		document.addField("item_title", "测试商品");
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	@Test
	public void deleteDocument() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.27.130:8080/solr");
		solrServer.deleteById("test001");
		solrServer.commit();
	}
}
