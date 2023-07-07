package com.lawzone.market.product.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductJdbcDAO {
	public String pageListQuery(String pageCnt, String maxPageCnt
			, String cateCodeYn, String productIdYn, String productNameYn, String sellerYn
			, String favoriteYn, String sellerId, String useYn, String eventId, Boolean isSoldOutHidden, String productSortCode) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select * from (select")
			  .append("\n pi2.product_id as productId")
			  .append("\n , pi2.product_name as productName")
			  //.append("\n , round(pi2.product_price,0) as productPrice")
			  .append("\n , round(ifnull((select case when epai.event_product_discount_cf_code = '01' then epai.event_product_discount_cf_value ")
			  .append("\n when epai.event_product_discount_cf_code = '02' then pi2.product_price - (pi2.product_price * (epai.event_product_discount_cf_value /100)) end ")
			  .append("\n from lz_market.event_product_amount_info epai ")
			  .append("\n where epai.event_id = pi2.event_id ")
			  .append("\n and DATE_FORMAT(now(), '%H%i') between epai.begin_time and epai.end_time ")
			  .append("\n order by epai.begin_time desc ")
			  .append("\n limit 1 ")
			  .append("\n ),pi2.product_price),0) as productPrice ")
			  .append("\n , round(pi2.product_stock,0) as productStock")
			  .append("\n , pi2.product_desc as productDesc")
			  .append("\n , pii.thumbnail_image_path as thumbnailImagePath")
			  .append("\n , ifnull((select sum(poii.product_count) ")
			  .append("\n from lz_market.product_order_item_info poii ")
			  .append("\n where poii.product_id = pi2.product_id ")
			  .append("\n and poii.order_item_state_code = '003'),0) as cumulative_sales_count ")
			  .append("\n , si.shop_name as shopName ")
			  .append("\n , si.today_delivery_standard_time as today_delivery_standard_time ")
			  .append("\n , pi2.product_category_code as productCategoryCode ")
			  .append("\n , pci.product_category_small_name as productCategorySmallName ")
			  .append("\n , ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = pi2.seller_id),'') as slsDateText ")
			  .append("\n , pi2.use_yn as useYn ")
			  .append("\n , pi2.event_id as eventId ")
			  .append("\n , ceil(si.combined_delivery_standard_amount) as combinedDeliveryStandardAmount ")
			  .append("\n , ceil(si.delivery_amount) as deliveryAmount ")
			  .append("\n , si.combined_delivery_yn as combinedDeliveryYn ")
			  .append("\n , (select count(1) from lz_market.product_order_item_info poii ")
			  .append("\n where poii.product_id = pi2.product_id ")
			  .append("\n and poii.delivery_state_code = '400') as paymentCount ")
			  .append("\n , (select count(1) from lz_market.product_review_info pri ")
			  .append("\n where pri.product_id = pi2.product_id) as productReviewCount ")
			  .append("\n from lz_market.product_info pi2")
			  .append("\n , lz_market.product_image_info pii")
			  .append("\n , lz_market.seller_info si")
			  .append("\n , lz_market.product_category_info pci");
			  if("Y".equals(favoriteYn)) {
				  _query.append("\n 	, lz_market.seller_favorite_info sfi");
			  }
			  _query.append("\n where pi2.product_id = pii.product_id")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pi2.product_category_code = pci.product_category_code  ");
			  if("Y".equals(favoriteYn)) {
					_query.append("\n and si.seller_id = sfi.seller_id ");
				}
			  _query.append("\n and pii.delegate_thumbnail_yn = 'Y' ")
			  .append("\n and pii.image_cfcd = '01' ");
			  
		if(!"".equals(useYn)) {
			_query.append("\n and pi2.use_yn = ?");
		}
			  
		if("Y".equals(cateCodeYn)) {
			_query.append("\n and pi2.product_category_code like concat('%',?,'%')");
		}
		
		if("Y".equals(productIdYn)) {
			_query.append("\n and pi2.product_id = ?");
		}
		
		if("Y".equals(productNameYn)) {
			_query.append("\n and pi2.product_name like concat('%',?,'%')");
		}
		
		if("Y".equals(sellerYn)) {
			_query.append("\n and pi2.seller_id = ?");
		} else {
			if(!("".equals(sellerId) || sellerId == null)) {
				_query.append("\n and pi2.seller_id = ?");
			}
		}
		
		if("Y".equals(favoriteYn)) {
			_query.append("\n and sfi.user_id = ? ");
		}
		
		if("".equals(eventId) || eventId == null) {
			_query.append("\n and pi2.event_id is null ");
		} else {
			_query.append("\n and pi2.event_id = ? ");
		}
		
		if(isSoldOutHidden) {
			_query.append("\n and pi2.product_stock > 0 ");
		}
		
		_query.append("\n ) A ");
		
		if("001".equals(productSortCode)) {
			_query.append("\n order by A.productId DESC");
		} else if ("002".equals(productSortCode)) {
			_query.append("\n order by A.paymentCount DESC, A.productReviewCount DESC");
		} else if ("003".equals(productSortCode)) {
			_query.append("\n order by A.productPrice ASC");
		} else if ("004".equals(productSortCode)) {
			_query.append("\n order by A.productPrice DESC");
		}
		
		_query.append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String pageQuery(String maxPageCnt
			, String cateCodeYn, String productIdYn, String productNameYn, String sellerYn
			, String favoriteYn, String sellerId, String useYn, String eventId, Boolean isSoldOutHidden ) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_info pi2")
			  .append("\n 	, lz_market.product_image_info pii")
			  .append("\n 	, lz_market.seller_info si")
			  .append("\n 	, lz_market.product_category_info pci ");
			  if("Y".equals(favoriteYn)) {
				  _query.append("\n 	, lz_market.seller_favorite_info sfi");
			  }
			  
		_query.append("\n where pi2.product_id = pii.product_id")
			  .append("\n and pi2.seller_id = si.seller_id ")
			  .append("\n and pi2.product_category_code = pci.product_category_code ");
		
		if("Y".equals(favoriteYn)) {
			  _query.append("\n and si.seller_id = sfi.seller_id");
		  }
		
		_query.append("\n and pii.delegate_thumbnail_yn = 'Y'")
			  .append("\n and pii.image_cfcd = '01'");
		
		if(!"".equals(useYn)) {
			_query.append("\n and pi2.use_yn = ?");
		}
		
		if("Y".equals(cateCodeYn)) {
			_query.append("\n and pi2.product_category_code = ?");
		}
		if("Y".equals(productIdYn)) {
			_query.append("\n and pi2.product_id = ?");
		}
		
		if("Y".equals(productNameYn)) {
			_query.append("\n and pi2.product_name like concat('%',?,'%')");
		}
		if("Y".equals(sellerYn)) {
			_query.append("\n and pi2.seller_id = ?");
		} else {
			if(!("".equals(sellerId) || sellerId == null)) {
				_query.append("\n and pi2.seller_id = ?");
			}
		}
		
		if("Y".equals(favoriteYn)) {
			_query.append("\n and sfi.user_id = ? ");
		}
		
		if("".equals(eventId) || eventId == null) {
			_query.append("\n and pi2.event_id is null ");
		} else {
			_query.append("\n and pi2.event_id = ? ");
		}
		
		if(isSoldOutHidden) {
			_query.append("\n and pi2.product_stock > 0 ");
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
				//.append("\n 	, ceil(pi2.product_price) as product_price ")
				.append("\n 	, ceil(ifnull((select case when epai.event_product_discount_cf_code = '01' then epai.event_product_discount_cf_value ")
				.append("\n 	when epai.event_product_discount_cf_code = '02' then pi2.product_price - (pi2.product_price * (epai.event_product_discount_cf_value /100)) end ")
				.append("\n 	from lz_market.event_product_amount_info epai ")
				.append("\n 	where epai.event_id = pi2.event_id ")
				.append("\n 	and DATE_FORMAT(now(), '%H%i') between epai.begin_time and epai.end_time ")
				.append("\n 	order by epai.begin_time desc ")
				.append("\n 	limit 1 ")
				.append("\n 	),pi2.product_price)) as product_price ")
				.append("\n 	, ceil(pi2.product_stock) as product_stock ")
				.append("\n 	, pi2.product_desc as product_desc ")
				.append("\n 	, pi2.use_yn as use_yn ")
				.append("\n 	, pi2.product_category_code as product_category_code ")
				.append("\n 	, pi2.seller_id as seller_id ")
				.append("\n 	, DATE_FORMAT(pi2.create_datetime, '%Y-%m-%d %H:%i:%s') as create_datetime ")
				.append("\n 	, DATE_FORMAT(pi2.update_datetime, '%Y-%m-%d %H:%i:%s') as update_datetime ")
				.append("\n 	, si.shop_name as shop_name ")
				.append("\n 	, ui.user_name as user_name ")
				.append("\n 	, (select pii.thumbnail_image_path  from lz_market.product_image_info pii ")
				.append("\n 	where pii.product_id = pi2.product_id ")
				.append("\n 	and pii.delegate_thumbnail_yn = 'Y' ")
				.append("\n 	and pii.image_cfcd = '01' ")
				.append("\n 	limit 1) as thumbnail_image_path ")
				.append("\n 	, ui.profile_images_path as profile_images_path ")
				.append("\n 	, si.today_delivery_standard_time as today_delivery_standard_time ")
				.append("\n 	, '' as todayDeliveryYn ")
				.append("\n 	, '' as slsDate ")
				.append("\n 	, pi2.product_weight ")
				.append("\n 	, ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = pi2.seller_id),'') as slsDateText ")
				.append("\n 	, '' as  eventBeginTime ")
				.append("\n 	, '' as  eventEndTime ")
				.append("\n 	, '' as  eventBeginDate ")
				.append("\n 	, '' as  eventEndDate ")
				.append("\n 	, 0 as  personBuyCount ")
				.append("\n 	, 0 as eventCount ")
				.append("\n 	, pi2.event_id as eventId")
				.append("\n 	, ceil(si.combined_delivery_standard_amount) as combinedDeliveryStandardAmount ")
				.append("\n 	, ceil(si.delivery_amount) as deliveryAmount ")
				.append("\n 	, si.combined_delivery_yn as combinedDeliveryYn ")
				.append("\n FROM lz_market.product_info pi2 ")
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.user_info ui ")
				.append("\n WHERE pi2.seller_id = si.seller_id ")
				.append("\n and pi2.seller_id = ui.user_id ")
				.append("\n and pi2.product_id= ? ");
		
		return _query.toString();
	}
	
	public String productInfoCopyOrigin() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n insert into lz_market.product_info(product_id, begin_date, end_date, product_name, product_price, product_stock ")
				.append("\n 	, product_desc, use_yn, product_category_code, seller_id,today_delivery_standard_time, product_weight, event_id, create_datetime, update_datetime, create_user, update_user) ")
				.append("\n select ")
				.append("\n 	? ")
				.append("\n 	, b.begin_date ")
				.append("\n 	, b.end_date ")
				.append("\n 	, b.product_name ")
				.append("\n 	, b.product_price ")
				.append("\n 	, b.product_stock ")
				.append("\n 	, b.product_desc ")
				.append("\n 	, 'Y' ")
				.append("\n 	, b.product_category_code ")
				.append("\n 	, b.seller_id ")
				.append("\n 	, b.today_delivery_standard_time ")
				.append("\n 	, b.product_weight ")
				.append("\n 	, b.event_id ")
				.append("\n 	, now() ")
				.append("\n 	, now() ")
				.append("\n 	, ? ")
				.append("\n 	, ? ")
				.append("\n from lz_market.product_info b ")
				.append("\n where b.product_id = ? ");
		
		return _query.toString();
	}
	
	public String eventProductInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     A.* ")
				.append("\n from ")
				.append("\n     (select ")
				.append("\n         pi2.product_id as productId ")
				.append("\n         , ifnull(epai.event_product_name, pi2.product_name) as productName ")
				.append("\n       , round(ifnull(case ")
				.append("\n                 when epai.event_product_discount_cf_code = '01' then epai.event_product_discount_cf_value ")
				.append("\n                 when epai.event_product_discount_cf_code = '02' then pi2.product_price - (pi2.product_price * (epai.event_product_discount_cf_value /100)) ")
				.append("\n           end, pi2.product_price), 0) as productPrice ")
				.append("\n         , round(pi2.product_price,0) as originalProductPrice ")
				.append("\n         , round(pi2.product_stock,0) as productStock ")
				.append("\n         , pi2.product_desc as productDesc ")
				.append("\n         , pii.thumbnail_image_path as thumbnailImagePath ")
				.append("\n         , ifnull((select sum(poii.product_count) ")
				.append("\n         		from lz_market.product_order_item_info poii ")
				.append("\n         		where poii.product_id = pi2.product_id ")
				.append("\n             	and poii.order_item_state_code = '003'), 0) as cumulative_sales_count ")
				.append("\n         , si.shop_name as shopName ")
				.append("\n         , si.today_delivery_standard_time as today_delivery_standard_time ")
				.append("\n         , pi2.product_category_code as productCategoryCode ")
				.append("\n         , pci.product_category_small_name as productCategorySmallName ")
				.append("\n         , ifnull((select ")
				.append("\n         			cdi.dtl_code_text ")
				.append("\n 			from lz_market.cd_dtl_info cdi ")
				.append("\n         		where cdi.code_no = 8 ")
				.append("\n             	and cdi.dtl_code = pi2.seller_id), '') as slsDateText ")
				.append("\n         , pi2.use_yn as useYn ")
				.append("\n         , pi2.event_id as eventId ")
				.append("\n         , ceil(si.combined_delivery_standard_amount) as combinedDeliveryStandardAmount ")
				.append("\n         , ceil(si.delivery_amount) as deliveryAmount ")
				.append("\n         , si.combined_delivery_yn as combinedDeliveryYn ")
				.append("\n         , (select count(1) ")
				.append("\n         	from lz_market.product_order_item_info poii ")
				.append("\n         	where poii.product_id = pi2.product_id ")
				.append("\n             and poii.delivery_state_code = '400') as paymentCount ")
				.append("\n         , (select count(1) ")
				.append("\n 		from lz_market.product_review_info pri ")
				.append("\n         	where pri.product_id = pi2.product_id) as productReviewCount ")
				.append("\n         , ifnull(epai.begin_time, if(ifnull(em.event_begin_time,'') = '', '0000', em.event_begin_time)) as eventBeginTime ")
				.append("\n         , ifnull(epai.end_time, if(ifnull(em.event_end_time,'') = '', '2400', em.event_end_time)) as eventEndTime ")
				.append("\n         , epai.event_product_discount_rate as discountRate ")
				.append("\n         , date_format(em.event_begin_date, '%Y-%m-%d') as eventBeginDate ")
				.append("\n         , date_format(em.event_end_date, '%Y-%m-%d') as eventEndDate ")
				.append("\n     from ")
				.append("\n         lz_market.product_info pi2 ")
				.append("\n         left outer join lz_market.event_product_amount_info epai ")
				.append("\n          on epai.event_id = pi2.event_id ")
				.append("\n         , lz_market.product_image_info pii ")
				.append("\n         , lz_market.seller_info si ")
				.append("\n         , lz_market.product_category_info pci ")
				.append("\n         , lz_market.event_mst em ")
				.append("\n     where ")
				.append("\n         pi2.product_id = pii.product_id ")
				.append("\n         and pi2.seller_id = si.seller_id ")   
				.append("\n         and pi2.product_category_code = pci.product_category_code ")
				.append("\n         and pi2.event_id = em.event_id ")
				.append("\n         and pii.delegate_thumbnail_yn = 'Y' ")
				.append("\n         and pii.image_cfcd = '01' ")
				.append("\n         and pi2.use_yn = 'Y' ")
				.append("\n         and pi2.event_id = ? ")
				.append("\n     ) A  ")
				.append("\n order by A.productId ");
		//.append("\n     A.productId DESC  limit 0, 5 ");

		
		return _query.toString();
	}
}
