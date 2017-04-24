package com.taotao.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.service.RedisCleanService;

@Service
public class RedisCleanServiceImpl implements RedisCleanService {

	//rest服务层地址
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_INDEX_AD_URL}")
	private String REST_INDEX_AD_URL;
	
	@Override
	public void cleanRedisTocontent(String param){
		try{
			HttpClientUtil.doGet(REST_BASE_URL+REST_INDEX_AD_URL+param);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
