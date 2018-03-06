package com.web.poker.model;

import java.util.List;

/***
 * Poker对象
 * token : 身份确认唯一标识
 * nick : 昵称
 * telnum : 手机号
 * roomCode : 房间号
 * readyState : 准备标识
 * pokerList : 手牌列表
 * isMain : 地主权重标识
 * afterP : 上一个人
 * beforeP : 下一个人
 * @author bbg
 *
 */


public class Poker {
	// 身份确认唯一标识
	private String key;
	// 昵称
	private String nick;
	// 手机号
	private String telnum; 
	// 房间号
	private String roomCode;
	// 准备标识
	private String readyState;
	// 手牌列表
	private List<String> pokerList;
	// 地主权重标识
	private String isMain;
	// 上一个人
	private String afterP;
	// 下一个人
	private String beforeP;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getTelnum() {
		return telnum;
	}
	public void setTelnum(String telnum) {
		this.telnum = telnum;
	}
	public String getRoomCode() {
		return roomCode;
	}
	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}
	public String getReadyState() {
		return readyState;
	}
	public void setReadyState(String readyState) {
		this.readyState = readyState;
	}
	public List<String> getPokerList() {
		return pokerList;
	}
	public void setPokerList(List<String> pokerList) {
		this.pokerList = pokerList;
	}
	public String getIsMain() {
		return isMain;
	}
	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}
	public String getAfterP() {
		return afterP;
	}
	public void setAfterP(String afterP) {
		this.afterP = afterP;
	}
	public String getBeforeP() {
		return beforeP;
	}
	public void setBeforeP(String beforeP) {
		this.beforeP = beforeP;
	}
	
}
