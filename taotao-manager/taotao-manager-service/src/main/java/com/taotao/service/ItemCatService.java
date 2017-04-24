package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EUITreeNode;

public interface ItemCatService {
		
	List<EUITreeNode> getCatList(long parentId);
}
