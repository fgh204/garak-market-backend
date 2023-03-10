package com.lawzone.market.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateUtils {
	public static String getCurrentDate(String pattern) throws NullPointerException, IllegalArgumentException
	{
		return getCurrentDate(pattern, Locale.KOREA);
	}

	public static String getCurrentDate(String pattern, Locale locale) throws NullPointerException,
			IllegalArgumentException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(new Date());
	}

	// String date = "20051121", String time = "14:00" 을
	// 2005-11-21-14.00.00.000000 으로 바꿔줌
	public static String makeDateType2(String date, String time)
	{
		date = date.replaceAll("-", "");
		time = time.replaceAll(":", "");

		int fYear = Integer.parseInt(date.substring(0, 4));
		int fMonth = Integer.parseInt(date.substring(4, 6)) - 1;
		int fDay = Integer.parseInt(date.substring(6, 8));
		int fTime = Integer.parseInt(time.substring(0, 2));
		int fMin = Integer.parseInt(time.substring(2, 4));

		Calendar cal = Calendar.getInstance();

		cal.set(fYear, fMonth, fDay, fTime, fMin, 0);

		return cal.getTime().toString();
	}

	/**
	 * 다음달 구하기
	 */
	public static String getNextMonth(Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);

		Date newDate = cal.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String nextMonth = formatter.format(newDate);

		return nextMonth;
	}

	// '20060615' 을 '2006-06-15' 로 바꿔줌
	public static String makeDateType(String date)
	{
		if (date.length() == 8)
			date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);

		return date;
	}

	/**
	 * String 을 Date Type 으로 바꿔줌
	 * 예) String date = "20051121"
	 * String time = "14:00"
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date makeDateType(String date, String time)
	{
		date = date.replaceAll("-", "");
		time = time.replaceAll(":", "");

		int fYear = Integer.parseInt(date.substring(0, 4));
		int fMonth = Integer.parseInt(date.substring(4, 6)) - 1;
		int fDay = Integer.parseInt(date.substring(6, 8));
		int fTime = Integer.parseInt(time.substring(0, 2));
		int fMin = Integer.parseInt(time.substring(2, 4));

		Calendar cal = Calendar.getInstance();

		cal.set(fYear, fMonth, fDay, fTime, fMin, 0);

		return cal.getTime();
	}

	// date format : 2005-01-12 10:09:22 ==> 20050112100922
	public static Date stringToDate(String date)
	{
		try
		{
			if (date != null && date.length() >= 8)
			{
				Calendar dateCal = Calendar.getInstance();
				dateCal.clear();

				date = format(date);

				int year = Integer.parseInt(date.substring(0, 4));
				int month = Integer.parseInt(date.substring(4, 6)) - 1;
				int day = Integer.parseInt(date.substring(6, 8));

				SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
				String currentDate = sdf.format(new Date());
				//String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

				int hour = Integer.parseInt(currentDate.substring(0, 2));
				int min = Integer.parseInt(currentDate.substring(2, 4));
				int sec = Integer.parseInt(currentDate.substring(4));

				if (date.length() > 8)
				{
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
					sec = Integer.parseInt(date.substring(12, 14));
				}

				dateCal.set(year, month, day, hour, min, sec);
				return dateCal.getTime();
			}
			else
			{
				return null;
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	// date format : 2005-01-12 10:09:22 ==> 20050112100922
	public static Date stringToDateAddSec(String date, int addSec)
	{
		try
		{
			if (date != null && date.length() >= 8)
			{
				Calendar dateCal = Calendar.getInstance();
				dateCal.clear();

				date = format(date);

				int year = Integer.parseInt(date.substring(0, 4));
				int month = Integer.parseInt(date.substring(4, 6)) - 1;
				int day = Integer.parseInt(date.substring(6, 8));

				SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
				String currentDate = sdf.format(new Date());

				int hour = Integer.parseInt(currentDate.substring(0, 2));
				int min = Integer.parseInt(currentDate.substring(2, 4));
				int sec = Integer.parseInt(currentDate.substring(4));

				if (date.length() > 8)
				{
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
					sec = Integer.parseInt(date.substring(12, 14)) + addSec;
				}

				dateCal.set(year, month, day, hour, min, sec);
				return dateCal.getTime();
			}
			else
			{
				return null;
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// date format : 2005-01-12 10:09:22.000000 ==> 20050112100922000000
	public static Date stringToTimestamp(String date)
	{
		try
		{
			if (date != null && date.length() >= 8)
			{
				Calendar dateCal = Calendar.getInstance();
				dateCal.clear();

				date = format(date);

				int year = Integer.parseInt(date.substring(0, 4));
				int month = Integer.parseInt(date.substring(4, 6)) - 1;
				int day = Integer.parseInt(date.substring(6, 8));

				SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
				String currentDate = sdf.format(new Date());

				int hour = Integer.parseInt(currentDate.substring(0, 2));
				int min = Integer.parseInt(currentDate.substring(2, 4));
				int sec = Integer.parseInt(currentDate.substring(4));

				if (date.length() > 8)
				{
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
					sec = Integer.parseInt(date.substring(12));
				}

				dateCal.set(year, month, day, hour, min, sec);
				return dateCal.getTime();
			}
			else
			{
				return null;
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String dateToString(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		}
		return null;
	}

	public static String timeStampToString(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		}
		return null;
	}

	public static String getSimpleDate(Timestamp date) throws NullPointerException, IllegalArgumentException
	{
		return String.valueOf(date).substring(0, 10);
	}

	private static String format(String s)
	{
		s = org.springframework.util.StringUtils.replace(s, "-", "");
		s = org.springframework.util.StringUtils.replace(s, ":", "");
		s = org.springframework.util.StringUtils.replace(s, ".", "");
		s = org.springframework.util.StringUtils.replace(s, " ", "");
		return s;
	}

	//
	// private static String getNextDay(String meetingDay)
	// {
	// Calendar cal = Calendar.getInstance();
	//
	// int fYear = Integer.parseInt(meetingDay.substring(0, 4));
	// int fMonth = Integer.parseInt(meetingDay.substring(5, 7)) - 1;
	// int fDay = Integer.parseInt(meetingDay.substring(8, 10));
	//
	// int hour = Integer.parseInt(meetingDay.substring(11, 13));
	// int minute = Integer.parseInt(meetingDay.substring(14, 16));
	// cal.set(fYear, fMonth, fDay, hour, minute, 0);
	//
	// cal.add(Calendar.DAY_OF_MONTH, 1);
	// String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
	// + addZero(Integer.toString(cal.get(Calendar.MONTH) + 1), 2) + "-"
	// + addZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2) + "-"
	// + addZero(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)), 2) + "."
	// + addZero(Integer.toString(cal.get(Calendar.MINUTE)), 2) + "."
	// + addZero(Integer.toString(cal.get(Calendar.SECOND)), 2) + ".000000";
	//
	// return date;
	//
	// }

	// private static String addZero(String data, int count)
	// {
	//
	// for (int i = data.length(); i < count; i++)
	// {
	// data = "0" + data;
	// }
	// return data;
	// }

	public static String getSimpleDate(Date date) throws NullPointerException, IllegalArgumentException
	{
		return String.valueOf(date).substring(0, 10);
	}

	public static String getSimpleDate(Date date, int from, int to) throws NullPointerException,
			IllegalArgumentException
	{
		return String.valueOf(date).substring(from, to);
	}

	// 어제 구하기
	public static String getYesterDate(Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.add(Calendar.DATE, -1);

		Date newDate = cal.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String yesterMonth = formatter.format(newDate);

		return yesterMonth;
	}

	public static String getCurrentTime()
	{
		String time = "";

		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		time = sdf.format(cal.getTime());

		return time;
	}

	public static String getCurrentTime(String pattern)
	{
		String time = "";

		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern);

		time = sdf.format(cal.getTime());

		return time;
	}

	public static Long getMilliseconds()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		return new Long(sf.format(new Date()));
	}

	public static String getNextDate(Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.add(Calendar.DATE, 1);

		Date newDate = cal.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String yesterMonth = formatter.format(newDate);

		return yesterMonth;
	}

	/**
	 * 문자열을  Date형으로 바꾼다.
	 * @param dateText 바꿀문자열
	 * @param pattern Date타입 ex) stringToDate("2010-10-10 10:10:10", "yyyy-MM-dd HH:mm:ss");
	 * @return
	 */
	public static Date stringToDate(String dateText, String pattern)
	{
		Date date = null;
		
		if(!StringUtils.isNullString(dateText)) {
			try
			{
				SimpleDateFormat sdf = null;
				if(!StringUtils.isNullString(pattern)) {
					sdf= new SimpleDateFormat(pattern);
				}
				else {
					sdf= new SimpleDateFormat("yyyy-MM-dd");
				}
				date = sdf.parse(dateText);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * Date를 지정한 패턴의 문자열로 반환
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToString(Date date, String pattern)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(date);
		}
		return "";
	}

	/**
	 * 년월일시분초를 넘겨 DATE를 반환
	 * @param year
	 * @param month (실제  month 1월이면 1을 입력)
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date makeDate(int year, int month, int day, int hour, int minute, int second)
	{
		Date date = null;
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);

			date = cal.getTime();
		}
		catch (Exception e)
		{
		}
		return date;
	}

	/**
	 * 해당 년월의 일수를 가져옴 (윤달 계산)
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDays(int year, int month)
	{
		int dayList[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (month == 2)
		{ // 윤달 고려
			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
			{
				return (29);
			}
		}
		return (dayList[month - 1]);
	}

	/**
	 * 특정 년도의 특정 달의 1일에 해당하는 시작 인덱스를 반환
	 * @param strDate
	 * @return
	 */
	public static int getStartDay(String strDate)
	{
		Calendar cal = Calendar.getInstance();
		Date date = stringToDate(strDate, "yyyy-MM-dd");
		cal.setTime(date);

		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH) - 1;

		return (weekOfMonth * 7) + dayOfWeek;
	}

	/**
	 * 두날자의 차이를 구한다.
	 * @param startDate
	 * @param endDate
	 * @param pattern
	 * @return
	 */
	public static long getDateDiff(String startDate, String endDate, String pattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		long diff = 0L;
		try
		{
			Calendar startCal = new GregorianCalendar();
			Calendar endCal = new GregorianCalendar();
			startCal.setTime(sdf.parse(startDate));
			endCal.setTime(sdf.parse(endDate));

			long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			diff = diffMillis / (24 * 60 * 60 * 1000);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return diff;
	}

	/**
	 * Date 를 넘겨 그날자의 요일을 반환 (영문 한글 선택 가능)
	 * @param date
	 * @param korean
	 * @return
	 */
	public static String getDayOfWeek(Date date, boolean korean)
	{
		String[][] week = { { "일", "Sun" }, { "월", "Mon" }, { "화", "Tue" }, { "수", "Wen" }, { "목", "Thu" },
				{ "금", "Fri" }, { "토", "Sat" } };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		if (korean)
		{
			return week[cal.get(Calendar.DAY_OF_WEEK) - 1][0];
		}
		else
		{
			return week[cal.get(Calendar.DAY_OF_WEEK) - 1][1];
		}
	}

	/**
	 * Date 를 넘겨 그날자의 요일 인덱스를 반환
	 * @param date
	 * @param korean
	 * @return
	 */
	public static int getDayOfWeek(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 두 날자의 달의 차이를 구한다. 요일은 무시하고 년, 월 만 가지고 판단
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getMonthDiffIgnoreDay(Date from, Date to)
	{
		int returnVal = 0;
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.setTime(from);
		toCal.setTime(to);

		returnVal = (toCal.get(Calendar.MONTH) - fromCal.get(Calendar.MONTH))
				+ ((toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR)) * 12);
		return returnVal + 1;
	}

	/**
	 * YYYYMM 에 해당하는 주의 갯수를 반환
	 * @param date
	 * @return
	 */
	public static int getWeekOfMonthCount(Date date)
	{
		// 일월화수목금토
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int returnVal = 0; // 반환값
		int dayofWeek = getDayOfWeek(date); // 해당월의 1일의 시작카운트
		int dayCount = getDays(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1); // 월의 요일수
		returnVal = 1 + (int) ((dayCount - (8 - dayofWeek)) / 7) + ((dayCount - (8 - dayofWeek)) % 7 > 0 ? 1 : 0);
		return returnVal;
	}

	/**
	 * 년, 월, 주 에 해달하는 시작일과 종료일을 반환
	 * @param year
	 * @param month
	 * @param week
	 * @return
	 */
	public static String[] getDayOfWeekFromTo(int year, int month, int week)
	{
		String[] returnVal = new String[2];
		Date date = stringToDate(year + "-" + (month < 10 ? "0" + month : month) + "-01", "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int fromDay = 0; // 시작일
		int toDay = 0; // 종료일 
		int dayofWeek = getDayOfWeek(date); // 해당월의 1일의 시작카운트
		int dayCount = getDays(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1); // 월의 요일수
		if (week == 1)
		{
			fromDay = 1;
			toDay = (dayofWeek == 1 ? 7 : 8 - dayofWeek);
		}
		else
		{
			fromDay = (dayofWeek == 1 ? 7 : 8 - dayofWeek) + ((week - 2) * 7) + 1;
			toDay = (fromDay + 7) > dayCount ? dayCount : (fromDay + 6);
		}
		//fromDay = (dayofWeek == 1 ? 7 : 8-dayofWeek) + ((week-2)*7) + 1;
		returnVal[0] = year + "-" + (month < 10 ? "0" + month : month) + "-" + (fromDay < 10 ? "0" + fromDay : fromDay);
		returnVal[1] = year + "-" + (month < 10 ? "0" + month : month) + "-" + (toDay < 10 ? "0" + toDay : toDay);
		return returnVal;
	}

	/**
	 * Date의 특정 필드에 시간을 더하여 원하는 포멧의 문자열로 반환 
	 * @param strDate 문자열 날자
	 * @param strDateType 문자열날자의 패턴
	 * @param type 연산할 날자 필드   ex) Calendar.DAY_OF_MONTH
	 * @param val 가감할 값
	 * @param dateType 리턴할 날자 타입
	 * @return
	 */
	public static String addDate(String strDate, String strDateType, String type, int val, String dateType)
	{
		Date date = stringToDate(strDate, strDateType);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		type = type.toUpperCase();
		int dateAddType = 0;
		
		if("Y".equals(type)) {
			dateAddType = Calendar.YEAR;
		} else if("M".equals(type)) {
			dateAddType = Calendar.MONTH;
		}else if("D".equals(type)) {
			dateAddType = Calendar.DATE;
		} 
			
		cal.add(dateAddType, val);
		return dateToString(cal.getTime(), dateType);
	}

	/**
	 * Date의 특정 필드에 시간을 더하여 원하는 포멧의 문자열로 반환  
	 * @param strDate 문자열 날자
	 * @param strDateType 문자열날자의 패턴
	 * @param type 연산할 날자 필드   ex) Calendar.DAY_OF_MONTH
	 * @param val 가감할 값
	 * @return
	 */
	public static Date addDate(String strDate, String strDateType, int type, int val)
	{
		Date date = stringToDate(strDate, strDateType);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(type, val);
		return cal.getTime();
	}
	
	/**
	 * Date의 특정 필드에 시간을 더하여 원하는 포멧의 문자열로 반환  
	 * @param date Date 날짜
	 * @param type 연산할 날자 필드   ex) Calendar.DAY_OF_MONTH
	 * @param val 가감할 값
	 * @return
	 */
	public static Date addDate(Date date, int type, int val)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(type, val);
		return cal.getTime();
	}

	public static String addZero(String data, int count)
	{

		for (int i = data.length(); i < count; i++)
		{
			data = "0" + data;
		}
		return data;
	}
	
	public static String getNextMonth(String dateInfo)
	{
		Calendar cal = Calendar.getInstance();

		int year = Integer.parseInt(dateInfo.substring(0, 4));
		int month = Integer.parseInt(dateInfo.substring(5, 7));
		int day = 1;

		cal.set(year, month, day);
		cal.add(Calendar.MONTH, 1);
		String date = Integer.toString(cal.get(Calendar.YEAR)) + addZero(Integer.toString(cal.get(Calendar.MONTH)), 2)
				+ addZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2);
		return date;
	}

	public static String getPreMonth(String dateInfo)
	{
		Calendar cal = Calendar.getInstance();

		int year = Integer.parseInt(dateInfo.substring(0, 4));
		int month = Integer.parseInt(dateInfo.substring(5, 7));
		int day = 1;

		cal.set(year, month, day);
		cal.add(Calendar.MONTH, -1);
		String date = Integer.toString(cal.get(Calendar.YEAR)) + addZero(Integer.toString(cal.get(Calendar.MONTH)), 2)
				+ addZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2);
		return date;
	}

	public static String getNextDay(String s)
	{
		Calendar cal = Calendar.getInstance();

		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(5, 7)) - 1;
		int day = Integer.parseInt(s.substring(8, 10));
		if (s != null && s.length() >= 8)
		{
			cal.set(year, month, day);
		}
		cal.add(Calendar.DAY_OF_MONTH, 1);
		String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(cal.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2);

		return date;

	}

	public static String getPreDay(String s)
	{
		Calendar cal = Calendar.getInstance();

		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(5, 7)) - 1;
		int day = Integer.parseInt(s.substring(8, 10));
		if (s != null && s.length() >= 8)
		{
			cal.set(year, month, day);
		}
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(cal.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2);

		return date;

	}

	public static String getNextSunDay(String date)
	{
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		gre.add(Calendar.DAY_OF_MONTH, 8 - num);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getNextSatDay(String date)
	{
		// int year = Integer.parseInt(date.substring(0, 4));
		// int month = Integer.parseInt(date.substring(4, 6))-1;
		// int day = Integer.parseInt(date.substring(6, 8)) ;

		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		gre.add(Calendar.DAY_OF_MONTH, 8 - num + 6);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}

	public static String getSunDay(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		int addDay = num - 1;
		gre.add(Calendar.DAY_OF_MONTH, -addDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}

	public static String getPreSunDay(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		int addDay = num + 6;
		gre.add(Calendar.DAY_OF_MONTH, -addDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getPreSatDay(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		int addDay = num + 6;
		gre.add(Calendar.DAY_OF_MONTH, -addDay+6);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}

	public static String getSaturday(String date)
	{
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		int addDay = num - 7;
		gre.add(Calendar.DAY_OF_MONTH, -addDay);

		String saturday = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return saturday;
	}
	
	public static String getWeek(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		return String.valueOf(gre.get(Calendar.WEEK_OF_YEAR));
	}

	public static String getNextWeek(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		gre.add(Calendar.DAY_OF_MONTH, 7);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getPreWeek(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		gre.add(Calendar.DAY_OF_MONTH, -7);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}

	public static String getReportDay(String date, int reportDay)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		gre.add(Calendar.DAY_OF_MONTH, 1 - num + reportDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getPreReportDay(String date, int reportDay)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		gre.add(Calendar.DAY_OF_MONTH, -6 - num + reportDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getNextReportDay(String date, int reportDay)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		if (num > reportDay)
			gre.add(Calendar.DAY_OF_MONTH, 8 - num + reportDay);
		else
			gre.add(Calendar.DAY_OF_MONTH, -6 - num + reportDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getMonDay(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);

		gre.add(Calendar.DAY_OF_WEEK, num);
		
		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getWeekOfSunday(String year, String week)
	{
		Calendar gre = Calendar.getInstance();
	
		gre.set(Calendar.YEAR, Integer.parseInt(year));
		gre.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
		
		int num = gre.get(Calendar.DAY_OF_WEEK);

		int addDay = num - 1;
		gre.add(Calendar.DAY_OF_MONTH, -addDay);

		String sunDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return sunDay;
	}
	
	public static String getFriDay(String date)
	{ // 2005/09/09
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		Calendar gre = Calendar.getInstance();

		gre.set(year, month, day);

		int num = gre.get(Calendar.DAY_OF_WEEK);
		
		gre.add(Calendar.DAY_OF_MONTH, 1 - num + 5);
		
		String friDay = Integer.toString(gre.get(Calendar.YEAR)) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.MONTH) + 1), 2) + "-"
				+ addZero(Integer.toString(gre.get(Calendar.DAY_OF_MONTH)), 2);
		return friDay;
	}
	
	/**
	 * 년, 월, 일을 념겨 주차정보는 가져온다.
	 * @param year
	 * @param month
	 * @param day
	 * @see
	 * return key info
	 * - year : 년도
	 * - weekOfYear : 주차
	 */
	public static Map<String, Integer> getWeekOfYear(int year, int month, int day) {
		int diff = 0;
		int max = 0;
		int returnYear = year;
		int weekCount = 1;
		Date date = null;
		Calendar start;
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		
		// 1주차 기준일을 위한....
		start = Calendar.getInstance();
		date = stringToDate(year+"-01-01", "yyyy-MM-dd");
		start.setTime(date);
		
		// 주차검사용
		Calendar cal = Calendar.getInstance();
		date = stringToDate(year+"-"+(month<10?"0"+month:month)+"-"+(day<10?"0"+day:day), "yyyy-MM-dd");
		cal.setTime(date);
		
		//**********************************************************************
		// 해당년도의 1주차 시작일을 셋팅
		//**********************************************************************
		int dow = start.get(Calendar.DAY_OF_WEEK); // 1월 1일의 시작요일 1-7 일-토
		
		if(dow == 1) { // 일 (1주차가 12월부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -2);
		}
		else if(dow == 2) { // 월 (1주차가 12월부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -3);
		}
		else if(dow == 3) { // 화 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 3);
		}
		else if(dow == 4) { // 수 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 2);
		}
		else if(dow == 5) { // 목 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 1);
		}
		else if(dow == 6) { // 금 (1주차가 금요일부터 시작 - 1월1일이 금요일)
			start.add(Calendar.DAY_OF_MONTH, 0);
		}
		else if(dow == 7) { // 토 (1주차가 금요일부터 시작 - 1주차가 작년 12월 31일부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		if(cal.getTimeInMillis() < start.getTimeInMillis()) {
			start = getStartCal(year-1);
			--returnYear;
			diff++;
		}
		
		Calendar startClon = (Calendar)start.clone();
		startClon.add(Calendar.DAY_OF_MONTH, 7*52);
		int temp = startClon.get(Calendar.DAY_OF_MONTH);
		if(31-temp > 3) {
			max = 53;
		}
		else {
			max = 52;
		}
		
		//**********************************************************************
		// 검사하려는 날의 주차를 찾는다.
		//**********************************************************************
		while(weekCount <= max) {
			Calendar tempCal = (Calendar)start.clone();
			tempCal.add(Calendar.DAY_OF_MONTH, 6);
			
			if(start.getTime().getTime() <= cal.getTime().getTime() && cal.getTime().getTime() <= tempCal.getTime().getTime()) {
				break;
			}
			else {
				tempCal.add(Calendar.DAY_OF_MONTH, 1);
				start = (Calendar)tempCal.clone();
				++weekCount;
			}
		}
		
		if(weekCount > max) {
			returnYear += diff+1;
			weekCount = 1;
		}
		
		returnMap.put("year", returnYear);
		returnMap.put("weekOfYear", weekCount);
		return returnMap;
	}
	
	/**
	 * 년도, 주차를 넘겨 시작일과 종료일을 가져온다.
	 * @param weekOfYear
	 * @return
	 */
	public static Map<String, Date> getWeekOfYearDate(int year, int weekOfYear) {
		Map<String, Date> retrunMap = new HashMap<String, Date>();
		Calendar start = null;
		Calendar end = null;
		
		start = getStartCal(year);
		start.add(Calendar.DAY_OF_MONTH, 7*(weekOfYear-1));
		
		end = getStartCal(year);
		end.add(Calendar.DAY_OF_MONTH, (7*weekOfYear)-1);
		
		retrunMap.put("start", start.getTime());
		retrunMap.put("end", end.getTime());
		return retrunMap;
	}
	
	/**
	 * 특정 년도의 1주차 시작일을 가져온다.
	 * @param year
	 * @return
	 */
	public static Calendar getStartCal(int year) {
		// 1주차 기준일을 위한....
		Calendar start  = Calendar.getInstance();
		Date date = stringToDate(year+"-01-01", "yyyy-MM-dd");
		start.setTime(date);
		
		//**********************************************************************
		// 해당년도의 1주차 시작일을 셋팅
		//**********************************************************************
		int dow = start.get(Calendar.DAY_OF_WEEK); // 1월 1일의 시작요일 1-7 일-토
		
		if(dow == 1) { // 일 (1주차가 12월부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -2);
		}
		else if(dow == 2) { // 월 (1주차가 12월부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -3);
		}
		else if(dow == 3) { // 화 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 3);
		}
		else if(dow == 4) { // 수 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 2);
		}
		else if(dow == 5) { // 목 (1주차가 금요일부터 시작 - 1월의 금요일 이전의 날자가 작년의 52주차나 53주차로 들어감)
			start.add(Calendar.DAY_OF_MONTH, 1);
		}
		else if(dow == 6) { // 금 (1주차가 금요일부터 시작 - 1월1일이 금요일)
			start.add(Calendar.DAY_OF_MONTH, 0);
		}
		else if(dow == 7) { // 토 (1주차가 금요일부터 시작 - 1주차가 작년 12월 31일부터 시작)
			start.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		return start;
	}
	
	public static Map<String, String> getKsdWeekDate(String wantDate) {
		Map<String, String> params = new HashMap<String, String>();
		Calendar cal = Calendar.getInstance();
		
		if(!StringUtils.isNullString(wantDate)) {
			// 원하는 날자로 돌리고 싶을 경우
			Date date = DateUtils.stringToDate(wantDate, "yyyy-MM-dd");
			cal.setTime(date);
		}
		else {
			// 직전주를 돌리고싶을 경우
			cal.add(Calendar.DAY_OF_MONTH, -3);
		}
		
		Map<String, Integer> weekOfYearMap = DateUtils.getWeekOfYear(
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));

		String year = weekOfYearMap.get("year").toString();
		String week = weekOfYearMap.get("weekOfYear").toString();
		
		Map<String, Date> weekMap = DateUtils.getWeekOfYearDate(Integer.parseInt(year), Integer.parseInt(week));
		
		String startDate = DateUtils.dateToString(weekMap.get("start"), "yyyy-MM-dd");
		String endDate = DateUtils.dateToString(weekMap.get("end"), "yyyy-MM-dd");
		
		params.put("year", year);
		params.put("week", week);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		
		return params;
	}
}
