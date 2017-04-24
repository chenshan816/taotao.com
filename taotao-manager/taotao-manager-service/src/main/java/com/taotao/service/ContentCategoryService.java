package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

public interface ContentCategoryService {
	
	List<EUITreeNode> getContentCategoryList(long parentId);
	
	TaotaoResult createContentCategory(long parentId,String name);
	
	TaotaoResult deleteContentCategory(long id);
	
	TaotaoResult updateContentCategory(long id,String name);
}
