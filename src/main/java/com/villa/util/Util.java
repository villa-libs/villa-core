package com.villa.util;

import com.villa.comm.CallBack;
import com.villa.comm.NoReturnCallBack;
import com.villa.util.encrypt.EncryptionUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 所有断言 为false就会报错
 */
public class Util {
	public static final String assertionIsTrue = "assertionIsTrue";
	public static final String assertionIsFalse = "assertionIsFalse";
	public static final String assertionIsNumber = "assertionIsNumber";
	public static final String assertionIsUrl = "assertionIsUrl";
	public static final String assertionIsEmail = "assertionIsEmail";
	public static final String assertionIsPhone = "assertionIsPhone";
	public static final String assertionIsNumberNotEq = "assertionIsNumberNotEq";
	public static final String assertionIsNumberEq = "assertionIsNumberEq";
	public static final String assertionIsStrEq = "assertionIsStrEq";
	public static final String assertionIsStrNotEq = "assertionIsStrNotEq";
	public static final String assertionIsNull = "assertionIsNull";
	public static final String assertionIsNotNull = "assertionIsNotNull";
	public static final String assertionIsNotNullOrEmpty = "assertionIsNotNullOrEmpty";
	public static final String assertionIsIdCard = "assertionIsIdCard";
	public static final String assertionIsEmptyCollection = "assertionIsEmptyCollection";
	public static final String assertionIsNotEmptyCollection = "assertionIsNotEmptyCollection";
	public static final String assertionNoException = "assertionNoException";
	public static final String assertionIsRangeLength = "assertionIsRangeLength";
	public static final String assertionIsCarNum = "assertionIsCarNum";
	public static final String assertionIsOnlyStrAndNum = "assertionIsOnlyStrAndNum";
	public static final String assertionIsContainChinese = "assertionIsContainChinese";
	public static final String assertionIsNotContainChinese = "assertionIsNotContainChinese";
	public static final String assertionIsStrAndNum = "assertionIsStrAndNum";
	public static final String assertionIsUpLow = "assertionIsUpLow";
	public static final String assertionIsUpLowNum = "assertionIsUpLowNum";
	public static final String assertionIsUpLowNumChar = "assertionIsUpLowNumChar";
	public static final String assertionIsUpOneAndLowNum = "assertionIsUpOneAndLowNum";
	public static final String assertionIsUpOneAndLowNumChar = "assertionIsUpOneAndLowNumChar";
	public static final String assertionIsGtZero = "assertionIsGtZero";
	public static final String assertionIsMax = "assertionIsMax";
	public static final String assertionIsMin = "assertionIsMin";

