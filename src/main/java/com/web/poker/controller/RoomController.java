package com.web.poker.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.poker.common.constants.Constants;
import com.web.poker.common.constants.MsgCode;
import com.web.poker.common.utils.Exec;
import com.web.poker.common.utils.StringUtils;
import com.web.poker.common.utils.UserInfoUtil;
import com.web.poker.model.Poker;
import com.web.poker.result.BaseResult;

@Controller
public class RoomController {
	
	static final Logger logger = LoggerFactory.getLogger(RoomController.class);
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	HttpServletResponse response;
	
	private ConcurrentHashMap<String, List<String>> roomMap = new ConcurrentHashMap<String,List<String>>();
	
	/**
	 * 异步获取房间号
	 * @return
	 */
	@RequestMapping("/ajaxGetRoomCode")
	@ResponseBody
	public Map<String, String> ajaxGetRoomCode(){
		Map<String, String> result = new HashMap<String, String>();
		String roomCode = "";
		List<String> list = new ArrayList<String>();
		
		String token = (String) request.getSession().getAttribute(Constants.SESSION_ATRR_KEY);
		Poker poker = UserInfoUtil.getUser(token);
		if(null == poker){
			result.put("msg", "登录失效");
			return result;	
		}
		list.add(poker.getKey());
		
		if(roomMap.isEmpty()){//当没有房间的时候随机创建一个
			roomCode = ((int) ((Math.random()*9+1)*100000))+"";
		}else{//如果存在房间则需要判断房间号是否重复
			boolean flag = true;//是否找到房间号
			while(flag){
				roomCode = ((int) ((Math.random()*9+1)*100000))+"";
				Enumeration<String> enu = roomMap.keys();//取map中所有房间号
				while (enu.hasMoreElements()) {
					String rooms = (String) enu.nextElement();
					if(roomCode.equals(rooms)){//如果房间号存在则跳出循环
						flag = true;
						break;
					}else{
						flag = false;
					}
				}
			}
		}
		roomMap.put(roomCode, list);
		poker.setRoomCode(roomCode);
		try {
			UserInfoUtil.setUser(token, poker);
		} catch (Exception e) {
			//Socket read timed out
			if(e.getMessage().contains("Socket read timed out") || e.getMessage().contains("timed out") ||
					e.getMessage().contains("Socket") ||
					e.getMessage().contains("Connection") || e.getMessage().contains("IO")){
				result.put("msg", "获取房间号失败，网络异常!请稍后再试!谢谢!");
			}else{
				result.put("msg", "获取房间号失败!"+e.getMessage());
			}
			return result;	
		}
		result.put("roomCode", roomCode);
		return result;
	}
	
	/**
	 * 异步进入房间
	 * @param roomCode
	 * @return
	 */
	@RequestMapping("/putRoomByCode")
	@ResponseBody
	public Map<String, String> putRoomByCode(String roomCode){
		Map<String, String> result = new HashMap<String, String>();
		boolean flag = false;//是否找到房间号
		Enumeration<String> enu = roomMap.keys();//取map中所有房间号
		while (enu.hasMoreElements()) {
			String rooms = (String) enu.nextElement();
			if(roomCode.equals(rooms)){//如果房间号存在则跳出循环
				flag = true;
				break;
			}else{
				flag = false;
			}
		}
		
		if(flag){//找到房间号，则进入房间并跳转连接接页面
			String token = (String) request.getSession().getAttribute(Constants.SESSION_ATRR_KEY);
			Poker poker = UserInfoUtil.getUser(token);
			if(null == poker){
				result.put("msg", "登录失效");
				return result;	
			}
			List<String> list = roomMap.get(roomCode);
			list.add(poker.getKey());//玩家key存入房间玩家列表
			roomMap.put(roomCode, list);//玩家列表存入房间
			poker.setRoomCode(roomCode);//房间号存入玩家属性
			try {
				UserInfoUtil.setUser(token, poker);
			} catch (Exception e) {
				//Socket read timed out
				if(e.getMessage().contains("Socket read timed out") || e.getMessage().contains("timed out") ||
						e.getMessage().contains("Socket") ||
						e.getMessage().contains("Connection") || e.getMessage().contains("IO")){
					result.put("msg", "进入失败，网络异常!请稍后再试!谢谢!");
				}else{
					result.put("msg", "进入房间失败!"+e.getMessage());
				}
				return result;	
			}
			result.put("roomCode", roomCode);
			return result;	
		}else{//否则回到输房间号页面
			result.put("msg", "房间号不正确");
			return result;	
		}
		
	}
	
	
	/**
	 * 向房间里面的人发牌
	 * @param roomCode
	 * @return
	 */
	@RequestMapping("/playFull")
	@ResponseBody
	public BaseResult playFull(String roomCode){
		return doPlayFull(roomCode);
	}
	
