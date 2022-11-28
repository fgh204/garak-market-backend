package com.lawzone.market.user.dao;

import org.springframework.stereotype.Component;

@Component
public class UserInfoJdbcDAO {
	public String marketUserInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ui.user_name ")
			  .append("\n   , ui.seller_yn ")
			  .append("\n   , ui.phone_number ")
			  .append("\n   , si.shop_name ")
			  .append("\n   , ui.email ")
			  .append("\n   , ui.nickname ")
			  .append("\n   , ui.profile_images_path ")
			  .append("\n from lz_market.user_info ui ")
			  .append("\n 	left outer join lz_market.seller_info si ")
			  .append("\n 	on ui.user_id = si.seller_id  ")
			  .append("\n where ui.user_id = ? ");
		
		return _query.toString();
	}
}
