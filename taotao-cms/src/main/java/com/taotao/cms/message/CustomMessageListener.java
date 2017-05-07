package com.taotao.cms.message;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.cms.pojo.ItemInfo;
import com.taotao.cms.service.ItemService;
import com.taotao.cms.service.StaticPageService;
import com.taotao.common.pojo.TaotaoResult;



/**
 * 商品修改的处理类
 * @author cs
 *
 */
public class CustomMessageListener implements MessageListener{
	
	@Autowired
	private StaticPageService staticPageService;
	@Autowired
	private ItemService itemService;
	
	@Override
	public void onMessage(Message message) {
		ActiveMQTextMessage am = (ActiveMQTextMessage) message;
		try{
			System.out.println("cms:"+am.getText());
			String id = am.getText();
			//准备数据
			Map<String,Object> root = new HashMap<String, Object>();
			//获取基本信息
			TaotaoResult result = itemService.getItemBaseInfo(Long.parseLong(id));
			ItemInfo itemBaseInfo =  (ItemInfo) result.getData();
			//获取参数信息
			TaotaoResult itemParam = itemService.getItemParam(Long.parseLong(id));
			root.put("keyword", "衣服");
			root.put("itemParam", itemParam);
			root.put("item", itemBaseInfo);
			//静态化
			staticPageService.itemStaticPage(root, id);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
