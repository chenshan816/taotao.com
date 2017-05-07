package com.taotao.cms.service.Impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.cms.pojo.ItemInfo;
import com.taotao.cms.service.ItemService;
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

	@Override
	public TaotaoResult getItemBaseInfo(long id) {
		TbItem item = itemMapper.selectByPrimaryKey(id);
		//包装到ItemInfo类中
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setBarcode(item.getBarcode());
		itemInfo.setCid(item.getCid());
		itemInfo.setId(item.getId());
		itemInfo.setImage(item.getImage());
		itemInfo.setNum(item.getNum());
		itemInfo.setPrice(item.getPrice());
		itemInfo.setSellPoint(item.getSellPoint());
		itemInfo.setStatus(item.getStatus());
		itemInfo.setTitle(item.getTitle());
		itemInfo.setCreated(item.getCreated());
		itemInfo.setUpdated(item.getUpdated());
		//使用TaotaoResult包装
		return TaotaoResult.ok(itemInfo);
	}

	@Override
	public TaotaoResult getItemDesc(long id) {

		TbItemDescExample example = new TbItemDescExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(id);
		TbItemDesc itemDesc = itemDescMapper.selectByExampleWithBLOBs(example).get(0);
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(itemDesc);
	}

	@Override
	public TaotaoResult getItemParam(long itemId) {
		
		TbItemParamItem itemParamItem = null;
		//获取ItemParam信息
		TbItemParamItemExample example = new TbItemParamItemExample();
		com.taotao.pojo.TbItemParamItemExample.Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
		if(list != null && list.size() >0){
			itemParamItem = list.get(0);
		}else{
			return TaotaoResult.build(400, "无此商品规格");
		}
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(itemParamItem);
	}

	
}
