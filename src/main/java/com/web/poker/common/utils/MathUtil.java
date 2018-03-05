package com.web.poker.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

public class MathUtil {
	
	public final static BigDecimal ZERO = new BigDecimal(0);
	public final static BigDecimal ONE = new BigDecimal(1);
	public final static BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * 保留两位小数
	 * @param num
	 * @return
	 */
	public static Float formatNumberToTwoDecimal(float num){
		BigDecimal b = new BigDecimal(num); 
		return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue(); //四舍五入，保留两位小数
	}
	
	/**
	 * 保留四位小数
	 * @param num
	 * @return
	 */
	public static Float formatNumberToFourDecimal(float num){
		BigDecimal b = new BigDecimal(num); 
		return b.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue(); //四舍五入，保留两位小数
	}
	
	/**
	 * 转换为百分数
	 * @param num  待转换数字
	 * @parm pointNum 小数位
	 * @return  
	 */
	public static String formatPercent2(Float num, int pointNum) {
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(pointNum);
		return nt.format(num==null?0:num);
	}
	
	/**
	 * 相加
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static Float twoNumberPlus(float num1,float num2){
		BigDecimal b1 = new BigDecimal(Float.toString(num1));
		BigDecimal b2 = new BigDecimal(Float.toString(num2));
		return b2.add(b1).floatValue();
	}
	
	/**
	 * 相乘
	 * @return
	 */
	public static Float twoNumberMultiply(float num1,float num2){
		BigDecimal b1 = new BigDecimal(Float.toString(num1==0?0:num1));
		BigDecimal b2 = new BigDecimal(Float.toString(num2==0?0:num2));
		return b2.multiply(b1).floatValue();
	}
	
	/**
	 * 相减
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static Float twoNumberSubstract(float num1,float num2) {
		BigDecimal b1 = new BigDecimal(Float.toString(num1==0?0:num1));
		BigDecimal b2 = new BigDecimal(Float.toString(num2==0?0:num2));
		return b2.subtract(b1).floatValue();
	}
	
	/**
	 * 生成3位序列
	 * @param seq
	 * @return
	 */
	public static String create2Seq(int seq) {
		String result = null;
		if(seq%10==seq) {
			result = "0" + String.valueOf(seq);
		}else if(seq%100==seq) {
			result = String.valueOf(seq);
		}
		return result;
	}
	public static BigDecimal getBigDecimal( Object value ) {  
        BigDecimal ret = null;  
        if( value != null ) {  
            if( value instanceof BigDecimal ) {  
                ret = (BigDecimal) value;  
            } else if( value instanceof String ) {  
                ret = new BigDecimal( (String) value );  
            } else if( value instanceof BigInteger ) {  
                ret = new BigDecimal( (BigInteger) value );  
            } else if( value instanceof Number ) {  
                ret = new BigDecimal( ((Number)value).doubleValue() );  
            } else {  
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");  
            }  
        }  
        return ret;  
    }  
}
