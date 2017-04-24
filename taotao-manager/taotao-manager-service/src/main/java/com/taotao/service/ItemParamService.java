package com.taotao.service;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemParam;


public interface ItemParamService {

	EUIDateGridResult getItemParamList(Integer page, Integer rows);

	TaotaoResult getItemParamByCid(Long itemCatId);

	TaotaoResult itemParamSave(TbItemParam itemParam);


}
