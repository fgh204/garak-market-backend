package com.lawzone.market.util;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.user.service.UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UtilService {
	//private final UtilRepository utilRepository;
	
	private final EntityManager em;

	public String getNextVal(String kw) {
		
		String _dateFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		String _kw = "";
		_kw = kw;
		if("ORDER_NO".equals(kw)){
			_kw = kw + "_" + _dateFormat.substring(0,4);
		}
		
		String sql = "select "
				+ "nextval(?) "
				+ "from dual";
		
		Query nativeQuery = em.createNativeQuery(sql)
							  .setParameter(1,_kw);
		
		String nextVal = nativeQuery.getSingleResult().toString();
		
//		String sql = "select user_id,email from lz_market.user_info";
//		
//		Query nativeQuery = em.createNativeQuery(sql);
//
//		JpaResultMapper jpaResultMapper = new JpaResultMapper();
//	    List<Aa> products = jpaResultMapper.list(nativeQuery, Aa.class);
//		
//		log.info("parameter1 = " + JsonUtils.returnValue("111", "222", products));
		
		em.close();
		
		String _rtnId = "";
		
		if("PRODUCT_ID".equals(kw)) {
			_rtnId = StringUtils.leftPad(nextVal, 12,"0");
		}else if("ORDER_NO".equals(kw)){
			_rtnId = _dateFormat.substring(0,4) + StringUtils.leftPad(nextVal, 8,"0");
		}else {
			_rtnId = nextVal;
		}
		
		return _rtnId;
	}
	
	public List getQueryString(String queryString, Object obj, ArrayList arryList) {
		String sql = queryString;
		
		Object queryObl = new Object();
		
		queryObl = obj.getClass();
		
		Query nativeQuery = em.createNativeQuery(sql);
		
		if(arryList != null) {
			for(int i = 0; i < arryList.size(); i++) {
				nativeQuery.setParameter((i+1),arryList.get(i).toString());
				
				log.info("Parameter" + (i+1) + "==============" + arryList.get(i).toString());
			}
		}
		JpaResultMapper jpaResultMapper = new JpaResultMapper();
		List rtnList = jpaResultMapper.list(nativeQuery, obj.getClass());
		
		//log.info("parameter1 = " + JsonUtils.returnValue("111", "222", products));
		
		em.close();
		
		return rtnList;
	}
	
	public Integer getQueryStringUpdate(String queryString, ArrayList arryList) {
		String sql = queryString;
		Query nativeQuery = em.createNativeQuery(sql);
		
		if(arryList != null) {
			for(int i = 0; i < arryList.size(); i++) {
				nativeQuery.setParameter((i+1),arryList.get(i).toString());
				
				log.info("Parameter" + (i+1) + "==============" + arryList.get(i).toString());
			}
		}
		int query = nativeQuery.executeUpdate();
		
		em.close();
		
		return query;
	}
	
	public String getQueryStringChk(String queryString, ArrayList arryList) {
		String sql = queryString;
		Query nativeQuery = em.createNativeQuery(sql);
		
		if(arryList != null) {
			for(int i = 0; i < arryList.size(); i++) {
				nativeQuery.setParameter((i+1),arryList.get(i).toString());
				
				log.info("Parameter" + (i+1) + "==============" + arryList.get(i).toString());
			}
		}
		String chkVal = nativeQuery.getSingleResult().toString();
		
		em.close();
		
		return chkVal;
	}
}
