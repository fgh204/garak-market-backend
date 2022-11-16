package com.lawzone.market.review.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductReviewInfoJdbcDAO {
	public String getProductReviewPageInfo(String maxPageCnt, String ReviewRank) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_review_info pri")
			  .append("\n 	, lz_market.user_info ui ")
			  .append("\n 	, lz_market.product_info pi2")
			  .append("\n where pri.product_id = pi2.product_id")
			  .append("\n and pri.user_id = ui.user_id")
			  .append("\n and pri.product_id = ?");
		
		if(!"".equals(ReviewRank)) {
			_query.append("\n and pri.product_score  = ?");
		}

		return _query.toString();
	}
	
	public String getProductReviewList(String pageCnt, String productScore, String maxPageCnt, String orderCd) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	ui.user_name as user_name ")
				.append("\n 	, pri.review_number as review_number ")
				.append("\n 	, pri.product_score as product_score ")
				.append("\n 	, pri.fresh_score as fresh_score ")
				.append("\n 	, pri.taste_score as taste_score ")
				.append("\n 	, pri.packaging_score as packaging_score ")
				.append("\n 	, pri.shipping_score as shipping_score ")
				.append("\n 	, pi2.product_id as product_id ")
				.append("\n 	, lz_market.FN_PRDT_NM(pi2.product_id) as product_name ")
				.append("\n 	, DATE_FORMAT(pri.update_date, '%Y-%m-%d') as review_date ")
				.append("\n 	, pri.review_title as review_title ")
				.append("\n 	, pri.review_text as review_text ")
				.append("\n 	, (select user_name  from lz_market.user_info ui2 ")
				.append("\n 		where ui2.user_id = pi2.seller_id) as seller_name ")
				.append("\n from lz_market.product_review_info pri ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n where pri.product_id = pi2.product_id ")
				.append("\n and pri.user_id = ui.user_id ")
				.append("\n and pri.product_id = ? ");
		if(!"".equals(productScore)) {
			_query.append("\n and pri.product_score  = ?");
		}
		if("01".equals(orderCd)) {
			_query.append("\n order by pri.review_number desc ");
		}else {
			_query.append("\n order by pri.product_score desc ");
		}
		_query.append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String chkReviewYn() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	case when count(1) > 0 then 'Y' else 'N' END ")
				.append("\n from lz_market.product_order_info poi ")
				.append("\n  	, lz_market.product_order_item_info poii ")
				.append("\n where poi.order_no = poii.order_no ")
				.append("\n and poi.user_id = ? ")
				.append("\n and poii.product_id = ? ")
				.append("\n and poii.order_item_state_code = '003' ");
		
		return _query.toString();
	}
}
