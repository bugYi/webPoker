package com.web.poker.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.web.poker.common.constants.Constants;
import com.web.poker.common.utils.UserInfoUtil;
import com.web.poker.configurator.GetHttpSessionConfigurator;
import com.web.poker.model.Poker;


@ServerEndpoint(value="/websocket",configurator=GetHttpSessionConfigurator.class)
public class EndPointServer {

    //public static  List<EndPointServer>  sessionList= new ArrayList<EndPointServer>();

	public static ConcurrentHashMap<String, EndPointServer> sessionMap = new ConcurrentHashMap<String, EndPointServer>();
	
	public String key;
    public Session  session ;
    public HttpSession httpSession;

     @OnOpen
     public void onOpen(Session session,EndpointConfig config) throws IOException {
         HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
         String token = (String) httpSession.getAttribute(Constants.SESSION_ATRR_KEY);
		 Poker poker = UserInfoUtil.getUser(token);
		 if(null == poker){
			//用户未登陆
             try {
                 session.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
		 }else{
			 key = poker.getKey();
        	 poker.setSessionId(session.getId());
        	 try {
				UserInfoUtil.setUser(token, poker);
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 this.session = session;
             this.httpSession = httpSession;
             sessionMap.put(key, this);
         }
    }

     @OnMessage
     public void onMessage(Session session,String message) throws IOException, InterruptedException {
    	 String token = (String) httpSession.getAttribute(Constants.SESSION_ATRR_KEY);
		 Poker poker = UserInfoUtil.getUser(token);
    	 System.out.println(poker.getNick()+"消息："+message);
    	 if("@heart".equals(message)){
 			return;
 		 }
    	 //当前房间号
    	 String roomCode = poker.getRoomCode();
    	 
    	 Enumeration<String> enu = sessionMap.keys();
 		 while (enu.hasMoreElements()) {//遍历所有连接
 			String key = (String) enu.nextElement();
 			String roomCodeTemp = (String)sessionMap.get(key).httpSession.getAttribute("roomCode");
 			if(roomCodeTemp.equals(roomCode)){//根据当前发消息人的房间号，发送对应的消息到对应的房间中
 				sessionMap.get(key).sendMessage(poker.getNick()+":"+message);
 			}			
 		}
     }

     @OnError
     public void onError(Throwable t) {
         //以下代码省略...
    	 sessionMap.remove(key);
    	 String token = (String) httpSession.getAttribute(Constants.SESSION_ATRR_KEY);
		 Poker poker = UserInfoUtil.getUser(token);
		 poker.setReadyState(false);
		 try {
			UserInfoUtil.setUser(token, poker);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
         System.err.println("出错了");
     }

     @OnClose
     public void onClose(Session session, CloseReason reason) {
         //以下代码省略...
    	 sessionMap.remove(key);
    	 String token = (String) httpSession.getAttribute(Constants.SESSION_ATRR_KEY);
		 Poker poker = UserInfoUtil.getUser(token);
		 poker.setReadyState(false);
		 try {
			UserInfoUtil.setUser(token, poker);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
         System.out.println(session.getId()+"退出链接");
     } 

     
     /**
      * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
      * @param message
      * @throws IOException
      */
     public void sendMessage(String message) throws IOException{
         this.session.getBasicRemote().sendText(message);
         //this.session.getAsyncRemote().sendText(message);
     }

}
