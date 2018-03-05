package com.web.poker.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * url编码，解码工具类
 * @author kevin
 */
public class UrlFunction {
	
	private final static Logger log = LoggerFactory.getLogger(UrlFunction.class);
	
	private UrlFunction(){}

	/**
	 * 对URL进行编码
	 * @param url
	 * @return
	 * @author kevin
	 */
	public static String encode(final String url) {
		try {
			return Base64.encodeBase64URLSafeString(url.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("无法进行编码", e);
			return null;
		}
	}
	
	/**
	 * 对URL进行解码
	 * @param url
	 * @return
	 * @author kevin
	 */
	public static String decode(final byte[] url) {
		return new String(Base64.decodeBase64(url));
	}
	
	/**
	 * 拼接字符串
	 * @param first
	 * @param seconde
	 * @return
	 * @author kevin
	 */
	public static String concat(List<String> strs) {
		StringBuffer buffer = new StringBuffer();
		for (String str : strs) {
			buffer.append(str);
		}
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		String parm = UrlFunction.encode("a=" + "1000" + "&b=" + "T10001");
		//String ep = encode(parm);
		System.out.println("encode:" + parm);
		String dp = UrlFunction.decode(parm.getBytes());
		System.out.println("decode:" + dp);
	}
	
	/**
	 * 解析Url参数为系统商品查询参数对象
	 * @param qryParam
	 * @return
	 * @author kevin
	 */
	/*public static ItemQryParamDTO resolveUrlParams(String qryParam) {
		log.info("URL参数：{}", qryParam);
		ItemQryParamDTO paramDTO = new ItemQryParamDTO();
		if(StringUtils.isBlank(qryParam)) {
			return paramDTO;
		}
		//解码
		String decodedParam = UrlFunction.decode(qryParam.getBytes());
		log.info("解码后的参数：{}", decodedParam);
		//解析
		String[] params = decodedParam.split("&");
		Map<String, String> paramMap = new HashMap<String, String>();
		for (String param : params) {
			String[] kv = param.split("=");
			String key = kv[0];
			String value = kv.length > 1 ? kv[1] : "";
			paramMap.put(key, value);
		}
		try {
			//对象转换
			BeanUtils.populate(paramDTO, paramMap);
		} catch (Exception e) {
			log.error("参数对象转换失败", e);
		}
		return paramDTO;
	}*/
	
	/**
	 * 解析Url参数为系统商品查询参数对象
	 * @param qryParam
	 * @return
	 * @author kevin
	 */
	/*public static ItemQryParamDTO resolveUrlParamsFromRequest() {
		HttpServletRequest request = FacesUtils.GetRequest();
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> paramMap = new HashMap<String, String>();
		while(paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);
			paramMap.put(name, value);
		}
		ItemQryParamDTO paramDTO = new ItemQryParamDTO();
		if(paramMap == null || paramMap.isEmpty()) {
			return paramDTO;
		}
		try {
			//对象转换
			BeanUtils.populate(paramDTO, paramMap);
		} catch (Exception e) {
			log.error("参数对象转换失败", e);
		}
		return paramDTO;
	}*/
}