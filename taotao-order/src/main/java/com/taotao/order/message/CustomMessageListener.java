package com.taotao.order.message;

import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.mapper.TbCartMapper;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;

/**
 * 登录之后进行购物车和cookie之间的调整
 * @author dell
 *
 */
public class CustomMessageListener implements MessageListener{

	@Autowired
	private TbCartMapper cartMapper;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ActiveMQObjectMessage am = (ActiveMQObjectMessage) message;
		try{
			CartInfo cartInfo = (CartInfo) am.getObject();
			System.out.println("order:"+cartInfo.getUser().getUsername());
			//进行处理
			putCookieIntoDB(cartInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void putCookieIntoDB(CartInfo cartInfo) {
		if(cartInfo.getUser() == null){
			System.out.println("无用户信息");
			return;
		}
		List<TbCart> cartItemList = cartInfo.getCookieCartItemList();
		if(cartItemList == null || cartItemList.size() == 0)
			return;
		for(int i=0;i<cartItemList.size();i++){
			TbCart cartItem = cartItemList.get(i);
			cartItem.setUserId(cartInfo.getUser().getId());
			cartMapper.insert(cartItem);
		}
	}

}
