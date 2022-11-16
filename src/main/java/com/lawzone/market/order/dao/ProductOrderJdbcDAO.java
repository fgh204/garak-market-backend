package com.lawzone.market.order.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductOrderJdbcDAO {
	public String custProductItemByOrderId(String statCd) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  //.append("\n	b.order_no as order_no")
			  .append("\n   lz_market.FN_PRDT_NM(b.product_id) as product_name")
			  .append("\n	, lz_market.FN_DTL_NM(2,b.order_item_state_code) as state_name")
			  .append("\n	, b.product_count as product_count")
			  .append("\n	, round(b.product_price,0)")
			  .append("\n	, pii.thumbnail_image_path ")
			  .append("\n	, b.product_id ")
			  .append("\nfrom lz_market.product_order_item_info b")
			  .append("\n	, lz_market.product_image_info pii")
			  .append("\nwhere b.product_id = pii.product_id")
			  .append("\nand b.order_no = ?")
			  .append("\nand pii.delegate_thumbnail_yn = 'Y'");
		if("".equals(statCd)) {
			_query.append("\nand b.order_item_state_code <> ?");
		}else {
			_query.append("\nand b.order_item_state_code = ?");
		}
		
			  		
		return _query.toString();
	}
	
	public String custProductInfoByOrderId(String statCd) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\nselect") 
				.append("\n	poi.order_no")  
				.append("\n	, ui.user_name")   
				.append("\n	, poi.zonecode")
				.append("\n	, round(poi.product_total_price,0) ") 
				.append("\n	, case when ifnull(poi.building_name,'') = '' then poi.address\r\n"
						+ "        	else concat(poi.address , ' (' , poi.building_name ,')') end as address ") 
				.append("\n	, poi.detail_address ")
				.append("\n	, poi.order_name ")
				.append("\n	, poi.recipient_name ")
				.append("\n	, DATE_FORMAT(poi.order_date, '%Y-%m-%d') as order_date ")
				.append("\n	, poi.phone_number as phone_number ")
				.append("\n	, poi.sub_phone_number as sub_phone_number ")
				.append("\n	, poi.delivery_message as delivery_message ")
				.append("\nfrom lz_market.product_order_info poi ") 
				.append("\n	, lz_market.user_info ui ") 
				.append("\nwhere poi.user_id = ui.user_id ")
				.append("\nand order_no = ?");
		
				if("".equals(statCd)) {
					_query.append("\nand order_state_code <> ?");
				}else {
					_query.append("\nand order_state_code = ?");
				}
					
		return _query.toString();
	}
	
	public String modifyOrderInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_info")
			  .append("\n set order_state_code = ?")
			  .append("\n , update_date = now()")
			  .append("\n where order_no = ?")
			  .append("\n and order_state_code = ?");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoStat(String _productIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_state_code = ?")
				.append("\n , update_date = now()")
				.append("\n where order_no = ?")
				.append("\n and order_item_state_code = ?");
				if("Y".equals(_productIdYn)) {
					_query.append("\nand product_id = ?");
				}
		
		return _query.toString();
	}
	
	public String adminOrderItemList(String userNameYn, String page) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	poii.order_no as orderNo ")
				.append("\n 	, concat(substr(pi2.product_name,1,20), '...') as orderName ") 
				.append("\n 	, DATE_FORMAT(poi.order_date,'%Y-%m-%d') as orderDate ")
				.append("\n 	, lz_market.FN_DTL_NM(2, poii.order_item_state_code) as statName ")
				.append("\n 	, concat(poii.product_count,'') as orderCount ")
				.append("\n 	, format(poii.product_price,0) as productPrice ")
				.append("\n 	, ui.user_name as userName ")
				.append("\n from lz_market.product_order_item_info poii ") 
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n 	, lz_market.product_order_info poi ") 
				.append("\n 	, lz_market.user_info ui ")
				.append("\n where poii.product_id = pi2.product_id ") 
				.append("\n and poii.order_no = poi.order_no ")
				.append("\n and poi.user_id = ui.user_id ")
				.append("\n and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ")
				.append("\n and poii.order_item_state_code like concat('%',?,'%') ")
				.append("\n and pi2.seller_id = ? ");
				if("Y".equals(userNameYn)) {
					_query.append("\n and ui.user_name = ?");
				}
				_query.append("\n limit " + page + " , 10");
		return _query.toString();
	}
	
	public String adminOrderItemPageInfo(String userNameYn, String page) {
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
				.append("\n from lz_market.product_order_item_info poii ") 
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n 	, lz_market.product_order_info poi ") 
				.append("\n 	, lz_market.user_info ui ")
				.append("\n where poii.product_id = pi2.product_id ") 
				.append("\n and poii.order_no = poi.order_no ")
				.append("\n and poi.user_id = ui.user_id ")
				.append("\n and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ")
				.append("\n and poii.order_item_state_code like concat('%',?,'%') ")
				.append("\n and pi2.seller_id = ? ");
				if("Y".equals(userNameYn)) {
					_query.append("\n and ui.user_name = ?");
				}
				_query.append(")page");
		return _query.toString();
	}
	
	public String custOrderList(String pageCnt, String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
		  .append("\n poi.order_no as order_no")
		  .append("\n , DATE_FORMAT(poi.order_date ,'%Y-%m-%d') as order_date")
		  .append("\n , poi.order_state_code as order_state_code ")
		  .append("\n , lz_market.FN_DTL_NM(1,poi.order_state_code) as order_state_name ")
		  .append("\n from lz_market.product_order_info poi ")
		  .append("\n where order_state_code <> '001'")
		  .append("\n and user_id = ?")
		  .append("\n order by create_date desc ")
		  .append("\n limit " + pageCnt + ", " + maxPageCnt);
		
	return _query.toString();
	}
	
	public String custOrderListPageInfo(String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_cnt")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_order_info poi")
			  .append("\n where order_state_code <> '001'")
			  .append("\n and user_id = ?");
		
		return _query.toString();
	}
}
