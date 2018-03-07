package com.web.poker.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.poker.common.constants.Constants;
import com.web.poker.common.constants.MsgCode;
import com.web.poker.common.utils.StringUtils;
import com.web.poker.common.utils.TokenUtils;
import com.web.poker.common.utils.UserInfoUtil;
import com.web.poker.model.Poker;
import com.web.poker.result.BaseResult;



@Controller
public class LoginController {
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	HttpServletResponse response;
	
	static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@RequestMapping("/toLogin")
	@ResponseBody
	public BaseResult toLogin(String userName, String telNum){
		BaseResult result = new BaseResult();
		try{
			HttpSession httpSession = request.getSession();
			
			//一个手机号码只能登陆一次
			String oldToken  = UserInfoUtil.getTokenByUserId(telNum);
			if(StringUtils.isNotBlank(oldToken)){//如果有过登录，注销老token
				UserInfoUtil.logout(oldToken);
			}
			Poker poker = new Poker();
			poker.setKey(telNum);
			poker.setTelNum(telNum);
			poker.setNick(userName);
			
			//根据手机号生成token值
			String token = TokenUtils.getInstance().generateToken(telNum);
			UserInfoUtil.setUser(token, poker);//把用户对象缓存到Redis中
			
			//session中存入token值
			httpSession.setAttribute(Constants.SESSION_ATRR_KEY, token);
			result.setMsgCode(MsgCode.msgCode_0000);
			result.setMsg(userName);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage()+"---cause:"+e.getCause());
			result.setMsgCode(MsgCode.msgCode_9999);
			//Socket read timed out
			if(e.getMessage().contains("Socket read timed out") || e.getMessage().contains("timed out") ||
					e.getMessage().contains("Socket") ||
					e.getMessage().contains("Connection") || e.getMessage().contains("IO")){
				result.setMsg("登录失败，网络异常!请稍后再试!谢谢!");
			}else{
				result.setMsg("登录失败!"+e.getMessage());
			}
			return result;
		}
	}
	
	@RequestMapping("/queryUserName")
	@ResponseBody
	public String queryUserName(){
		String token = (String) request.getSession().getAttribute(Constants.SESSION_ATRR_KEY);
		return StringUtils.isEmpty(token) ? "" : token;
	}
	
	@RequestMapping("/queryRoomCode")
	@ResponseBody
	public String queryRoomCode(){
		String token = (String) request.getSession().getAttribute(Constants.SESSION_ATRR_KEY);
		Poker poker = UserInfoUtil.getUser(token);
		return StringUtils.isEmpty(poker.getRoomCode()) ? "" : poker.getRoomCode();
	}

}
