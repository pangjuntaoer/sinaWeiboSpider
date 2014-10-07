package com.weibocrawl.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class DateUtil {
	private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();

	/**
	 * 通过分钟时间比较
	 * 
	 * @param comparedTime
	 *            被比较的时间
	 * @param frequncy
	 *            时间间隔 ，可以为0
	 * @param compareingTime
	 *            待比较的时间，为Null的的话就和当前时间比较
	 */
	public static boolean compareTimeByMinutes(String comparedTime,
			int frequncy, String compareingTime) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			start.setTime(sdf.parse(comparedTime));
			if (compareingTime == null) {
				compareingTime = sdf.format(new Date());
			}
			end.setTime(sdf.parse(compareingTime));
			start.add(Calendar.MINUTE, frequncy);
			return start.after(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static String formatDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	public static Date fomatStringToDate(String date) {
		if (date == null || "".equals(date)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date fomatDateToDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(date);
		System.out.println(strDate);
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDate(String str, String format) {
		if (str == null || "".equals(str)) {
			return null;
		}
		SimpleDateFormat sdf = formatMap.get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.put(format, sdf);
		}
		try {
			synchronized (sdf) {
				// SimpleDateFormat is not thread safe
				return sdf.parse(str);
			}
		} catch (ParseException pe) {
		}
		return null;
	}

	public static void main(String[] args) {
		// System.out.println(compareTimeByMinutes("2013-04-12 15:00:00", 10,
		// "2013-04-12 15:09:00"));
			System.out.println(fomatStringToDate("2013-04-24 10:26:36"));
			System.out.println(Thread.currentThread().getName());
		// Date date = new Date();
		// System.out.println(fomatDateToDate(date));
	}
}
