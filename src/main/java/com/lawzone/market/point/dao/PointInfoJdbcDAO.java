package com.lawzone.market.point.dao;

import org.springframework.stereotype.Component;

@Component
public class PointInfoJdbcDAO {
	public String pointAmount() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	ceil(sum(point_value)) ")
			  .append("\n from lz_market.point_detail_info pi2 ")
			  .append("\n where social_id = ? ")
			  .append("\n and point_expiration_datetime > now() ");	
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
			  .append("\n and pi2.social_id = ? ")
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
			  .append("\n and pi2.social_id = ? ")
			  .append("\n and pi2.event_id = ? ")
			  .append("\n order by pdi.point_expiration_datetime asc ");
		return _query.toString();
	}
}
