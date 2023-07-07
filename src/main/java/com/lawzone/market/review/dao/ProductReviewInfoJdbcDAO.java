package com.lawzone.market.review.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductReviewInfoJdbcDAO {
	public String getProductReviewPageInfo(String maxPageCnt, String _productId, String _sellerId, String _productScore) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_review_info pri")
			  .append("\n 	, lz_market.user_info ui ")
			  .append("\n 	, lz_market.product_info pi2")
			  .append("\n where pri.product_id = pi2.product_id")
			  .append("\n and pri.user_id = ui.user_id");
		
			if(!("".equals(_productId) || _productId == null)) {
				_query.append("\n and pri.product_id = ? ");
			}		
			if(!("".equals(_sellerId) || _sellerId == null)) {
				_query.append("\n and pi2.seller_id = ? ");
			}		
			if(!("".equals(_productScore) || _productScore == null)) {
				_query.append("\n and pri.product_score  = ?");
			}
		
		return _query.toString();
	}
	
	public String getProductReviewList(String pageCnt, String productScore, String maxPageCnt, String orderCd, String _productId, String _sellerId) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	ui.user_name as user_name ")
				.append("\n 	, ui.nickname  as nickname ")
				.append("\n 	, pri.review_number as review_number ")
				.append("\n 	, pri.product_score as product_score ")
				.append("\n 	, pri.fresh_score as fresh_score ")
				.append("\n 	, pri.taste_score as taste_score ")
				.append("\n 	, pri.packaging_score as packaging_score ")
				.append("\n 	, pri.delivery_score as delivery_score ")
				.append("\n 	, pri.reorder_score  as reorder_score ")
				.append("\n 	, pi2.product_id as product_id ")
				.append("\n 	, lz_market.FN_PRDT_NM(pi2.product_id) as product_name ")
				.append("\n 	, DATE_FORMAT(pri.update_datetime, '%Y-%m-%d') as review_date ")
				.append("\n 	, pri.review_title as review_title ")
				.append("\n 	, pri.review_text as review_text ")
				.append("\n 	, (select user_name  from lz_market.user_info ui2 ")
				.append("\n 		where ui2.user_id = pi2.seller_id) as seller_name ")
				.append("\n 	, pri.order_no as order_no ")
				.append("\n 	, DATE_FORMAT(pri.create_datetime, '%Y-%m-%d %H:%i:%s') as createDatetime ")
				.append("\n 	, DATE_FORMAT(pri.update_datetime, '%Y-%m-%d %H:%i:%s') as updateDatetime ")
				.append("\n 	, ui.profile_images_path ")
				.append("\n from lz_market.product_review_info pri ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n where pri.product_id = pi2.product_id ")
				.append("\n and pri.user_id = ui.user_id ");
		if(!("".equals(_productId) || _productId == null)) {
			_query.append("\n and pri.product_id = ? ");
		}		
		if(!("".equals(_sellerId) || _sellerId == null)) {
			_query.append("\n and pi2.seller_id = ? ");
		}		
		if(!("".equals(productScore) || productScore == null)) {
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
				.append("\n and poii.order_no = ? ")
				.append("\n and poii.order_item_state_code <> '001' ");
		
		return _query.toString();
	}
	
	public String getAverageScoreInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	round(sum(pri.product_score)/ count(1),1) as product_average_score ")
			  .append("\n   , round(sum(pri.fresh_score)/ count(1),1) as fresh_average_score ")
			  .append("\n 	, round(sum(pri.taste_score)/ count(1),1) as taste_average_score ")
			  .append("\n 	, round(sum(pri.packaging_score)/ count(1),1) as packaging_average_score ")
			  .append("\n 	, round(sum(pri.delivery_score)/ count(1),1) as delivery_average_score ")
			  .append("\n 	, count(1) as seller_review_count ")
			  .append("\n from lz_market.product_review_info pri ")
			  .append("\n 	, lz_market.product_info pi2 ")
			  .append("\n where pri.product_id = pi2.product_id ")
			  .append("\n and pi2.seller_id = ? ");		
		
		return _query.toString();
	}
	
	public String getMyProductReviewPageInfo(String maxPageCnt, String productIdYn, String orderNoYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_review_info pri")
			  .append("\n 	, lz_market.user_info ui ")
			  .append("\n 	, lz_market.product_info pi2")
			  .append("\n where pri.product_id = pi2.product_id")
			  .append("\n and pri.user_id = ui.user_id")
			  .append("\n and pri.user_id = ? ");
		if("Y".equals(productIdYn)) {
			_query.append("\n and pri.product_id = ? ");
		}
		
		if("Y".equals(orderNoYn)) {
			_query.append("\n and pri.order_no = ? ");
		}
			  
		return _query.toString();
	}
	
	public String getMyProductReviewList(String pageCnt, String maxPageCnt, String orderCd, String productIdYn, String orderNoYn ) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	ui.user_name as user_name ")
				.append("\n 	, ui.nickname  as nickname ")
				.append("\n 	, pri.review_number as review_number ")
				.append("\n 	, pri.product_score as product_score ")
				.append("\n 	, pri.fresh_score as fresh_score ")
				.append("\n 	, pri.taste_score as taste_score ")
				.append("\n 	, pri.packaging_score as packaging_score ")
				.append("\n 	, pri.delivery_score as delivery_score ")
				.append("\n 	, pri.reorder_score  as reorder_score ")
				.append("\n 	, pi2.product_id as product_id ")
				.append("\n 	, lz_market.FN_PRDT_NM(pi2.product_id) as product_name ")
				.append("\n 	, DATE_FORMAT(pri.update_datetime, '%Y-%m-%d') as review_date ")
				.append("\n 	, pri.review_title as review_title ")
				.append("\n 	, pri.review_text as review_text ")
				.append("\n 	, (select user_name  from lz_market.user_info ui2 ")
				.append("\n 		where ui2.user_id = pi2.seller_id) as seller_name ")
				.append("\n 	, pri.order_no as order_no ")
				.append("\n 	, DATE_FORMAT(pri.create_datetime, '%Y-%m-%d %H:%i:%s') as createDatetime ")
				.append("\n 	, DATE_FORMAT(pri.update_datetime, '%Y-%m-%d %H:%i:%s') as updateDatetime ")
				.append("\n 	, ui.profile_images_path ")
				.append("\n from lz_market.product_review_info pri ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n 	, lz_market.product_info pi2 ")
				.append("\n where pri.product_id = pi2.product_id ")
				.append("\n and pri.user_id = ui.user_id ")
				.append("\n and pri.user_id = ? ");
		if("Y".equals(productIdYn)) {
			_query.append("\n and pri.product_id = ? ");
		}
				
		if("Y".equals(orderNoYn)) {
			_query.append("\n and pri.order_no = ? ");
		}

		if("01".equals(orderCd)) {
			_query.append("\n order by pri.review_number desc ");
		}else {
			_query.append("\n order by pri.product_score desc ");
		}
		_query.append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String getProductReviewCountInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select sum(b.taste_score_3) as taste_score_3 ")
				.append("\n  		, sum(b.taste_score_2) as taste_score_2 ")
				.append("\n  		, sum(b.taste_score_1) as taste_score_1 ")
				.append("\n  		, sum(b.fresh_score_3) as fresh_score_3 ")
				.append("\n  		, sum(b.fresh_score_2) as fresh_score_2 ")
				.append("\n  		, sum(b.fresh_score_1) as fresh_score_1 ")
				.append("\n  		, sum(b.delivery_score_3) as delivery_score_3 ")
				.append("\n  		, sum(b.delivery_score_2) as delivery_score_2 ")
				.append("\n  		, sum(b.delivery_score_1) as delivery_score_1 ")
				.append("\n  		, sum(b.reorder_score_3) as reorder_score_3 ")
				.append("\n  		, sum(b.reorder_score_2) as reorder_score_2 ")
				.append("\n  		, sum(b.reorder_score_1) as reorder_score_1 ")
				.append("\n			, ROUND((sum(b.taste_score_3)/sum(b.total)) * 100) as taste_score_3_rate ")
				.append("\n			, ROUND((sum(b.taste_score_2)/sum(b.total)) * 100) as taste_score_2_rate ")
				.append("\n			, ROUND( (sum(b.taste_score_1)/sum(b.total)) * 100) as taste_score_1_rate ")
				.append("\n			, ROUND((sum(b.fresh_score_3)/sum(b.total)) * 100) as fresh_score_3_rate ")
				.append("\n			, ROUND((sum(b.fresh_score_2)/sum(b.total)) * 100) as fresh_score_2_rate ")
				.append("\n			, ROUND( (sum(b.fresh_score_1)/sum(b.total)) * 100) as fresh_score_1_rate ")
				.append("\n			, ROUND((sum(b.delivery_score_3)/sum(b.total)) * 100) as delivery_score_3_rate ")
				.append("\n			, ROUND((sum(b.delivery_score_2)/sum(b.total)) * 100) as delivery_score_2_rate ")
				.append("\n			, ROUND( (sum(b.delivery_score_1)/sum(b.total)) * 100) as delivery_score_1_rate ")
				.append("\n			, ROUND((sum(b.reorder_score_3)/sum(b.total)) * 100) as reorder_score_3_rate ")
				.append("\n			, ROUND((sum(b.reorder_score_2)/sum(b.total)) * 100) as reorder_score_2_rate ")
				.append("\n			, ROUND( (sum(b.reorder_score_1)/sum(b.total)) * 100) as reorder_score_1_rate ")
				.append("\n  from ( ")
				.append("\n  select  ")
				.append("\n  	case when a.taste_score = 3 then '1' else '0' end as taste_score_3 ")
				.append("\n  	, case when a.taste_score = 2 then '1' else '0' end as taste_score_2 ")
				.append("\n  	, case when a.taste_score = 1 then '1' else '0' end as taste_score_1 ")
				.append("\n  	, case when a.fresh_score = 3 then '1' else '0' end as fresh_score_3 ")
				.append("\n  	, case when a.fresh_score = 2 then '1' else '0' end as fresh_score_2 ")
				.append("\n  	, case when a.fresh_score = 1 then '1' else '0' end as fresh_score_1 ")
				.append("\n  	, case when a.delivery_score = 3 then '1' else '0' end as delivery_score_3 ")
				.append("\n  	, case when a.delivery_score = 2 then '1' else '0' end as delivery_score_2 ")
				.append("\n  	, case when a.delivery_score = 1 then '1' else '0' end as delivery_score_1 ")
				.append("\n  	, case when a.reorder_score = 3 then '1' else '0' end as reorder_score_3 ")
				.append("\n  	, case when a.reorder_score = 2 then '1' else '0' end as reorder_score_2 ")
				.append("\n  	, case when a.reorder_score = 1 then '1' else '0' end as reorder_score_1 ")
				.append("\n  	, '1' as total ")
				.append("\n  from ( ")
				.append("\n  select ")
				.append("\n     pri.taste_score  ")
				.append("\n     , pri.fresh_score  ")
				.append("\n     , pri.delivery_score  ")
				.append("\n     , pri.reorder_score  ")
				.append("\n from ")
				.append("\n     lz_market.product_review_info pri    , ")
				.append("\n     lz_market.product_info pi2    ")
				.append("\n where ")
				.append("\n     pri.product_id = pi2.product_id    ")
				.append("\n     and pi2.seller_id = ? ) a ")
				.append("\n  )b ");		
		
		return _query.toString();
	}
	
	public String adminCustReviewPageInfo(String page, String maxPageCnt) {
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
			.append("\n from lz_market.product_review_info pri ") 
			.append("\n , lz_market.user_info ui ")
			.append("\n where pri.user_id = ui.user_id ") 
			.append("\n and pri.create_datetime between ? and ? ")
			.append("\n )page ");
		
		return _query.toString();
	}
	
	public String adminCustReviewInfoList(String page, String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n 	 pri.product_id ")
			.append("\n 	 , lz_market.FN_PRDT_NM(pri.product_id) ")
			.append("\n 	 , pri.order_no ")
			.append("\n 	 , pri.user_id ")
			.append("\n 	 , pri.taste_score ")
			.append("\n 	 , pri.fresh_score ")
			.append("\n 	 , pri.delivery_score ")
			.append("\n 	 , pri.reorder_score ")
			.append("\n 	 , pri.product_score ")
			.append("\n 	 , pri.packaging_score ")
			.append("\n 	 , pri.review_title ")
			.append("\n 	 , pri.review_text ")
			.append("\n 	 , ui.user_name ")
			.append("\n 	 , DATE_FORMAT(pri.update_datetime, '%Y.%m.%d %H:%i:%s') as updateDatetime ")
			.append("\n 	 , ui.phone_number ")
			.append("\n 	 , (select case when count(1) > 0 then 'Y' else 'N' end  ")
			.append("\n 	 from lz_market.product_image_info pii ")
			.append("\n 	 where pii.product_id = pri.product_id ")
			.append("\n 	 and pii.order_no = pri.order_no) as reviewImgYn ")
			.append("\n from lz_market.product_review_info pri ")
			.append("\n 	, lz_market.user_info ui ")
			.append("\n where pri.user_id = ui.user_id ")
			.append("\n and pri.create_datetime between ? and ? ")
			.append("\n order by pri.update_datetime desc ")
			.append("\n limit " + page + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String adminCustReviewImgInfoList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n		pii.product_id ")
			.append("\n 	 , pii.order_no ")
			.append("\n 	 , pii.thumbnail_image_path ")
			.append("\n from lz_market.product_image_info pii ")
			.append("\n where pii.product_id = ? ")
			.append("\n and pii.order_no = ? ");
		return _query.toString();
	}
}