	public BaseResult doPlayFull(String roomCode){
		//房间里面连接人sid列表
		List<String> sidList = querySidByRoomCode(roomCode);
		
		List<String> nameSidList = queryNameAndSidByRoomCode(roomCode);
		
		//发好的牌列表
		Exec exec = new Exec();//默认54张牌，留三张底牌，三人
		List<String> pokList = exec.getList();
		
		try {
			StringBuffer sTemp = new StringBuffer();
			for (String nameSid : nameSidList) {
				sTemp.append(nameSid + ";");
			}
			toRoomSendMsg(roomCode, "@100:" + sTemp.toString());
			for (int i = 0; i < 3; i++) {
				toSendMsg(sidList.get(i), "@100pokers-" + pokList.get(i));
			}
			toRoomSendMsg(roomCode, "@100pokersUn-" + pokList.get(3));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new BaseResult("9999", "失败");
		}
		return new BaseResult("0000", "成功");	
	}
	
	
	 /**
     * 查询所有在线人员的sid
     * @param modelMap
     * @param session
     * @return
     */
    @RequestMapping("/getAllOnline")
    @ResponseBody
    public List<String> getAllOnline(ModelMap modelMap,HttpSession session){
        List<String> ids=new ArrayList<String>();
        
        Enumeration<String> enu = EndPointServer.sessionMap.keys();
		while (enu.hasMoreElements()) {//遍历所有连接
			String key = (String) enu.nextElement();
			ids.add(key);//key是连接对象的电话
			//EndPointServer.sessionMap.get(key);
		}
        
        return ids;
    }

    /**
     * 向用户发送消息
     * @param modelMap
     * @param session
     * @param sid
     * @param msg
     * @return
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/toSendMsg")
    public BaseResult toSendMsg(String sid,String msg) throws IOException{
    	sendMsgBySid(sid, msg);
        return new BaseResult("0000","成功");
    }
    
    public void sendMsgBySid(String sid,String msg) throws IOException{
    	 if (StringUtils.isNotEmpty(sid)) {//sid不为空，则指定用户发送消息
     		EndPointServer.sessionMap.get(sid).sendMessage(msg);
         }else {
         	Enumeration<String> enu = EndPointServer.sessionMap.keys();
     		while (enu.hasMoreElements()) {//遍历所有连接
     			String key = (String) enu.nextElement();
     			EndPointServer.sessionMap.get(key).sendMessage(msg);
     		}
         }
    }
    
    
    /**
     * 向房间发送消息
     * @param modelMap
     * @param session
     * @param sid
     * @param msg
     * @return
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/toRoomSendMsg")
    public BaseResult toRoomSendMsg(String roomCode,String msg) throws IOException{
    	sendMsgByRoom(roomCode, msg);
        return new BaseResult("0000","发送成功");
    }
    
    
    public void sendMsgByRoom(String roomCode,String msg) throws IOException{
    	if (StringUtils.isNotEmpty(roomCode)) {//roomCode不为空，则指定房间发送消息
        	List<String> list = roomMap.get(roomCode);//取房间中的用户列表
    		for (String key : list) {
    			EndPointServer.sessionMap.get(key).sendMessage(msg);
			}
        }else {
        	Enumeration<String> enu = EndPointServer.sessionMap.keys();
    		while (enu.hasMoreElements()) {//遍历所有连接
    			String key = (String) enu.nextElement();
    			EndPointServer.sessionMap.get(key).sendMessage(msg);
    		}
        }
    }
    
    @RequestMapping("/toReady")
    @ResponseBody
    public BaseResult toReady(){
    	String token = (String) request.getSession().getAttribute(Constants.SESSION_ATRR_KEY);
		Poker poker = UserInfoUtil.getUser(token);
		poker.setReadyState(true);
		try {
			UserInfoUtil.setUser(token, poker);
		} catch (Exception e) {
			//Socket read timed out
			logger.error(e.getMessage());
		}
		
		String roomCode = poker.getRoomCode();
		if(isAllReady(roomCode)){//传入房间号判断是否全部准备好了，如果是，则直接往房间发牌
			doPlayFull(roomCode);
		}
    	return new BaseResult("0000", "成功");	
    }
    
    public boolean isAllReady(String roomCode){
    	int i = 0;
    	if (StringUtils.isNotEmpty(roomCode)) {//roomCode不为空
        	List<String> list = roomMap.get(roomCode);//取房间中的用户列表
    		for (String key : list) {
    			String token = UserInfoUtil.getTokenByUserId(key);
    			Poker poker = UserInfoUtil.getUser(token);
    			if(poker.getReadyState()){
    				i++;
    			}
			}
        }
		if(i >= 3){//当三个人都准备好了则返回true
			return true;
		}else{
			return false;
		}
    }
    
    
    public List<String> querySidByRoomCode(String roomCode){
    	List<String> list = roomMap.get(roomCode);//取房间中的用户列表
		return list;
    }
    
    
    /**
     * 
     * @param roomCode
     * 根据房间号获取房间里面连接人的昵称和id
     * @return
     */
    public List<String> queryNameAndSidByRoomCode(String roomCode){
    	List<String> result = new ArrayList<String>();
    	List<String> list = roomMap.get(roomCode);//取房间中的用户列表
    	for (String key : list) {
			String token = UserInfoUtil.getTokenByUserId(key);
			Poker poker = UserInfoUtil.getUser(token);
			String nameAndSid = poker.getNick()	+ "," + poker.getTelNum();
			result.add(nameAndSid);
		}
    	
		return result;
    }

    /**
     * 广播发送消息
     * @param modelMap
     * @param session
     * @param msg
     * @return
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/toBroadCastMsg")
    public ModelMap toBroadCastMsg(ModelMap modelMap,HttpSession session,String msg) throws IOException{
        if (msg != null && msg.length() > 0) {
        	Enumeration<String> enu = EndPointServer.sessionMap.keys();
    		while (enu.hasMoreElements()) {//遍历所有连接
    			String key = (String) enu.nextElement();
    			EndPointServer.sessionMap.get(key).sendMessage(msg);
    		}
        }else {
            modelMap.put("msg", -1);//参数错误
        }

        return modelMap;
    }
}
