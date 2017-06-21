package com.taotao.order.websocket.socket;  
  
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
  
public class SystemWebSocketHandler implements WebSocketHandler {  
  
        private static Map<String,WebSocketSession> users = new HashMap<String, WebSocketSession>();  
           
        @Override  
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {  
        }  
       
        @Override  
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {  
              String orderId  =   (String) message.getPayload();
              if(users.containsKey(orderId)){
            	  sendMsg(new TextMessage("编号为"+orderId+"订单已失效，请重新购买！"),session);
              }else{
            	  users.put(orderId, session);
              }
        }  
        
        private static void sendMsg(TextMessage msg,WebSocketSession session){
        	try {
        		System.out.println(msg);
        		session.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        //发送数据
        public static void sendMsg(TextMessage msg,String orderId){
        	if(users.containsKey(orderId)){
        		sendMsg(msg, users.get(orderId));
        	}
        }
       
        @Override  
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {  
            if(session.isOpen()){  
                session.close();  
            }  
        }  
       
          
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {  
                
        }  
       
        @Override  
        public boolean supportsPartialMessages() {  
            return false;  
        }  
        
}  