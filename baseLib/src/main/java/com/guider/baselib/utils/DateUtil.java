package com.guider.baselib.utils;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 * @author donggang
 *		
 */
public class DateUtil
{
	private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 获得默认的 date pattern
	 */
	public static String getDatePattern()
	{
		return defaultDatePattern;
	}
	
	/**
	 * 获得当前日期的字符串格式
	 * 
	 * @return
	 */
	@Deprecated
	public static String getCurrDateString()
	{
		return getDateString(new Date());
	}
	
	/**
	 * 获得Date的字符串格式
	 * 
	 * @param date
	 * @return
	 */
	@Deprecated
	public static String getDateString(Date date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(defaultDatePattern);
		return formatter.format(date);
	}
	
	/**
	 * 使用预设格式将字符串转为Date
	 */
	@Deprecated
	public static Date parse(String strDate)
	{
		return parse(strDate, getDatePattern());
	}
	
	/**
	 * 使用参数Format将字符串转为Date
	 */
	@Deprecated
	public static Date parse(String strDate, String pattern)
	{
		try
		{
			return null ==strDate ? null : new SimpleDateFormat(pattern).parse(strDate);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 获取当前本地时间
	 * @return
     */
	public static Date localNow()
	{
		return new Date();
	}

	/**
	 * 获取当前国际时间
	 * @return
	 */
	private static DateTime utcDateTimeNow()
	{
		return DateTime.now(DateTimeZone.UTC);
	}

	/**
	 * 获取当前国际时间
	 * @return
	 */
	public static Date utcNow()
	{
		return localToUtc(localNow());
	}

	/**
	 * 时区转换
	 * @param date 日期
	 * @param oldZone 旧时区
	 * @param newZone 新时区
	 * @return
	 */
	public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone)
	{
		Date dateTmp = null;
		if (date != null) {
			int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
			dateTmp = new Date(date.getTime() - timeOffset);
		}
		return dateTmp;
	}

	/**
	 * 本地时间转换成国际时间
	 * @param date 本地时间
	 * @return
     */
	public static Date localToUtcV2(Date date)
	{
		if (null == date)
			return utcNow();
		return changeTimeZone(date, TimeZone.getDefault(), TimeZone.getTimeZone("UTC"));
	}

	/**
	 * 国际时间转换成本地时间
	 * @param date 国际时间
	 * @return
	 */
	public static Date utcToLocalV2(Date date)
	{
		if (null == date)
			return localNow();
		return changeTimeZone(date, TimeZone.getTimeZone("UTC"), TimeZone.getDefault());
	}

	/**
	 * 本地时间转换成国际时间
	 * @param date 本地时间
	 * @return
	 */
	public static Date localToUtc(Date date)
	{
		if (null == date)
			return utcNow();
		return stringToDate(dateToString(date, defaultDatePattern, TimeZone.getTimeZone("UTC")));
	}

	/**
	 * 国际时间转换成本地时间
	 * @param date 国际时间
	 * @return
	 */
	public static Date utcToLocal(Date date)
	{
		if (null == date)
			return localNow();
		return stringToDate(dateToString(date, defaultDatePattern, TimeZone.getDefault()));
	}

	/**
	 * 按照默认格式把字符串时间转换成时间对象
	 * @param str 字符串时间
	 * @return
     */
	public static Date stringToDate(String str)
	{
		return stringToDate(str, defaultDatePattern);
	}

	/**
	 * 按照指定格式把字符串时间转换成时间对象
	 * @param str 字符串时间
	 * @param formater 格式
	 * @return
	 */
	public static Date stringToDate(String str, String formater)
	{
		return stringToDate(str, formater, TimeZone.getDefault());
	}

	/**
	 * 按照指定格式和时区把字符串时间转换成时间对象
	 * @param str 字符串时间
	 * @param formater 格式
	 * @param timeZone  时区
	 * @return
	 */
	public static Date stringToDate(String str, String formater, TimeZone timeZone)
	{
		try
		{
			if (null == str)
				return localNow();
			SimpleDateFormat sdf = new SimpleDateFormat(formater);
			sdf.setTimeZone(timeZone);
			return sdf.parse(str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return localNow();
	}

	/**
	 * 获取当前本地时间的字符串
	 * @return
     */
	public static String localNowString()
	{
		return dateToString(localNow());
	}

	/**
	 * 获取当前本地时间的字符串 按年月日格式
	 * @return
	 */
	public static String localNowStringByPattern(String pattern)
	{
		return dateToString(localNow(),pattern);
	}

	/**
	 * 把时间对象以默认格式转换成字符串
	 * @param date
	 * @return
     */
	public static String dateToString(Date date)
	{
		return dateToString(date, defaultDatePattern);
	}

	/**
	 * 把时间以指定格式转换成字符串
	 * @param date 时间对象
	 * @param formater 格式
     * @return
     */
	public static String dateToString(Date date, String formater)
	{
		return dateToString(date, formater, TimeZone.getDefault());
	}

	/**
	 * 把时间对象以默认格式指定时区转换成字符串
	 * @param date 时间对象
	 * @param timeZone 时区
     * @return
     */
	public static String dateToString(Date date, TimeZone timeZone)
	{
		return dateToString(date, defaultDatePattern, timeZone);
	}

	/**
	 *把时间对象以指定格式指定时区转换成字符串
	 * @param date 时间
	 * @param formater 格式
	 * @param timeZone 时区
     * @return
     */
	public static String dateToString(Date date, String formater, TimeZone timeZone)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(formater);
		formatter.setTimeZone(timeZone);
		return formatter.format(date);
	}

	/**
	 * 秒数时间戳转化为本地时间，秒数未1970.1.1后的秒数
	 * @param timeStamp
	 * @return
     */
	public static Date stampToDate(String timeStamp)
	{
		return stampToDate(Long.decode(timeStamp));
	}

	/**
	 * 秒数时间戳转化为本地时间，秒数未1970.1.1后的秒数
	 * @param timeStamp
	 * @return
     */
	public static Date stampToDate(long timeStamp)
	{
		return new Date(timeStamp * 1000);
	}

	/**
	 * 时间转换为秒数时间戳，秒数时间戳未1970.1.1往后的
	 * @param date
	 * @return
     */
	public static long dateToStamp(Date date)
	{
		return date.getTime() / 1000;
	}

	/**
	 * 当前本地时间转换为秒数时间戳，秒数时间戳未1970.1.1往后的
	 * @return
     */
	public static long localNowDateToStamp()
	{
		return dateToStamp(localNow());
	}

	/**
	 * 我的后台用的都是这个时间格式
	 * @param date
	 * @return
	 */
	public static String getDateFormat(Date date) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format.format(date);
	}

}
