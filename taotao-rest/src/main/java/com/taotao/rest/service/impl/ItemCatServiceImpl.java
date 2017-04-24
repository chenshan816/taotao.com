package com.taotao.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.JsonUtils;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.pojo.ItemCatNode;
import com.taotao.rest.service.ItemCatService;

/**
 * 分类json服务管理
 * @author cs
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${INDEX_CONTENT_CATEGOTY_REDIS_KEY}")
	private String INDEX_CONTENT_CATEGOTY_REDIS_KEY;

	@Override
	public CatResult getItemCatList() {
		CatResult catResult = new CatResult();
		//查询分类列表
		catResult.setData(getCatList(0));
		return catResult;
	}
	/**
	 * 查询分类列表	
	 * @param parentid 父元素的id
	 * @return
	 */
	private List<?> getCatList(long parentid){
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentid);
		List<Object> resultList = new ArrayList<Object>();
		//查询缓存
		try{
			String result = jedisClient.hget(INDEX_CONTENT_CATEGOTY_REDIS_KEY,parentid+"");
			if(!StringUtils.isBlank(result)){
				//转换为list
				return JsonUtils.jsonToList(result, Object.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//执行查询
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		int count =0;
		for(TbItemCat item:list){
			if(count == 14) break;//防止首页超长处理
			if(item.getIsParent()){
				ItemCatNode node = new ItemCatNode();
				if(parentid == 0){
					node.setName("<a href='/products/"+item.getId()+".html'>"+item.getName()+"</a>");
					count++;
				}else{
					node.setName(item.getName());
				}
				node.setUrl("/products/"+item.getId()+".html");
				node.setItem(getCatList(item.getId()));
				resultList.add(node);
			}else{
				resultList.add("/products/"+item.getId()+".html|"+item.getName());
			}
		}
		try{
			String cacheString = JsonUtils.objectToJson(resultList);
			jedisClient.hset("INDEX_CONTENT_CATEGOTY_REDIS_KEY",parentid+"", cacheString);
		}catch(Exception e1){
			e1.printStackTrace();
		}
		return resultList;
	}
}
