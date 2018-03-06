package com.web.poker.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class LoginController {
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	HttpServletResponse response;
	
	
	@RequestMapping("/toLogin")
	@ResponseBody
	public String toLogin(String userId){
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute("webSocketSessionId", userId);
		return userId;
	}
	
	@RequestMapping("/queryUserName")
	@ResponseBody
	public String queryUserName(){
		String userId = (String) request.getSession().getAttribute("webSocketSessionId");
		return StringUtils.isEmpty(userId) ? "" : userId;
	}
	
	@RequestMapping("/queryRoomCode")
	@ResponseBody
	public String queryRoomCode(){
		String roomCode = (String) request.getSession().getAttribute("roomCode");
		return roomCode;
	}
	
	@RequestMapping("/toWebsocket")
	public String toWebsocket(String userId){
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute("webSocketSessionId", userId);
		return "login";
	}


}
