package com.lawzone.market.point.dao;

import org.springframework.stereotype.Component;

@Component
public class PointInfoJdbcDAO {
	public String pointAmount() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	ceil(sum(point_value)) ")
			  .append("\n from lz_market.point_detail_info pi2 ")
			  .append("\n where user_id = ? ")
			  .append("\n and point_expiration_datetime > now() ");	
		return _query.toString();
	}
	
	public String expirationpointAmount() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	ceil(sum(point_value)) ")
			  .append("\n from lz_market.point_detail_info pi2 ")
			  .append("\n where user_id = ? ")
			  .append("\n and DATE_FORMAT(point_expiration_datetime, '%Y%m') = DATE_FORMAT(now(), '%Y%m') ");	
		return _query.toString();
	}
	
	public String pointInfoByOrderNo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	pdi.point_save_id  ")
			  .append("\n 	,  ABS(pdi.point_value) ")
			  .append("\n 	, '' ")
			  .append("\n from lz_market.point_info pi2 ")
			  .append("\n 	, lz_market.point_detail_info pdi ")	
			  .append("\n where pi2.point_id = pdi.point_original_id ")
			  .append("\n and pi2.user_id = ? ")
			  .append("\n and pi2.event_id = ? ");
		return _query.toString();
	}
	
	public String pointDetailInfoByOrderNo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	pdi.point_original_id ")
			  .append("\n 	, pdi.point_save_id ")
			  .append("\n 	, ABS(pdi.point_value) ")
			  .append("\n 	, date_Format(pdi.point_expiration_datetime, '%Y-%m-%d %H:%i:%s') ")
			  .append("\n from lz_market.point_info pi2 ")
			  .append("\n 	, lz_market.point_detail_info pdi ")	
			  .append("\n where pi2.point_id = pdi.point_original_id ")
			  .append("\n and pi2.user_id = ? ")
			  .append("\n and pi2.event_id = ? ")
			  .append("\n order by pdi.point_expiration_datetime asc ");
		return _query.toString();
	}
	
	public String pointAccumulationYn () {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	case when ei.duplicate_limit_yn = 'Y' and ei.duplicate_limit_count <= count(pi2.user_id) then 'N' else 'Y' end as pointYn ")
			  .append("\n , (select ei2.point_amount  from lz_market.event_info ei2 ")
			  .append("\n where event_id = ?) as pointAmount ")
			  .append("\n from lz_market.point_info pi2 , ")
			  .append("\n 	lz_market.event_info ei ")
			  .append("\n where pi2.event_id = ei.event_id ")
			  .append("\n and pi2.user_id in (select ui.user_id  from lz_market.user_info ui where ui.social_id = ?) ")
			  .append("\n and pi2.event_code = ? ")	
			  .append("\n and ei.event_id = ? ");
		return _query.toString();
	}
	
	public String pointInfoList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	case when pi2.event_code = '003' then concat(if(pi2.point_state_code = '001','주문취소 적립','주문시 사용'), '(주문번호 : ', pi2.event_id, ')') else ei.event_name end as pointName ")
			  .append("\n 	, pi2.point_value as pointAmount ")
			  .append("\n 	, concat(DATE_FORMAT(pi2.point_regist_date, '%Y.%m.%d'), ' (',DATE_FORMAT(pi2.point_expiration_datetime, '%Y.%m.%d'),' 만료)') as pointDate ")
			  .append("\n 	, case when DATE_FORMAT(pi2.point_expiration_datetime, '%Y%m%d') < DATE_FORMAT(now(), '%Y%m%d') then 'Y' else 'N' end as expirationYn ")
			  .append("\n from lz_market.point_info pi2 ")
			  .append("\n left outer join lz_market.event_info ei ")
			  .append("\n 	on pi2.event_id = ei.event_id ")
			  .append("\n where user_id = ? ");
		return _query.toString();
	}
}
