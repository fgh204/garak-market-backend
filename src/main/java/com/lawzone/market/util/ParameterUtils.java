package com.lawzone.market.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.product.controller.ProductController;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONException;

@Slf4j
public class ParameterUtils {
	/**
	 * 파라메터값 을 list로 
	 * @param params
	 * @param paramName
	 * @return 
	 */
	public static List setDtoList(Map params, Object obj, String paramGb, SessionBean sessionBean) {
		List returnParamList = null;
		
		
			List arrayParamList = new ArrayList();
			List paramList = (List) params.get("dataset");
			
			for(int i = 0;i < paramList.size(); i++ ) {
				Map mapCdList = new HashMap();
				mapCdList.put("dataset", (Map) paramList.get(i));
				Object newObj = new Object();
				newObj = SerializationUtils.clone((Serializable) obj);
				
				arrayParamList.add(setDto(mapCdList,newObj, paramGb, sessionBean));
			}
			returnParamList = arrayParamList;	
		
		return returnParamList;	
	}
	
	/**
	 * 파라메터값 을 vo
	 * @param params
	 * @param paramName
	 * @return 
	 */
	public static Object setDto(Map params, Object obj, String paramGb, SessionBean sessionBean) {
		
		Object returnParamObject = null;
		
		Map paramList = (Map) params.get("dataset");
		if( paramList == null) paramList = params;
		
		BeanMap beanMap = new BeanMap(obj);

        Iterator<String> it = beanMap.keyIterator();
        it = beanMap.keyIterator();
        while( it.hasNext() )
        {
            String key = it.next();
            Object value = paramList.get(key);
            String strValue = String.valueOf(value);
            String columnType = String.valueOf(beanMap.getType(key));
            
            //log.info("key===>" + key);
            //log.info("value===>" + value);
            
            if (value != null && !StringUtils.isNullString(strValue))
            {
            	if (columnType.indexOf("Character") > -1)
            	{
	            	beanMap.put(key, new Character(StringUtils.stringToCharactor(strValue)));
	            }
            	else if (columnType.indexOf(".BigInteger") > -1)
            	{
	            	beanMap.put(key,  new BigInteger(strValue));
	            }
            	else if (columnType.indexOf(".Integer") > -1)
            	{
	            	beanMap.put(key, Integer.parseInt(strValue));
	            }
            	else if (columnType.indexOf("String") > -1)
            	{
            		beanMap.put(key, strValue);
	            }
	            else if (columnType.indexOf(".BigDecimal") > -1)
	            {
	            	beanMap.put(key, new BigDecimal(strValue));
	            }
	            else
	            {
	            	beanMap.put(key, strValue);
	            }
            }else if("userId".equals(key)) {
    			beanMap.put(key, sessionBean.getUserId());
    		}
            else if(!"class".equals(key) && !"ipdttm".equals(key) && !"ipempno".equals(key) && !"ippgid".equals(key) && !"ipipad".equals(key)){
            	if(!"update".equals(paramGb)) {
            		beanMap.put(key, null);
            	}
            }
        }
		returnParamObject = obj;
		
		return returnParamObject;	
	}
	
	public static void copyNonNullProperties(Object src, Object target) throws IllegalAccessException, InvocationTargetException {
	    BeanUtils.copyProperties(getNullPropertyNames(src), target);
	}

	public static String[] getNullPropertyNames (Object source) {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

	    Set<String> emptyNames = new HashSet<String>();
	    for(java.beans.PropertyDescriptor pd : pds) {
	        Object srcValue = src.getPropertyValue(pd.getName());
	        if (srcValue == null) emptyNames.add(pd.getName());
	    }
	    String[] result = new String[emptyNames.size()];
	    return emptyNames.toArray(result);
	}
}
