package com.lawzone.market.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {
	public static Map getVo(Object data) throws Exception
	{
		Map retMap = new HashMap();
		
		Class cl = data.getClass();
		
		Method[] m = cl.getMethods();
		
		String name = "";
		
		for (int i = 0; i < m.length; i++) 
		{
			if (m[i].getName().startsWith("get") && !"getClass".equals(m[i].getName())) 
			{
				name = m[i].getName().replace("get", "");
				if(name.length() > 1)
				{
					name = name.substring(0, 1).toLowerCase() + name.substring(1);
				}
				retMap.put(name, StringUtils.getString(m[i].invoke(data) + ""));
				
			}

		}
		
		return retMap;
	}
	
	//Header 값 세팅 인서트 데이터 세팅
	
	//Header 값 세팅 업데이트 데이터 세팅
}
