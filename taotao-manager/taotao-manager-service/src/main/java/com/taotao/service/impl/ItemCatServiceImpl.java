package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EUITreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.service.ItemCatService;

/**
 * 商品分类管理Service层
 * @author dell
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EUITreeNode> getCatList(long parendId) {
		//根据parendId 创建查询条件
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parendId);
		criteria.andStatusEqualTo(1);//1表示正常 2表示删除--逻辑删除
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		List<EUITreeNode> treeNodeList = new ArrayList<EUITreeNode>();
		for(TbItemCat itemCat:list){
			EUITreeNode node = new EUITreeNode();
			node.setId(itemCat.getId());
			node.setText(itemCat.getName());
			node.setState(itemCat.getIsParent()?"closed":"open");
			treeNodeList.add(node);
		}
		return treeNodeList;
	}

}
