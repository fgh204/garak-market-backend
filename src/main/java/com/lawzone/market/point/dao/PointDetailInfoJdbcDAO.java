package com.lawzone.market.point.dao;

import org.springframework.stereotype.Component;

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
		.append("\n and pi2.social_id = ? ")
		.append("\n group by pdi.point_save_id ")
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
		.append("\n and pi2.social_id = ? ")
		.append("\n order by pdi.point_detail_id asc ");	
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
		.append("\n where pdi.point_original_id = pi2.point_id ")
		.append("\n and pi2.point_expiration_datetime > now() ")
		.append("\n and pdi.point_state_code = ? ")
		.append("\n and pi2.social_id = ? ")
		.append("\n and pdi.point_detail_id >= ? ")
		.append("\n order by pdi.point_detail_id asc ");	
		return _query.toString();
	}
}
