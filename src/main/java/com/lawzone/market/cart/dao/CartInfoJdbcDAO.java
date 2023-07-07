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
			  //.append("\n	, round(pi2.product_price, 0) as product_price ")
			  .append("\n	, round(ifnull((select case when epai.event_product_discount_cf_code = '01' then epai.event_product_discount_cf_value ")
			  .append("\n	when epai.event_product_discount_cf_code = '02' then pi2.product_price - (pi2.product_price * (epai.event_product_discount_cf_value /100)) end ")
			  .append("\n	from lz_market.event_product_amount_info epai ")
			  .append("\n	where epai.event_id = pi2.event_id ")
			  .append("\n	and DATE_FORMAT(now(), '%H%i') between epai.begin_time and epai.end_time ")
			  .append("\n 	order by epai.begin_time desc ")
			  .append("\n 	limit 1 ")
			  .append("\n	),pi2.product_price),0) as productPrice ")
			  .append("\n	, round(ci.product_count , 0) as product_count ")
			  .append("\n	, pii.thumbnail_image_path as thumbnail_image_path ")
			  .append("\n	, si.shop_name as shop_name ")
			  .append("\n	, ui.user_name as seller_name ")
			  .append("\n	, si.today_delivery_standard_time ")
			  .append("\n	, '' as todayDeliveryYn ")
			  .append("\n	, '' as slsDate ")
			  .append("\n	, ceil(pi2.product_stock) as product_stock ")
			  .append("\n	, pci.product_category_code ")
			  .append("\n	, pci.product_category_small_name ")
			  .append("\n	, si.seller_id ")
			  .append("\n 	, ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = pi2.seller_id),'') as slsDateText ")
			  .append("\n 	, '' as  eventBeginTime ")
			  .append("\n 	, '' as  eventEndTime ")
			  .append("\n 	, '' as  eventBeginDate ")
			  .append("\n 	, '' as  eventEndDate ")
			  .append("\n 	, 0 as  personBuyCount ")
			  .append("\n 	, 0 as eventCount ")
			  .append("\n 	, pi2.event_id as eventId ")
			  .append("\n 	, DATE_FORMAT(ci.create_datetime, '%Y-%m-%d %H:%i:%s') as createDatetime ")
			  .append("\n 	, DATE_FORMAT(ci.update_datetime , '%Y-%m-%d %H:%i:%s') as updateDatetime ")
			  .append("\n from lz_market.cart_info ci")
			  .append("\n 	 , lz_market.product_info pi2 ")
			  .append("\n 	 , lz_market.product_image_info pii ")
			  .append("\n 	 , lz_market.seller_info si ")
			  .append("\n 	 , lz_market.user_info ui ")
			  .append("\n 	 , lz_market.product_category_info pci ")
			  .append("\n where ci.product_id = pi2.product_id")
			  .append("\n and pi2.product_id = pii.product_id ")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pi2.seller_id = ui.user_id ")
			  .append("\n and pi2.product_category_code = pci.product_category_code ")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y' ")
			  .append("\n and pii.image_cfcd = '01' ")
			  .append("\n and pi2.use_yn = 'Y'")
			  .append("\n and ci.user_id = ?")
			  .append("\n and si.seller_id = ?");
		return _query.toString();
	}
	
	public String getCartSellerInfoList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	pi2.seller_id as sellerId")
				.append("\n 	, sum(pi2.product_price * ci.product_count) as productPrice ")
				.append("\n 	, si.combined_delivery_yn as combinedDeliveryYn ")
				.append("\n 	, si.shop_name as shopName ")
//				.append("\n 	, case when si.combined_delivery_yn = 'N' then 0 ")
//				.append("\n 		   when si.combined_delivery_yn = 'Y' AND sum(pi2.product_price * ci.product_count) > si.combined_delivery_standard_amount then 0 ")
//				.append("\n 		   else si.delivery_amount END as deliveryAmount ")
				.append("\n 	, si.delivery_amount as deliveryAmount ")
				.append("\n 	, si.combined_delivery_standard_amount as combinedDeliveryStandardAmount ")
				.append("\n from lz_market.cart_info ci ")
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n 	, lz_market.product_image_info pii ")
				.append("\n 	, lz_market.seller_info si ")
				.append("\n where ci.product_id = pi2.product_id ")
				.append("\n and pi2.product_id = pii.product_id ")
				.append("\n and pi2.seller_id = si.seller_id ")
				.append("\n and pii.delegate_thumbnail_yn = 'Y' ")
				.append("\n and pii.image_cfcd = '01' ")
				.append("\n and pi2.use_yn = 'Y' ")
				.append("\n and ci.user_id = ? ")
				.append("\n group by pi2.seller_id ")
				.append("\n order by pi2.seller_id ");
		return _query.toString();
	}
}
