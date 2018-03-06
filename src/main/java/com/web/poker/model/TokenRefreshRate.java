package com.web.poker.model;

import java.io.Serializable;


/**
 * token刷新频率
 * @author bbg
 *
 */
public class TokenRefreshRate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6588943204119853973L;
	
	private Long userid;
	private Integer frequency = 0;
	private Long lastTime;
	
	public TokenRefreshRate(Long userid, Integer frequency, Long lastTime) {
		super();
		this.userid = userid;
		this.frequency = frequency;
		this.lastTime = lastTime;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	public Long getLastTime() {
		return lastTime;
	}
	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

}
