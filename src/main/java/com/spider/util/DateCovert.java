package com.spider.util;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import com.tomcong.util.DateHelper;
import com.tomcong.util.StringHelper;
/**
 * 处理日期类的转换
 * @author tomcong
 *
 */
public class DateCovert extends DateHelper{
	public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";
	public static Timestamp format(String dateStr){
		return format(dateStr,DEFAULT_PATTERN);
	}
	public static Timestamp format(String dateStr,String pattern){
		    Date date =parseString(dateStr, pattern);
		    if(date!=null){
		    	  return new Timestamp(date.getTime());
		    }else{
		    	System.err.println(dateStr+">>>>>日期类转换异常!");
		    }
		    return null;
	}
	
	public static String parse(String dateStr,String pattern){
		   if(StringHelper.isEmpty(dateStr)||StringHelper.isEmpty(pattern))return "";
		    int len = dateStr.length()>pattern.length()?pattern.length():dateStr.length();
		    return dateStr.substring(0, len);
	}
	public static String parse(String dateStr){
		  return parse(dateStr,DEFAULT_PATTERN);
	}
	
	public static String getCurrentDate(String pattern){
		return formatDate(new Date(), pattern);
	}
	public static String getCurrentDate(){
		return formatDate(new Date(), DEFAULT_PATTERN);
	}
	
	public static Date getLastMoth(Date date){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
	}
	
	public static Date getYesterday(Date date){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
	}
	
	public static Date getBeforeDay(Date date,int day){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1*day);
        return cal.getTime();
	}
	public static String decode(String key){
		/*
		try {
			return URLDecoder.decode(key,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return key;
	}
	public static String changeDatePatternStr(String date,String srcPatten,String targetPatten){
		if(StringHelper.isEmpty(date))return "";
		return DateCovert.formatDate(DateCovert.parseString(
				date,srcPatten), targetPatten);
	}

	public static SerialClob covert(String str) {

		try {
			Clob c = new SerialClob(str.toCharArray());// String ×ª clob
			@SuppressWarnings("unused")
			String clobString = c.getSubString(1, (int) c.length());// clob ×ª
			return (SerialClob) c;
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
