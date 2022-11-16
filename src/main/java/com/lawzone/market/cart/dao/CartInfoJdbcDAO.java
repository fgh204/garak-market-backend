package com.lawzone.market.cart.dao;

import org.springframework.stereotype.Component;

@Component
public class CartInfoJdbcDAO {
	public String getCartInfoList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	pi2.product_id as product_id ")
			  .append("\n	, ci.cart_number as cart_number ")
			  .append("\n	, pi2.product_name  as product_name ")
			  .append("\n	, round(pi2.product_price, 0) as product_price ")
			  .append("\n	, round(ci.product_count , 0) as product_count ")
			  .append("\n	, pii.thumbnail_image_path as thumbnail_image_path ")
			  .append("\n	, si.shop_name as shop_name ")
			  .append("\n from lz_market.cart_info ci")
			  .append("\n 	 , lz_market.product_info pi2 ")
			  .append("\n 	 , lz_market.product_image_info pii ")
			  .append("\n 	 , lz_market.seller_info si ")
			  .append("\n where ci.product_id = pi2.product_id")
			  .append("\n and pi2.product_id = pii.product_id ")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y'")
			  .append("\n and pi2.use_yn = 'Y'")
			  .append("\n and ci.user_id = ?");
		return _query.toString();
	}
}
