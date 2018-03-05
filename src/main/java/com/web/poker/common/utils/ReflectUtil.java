package com.web.poker.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.web.poker.annotation.NotMap;
import com.web.poker.annotation.NotSignMap;



/**
 * 反射工具类
 * @author kevin
 */
public class ReflectUtil {
	
	private final static Logger log = LoggerFactory.getLogger(ReflectUtil.class);

	public static Map<String, String> wrap2SignMap(Object obj) throws Exception {
		Assert.notNull(obj, "被转换对应不能为空");
		try {
			Map<String, String> objMap = new HashMap<String, String>();
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				//设置可读
				field.setAccessible(true);
				//判断是否标注了不可签名的注解
				boolean canSign = field.isAnnotationPresent(NotSignMap.class);
				//如果标注了该注解，则对应的属性不参与签名
				if(canSign) {
					continue;
				}
				objMap.put(field.getName(), String.valueOf(field.get(obj)));
			}
			return objMap;
		} catch (Exception e) {
			log.error("对象转换失败。", e);
			throw new Exception("对象转换失败" + e.getMessage());
		}
	}
	
	public static Map<String, String> wrap2Map(Object obj) throws Exception {
		Assert.notNull(obj, "被转换对应不能为空");
		try {
			Map<String, String> objMap = new HashMap<String, String>();
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				//设置可读
				field.setAccessible(true);
				//判断是否标注了不可签名的注解
				boolean canSign = field.isAnnotationPresent(NotMap.class);
				//如果标注了该注解，则对应的属性不参与签名
				if(canSign) {
					continue;
				}
				objMap.put(field.getName(), String.valueOf(field.get(obj)));
			}
			return objMap;
		} catch (Exception e) {
			log.error("对象转换失败。", e);
			throw new Exception("对象转换失败" + e.getMessage());
		}
	}
	
	public static Object wrapReq2Obj(HttpServletRequest request, Object obj) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> c = obj.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			//判断是否标注了不可签名的注解
			boolean canNotSign = field.isAnnotationPresent(NotSignMap.class);
			if(!canNotSign) {
				String fieldName = field.getName();
				String param = request.getParameter(fieldName);
				log.info("fieldName:{},value:{}", fieldName, param);
				
				if(field.getType() == String.class) {
					if(param != null){
						field.set(obj, param);
					}
					
				} else if(field.getType() == Long.class) {
					if(StringUtils.isNotBlank(param)){
						field.set(obj, Long.parseLong(param));
					}
				}
				//field.set(obj, field.getType().getConstructor(field.getType()).newInstance(param));
			}
			
		}
		return obj;
	}
}