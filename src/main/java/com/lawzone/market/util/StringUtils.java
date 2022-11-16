package com.lawzone.market.util;

import java.util.Map;

public class StringUtils {
	/**
	 * 구분자로 구분된 컬럼명을 카멜 표기법으로 바꿈   예)BBS_REF -> bbsRef
	 * @param str 
	 * @param seperator
	 * @return
	 */
	public static String getCamelNotation(String str, String seperator) {
		StringBuffer retVal = new StringBuffer();
		if(!isNullString(str)) {
			String[] strArr = str.split(seperator);
			for(int idx = 0 ; idx < strArr.length ; idx++) {
				if(idx == 0) {
					retVal.append(strArr[idx].toLowerCase());
				}
				else {
					retVal.append(strArr[idx].toUpperCase().charAt(0) + strArr[idx].toLowerCase().substring(1));
				}
			}
		}
		return retVal.toString();
	}
	
	public static String getMapData(Map aMap, String colId)
	{
		String rtnStringVal = "";
		if(aMap == null)
		{
			
		}
		else
		{
			rtnStringVal = String.valueOf(aMap.get(colId));
	
			if (rtnStringVal == null || rtnStringVal.equals("null"))
				rtnStringVal = "";
		}

		return rtnStringVal;
	}
	
	public static String getString(String str)
	{
		if (StringUtils.isNullString(str))
		{
			return "";
		}
		else
		{
			return str;
		}
	}
	
	public static boolean isNullString(String str)
	{
		if (str != null)
		{
			str = str.trim();
			if ( !"".equals(str) && !"null".equals(str) )
				return false;
		}
		return true;
	}
	
	public static Character stringToCharactor(String strVal)
	{
		if (strVal != null)
			strVal = strVal.trim();

		Character charVal;

		if (strVal == null || strVal == "")
		{
			charVal = new Character(' ');
		}
		else
		{
			charVal = new Character(strVal.charAt(0));
		}

		return charVal;
	}
}
