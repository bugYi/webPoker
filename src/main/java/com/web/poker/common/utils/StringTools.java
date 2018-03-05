package com.web.poker.common.utils;

public class StringTools {
	
	
	public static boolean isLetterDigit(String str) {
		  String regex = "^[a-z0-9A-Z]+$";
		  return str.matches(regex);
		 }

}