	/**
	 * 断言是结果为false	为true则报错
	 * 可被Validate注解使用
	 */
	public static void assertionIsFalse(boolean b,String msg,Object...params){
		if(b)throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言结果为true    为false则报错
	 * 可被Validate注解使用
	 */
	public static void assertionIsTrue(boolean b,String msg,Object...params){
		if(!b)throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串是数字
	 * 可被Validate注解使用
	 */
	public static void assertionIsNumber(String str,String msg,Object...params){
		if(!isNumeric(str))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言是邮箱
	 * 可被Validate注解使用
	 */
	public static void assertionIsUrl(String str,String msg,Object...params){
		if(!isUrl(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言是邮箱
	 * 可被Validate注解使用
	 */
	public static void assertionIsEmail(String str,String msg,Object...params){
		if(!isEmail(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言是手机号码
	 * 可被Validate注解使用
	 */
	public static void assertionIsPhone(String str,String msg,Object...params){
		if(!isMobile(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言两个数字不相等 支持基本数据类型及其包装类和bigDecimal,bigInteger
	 */
	public static void assertionIsNumberNotEq(Object num1,Object num2,String msg,Object...params){
		if(isNull(num1)||isNull(num2))throw new RuntimeException(String.format(msg,params));
		if(num1 instanceof BigDecimal||num2 instanceof BigDecimal
				|| num1 instanceof BigInteger || num2 instanceof BigInteger){
			if(new BigDecimal(num1.toString()).compareTo(new BigDecimal(num2.toString()))==0)throw new RuntimeException(String.format(msg,params));
		}else if(num1.equals(num2))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言两个数字相等 支持基本数据类型及其包装类和bigDecimal,bigInteger
	 */
	public static void assertionIsNumberEq(Object num1,Object num2,String msg,Object...params){
		if(isNull(num1)||isNull(num2))throw new RuntimeException(String.format(msg,params));
		if(num1 instanceof BigDecimal||num2 instanceof BigDecimal
			|| num1 instanceof BigInteger || num2 instanceof BigInteger){
			if(new BigDecimal(num1.toString()).compareTo(new BigDecimal(num2.toString()))!=0)throw new RuntimeException(String.format(msg,params));
		}else if(!num1.equals(num2))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言两个字符串相等  注意 两个字符串都不能为空 否则一样报错
	 */
	public static void assertionIsStrEq(String str1,String str2,String msg,Object...params){
		if(isNullOrEmpty(str1)||isNullOrEmpty(str2)||!str1.equals(str2))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言两个字符串不能相等 一般用在修改密码时 新旧不密码不能相同
	 */
	public static void assertionIsStrNotEq(String str1,String str2,String msg,Object...params){
		if(isNullOrEmpty(str1)||isNullOrEmpty(str2)||str1.equals(str2))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言为null
	 * 可被Validate注解使用
	 */
	public static void assertionIsNull(Object obj,String msg,Object...params){
		if(isNotNull(obj))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言不为null
	 * 可被Validate注解使用
	 */
	public static void assertionIsNotNull(Object obj,String msg,Object...params){
		if(isNull(obj))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言非空
	 * 可被Validate注解使用
	 */
	public static void assertionIsNotNullOrEmpty(String str,String msg,Object...params){
		if(isNullOrEmpty(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串是否是身份证
	 * 可被Validate注解使用
	 */
	public static void assertionIsIdCard(String idCard,String msg,Object...params){
		if(!isCard(idCard))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言集合为空
	 * 可被Validate注解使用
	 */
	public static void assertionIsEmptyCollection(Collection collection, String msg,Object...params) {
		if (collection!=null && !collection.isEmpty())throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言集合不为空
	 * 可被Validate注解使用
	 */
	public static void assertionIsNotEmptyCollection(Collection collection, String msg,Object...params) {
		if (collection==null || collection.isEmpty())throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言代码不会报错
	 * 可被Validate注解使用
	 */
	public static void assertionNoException(NoReturnCallBack fn, String msg, Object...params){
		try{
			fn.callback();
		}catch (Exception e){
			throw new RuntimeException(String.format(msg,params));
		}
	}
	/**
	 * 断言代码不会报错
	 * 可被Validate注解使用
	 */
	public static Object assertionNoException(CallBack fn, String msg, Object...params){
		try{
			return fn.callback();
		}catch (Exception e){
			throw new RuntimeException(String.format(msg,params));
		}
	}
	public static void assertionIsRangeLength(String str,int minLength,int maxLength,String msg,Object...params){
		if(!isRangeLength(str,minLength,maxLength))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言字符串为车牌号码
	 */
	public static void assertionIsCarNum(String str,String msg,Object...params){
		if(!isCarNum(str))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言字符串仅能为字母+数字 不能有特殊符号
	 */
	public static void assertionIsOnlyStrAndNum(String str,String msg,Object...params){
		if(!isOnlyStrAndNum(str))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 断言包含中文 不包含中文报错
	 */
	public static void assertionIsContainChinese(String str,String msg,Object...params){
		if(!isContainChinese(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言不包含中文 包含中文报错
	 */
	public static void assertionIsNotContainChinese(String str,String msg,Object...params){
		if(isContainChinese(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串包含字母+数字
	 */
	public static void assertionIsStrAndNum(String str,String msg,Object...params){
		if(!isStrAndNum(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串有且必有大写小写字母
	 */
	public static void assertionIsUpLow(String str,String msg,Object...params){
		if(!isUpLow(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串有且必有大写小写字母+数字
	 */
	public static void assertionIsUpLowNum(String str,String msg,Object...params){
		if(!isUpLowNum(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串有且必有大写小写字母+数字+特殊字符
	 */
	public static void assertionIsUpLowNumChar(String str,String msg,Object...params){
		if(!isUpLowNumChar(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串有且必有大写小写字母+数字 且首字母大写
	 */
	public static void assertionIsUpOneAndLowNum(String str,String msg,Object...params){
		if(!isUpOneAndLowNum(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言字符串有且必有大写小写字母+数字+特殊字符 且首字母大写
	 */
	public static void assertionIsUpOneAndLowNumChar(String str,String msg,Object...params){
		if(!isUpOneAndLowNumChar(str))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言值大于0 如果不是BigDecimal/BigInteger/Integer/Double/Float/Byte 直接断言失败
	 */
	public static void assertionIsGtZero(Object value,String msg,Object...params){
		if(!isGtZero(value))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言值最大为值 小于等于max
	 */
	public static void assertionIsMax(Object value,Object max,String msg,Object...params){
		if(!isMax(value,max))throw new RuntimeException(String.format(msg,params));
	}
	/**
	 * 断言值最大为值 大于等于min
	 */
	public static void assertionIsMin(Object value,Object min,String msg,Object...params){
		if(!isMin(value,min))throw new RuntimeException(String.format(msg,params));
	}

	/**
	 * 从字符串中获取数字的字符串
	 * @return
	 */
	public static String getNumberByStr(String str) {
		String regEx="[^0-9]";  
		Pattern p = Pattern.compile(regEx);  
		Matcher m = p.matcher(str); 
		return m.replaceAll("").trim();
	}

	/**
	 * 字符串是否为整数
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
	}
	/**
	 * 字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]+");
	    if(str.indexOf(".")>0){//判断是否有小数点
	        if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
	            return pattern.matcher(str.replace(".","")).matches();
	        }else {
	            return false;
	        }
	    }else {
	        return pattern.matcher(str).matches();
	    }
	}
	/**
	 * 判断是否为null
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}
	/**
	 * 判断是否不为null
	 */
	public static boolean isNotNull(Object obj) {
		return obj != null;
	}
	/**
	 * 字符串是否包含中文
	 * @param str 待校验字符串
	 * @return true 包含中文字符 false 不包含中文字符
	 */
	public static boolean isContainChinese(String str){
		if(isNullOrEmpty(str))return false;
		Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
		Matcher m = p.matcher(str);
		return m.find();
	}
	/**
	 * 验证字符串只能包含字母和数字
	 */
	public static boolean isOnlyStrAndNum(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^[0-9A-Za-z]+$";
		return str.matches(regex);
	}
	/**
	 * 验证字符串必须包含字母和数字
	 */
	public static boolean isStrAndNum(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^(?=.*[0-9].*)(?=.*[A-Za-z].*).*$";
		return str.matches(regex);
	}
	/**
	 * 验证字符串是否包含大写和小写
	 */
	public static boolean isUpLow(String str) {
		if(isNullOrEmpty(str)){
			return false;
		}
		char[] cs = str.toCharArray();
		//默认没有小写字母
		boolean low = false;
		//默认没有大写字母
		boolean upp = false;
		for (char c : cs) {
			if (Character.isLowerCase(c)){
				low = true;
			}
			if (Character.isUpperCase(c)){
				upp = true;
			}
		}
		if (upp&&low) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 验证字符串有且必有大写小写数字
	 */
	public static boolean isUpLowNum(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).*$";		
		return str.matches(regex);
	}
	/**
	 * 验证字符串有且必有大写小写数字特殊字符
	 */
	public static boolean isUpLowNumChar(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{4,}$";		
		return str.matches(regex);
	}
	/**
	 *  验证字符串有且必有大写小写数字
	 * 且首字母大写
	 */
	public static boolean isUpOneAndLowNum(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).*$";
		boolean matches = str.matches(regex);
		if (matches) {
			if (Character.isUpperCase(str.charAt(0))) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 验证字符串有且必有大写小写数字特殊字符
	 * 且首字母大写
	 */
	public static boolean isUpOneAndLowNumChar(String str) {
		if(isNullOrEmpty(str))return false;
		String regex = "^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{4,}$";		
		boolean matches = str.matches(regex);
		if (matches) {
			if (Character.isUpperCase(str.charAt(0))) {
				return true;
			}
		}
		return false;
	}
	/**
	 * BigDecimal跟BigInteger的大于0
	 * 判断类型 如果是BigDecimal/BigInteger是调方法比较  其他是大于符号
	 */
	public static boolean isGtZero(Object value) {
		if(isNull(value))return false;
		if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal)value;
			return bd.compareTo(new BigDecimal(0))==1;
		}
		if (value instanceof BigInteger) {
			BigInteger bi = (BigInteger)value;
			return bi.compareTo(new BigInteger("0"))==1;
		}
		if (value instanceof Integer) {
			Integer i = (Integer)value;
			return i>0;
		}
		if (value instanceof Long) {
			Long i = (Long)value;
			return i>0;
		}
		if (value instanceof Double) {
			Double i = (Double)value;
			return i>0;
		}
		if (value instanceof Float) {
			Float i = (Float)value;
			return i>0;
		}
		if (value instanceof Byte) {
			Byte i = (Byte)value;
			return i>0;
		}
		return false;
	}
	/**
	 * 是否小于等于max
	 * BigDecimal跟BigInteger的大于0
	 */
	public static boolean isMax(Object value,Object max) {
		if(isNull(value)||isNull(max))return false;
		if (value instanceof BigDecimal||max instanceof BigDecimal) {
			BigDecimal bd = new BigDecimal(value+"");
			BigDecimal bd2 = new BigDecimal(max+"");
			return bd.compareTo(bd2)!=1;
		}
		if (value instanceof BigInteger||max instanceof BigInteger) {
			BigInteger bi = new BigInteger(value+"");
			BigInteger bi2 =new BigInteger(max+"");
			return bi.compareTo(bi2)!=1;
		}
		if (value instanceof Integer||max instanceof Integer
				||value instanceof Long||max instanceof Long
				||value instanceof Double||max instanceof Double
				||value instanceof Float||max instanceof Float
				||value instanceof Short || max instanceof Short
				||value instanceof Byte || max instanceof Byte
			) {
			Double d = Double.parseDouble(value+"");
			Double d2 = Double.parseDouble(max+"");
			return d<=d2;
		}
		return false;
	}
	/**
	 * 是否大于等于min
	 */
	public static boolean isMin(Object value,Object min) {
		if(isNull(value)||isNull(min))return false;
		if (value instanceof BigDecimal||min instanceof BigDecimal) {
			BigDecimal bd = new BigDecimal(value+"");
			BigDecimal bd2 = new BigDecimal(min+"");
			return bd.compareTo(bd2)!=-1;
		}
		if (value instanceof BigInteger||min instanceof BigInteger) {
			BigInteger bi = new BigInteger(value+"");
			BigInteger bi2 =new BigInteger(min+"");
			return bi.compareTo(bi2)!=-1;
		}
		if (value instanceof Integer||min instanceof Integer
				||value instanceof Long||min instanceof Long
				||value instanceof Double||min instanceof Double
				||value instanceof Float||min instanceof Float
				||value instanceof Short || min instanceof Short
				||value instanceof Byte || min instanceof Byte
			) {
			Double d = Double.parseDouble(value+"");
			Double d2 = Double.parseDouble(min+"");
			return d>=d2;
		}
		return false;
	}
	/**
	 * 最小长度为指定长度
	 */
	public static boolean isMinLength(String value,int length) {
		return isNotNullOrEmpty(value)&&value.length()>=length;
	}
	/**
	 * 最大长度为指定长度
	 */
	public static boolean isMaxLength(String value, int length) {
		return isNotNullOrEmpty(value)&&value.length()<=length;
	}
	public static boolean isRangeLength(String value,int minLength,int maxLength){
		return isNotNullOrEmpty(value)&&value.length()<=maxLength&&value.length()>=minLength;
	}
	public static boolean isNotNullOrEmpty(String str) {
		return str != null && !"".equals(str.trim());
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || "".equals(str.trim())||"null".equals(str);
	}
	/**
	 * 根据身份证号码获取生日
	 */
	public static Date getBirthdayByIdCard(String idCard) {
		String Ai = "";
		// 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字
		if (idCard.length() == 18) {
			Ai = idCard.substring(0, 17);
		} else if (idCard.length() == 15) {
			Ai = idCard.substring(0, 6) + "19" + idCard.substring(6, 15);
		}
		try {
			String strYear = Ai.substring(6, 10);// 年份
			String strMonth = Ai.substring(10, 12);// 月份
			String strDay = Ai.substring(12, 14);// 日期
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			return s.parse(strYear + "-" + strMonth + "-" + strDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 验证是否是身份证号码格式
	 */
	public static boolean isCard(String IDStr) {
		if(isNullOrEmpty(IDStr))return false;
		String Ai = "";
		// 判断号码的长度 15位或18位
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			return false;
		}
		// 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(Ai);

		if (!isNum.matches()) {
			return false;
		}
		// 判断出生年月是否有效
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 日期

		Pattern pattern2 = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");
		Matcher m = pattern2.matcher(strYear + "-" + strMonth + "-" + strDay);
		if (!m.matches()) {
			return false;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			return false;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			return false;
		}
		return true;
	}
	public static boolean isCarNum(String carNumber) {
		if(isNullOrEmpty(carNumber))return false;
		String commonPlatePattern = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳])$";
		return carNumber.matches(commonPlatePattern);
	}
	/**
	 * MD5加密
	 */
	public static String MD5(String str) {
		return EncryptionUtil.encryptMD5(str);
	}
	/**
	 * 验证手机号码
	 */
	public static boolean isMobile(String str) {
		if (isNullOrEmpty(str)) {
			return false;
		}
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 验证邮箱
	 */
	public static boolean isEmail(String email) {
		if(isNullOrEmpty(email)){
			return false;
		}
		Pattern p = null;
		Matcher m = null;
		boolean b = false;

		p = Pattern.compile("[\\w\u4e00-\u9fa5!#$%&amp;'*+/=?^_`{|}~-]+(?:\\.[\\w\u4e00-\u9fa5!#$%&amp;'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?"); // 验证邮箱
		m = p.matcher(email);
		b = m.matches();
		return b;
	}
	/**
	 * 验证是否是链接
	 */
	public static boolean isUrl(String url) {
		if(isNullOrEmpty(url))return false;
		String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
		Pattern pattern = Pattern.compile(regex);
		if (pattern.matcher(url).matches()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取指定长度的随机数
	 */
	public static String getRandomNumber(int length) {
		Random r = new Random();
		String s = null;
		do{
			s = r.nextDouble()+"";
			if(s.contains("E")||s.contains("e")){
				//重新后去
				s = null;
			}
		}while (s==null);

		//如果小数点太短了 就再加几位
		while(length+3>s.length()){
			s+= getRandomNumber(length/2);
		}
		s = s.substring(3, 3 + length);
		return s;
	}

	/**
	 * 根据雪花算法获取18位随机数
	 */
	public static String getRandomNumberBySnowflake(){
		return SnowflakeIdWorker.getId().toString();
	}

	/**
	 * 根据雪花算法生产随机字符串
	 */
	public static String getRandomStrBySnowflake(){
		return SnowflakeIdWorker.getRandomStr();
	}
	static class SnowflakeIdWorker{
		private static SnowflakeIdWorker idWorker;
		static {
			idWorker = new SnowflakeIdWorker(1);
		}

		/** 开始时间截 (2019-07-01) */
		private static final long twepoch = 1561910400000L;
		/**
		 * 时间位取&
		 */
		private static final long timeBit = 0b1111111111111111111111111111111111111111110000000000000000000000L;

		/** 机器id所占的位数 */
		private final long workerIdBits = 10L;

		/** 数据标识id所占的位数 */
		private final long datacenterIdBits = 0L;

		/** 支持的最大机器id，结果是1023 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
		private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

		/** 支持的最大数据标识id，结果是0 */
		private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

		/** 序列在id中占的位数 */
		private final long sequenceBits = 12L;

		/** 机器ID向左移12位 */
		private final long workerIdShift = sequenceBits;

		/** 数据标识id向左移22位(12+10) */
		private final long datacenterIdShift = sequenceBits + workerIdBits;

		/** 时间截向左移22位(10+0+12) */
		private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

		/** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
		private final long sequenceMask = -1L ^ (-1L << sequenceBits);

		/** 工作机器ID(0~1023) */
		private long workerId;

		/** 数据中心ID(0) */
		private long datacenterId;

		/** 毫秒内序列(0~4095) */
		private long sequence;

		private long initSequence;

		/** 上次生成ID的时间截 */
		private long lastTimestamp = -1L;

		//==============================Constructors=====================================

		/**
		 * 构造函数
		 * @param workerId 工作ID (0~1023)
		 */
		private SnowflakeIdWorker(long workerId) {
			this(workerId,0);
		}
		/**
		 * 构造函数
		 * @param workerId 工作ID (0~1023)
		 * @param datacenterId 数据中心ID (0)
		 */
		private SnowflakeIdWorker(long workerId, long datacenterId) {
			if (workerId > maxWorkerId || workerId < 0) {
				throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
			}
			if (datacenterId > maxDatacenterId || datacenterId < 0) {
				throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
			}
			this.workerId = workerId;
			this.datacenterId = 0;
			//随机生成2000种可能
			this.initSequence=new Random().nextInt(2000);
			this.sequence=initSequence;
			//FIXME 1ms内有2000种可猜测
		}

		// ==============================Methods==========================================
		/**
		 * 获得下一个ID (该方法是线程安全的)
		 * @return SnowflakeId
		 */
		private synchronized long nextId() {
			long timestamp = timeGen();

			//如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
			if (timestamp < lastTimestamp) {
				throw new RuntimeException(
						String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
			}

			//如果是同一时间生成的，则进行毫秒内序列
			if (lastTimestamp == timestamp) {
				sequence = (sequence + 1) & sequenceMask;
				//毫秒内序列溢出
				if (sequence == 0) {
					//阻塞到下一个毫秒,获得新的时间戳
					timestamp = tilNextMillis(lastTimestamp);
				}
			}
			//时间戳改变，毫秒内序列重置
			else {
				sequence = initSequence;
			}

			//上次生成ID的时间截
			lastTimestamp = timestamp;

			//移位并通过或运算拼到一起组成64位的ID
			return ((timestamp - twepoch) << timestampLeftShift) //
					| (datacenterId << datacenterIdShift) //
					| (workerId << workerIdShift) //
					| sequence;
		}

		/**
		 * 从ID中获取时间
		 * @param id 由此类生成的ID
		 */
		public static Date getTime(long id){
			return new Date(((timeBit&id)>>22)+twepoch);
		}

		/**
		 * 阻塞到下一个毫秒，直到获得新的时间戳
		 * @param lastTimestamp 上次生成ID的时间截
		 * @return 当前时间戳
		 */
		private long tilNextMillis(long lastTimestamp) {
			long timestamp = timeGen();
			while (timestamp <= lastTimestamp) {
				timestamp = timeGen();
			}
			return timestamp;
		}

		/**
		 * 返回以毫秒为单位的当前时间
		 * @return 当前时间(毫秒)
		 */
		private long timeGen() {
			return System.currentTimeMillis();
		}

		/**
		 * 获取唯一ID
		 */
		public static Long getId() {
			return idWorker.nextId();
		}

		/**
		 * 获取随机字符串,length=13
		 */
		public static String getRandomStr() {
			return Long.toString(idWorker.nextId(), Character.MAX_RADIX);
		}
	}
	/**
	 * 将传入的字符串首字母大写
	 */
	public static String uppercaseFistChar(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	/**
	 * 隐藏字符串中间的几位数
	 * num1 前面显示几位
	 * num2 后面显示几位
	 * @return
	 */
	public static String hideStrCenter(String str,int num1,int num2){
		if(Util.isNullOrEmpty(str)){
			return null;
		}
		if(str.length()<=num1||str.length()<=num2){
			return str;
		}
		int hideLength = str.length()-num1-num2;
		String pre = str.substring(0, num1);
		String next = str.substring(hideLength+num1);
		return pre+"****"+next;
	}
	/**
	 * 将byte转为16进制
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(byte[] bytes){
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i=0;i<bytes.length;i++){
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length()==1){
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	/**
	 * 获取项目跟路径
	 */
	public static String getProjectRootPath(){
		return System.getProperty("user.dir");
	}

	/**
	 * 获取服务器主机地址加上下文
	 */
	public static String getHost(HttpServletRequest request){
		String contextPath = request.getContextPath();
		return  request.getScheme() + "://" + request.getServerName()+(isNotNullOrEmpty(contextPath)?"/"+contextPath:"");
	}

	/**
	 * 获取服务器域名
	 */
	public static String getDomain(HttpServletRequest request){
		String contextPath = request.getContextPath();
		return  request.getScheme() + "://" + request.getServerName();
	}
}
