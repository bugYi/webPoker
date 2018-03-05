package com.web.poker.common.utils;

import java.security.MessageDigest;
import java.util.HashMap;

public class MD5Utils {



	public static String getMD5String(String inStr) throws Exception {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = md5Bytes[i] & 0xFF;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		System.out.println("outStr:" + hexValue.toString());
		return hexValue.toString();
	}

	public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }
	
	/**
	 * 根据盐值对密码加密
	 * @param inStr
	 * @param salt
	 * @return
	 * @author kevin
	 */
	public static String string2MD5(String inStr, String salt) {
		String saltStr = salt == null ? "" : salt;
		String encryptPwd = saltStr + inStr;
		return string2MD5(encryptPwd);
	}

	
	public static void main(String[] args) throws Exception {
		HashMap<String, String> testMap = new HashMap<String,String>();
		testMap.put("aaa", "111");
		testMap.put("b", "2");
		testMap.put("c", "3");
		testMap.put("d", "4");
		
	    
		String temp_signature  =  MD5Utils.string2MD5(SortUtil.createLinkString(testMap, true, false, "")+"&"+MD5Utils.string2MD5("HrenKgXXTRZC").toLowerCase());
		System.out.println(temp_signature);
	}
}
