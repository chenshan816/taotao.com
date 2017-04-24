package com.taotao.portal.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.Utils.HttpClientUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.portal.pojo.ItemInfo;
import com.taotao.portal.service.ItemService;

/**
 * 淘淘商城展示页面中商品详细界面的service
 * @author cs
 *
 */
@Service
public class ItemServiceImpl implements ItemService {
	
	@Value("${ITEM_BASEINFO_URL}")
	private String ITEM_BASEINFO_URL;
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITEM_ITEMDESC_URL}")
	private String ITEM_ITEMDESC_URL;
	@Value("${ITEM_ITEMPARAM_URL}")
	private String ITEM_ITEMPARAM_URL;
	
	@Override
	public ItemInfo getItemById(long itemId) {
		//调用rest的服务层商品基本信息的展示
		try{
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_BASEINFO_URL+itemId);
			if(!StringUtils.isBlank(json)){
				TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, ItemInfo.class);
				if(taotaoResult.getStatus() == 200){
					ItemInfo itemInfo = (ItemInfo) taotaoResult.getData();
					return itemInfo;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getItemDescById(long itemId) {
		try{
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_ITEMDESC_URL+itemId);
			if(!StringUtils.isBlank(json)){
				TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemDesc.class);
				if(taotaoResult.getStatus() == 200){
					TbItemDesc itemDesc = (TbItemDesc) taotaoResult.getData();
					//取出商品描述信息
					return itemDesc.getItemDesc();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 根据商品id查找规格参数，转换为html的字符串
	 */
	@Override
	public String getItemParamById(long itemId) {
		try{
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_ITEMPARAM_URL+itemId);
			if(!StringUtils.isBlank(json)){
				TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemParamItem.class);
				if(taotaoResult.getStatus() == 200){
					TbItemParamItem itemParam = (TbItemParamItem) taotaoResult.getData();
					//将java对象转换为HTML
					String paramData = itemParam.getParamData();
					//生成html
					// 把规格参数json数据转换成java对象
					List<Map> jsonList = JsonUtils.jsonToList(paramData, Map.class);
					StringBuffer sb = new StringBuffer();
					sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\">\n");
					sb.append("    <tbody>\n");
					for(Map m1:jsonList) {
						sb.append("        <tr>\n");
						sb.append("            <th class=\"tdTitle\" colspan=\"2\">"+m1.get("group")+"</th>\n");
						sb.append("        </tr>\n");
						List<Map> list2 = (List<Map>) m1.get("params");
						for(Map m2:list2) {
							sb.append("        <tr>\n");
							sb.append("            <td class=\"tdTitle\">"+m2.get("k")+"</td>\n");
							sb.append("            <td>"+m2.get("v")+"</td>\n");
							sb.append("        </tr>\n");
						}
					}
					sb.append("    </tbody>\n");
					sb.append("</table>");
					//返回html片段
					return sb.toString();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
