package com.lawzone.market.product.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductJdbcDAO {
	public String pageListQuery(String pageCnt, String maxPageCnt
			, String cateCodeYn , String productIdYn , String productNameYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n pi2.product_id as productId")
			  .append("\n , pi2.product_name as productName")
			  .append("\n , round(pi2.product_price,0) as productPrice")
			  .append("\n , round(pi2.product_stock,0) as productStock")
			  .append("\n , pi2.product_desc as productDesc")
			  .append("\n , pii.thumbnail_image_path as thumbnailImagePath")
			  .append("\n , ifnull((select sum(poii.product_count) ")
			  .append("\n from lz_market.product_order_item_info poii ")
			  .append("\n where poii.product_id = pi2.product_id ")
			  .append("\n and poii.order_item_state_code = '003'),0) as cumulative_sales_count ")
			  .append("\n , si.shop_name as shopName ")
			  .append("\n from lz_market.product_info pi2")
			  .append("\n , lz_market.product_image_info pii")
			  .append("\n , lz_market.seller_info si")
			  .append("\n where pi2.product_id = pii.product_id")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y'")
			  .append("\n and pi2.use_yn = 'Y'");
		
		if("Y".equals(cateCodeYn)) {
			_query.append("\n and pi2.product_categories_code like concat('%',?,'%')");
		}
		
		if("Y".equals(productIdYn)) {
			_query.append("\n and pi2.product_id = ?");
		}
		
		if("Y".equals(productNameYn)) {
			_query.append("\n and pi2.product_name like concat('%',?,'%')");
		}
		_query.append("\n order by pi2.product_id DESC")
			  .append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String pageQuery(String maxPageCnt
			, String cateCodeYn , String productIdYn , String productNameYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_info pi2")
			  .append("\n 	, lz_market.product_image_info pii")
			  .append("\n 	, lz_market.seller_info si")
			  .append("\n where pi2.product_id = pii.product_id")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y'")
			  .append("\n and pi2.use_yn = 'Y'");
		if("Y".equals(cateCodeYn)) {
			_query.append("\n and pi2.product_categories_code = ?");
		}
		if("Y".equals(productIdYn)) {
			_query.append("\n and pi2.product_id = ?");
		}
		
		if("Y".equals(productNameYn)) {
			_query.append("\n and pi2.product_name like concat('%',?,'%')");
		}
		return _query.toString();
	}
	
	public String modifyProductStock(String _productIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_info pi2, lz_market.product_order_item_info poii")
			  //.append("\n set pi2.product_stock = (pi2.product_stock - poii.product_count)")
				.append("\n set pi2.product_stock = (case when poii.order_item_state_code = '001' then (pi2.product_stock - poii.product_count) ")
				.append("\n 							when poii.order_item_state_code = '003' then (pi2.product_stock + poii.product_count)")
				.append("\n								else pi2.product_stock end ) ")
			  .append("\n where pi2.product_id = poii.product_id")
			  .append("\n and poii.order_no = ?")
			  .append("\n and poii.order_item_state_code  = ?");
		if("Y".equals(_productIdYn)) {
			_query.append("\n and poii.product_id = ?");
		}
		
		return _query.toString();
	}
	
	public String productInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n SELECT ")
				.append("\n 	pi2.product_id as product_id ")
				.append("\n 	, DATE_FORMAT(pi2.begin_date, '%Y-%m-%d') as begin_date ")
				.append("\n 	, DATE_FORMAT(pi2.end_date, '%Y-%m-%d') as end_date ")
				.append("\n 	, pi2.product_name as product_name ")
				.append("\n 	, ceil(pi2.product_price) as product_price ")
				.append("\n 	, ceil(pi2.product_stock) as product_stock ")
				.append("\n 	, pi2.product_desc as product_desc ")
				.append("\n 	, pi2.use_yn as use_yn ")
				.append("\n 	, pi2.product_categories_code as product_categories_code ")
				.append("\n 	, pi2.seller_id as seller_id ")
				.append("\n 	, DATE_FORMAT(pi2.create_date, '%Y-%m-%d %H:%i:%s') as create_date ")
				.append("\n 	, DATE_FORMAT(pi2.update_date, '%Y-%m-%d %H:%i:%s') as update_date ")
				.append("\n 	, si.shop_name as shop_name ")
				.append("\n 	, ui.user_name as user_name ")
				.append("\n FROM lz_market.product_info pi2 ")
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n WHERE pi2.seller_id = si.seller_id ")
				.append("\n and pi2.seller_id = ui.user_id ")
				.append("\n and pi2.product_id= ? ");
		
		return _query.toString();
	}
}
