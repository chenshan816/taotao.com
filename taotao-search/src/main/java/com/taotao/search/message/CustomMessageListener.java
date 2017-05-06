package com.taotao.search.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.search.service.ItemService;

/**
 * 商品修改的处理类
 * @author cs
 *
 */
public class CustomMessageListener implements MessageListener{

	@Autowired
	private ItemService itemService;
	
	@Override
	public void onMessage(Message message) {
		ActiveMQTextMessage am = (ActiveMQTextMessage) message;
		try {
			//System.out.println(am.getText());
			//调用处理
			itemService.importItem(Long.parseLong(am.getText()));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
