package com.taotao.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbCartMapper;
import com.taotao.order.service.CartService;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;
import com.taotao.pojo.TbCartExample;
import com.taotao.pojo.TbCartExample.Criteria;
import com.taotao.pojo.TbItem;

/**
 * 购物车service
 * 
 * @author cs
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Value("${ITEM_BASEINFO_URL}")
	private String ITEM_BASEINFO_URL;
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;

	@Autowired
	private TbCartMapper cartMapper;

	/**
	 * 添加购物车商品 其中1表示增加 2表示直接就是数量
	 */
	@Override
	public TaotaoResult addCartItem(Long itemId, Integer num,
			CartInfo cartInfo, Integer type) {
		TbCart cartItem = null;
		// 获取cookies中的信息
		List<TbCart> cartItemList = cartInfo.getCookieCartItemList();
		if (cartItemList != null) {
			// 判断商品列表中是否已经存在此商品
			for (TbCart item : cartItemList) {
				if (item.getProductId().equals(itemId)) {
					cartItem = item;
					if (type == 1)
						cartItem.setNum(item.getNum() + num);
					else if (type == 2)
						cartItem.setNum(num);
					break;
				}
			}
		} else {
			cartItemList = new ArrayList<TbCart>();
		}

		if (cartItem == null) {
			cartItem = new TbCart();
			// 根据商品id查询商品基本信息
			String json = HttpClientUtil.doGet(REST_BASE_URL
					+ ITEM_BASEINFO_URL + itemId);
			// 转换为java对象
			TaotaoResult result = TaotaoResult.formatToPojo(json, TbItem.class);

			if (result.getStatus() == 200) {
				TbItem item = (TbItem) result.getData();
				if (item != null) {
					// 转换为精简的pojo
					cartItem.setProductId(item.getId());
					cartItem.setTitle(item.getTitle());
					cartItem.setPrice(item.getPrice());
					cartItem.setImage(item.getImage() == null ? "" : item
							.getImage().split(",")[0]);
					cartItem.setNum(num);
					cartItemList.add(cartItem);
				}
			}
		}
		cartInfo.setCookieCartItemList(cartItemList);
		// 数据进行落地
		if(cartInfo.getUser() != null){
			if(cartItem.getId() != null){
				TbCartExample example = new TbCartExample();
				Criteria criteria = example.createCriteria();
				criteria.andIdEqualTo(cartItem.getId());
				cartMapper.updateByExampleSelective(cartItem, example);
			}else{
				//进行插入
				cartItem.setUserId(cartInfo.getUser().getId());
				cartMapper.insert(cartItem);
			}
		}

		return TaotaoResult.ok(cartInfo);
	}

	/**
	 * 删除cookie中购物车中的商品
	 */
	@Override
	public TaotaoResult deleteCartItem(Long itemId, CartInfo cartInfo) {
		// 取购物车商品列表
		if (cartInfo.getUser() != null) {
			// 删除productId
			TbCartExample example = new TbCartExample();
			Criteria criteria = example.createCriteria();
			criteria.andProductIdEqualTo(itemId);
			cartMapper.deleteByExample(example);
		} else {
			List<TbCart> cartItemlist = cartInfo.getCookieCartItemList();
			// 判断商品列表中是否已经存在此商品
			for (TbCart item : cartItemlist) {
				if (item.getId() == itemId) {
					cartItemlist.remove(item);
					break;
				}
			}
			cartInfo.setCookieCartItemList(cartItemlist);
		}
		return TaotaoResult.ok(cartInfo);
	}

	@Override
	public TaotaoResult getCartItemList(CartInfo cartInfo) {
		List<TbCart> list = null;
		if (cartInfo.getUser() == null) {
			list = cartInfo.getCookieCartItemList();
		} else {
			TbCartExample example = new TbCartExample();
			Criteria criteria = example.createCriteria();
			criteria.andUserIdEqualTo(cartInfo.getUser().getId());
			list = cartMapper.selectByExample(example);
		}
		return TaotaoResult.ok(list);
	}

	@Override
	public TaotaoResult deleteCartItemByOrder(Long userId, List<TbCart> itemList) {
		//根据订单号删除购物车的商品
		if(userId == null){
			return TaotaoResult.build(400, "未登录不能删除");
		}
		if(itemList != null && itemList.size() > 0){
			for(int i=0;i<itemList.size();i++){
				TbCartExample example = new TbCartExample();
				Criteria criteria = example.createCriteria();
				criteria.andUserIdEqualTo(userId);
				criteria.andIdEqualTo(itemList.get(i).getId());
				cartMapper.deleteByExample(example);
			}
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400, "无删除信息或信息不匹配");
	}

}
