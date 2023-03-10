package com.lawzone.market.common.dao;

import org.springframework.stereotype.Component;

@Component
public class BoilerplateInfoJdbcDAO {
	public String adminBoilerplateList(String useYn, String userIdYn, String boilerplateName, String page) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	si.shop_name ")
				.append("\n 	, bi.boilerplate_number ") 
				.append("\n 	, bi.boilerplate_name ")
				.append("\n 	, bi.boilerplate_text ")
				.append("\n 	, bi.use_yn ")
				.append("\n from lz_market.boilerplate_info bi ") 
				.append("\n left outer join lz_market.seller_info si ")
				.append("\n 	on bi.user_id = si.seller_id ") 
				.append("\n where 1 = 1 ");
				if(!"".equals(useYn)) {
					_query.append("\n and bi.use_yn = ? ");
				}
				
				if("Y".equals(userIdYn)) {
					_query.append("\n and bi.user_id = ?");
				}
				if(!"".equals(boilerplateName)) {
					_query.append("\n and bi.boilerplate_name like concat('%', ? ,'%')");
				}
				_query.append("\n order by bi.boilerplate_number desc ")
				.append("\n limit " + page + " , 10");
		return _query.toString();
	}
	
	public String adminBoilerplatePageInfo(String useYn, String userIdYn, String boilerplateName, String page) {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n page.cnt as total_count ")
				.append("\n , page.p_cnt as total_pages ")
				.append("\n , if(page.p_cnt > " + page + " , 'Y', 'N') as has_next ")
				.append("\n , if(1 < " + page + " , 'Y', 'N') as has_previous ")
				.append("\n , " + page + " as page_number ")
				.append("\n from ( ")
				.append("\n select ") 
				.append("\n 	COUNT(1) as cnt ")
				.append("\n 	, CEIL (COUNT(1)/10) as p_cnt ")
				.append("\n from lz_market.boilerplate_info bi ") 
				.append("\n left outer join lz_market.seller_info si ")
				.append("\n 	on bi.user_id = si.seller_id ") 
				.append("\n where 1 = 1 ");
				
				if(!"".equals(useYn)) {
					_query.append("\n and bi.use_yn = ? ");
				}
				
				if("Y".equals(userIdYn)) {
					_query.append("\n and bi.user_id = ?");
				}
				
				if(!"".equals(boilerplateName)) {
					_query.append("\n and bi.boilerplate_name like concat('%', ? ,'%')");
				}

				_query.append("\n )page ");
		return _query.toString();
	}
}
