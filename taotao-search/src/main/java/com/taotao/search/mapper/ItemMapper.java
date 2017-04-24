package com.taotao.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taotao.common.pojo.Item;


public interface ItemMapper {
	
	List<Item> getItemList(); 
	
	Item getItemById(@Param("item_id") long itemId);
}
