package common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtil {
	/**
	 * 获取当前时间
	 * @param format
	 * @return
	 */
	public static Date getNowDate(String format) {
		Date currentTime = new Date();
		return stringToDate(dateToString(currentTime,format),format);
	}
		
	/**
	 * 日期转字符串
	 * @param time
	 * @return
	 */
	public static String dateToString(Date time,String format){ 
	    SimpleDateFormat formatter = new SimpleDateFormat(format); 
	    String ctime = formatter.format(time); 
	    return ctime; 
	} 
	
	/**
	 * 字符串转日期
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date stringToDate(String str,String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
	    Date date = null;
		try {
			date = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return date;
	}
	
	/**
	 * 获取随机文件名  时间+5位随机数
	 * @return
	 */
	public static String getRandomFileName(){
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formatter.format(currentTime);
		Random random = new Random();
		int rannum = (int)(random.nextDouble()*(99999-10000 + 1))+10000;// 获取5位随机数
		return str+rannum;
	}
}
