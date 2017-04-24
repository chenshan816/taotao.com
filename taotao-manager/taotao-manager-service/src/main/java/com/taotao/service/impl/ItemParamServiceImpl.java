package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamExample;
import com.taotao.pojo.TbItemParamExample.Criteria;
import com.taotao.service.ItemParamService;

@Service
public class ItemParamServiceImpl implements ItemParamService {
	@Autowired
	private TbItemParamMapper itemParamMapper;

	@Override
	public EUIDateGridResult getItemParamList(Integer page, Integer rows) {
		//执行查询，并分页
		TbItemParamExample example = new TbItemParamExample();
		PageHelper.startPage(page, rows);//，默认查询总页数
		List<TbItemParam> list = itemParamMapper.selectByExampleWithBLOBs(example);
		//获取分页信息
		PageInfo<TbItemParam> pageInfo = new PageInfo<TbItemParam>(list);
		long total = pageInfo.getTotal();
		return new EUIDateGridResult(total, list);
	}
	/**
	 * 获取规格模版
	 */
	@Override
	public TaotaoResult getItemParamByCid(Long itemCatId) {
		TbItemParamExample example = new TbItemParamExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemCatIdEqualTo(itemCatId);
		List<TbItemParam> list = itemParamMapper.selectByExampleWithBLOBs(example);
		if(list != null && list.size() >0)
			return TaotaoResult.ok(list.get(0));
		return TaotaoResult.ok();
	}
	/**
	 * 保存商品规格模版
	 */
	@Override
	public TaotaoResult itemParamSave(TbItemParam itemParam) {
		//补全itemParam
		itemParam.setCreated(new Date());
		itemParam.setUpdated(new Date());
		itemParamMapper.insert(itemParam);
		return TaotaoResult.ok();
	}
	
}
