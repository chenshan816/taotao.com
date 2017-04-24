package com.taotao.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemDescExample.Criteria;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ItemService;

/**
 * 商品信息详细页面管理
 * @author cs
 *
 */
@Service
public class ItemServiceImpl implements ItemService{
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;
	@Value("${REDIS_ITEM_BASE_EXPIRE}")
	private Integer REDIS_ITEM_BASE_EXPIRE;
	@Value("${REDIS_ITEM_DESC_EXPIRE}")
	private Integer REDIS_ITEM_DESC_EXPIRE;
	@Value("${REDIS_ITEM_PARAM_EXPIRE}")
	private Integer REDIS_ITEM_PARAM_EXPIRE;

	@Override
	public TaotaoResult getItemBaseInfo(long id) {
		//添加缓存逻辑
		TbItem item  = null;
		//1.从缓存中获取商品信息
		try{
			//从缓存中取商品信息
			String json = jedisClient.get(REDIS_ITEM_KEY+":"+id+":base");
			//判断是否有值
			if(!StringUtils.isBlank(json)){
				//将json转换为java对象
				item = JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(item);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//2.不存在，则添加缓存
		try{
			item = itemMapper.selectByPrimaryKey(id);
			//将商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY+":"+id+":base", JsonUtils.objectToJson(item));
			//设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY+":"+id+":base", REDIS_ITEM_BASE_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(item);
	}

	@Override
	public TaotaoResult getItemDesc(long id) {
		//添加缓存逻辑
		TbItemDesc itemDesc  = null;
		//1.从缓存中获取商品信息
		try{
			//从缓存中取商品信息
			String json = jedisClient.get(REDIS_ITEM_KEY+":"+id+":desc");
			//判断是否有值
			if(!StringUtils.isBlank(json)){
				//将json转换为java对象
				itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return TaotaoResult.ok(itemDesc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//2.不存在，则添加缓存
		try{
			TbItemDescExample example = new TbItemDescExample();
			Criteria criteria = example.createCriteria();
			criteria.andItemIdEqualTo(id);
			itemDesc = itemDescMapper.selectByExampleWithBLOBs(example).get(0);
			//将商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY+":"+id+":desc", JsonUtils.objectToJson(itemDesc));
			//设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY+":"+id+":desc", REDIS_ITEM_DESC_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(itemDesc);
	}

	@Override
	public TaotaoResult getItemParam(long itemId) {
		//添加缓存逻辑
		TbItemParamItem itemParamItem  = null;
		//1.从缓存中获取商品信息
		try{
			//从缓存中取商品信息
			String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":param");
			//判断是否有值
			if(!StringUtils.isBlank(json)){
				//将json转换为java对象
				itemParamItem = JsonUtils.jsonToPojo(json, TbItemParamItem.class);
				return TaotaoResult.ok(itemParamItem);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//2.不存在，则添加缓存
		try{
			TbItemParamItemExample example = new TbItemParamItemExample();
			com.taotao.pojo.TbItemParamItemExample.Criteria criteria = example.createCriteria();
			criteria.andItemIdEqualTo(itemId);
			List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
			if(list != null && list.size() >0){
				itemParamItem = list.get(0);
				//将商品信息写入缓存
				jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":param", JsonUtils.objectToJson(itemParamItem));
				//设置key的有效期
				jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":param", REDIS_ITEM_PARAM_EXPIRE);
			}else{
				return TaotaoResult.build(400, "无此商品规格");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(itemParamItem);
	}

	
}
