package com.lawzone.market.user.dao;

import org.springframework.stereotype.Component;

@Component
public class SellerInfoJdbcDAO {
	public String sellerYn() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	 if(count(1) > 0, 'Y','N') as sellerYn ")
			  .append("\n from lz_market.seller_info si ")
			  .append("\n 	, lz_market.user_info ui ")
			  .append("\n where si.seller_id = ui.user_id ")
			  .append("\n and ui.seller_yn = 'Y' ")
			  .append("\n and si.seller_id = ? ");	
		return _query.toString();
	}
}
