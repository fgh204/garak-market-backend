package com.lawzone.market.user.dao;

import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressInfoJdbcDAO {
	public String deliveryAddressBaseShippingYn() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update ")
			  .append("\n lz_market.delivery_address_info dai ")
			  .append("\n set dai.base_shipping_yn = 'N' ")
			  .append("\n where dai.user_id = ? ");	
		return _query.toString();
	}
}
