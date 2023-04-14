package com.lawzone.market.admin.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductOrderItemBookIdInfoJdbcDAO {
	public String adminBookIdList(String sellerIdYn, String deliverYn, String page) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	DATE_FORMAT(a.order_date,'%Y-%m-%d') as order_date ")
		.append("\n 	, DATE_FORMAT(pi2.create_datetime, '%Y-%m-%d %H:%i:%s') as order_dttm ")
		.append("\n 	, a.product_id ")
		.append("\n 	, a.book_id ")
		.append("\n 	, a.dong_group ")
		.append("\n 	, concat(a.receiver_address_road_cleaned, ' ', ifnull(a.receiver_address_building,''), '/' , a.receiver_address_cleaned) as receiver_address_road_cleaned ")
		.append("\n 	, a.receiver_address_road_detail ")
		.append("\n 	, a.receiver_address ")
		.append("\n 	, a.product_name ")
		.append("\n 	, a.delivery_message ")
		.append("\n 	, case when length(a.phone_number) = 11 then concat(substr(a.phone_number,1,3), '****', substr(a.phone_number,8,4)) else a.phone_number end as phone_number ")
		.append("\n 	, concat(substr(a.recipient_name,1,1) , '*', substr(a.recipient_name,3))  as  recipient_name ")
		.append("\n 	, a.order_no ")
		.append("\n 	, a.order_id_from_corp ")
		.append("\n 	, a.zonecode ")
		.append("\n 	, a.product_price ")
		.append("\n 	, a.address ")
		.append("\n 	, a.print_yn ")
		.append("\n 	, '' as chkValue ")
		.append("\n 	, DATE_FORMAT(a.update_datetime, '%Y/%m/%d %H:%i') as updateDatetime ")
		.append("\n 	, b.today_delivery_standard_time ")
		.append("\n 	, a.spot_name ")
		.append("\n 	, a.business_address ")
		.append("\n 	, a.shop_name ")
		.append("\n 	, a.result_message ")
		//.append("\n 	, case when c.order_item_dlng_state_code = '400' then 'Y' ELSE 'N' END as deliveryCompleteYn ")
		.append("\n 	, case when a.delivery_state_code = '400' then 'Y' ELSE 'N' END as deliveryCompleteYn ")
		.append("\n 	, a.seller_id as sellerId ")
		.append("\n 	, a.product_category_code as productCategoryCode ")
		.append("\n 	, a.delivery_order_id as deliveryOrderId ")
		.append("\n 	, a.access_method_text as accessMethodText ")
		.append("\n 	, a.combined_delivery_yn as combinedDeliveryYn ")
		.append("\n 	, '' as imgBase64 ")
		.append("\n from lz_market.product_order_item_book_id_info a ")
		.append("\n 	, lz_market.product_info b ")
		.append("\n 	, lz_market.product_order_item_info c ")
		.append("\n 	, lz_market.payment_info pi2 ")
		.append("\n where a.product_id = b.product_id ")
		.append("\n and a.order_no = c.order_no ")
		.append("\n and a.product_id = c.product_id ")
		.append("\n and a.order_no  = pi2.order_no ")
		.append("\n and c.order_item_state_code  <> '002' ");
		if("N".equals(deliverYn)) {
			_query.append("\n and a.delivery_state_code <> '400' ");
			//_query.append("\n and c.order_item_dlng_state_code not in('400','800') ");
		}
		_query.append("\n and a.order_date BETWEEN ? and ? ");
		if("Y".equals(sellerIdYn)) {
			_query.append("\n and b.seller_id = ? ");
		}
		_query.append("\n order by a.order_date desc, a.product_name ")
		.append("\n limit " + page + " , 10");
		return _query.toString();
	}
	
	public String adminBookIdPageInfo(String sellerIdYn, String deliverYn, String page) {
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
				.append("\n from lz_market.product_order_item_book_id_info a ") 
				.append("\n 	, lz_market.product_info b ")
				.append("\n 	, lz_market.product_order_item_info c ")
				.append("\n where a.product_id = b.product_id ")
				.append("\n and a.order_no = c.order_no ")
				.append("\n and a.product_id = c.product_id ")
				.append("\n and c.order_item_state_code <> '002' ");
				if("N".equals(deliverYn)) {
					_query.append("\n and a.delivery_state_code <> '400' ");
				}
				_query.append("\n and a.order_date BETWEEN ? and ? ");
				if("Y".equals(sellerIdYn)) {
					_query.append("\n and b.seller_id = ?");
				}
				_query.append("\n )page ");
		return _query.toString();
	}
	
	public String modifyBookIdPrintYn() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n update lz_market.product_order_item_book_id_info ")
				.append("\n set print_yn = ? ")
				.append("\n where order_id_from_corp = ? ")
				.append("\n and order_no = ? ") 
				.append("\n and product_id = ? ");
		
		return _query.toString();
	}
	
	public String adminBookIdSellerInfo() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n si.spot_id ")
				.append("\n , si.shop_name ")
				.append("\n , si.seller_phone_number ")
				.append("\n , si.business_address ")
				.append("\n , si.spot_name ")
				.append("\n , DATE_FORMAT(poii.create_datetime, '%H%i') ")
				.append("\n , poi.phone_number ")
				.append("\n , poi.recipient_name ")
				.append("\n , si.zonecode ")
				.append("\n , si.delivery_amount ")
				.append("\n , (select pci.product_category_small_name ")
				.append("\n from lz_market.product_category_info pci  ")
				.append("\n where pci.product_category_code = si.product_category_code) ")
				.append("\n , DATE_FORMAT(poi.create_datetime ,'%Y-%m-%dT%H:%i:%s+09:00') ")
				.append("\n , pi2.product_name ")
				.append("\n , poii.product_id ")
				.append("\n , poii.product_price ")
				.append("\n , poii.product_count ")
				.append("\n from lz_market.product_order_item_info poii ")
				.append("\n 	, lz_market.product_info pi2 ") 
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.product_order_info poi ")
				.append("\n where poii.product_id = pi2.product_id ") 
				.append("\n and pi2.seller_id = si.seller_id ")
				.append("\n and poii.order_no = poi.order_no ")
				.append("\n and poii.order_no = ? ")
				.append("\n and poii.product_id = ? ");

		return _query.toString();
	}
	
	public String adminBookIdSellerInfo2() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n si.spot_id ")
				.append("\n , si.shop_name ")
				.append("\n , si.seller_phone_number ")
				.append("\n , si.business_address ")
				.append("\n , si.spot_name ")
				.append("\n , DATE_FORMAT(poii.create_datetime, '%H%i') ")
				.append("\n , poi.phone_number ")
				.append("\n , poi.recipient_name ")
				.append("\n , si.zonecode ")
				.append("\n , si.delivery_amount ")
				.append("\n , (select pci.product_category_small_name ")
				.append("\n from lz_market.product_category_info pci  ")
				.append("\n where pci.product_category_code = si.product_category_code) ")
				.append("\n , DATE_FORMAT(poi.create_datetime ,'%Y-%m-%dT%H:%i:%s+09:00') ")
				.append("\n , pi2.product_name ")
				.append("\n , poii.product_id ")
				.append("\n , poii.product_price ")
				.append("\n , poii.product_count ")
				.append("\n from lz_market.product_order_item_info poii ")
				.append("\n 	, lz_market.product_info pi2 ") 
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.product_order_info poi ")
				.append("\n where poii.product_id = pi2.product_id ") 
				.append("\n and pi2.seller_id = si.seller_id ")
				.append("\n and poii.order_no = poi.order_no ")
				.append("\n and poii.order_no = ? ")
				.append("\n and poii.seller_id = ? ");

		return _query.toString();
	}
	
	public String orderBookIdInfo() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n poi.order_no ")
				.append("\n , poii.product_id ")
				.append("\n , DATE_FORMAT(poi.order_date,'%Y-%m-%d') as orderDate ")
				.append("\n , (select  ")
				.append("\n poibii.book_id  ")
				.append("\n from lz_market.product_order_item_book_id_info poibii ")
				.append("\n where poibii.order_id_from_corp = concat(poi.order_no,'_1') ")
				.append("\n and poibii.product_id = poii.product_id) as bookId ")
				.append("\n from lz_market.product_order_info poi ")
				.append("\n 	, lz_market.product_order_item_info poii ") 
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n where poi.order_no = poii.order_no ") 
				.append("\n and poii.product_id = pi2.product_id ")
				.append("\n and poi.user_id = ui.user_id ")
				.append("\n and poi.order_date <= now() ")
				.append("\n and poii.order_item_dlng_state_code = '300' ");

		return _query.toString();
	}
	
	public String orderDeliveryBookIdInfo() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n poibii.order_id_from_corp ")
				.append("\n , poibii.order_no ")
				.append("\n , poibii.product_id ")
				.append("\n , poibii.book_id ")
				.append("\n , DATE_FORMAT(poibii.order_date,'%Y-%m-%d') as orderDate ")
				.append("\n , poibii.seller_id  as sellerId ")
				.append("\n , poibii.product_category_code as productCategoryCode ")
				.append("\n , si.delivery_amount  as deliveryAmount ")
				.append("\n from lz_market.product_order_item_book_id_info poibii ")
				.append("\n 	, lz_market.seller_info si  ")
				.append("\n where poibii.seller_id = si.seller_id  ")
				.append("\n and poibii.order_date <= now() ")
				.append("\n and poibii.delivery_state_code = '300' ");

		return _query.toString();
	}
}
