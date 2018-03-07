package com.web.poker.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.web.poker.common.Global;
import com.web.poker.model.Poker;
import com.web.poker.model.TokenRefreshRate;



/**
 * 用户信息工具
 * 
 * @author zqw
 * @version 1.0 date : 2015/10/13
 */

public class UserInfoUtil {
	
	public static final int KEY_TIMEOUT = Integer.parseInt(Global.getConfig("token.expiration")  == null ? "7200" : Global.getConfig("token.expiration"));
	
	
	/**
	 * 从redis缓存中获取用户id
	 * @param key
	 * @return
	 */
	public static String getKey(String key) {
		Poker user = getUser(key);
		if (user != null)
			return user.getKey();
		else
			return "";
	}
	

	
	/**
	 * 从redis缓存中获取用户
	 * @param key
	 * @return
	 */
	public static Poker getUser(String key) {
		if(StringUtils.isNotBlank(key)){
			return (Poker) JedisUtils.getObject(key);
		}
		return null;
	}


	
	
	/**
	 * redis 缓存用户对象
	 * @param key
	 * @param user
	 */
	public static void setUser(String key ,Poker user) throws Exception{
		String r = JedisUtils.setObject(key, user ,KEY_TIMEOUT);
		if(r == null){
			throw new Exception("缓存失败，请检查redis相关.");
		}
	}
	
	/**
	 * redis 缓存用户和token对应
	 * @param key
	 * @param user
	 */
	public static void setTokenByUserId(String userid ,String token) throws Exception{
		String r = JedisUtils.set(userid, token, KEY_TIMEOUT);
		if(r == null){
			throw new Exception("缓存失败，请检查redis相关.");
		}
	}
	
	public static String getTokenByUserId(String userid) {
		return JedisUtils.get(String.valueOf(userid));
	}
	
	
	public static void logout(String key) {
		if(StringUtils.isNotBlank(key) && JedisUtils.exists(key)){
			Poker cs = getUser(key);
			if(cs != null && StringUtils.isNotBlank(cs.getKey())){
				JedisUtils.del(cs.getKey());
			}
			JedisUtils.del(key);
		}
	}
	
	
	public static void setFrequency(Long userid , TokenRefreshRate trr){
		JedisUtils.setObject(String.valueOf(userid), trr, KEY_TIMEOUT);
	}
	
	public static TokenRefreshRate getFrequency(Long userid){
		try {
			return (TokenRefreshRate)JedisUtils.getObject(String.valueOf(userid));
		} catch (Exception e) {
			return null;
		}
	}


	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}
}
