package com.taotao.service;

import com.taotao.common.pojo.EUIDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {
	
	EUIDateGridResult getContentListByCategoryId(int page,int rows,long categoryId);

	TaotaoResult saveContent(TbContent content);
	
	TaotaoResult updateContent(TbContent content);
}
