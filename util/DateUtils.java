package com.rf.gjframe.base.util;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 日期工具类
 * @author wangjw
 *
 */
public class DateUtils
{
  private static Log log = LogFactory.getLog(DateUtils.class);
  private static SimpleDateFormat timeFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
  /**
	 * 将Date类型转换为字符串
	 * 
	 * @param date
	 *            日期类型
	 * @return 日期字符串
	 */
	public static String format(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 将Date类型转换为字符串
	 * 
	 * @param date
	 *            日期类型
	 * @param pattern
	 *            字符串格式
	 * @return 日期字符串
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "null";
		}
		if (pattern == null || pattern.equals("") || pattern.equals("null")) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		return new java.text.SimpleDateFormat(pattern).format(date);
	}

  /**
   * 日期增加天数
   * @param date
   * @param nDay
   * @return
   */
  public static Date addDay(Date date, int nDay) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.DAY_OF_MONTH, nDay);
      Date result = cal.getTime();
      return result;
  }

  /**
   * 日期格式化为字符串
   * @param date
   * @return
   */
  public static String dateToStr(Date date) {
      return timeFormatter.format(date);
  }

  /**
   * 日期按给定的日期格式化为字符串
   * @param date
   * @param format
   * @return
   */
  public static String dateToStr(Date date, String format) {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      return formatter.format(date);
  }

  /**
   * 字符串格式化为日期类型
   * @param strDate
   * @param format
   * @return
   */
  public static Date strToDate(String strDate, String format) {
      Date date = null;
      SimpleDateFormat dateFormatter = new SimpleDateFormat( format);
      try {
          date = dateFormatter.parse(strDate);
      } catch (Exception e) {
      }
      return date;
  }

  /**
   * 字符串按默认格式化为日期类型
   * @param strDate
   * @return
   */
  public static Date strToDate(String strDate) {
      Date date = null;
      try {
          date = timeFormatter.parse(strDate);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return date;
  }

  /**
   * 日期转换为java.sql.timestamp格式
   * @param date
   * @return
   */
  public static java.sql.Timestamp dateToTimestamp(Date date) {
      return new java.sql.Timestamp(date.getTime());
  }

  private static final char[] zeroArray = "0000000000000000".toCharArray();

  /**
   * Pads the supplied String with 0's to the specified length and returns
   * the result as a new String. For example, if the initial String is
   * "9999" and the desired length is 8, the result would be "00009999".
   * This type of padding is useful for creating numerical values that need
   * to be stored and sorted as character data. Note: the current
   * implementation of this method allows for a maximum <tt>length</tt> of
   * 16.
   *
   * @param string the original String to pad.
   * @param length the desired length of the new padded String.
   * @return a new String padded with the required number of 0's.
   */
   public static final String zeroPadString(String string, int length) {
      if (string == null || string.length() > length) {
          return string;
      }
      StringBuffer buf = new StringBuffer(length);
      buf.append(zeroArray, 0, length-string.length()).append(string);
      return buf.toString();
   }

   /**
    * Formats a Date as a fifteen character long String made up of the Date's
    * padded millisecond value.
    *
    * @return a Date encoded as a String.
    */
   public static final String dateToMillis(Date date) {

      return zeroPadString(Long.toString(date.getTime()), 15);
   }

   /**
	 * 处理当前时间10年后日期
	 * @param strDate
	 * @return
	 */
	public static String aft10YearsOld(Object strDate){
		GregorianCalendar cal = (GregorianCalendar)Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(strDate==null){
			cal.add(Calendar.YEAR, 10);
			return getStrOfDate(cal.getTime());
		}else{
			try {
				cal.setTime(sdf.parse((String)strDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cal.add(Calendar.YEAR, 10);
			return sdf.format(cal.getTime());	
		}
	}
	
	/**
	 * @description	格式化指定日期为年月日格式
	 * @param date
	 * @return
	 * @author	wangjw
	 */
	public static String getStrOfDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
     * 判断当前日期是星期几
     * 
     * @param pTime 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
	public static int dayForWeek(Date pTime) throws Exception {
		  Calendar c = Calendar.getInstance();
		  c.setTime(pTime);
		  int dayForWeek = 0;
		  if(c.get(Calendar.DAY_OF_WEEK) == 1){
			  dayForWeek = 7;
		  }else{
			  dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		  }
		  return dayForWeek;
 	}
	/**获取上月日期
	 * @return
	 */
	public static String getLastMonth(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		return dateToStr(c.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 获取节假日集合
	 * @return
	 * @throws IOException 
	 */
	public List<String> getHoliday(String type) throws IOException {

		List<String> holidayList = new ArrayList<String>();

		//从配置文件中读取url
		Properties properties=new Properties(); 
		//输入流
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("holiday.properties");
		//加载文件
		properties.load(inputStream);
		//获取日期
		String holiday = properties.getProperty(type);
		//机关单位日期数组
		String[] holidays = holiday.split(",");

		for(int i = 0;i<holidays.length;i++){
			holidayList.add(holidays[i]);
		}
		return holidayList;
	}
}
