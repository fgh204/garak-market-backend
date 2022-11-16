package com.lawzone.market.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class JsonUtils {
	/**
	 * {"meta":{"msgNm":"ID가 존재하지 않습니다.","msgCd":"9999","dataCnt":1,"list":[{"key1":"YES1"}]}}
	 * 
	 * @param dataList
	 * @param type
	 * @param msgCd
	 * @param msgNm
	 * @return
	 * @throws Exception
	 */
	public static String listMakeJson(List dataList, String type, String msgCd, String msgNm) throws Exception
	{
		Map retData = new HashMap();
		List retList = new ArrayList();
		
		Map retMap = null; 
		
		Map map = null;

		for (int i = 0; i < dataList.size(); i++) 
		{
			retMap = new HashMap<String, String>();
			
			if(dataList.get(i) instanceof Map)
			{
				map = (Map) dataList.get(i);
				
				Iterator it = map.keySet().iterator();

				while (it.hasNext()) {
					String key = (String) it.next();
					
					retMap.put(StringUtils.getCamelNotation(key, "_"), StringUtils.getMapData(map, key));
				}
				
				retList.add(retMap);
			}
			else 
			{ 
				retList.add(new HashMap(ClassUtils.getVo(dataList.get(i)))); 
			}
			
//			retData.put("data", retMap);
		}
		
		retData = metaData(retList, msgCd, msgNm);
		
//		return new JSONArray(retList).toString();
		return mapToJson(retData);
	}
	
	public static Map metaData(List list, String msgCd, String msgNm)
	{
		Map meta = new HashMap();
		Map retMap = new HashMap();
		
		try
		{
//			hashMap.put("KEY", "YES");
			retMap.put("list", list);
			retMap.put("dataCount", list.size());
			retMap.put("msgCd", msgCd);
			retMap.put("msgNm", msgNm);
			
			meta.put("meta", retMap);
		}
		catch(Exception e)
		{
			retMap.put("totalCount", list.size());
			retMap.put("msgCd", "9999");
			retMap.put("msgNm", e.getMessage());
			
			meta.put("meta", retMap);
		}
		
//		return new JSONArray(retList).toString();
		return meta;
	}
	
	public static String mapToJson(Object obj)
	{
		try
		{
			return new JSONSerializer().toJSON(obj).toString();
		}
		catch(Exception eee)
		{
			return eee.getLocalizedMessage();
		}
	}
	
	public static String returnValue(String msgCd, String msgNm, Object returnObj)
	{
		Map<Object, Object> mapInfo = new HashMap<>();
		JsonConfig config = new JsonConfig();
    	config.setIgnoreDefaultExcludes(false);
    	config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); 

		try
		{
			mapInfo.put("msgCd", msgCd);
	    	mapInfo.put("msgNm", msgNm);
	    	mapInfo.put("result", returnObj);
	    	JSONObject result = JSONObject.fromObject(mapInfo, config);
	    	return result.toString();
		}
		catch(Exception e)
		{
			mapInfo.put("msgCd", "9999");
	    	mapInfo.put("msgNm", e.getLocalizedMessage());
			mapInfo.put("result", null);
			JSONObject result = JSONObject.fromObject(mapInfo, config);
			return result.toString();
		}
	}
}
