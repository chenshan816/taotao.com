package com.taotao.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.Utils.ExceptionUtil;
import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.IDUtils;
import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.Item;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	@Value("${SEARCH_BASE_URL}")
	private String SEARCH_BASE_URL;
	@Value("${SEARCH_IMPORT_URL}")
	private String SEARCH_IMPORT_URL;
	
	@Override
	public TbItem getItemById(long itemId) {
		//TbItem item = itemMapper.selectByPrimaryKey(itemId);
		//添加查询条件
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			TbItem item = list.get(0);
			return item;
		}
		return null;
	}
	@Override
	public EUIDateGridResult getItemList(int page, int rows) {
		//执行查询，并分页
		TbItemExample example = new TbItemExample();
		PageHelper.startPage(page, rows);//，默认查询总页数
		List<TbItem> list = itemMapper.selectByExample(example);
		//获取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
		long total = pageInfo.getTotal();
		return new EUIDateGridResult(total, list);
	}
	
	@Override
	public TaotaoResult createItem(TbItem item, String desc,String paramData) {
		//补全item
		//生成商品id
		Long itemId = IDUtils.genItemId();
		item.setId(itemId);
		//商品状态
		item.setStatus((byte) 1);
		//商品生成和更新状态
		item.setCreated(new Date());
		item.setUpdated(new Date());
		itemMapper.insert(item);
		//添加商品描述
		insertItemDesc(desc,item.getId());
		//添加商品规格参数
		insertItemParamItem(paramData,item.getId());
		return TaotaoResult.ok(itemId);
	}
	/**
	 * 添加商品规格参数
	 * @param paramData
	 * @param id
	 */
	private TaotaoResult insertItemParamItem(String paramData, Long itemId) {
		//补全商品规格参数
		TbItemParamItem record = new TbItemParamItem();
		record.setItemId(itemId);
		record.setParamData(paramData);
		record.setCreated(new Date());
		record.setUpdated(new Date());
		itemParamItemMapper.insert(record);
		return TaotaoResult.ok();
	}
	/**
	 * 添加商品描述
	 * @param desc 商品描述
	 */
	private TaotaoResult insertItemDesc(String desc,long itemId){
		TbItemDesc record = new TbItemDesc();
		record.setItemId(itemId);
		record.setItemDesc(desc);
		record.setCreated(new Date());
		record.setUpdated(new Date());
		itemDescMapper.insert(record);
		return TaotaoResult.ok();
	}
	/**
	 * 编辑商品
	 */
	@Override
	public TaotaoResult itemDesc(long id) {
		TbItemDescExample example = new TbItemDescExample();
		com.taotao.pojo.TbItemDescExample.Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(id);
		TbItemDesc itemDesc = itemDescMapper.selectByExampleWithBLOBs(example).get(0);
		TaotaoResult result = new TaotaoResult(itemDesc);
		return result;
	}
	/**
	 * 更新商品
	 */
	@Override
	public TaotaoResult updateItem(TbItem item, String desc,String paramData) {
		//补全item
		//商品更新状态
		item.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(item);
		//更改商品描述
		updateItemDesc(desc,item.getId());
		//更改规格参数
		UpdateItemParamItem(paramData,item.getId());
		return TaotaoResult.ok();
	}
	private TaotaoResult UpdateItemParamItem(String paramData, Long itemId) {
		TbItemParamItemExample example = new TbItemParamItemExample();
		com.taotao.pojo.TbItemParamItemExample.Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> record = itemParamItemMapper.selectByExampleWithBLOBs(example);
		if(record == null || record.size()<=0){
			return insertItemParamItem(paramData, itemId);
		}else{
			TbItemParamItem itemParamItem = record.get(0);
			itemParamItem.setItemId(itemId);
			itemParamItem.setParamData(paramData);
			itemParamItem.setUpdated(new Date());
			return TaotaoResult.ok();
		}
	}
	private TaotaoResult updateItemDesc(String desc, Long id) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		if(itemDesc == null){
			return insertItemDesc(desc,id);
		}else{
			itemDesc.setItemDesc(desc);
			itemDesc.setUpdated(new Date());
			itemDescMapper.updateByPrimaryKeySelective(itemDesc);
			return TaotaoResult.ok();
		}
	}
	/**
	 * 更新搜索服务层
	 */
	public TaotaoResult updateSolrService(long itemId){
		//更新搜索服务信息
		String json = HttpClientUtil.doGet(SEARCH_BASE_URL+SEARCH_IMPORT_URL+itemId);
		try{
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TaotaoResult.class);
			return taotaoResult;
		}catch(Exception e){
			e.printStackTrace();
			return TaotaoResult.build(500,ExceptionUtil.getStackTrace(e));
		}
	}

}
