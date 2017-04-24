package com.taotao.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT_REDIS_KEY}")
	private String INDEX_CONTENT_REDIS_KEY;
	
	@Override
	public List<TbContent> getContentList(long contentCategoryId) {
		//从缓存中取内容，但是如果抛异常，不能影响业务逻辑		
		try{
			String result = jedisClient.hget(INDEX_CONTENT_REDIS_KEY, contentCategoryId+"");
			if(!StringUtils.isBlank(result)){
				//转换为list
				return JsonUtils.jsonToList(result, TbContent.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCategoryId);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		//想缓存中添加内容
		try{
			String cacheString = JsonUtils.objectToJson(list);
			jedisClient.hset("INDEX_CONTENT_REDIS_KEY",contentCategoryId+"", cacheString);
		}catch(Exception e1){
			e1.printStackTrace();
		}
		return list;
	}
}
