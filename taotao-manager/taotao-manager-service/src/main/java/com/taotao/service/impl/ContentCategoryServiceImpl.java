package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.service.ContentCategoryService;
import com.taotao.service.RedisCleanService;

/**
 * 内容分类管理
 * @author cs
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Autowired
	private RedisCleanService redisCleanService;
	
	@Override
	public List<EUITreeNode> getContentCategoryList(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EUITreeNode> resultList = new ArrayList<EUITreeNode>();
		for(TbContentCategory contentCategory:list){
			//创建节点
			EUITreeNode node = new EUITreeNode();
			node.setId(contentCategory.getId());
			node.setText(contentCategory.getName());
			node.setState(contentCategory.getIsParent()?"closed":"open");
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult createContentCategory(long parentId, String name) {
		//补全对象
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		contentCategory.setParentId(parentId);
		contentCategory.setIsParent(false);
		contentCategory.setStatus(1);
		contentCategory.setSortOrder(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		long id = contentCategoryMapper.insert(contentCategory);
		contentCategory.setId(id);
		//查看父节点的isParent是否为true
		TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parentCat.getIsParent()){
			parentCat.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult deleteContentCategory(long id) {
		//获取父节点
		TbContentCategory currentNode = contentCategoryMapper.selectByPrimaryKey(id);
		long parentId = currentNode.getParentId();
		//删除该节点
		contentCategoryMapper.deleteByPrimaryKey(id);
		//寻找该节点下是否是节点，如果有则全部删除
		deleteSubNode(id);
		//判断父节点是否仍然是parentId
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> nodes = contentCategoryMapper.selectByExample(example);
		if(nodes == null || nodes.size() <=0){
			//更新
			TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
			parentCat.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		redisCleanService.cleanRedisTocontent(id+"");
		return TaotaoResult.ok();
	}

	private void deleteSubNode(long id) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		List<TbContentCategory> nodes = contentCategoryMapper.selectByExample(example);
		if(nodes != null && nodes.size()>0){
			for(TbContentCategory item:nodes){
				contentCategoryMapper.deleteByPrimaryKey(item.getId());
				deleteSubNode(item.getId());
			}
		}
	}

	@Override
	public TaotaoResult updateContentCategory(long id,String name) {
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setId(id);
		contentCategory.setName(name);
		contentCategory.setUpdated(new Date());
		contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		return TaotaoResult.ok();
	}

}
