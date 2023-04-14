package com.lawzone.market.point.dao;

import org.springframework.stereotype.Component;

import com.lawzone.market.point.service.PointInfoCDTO;

@Component
public class PointDetailInfoJdbcDAO {
	public String differencePointAmount() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	pdi.point_original_id ")
		.append("\n 	, pdi.point_save_id ")
		.append("\n 	, SUM(pdi.point_value) ")
		.append("\n 	, date_Format(pi2.point_expiration_datetime, '%Y-%m-%d %H:%i:%s') ")
		.append("\n from lz_market.point_detail_info pdi ")
		.append("\n 	, lz_market.point_info pi2 ")
		.append("\n where pdi.point_original_id = pi2.point_id ")
		.append("\n and pi2.point_expiration_datetime > now() ")
		.append("\n and pdi.point_state_code = ? ")
		.append("\n and pi2.user_id = ? ")
		.append("\n group by pdi.point_original_id ")
		.append("\n order by pdi.point_detail_id desc ")
		.append("\n limit 1 ");	
		return _query.toString();
	}
	
	public String pointAmount() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	pdi.point_original_id ")
		.append("\n 	, pdi.point_save_id ")
		.append("\n 	, pdi.point_value ")
		.append("\n 	, date_Format(pi2.point_expiration_datetime, '%Y-%m-%d %H:%i:%s') ")
		.append("\n from lz_market.point_detail_info pdi ")
		.append("\n 	, lz_market.point_info pi2 ")
		.append("\n where pdi.point_original_id = pi2.point_id ")
		.append("\n and pi2.point_expiration_datetime > now() ")
		.append("\n and pdi.point_state_code = ? ")
		.append("\n and pi2.user_id = ? ")
		.append("\n order by pdi.point_expiration_datetime , pdi.point_detail_id asc ");	
		return _query.toString();
	}
	
	public String pointAmount2() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	pdi.point_original_id ")
		.append("\n 	, pdi.point_save_id ")
		.append("\n 	, pdi.point_value ")
		.append("\n 	, date_Format(pdi.point_expiration_datetime, '%Y-%m-%d %H:%i:%s') ")
		.append("\n from lz_market.point_detail_info pdi ")
		.append("\n 	, lz_market.point_info pi2 ")
		//.append("\n 	, lz_market.point_detail_info pi2 ")
		.append("\n where pdi.point_original_id = pi2.point_id ")
		//.append("\n where pdi.point_save_id  = pi2.point_detail_id ")
		.append("\n and pi2.point_expiration_datetime > now() ")
		.append("\n and pdi.point_state_code = ? ")
		.append("\n and pi2.user_id = ? ")
		.append("\n and pdi.point_detail_id >= ? ")
		//.append("\n order by pdi.point_detail_id asc ");	
		.append("\n order by pdi.point_expiration_datetime , pdi.point_detail_id asc ");
		return _query.toString();
	}
	
	public String pointAmount3() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	pdi.point_original_id ")
		.append("\n 	, pdi.point_save_id ")
		.append("\n 	, SUM(pdi.point_value) ")
		.append("\n 	, date_Format(pdi.point_expiration_datetime, '%Y-%m-%d %H:%i:%s') ")
		.append("\n from lz_market.point_detail_info pdi ")
		.append("\n where pdi.point_expiration_datetime > now() ")
		.append("\n and pdi.user_id = ? ")
		//.append("\n and pdi.point_detail_id >= ? ")
		.append("\n GROUP by pdi.point_save_id ")	
		.append("\n order by pdi.point_expiration_datetime , pdi.point_detail_id asc ");
		return _query.toString();
	}
	
	public String pointHistInfo(String monthValue, String pointStateCode, String pageCnt, String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	pi2.point_id as pointId ")
		.append("\n 	, case when pi2.event_code = '003' then concat(if(pi2.point_state_code = '001','주문취소 적립','주문시 사용'), ' (주문번호 : ', pi2.event_id, ')') else ei.event_name end as pointName ")
		//.append("\n 	, pi2.point_value as pointAmount ")
		.append("\n 	, sum(pi2.point_value) as pointAmount ")
		//.append("\n 	, concat(DATE_FORMAT(pi2.point_regist_date, '%Y.%m.%d'), ' (',DATE_FORMAT(pi2.point_expiration_datetime, '%Y.%m.%d'),' 만료)') as pointDate ")
		.append("\n 	, concat(DATE_FORMAT(pi2.point_regist_date,'%Y.%m.%d'),if(pi2.point_state_code = '002','',concat(' (',DATE_FORMAT(pi2.point_expiration_datetime,'%Y.%m.%d'),' 만료)'))) as pointDate ")
		.append("\n 	, case when DATE_FORMAT(pi2.point_expiration_datetime, '%Y%m%d') < DATE_FORMAT(now(), '%Y%m%d') then 'Y' else 'N' end as expirationYn ")
		.append("\n 	, pi2.point_state_code as pointStatCode ")
		.append("\n 	, lz_market.FN_DTL_NM(12, pi2.point_state_code) as pointStatCodeName ")
		.append("\n 	, DATE_FORMAT(pi2.update_datetime, '%Y.%m.%d %H:%m:%s') as updateDatetime ")
		.append("\n 	, (select ui.user_name  from lz_market.user_info ui where ui.user_id = pi2.create_user ) as createUser ")
		.append("\n from lz_market.point_info pi2 ")
		.append("\n 	left outer join lz_market.event_info ei ")
		.append("\n 	on pi2.event_id = ei.event_id ")
		.append("\n where user_id = ? ");
		
		if(!("".equals(pointStateCode) || pointStateCode == null)) {
			_query.append("\n AND point_state_code = ? ");
		}
		
		if(!("".equals(monthValue) || monthValue == null)) {
			_query.append("\n and pi2.point_regist_date > DATE_SUB(now(), INTERVAL " + monthValue + " MONTH) ");
		}
		_query.append("\n group by case when pi2.event_code = '003' then concat(if(pi2.point_state_code = '001', '주문취소 적립', '주문시 사용'), ' (주문번호 : ', pi2.event_id, ')')")
				.append("\n else ei.event_name end ")
				//.append("\n , pi2.point_expiration_datetime ")
				.append("\n , case when pi2.event_code = '003' and pi2.point_state_code = '001' then DATE_FORMAT(pi2.point_regist_date, '%Y%m%d') else pi2.point_regist_date  end ")
				.append("\n order by pi2.point_id DESC")
		  		.append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String pointHistPageInfo(String monthValue, String pointStateCode, String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		
		.append("\n	   ceil(count(A.pointId) / " + maxPageCnt + ") AS total_page_count ")
		.append("\n    , count(A.pointId) AS total_count ")
		.append("\n	from ( ")
		.append("\n	select ")		
		.append("\n	point_id  as pointId ")
		.append("\n from lz_market.point_info pi2 ")
		.append("\n 	left outer join lz_market.event_info ei ")
		.append("\n 	on pi2.event_id = ei.event_id ")
		.append("\n where user_id = ? ");
		
		if(!("".equals(pointStateCode) || pointStateCode == null)) {
			_query.append("\n AND point_state_code = ? ");
		}
		
		if(!("".equals(monthValue) || monthValue == null)) {
			_query.append("\n and pi2.point_regist_date > DATE_SUB(now(), INTERVAL " + monthValue + " MONTH) ");
		}
		_query.append("\n group by case when pi2.event_code = '003' then concat(if(pi2.point_state_code = '001', '주문취소 적립', '주문시 사용'), ' (주문번호 : ', pi2.event_id, ')') ")
		.append("\n	else ei.event_name end")
		//.append("\n	, pi2.point_expiration_datetime")
		.append("\n	, case when pi2.event_code = '003' and pi2.point_state_code = '001' then DATE_FORMAT(pi2.point_regist_date, '%Y%m%d') else pi2.point_regist_date  end ")
		.append("\n	) A ");
		return _query.toString();
	}
}
