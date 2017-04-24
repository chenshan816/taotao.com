package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.service.ContentService;
import com.taotao.service.RedisCleanService;

/**
 * 内容管理Service
 * @author cs
 *
 */
@Service
public class ContentServiceImpl implements ContentService{
	
	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisCleanService redisCleanService;
	

	@Override
	public EUIDateGridResult getContentListByCategoryId(int page, int rows,
			long categoryId) {
		//根据内容分类进行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//分页查询
		PageHelper.startPage(page, rows);//默认查询总页数
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example );
		PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(list);
		long total = pageInfo.getTotal();
		return new EUIDateGridResult(total, list);
	}

	@Override
	public TaotaoResult saveContent(TbContent content) {
		//将内容补全
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		//调用rest服务层，清空缓存
		redisCleanService.cleanRedisTocontent(content.getCategoryId()+"");
		return TaotaoResult.ok();
	}

	

	@Override
	public TaotaoResult updateContent(TbContent content) {
		content.setUpdated(new Date());
		contentMapper.updateByPrimaryKeySelective(content);
		//调用rest服务层，清空缓存
		redisCleanService.cleanRedisTocontent(content.getCategoryId()+"");
		return TaotaoResult.ok();
	}
	
	
}
