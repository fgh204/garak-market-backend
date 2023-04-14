package com.lawzone.market.product.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductJdbcDAO {
	public String pageListQuery(String pageCnt, String maxPageCnt
			, String cateCodeYn, String productIdYn, String productNameYn, String sellerYn
			, String favoriteYn, String sellerId, String useYn) {
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
			  .append("\n , pi2.today_delivery_standard_time as today_delivery_standard_time ")
			  .append("\n , pi2.product_category_code as productCategoryCode ")
			  .append("\n , pci.product_category_small_name as productCategorySmallName ")
			  .append("\n , ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = pi2.seller_id),'') as slsDateText ")
			  .append("\n , pi2.use_yn as useYn ")
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
		
		_query.append("\n order by pi2.product_id DESC")
			  .append("\n limit " + pageCnt + ", " + maxPageCnt);
		return _query.toString();
	}
	
	public String pageQuery(String maxPageCnt
			, String cateCodeYn, String productIdYn, String productNameYn, String sellerYn
			, String favoriteYn, String sellerId, String useYn ) {
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
				.append("\n 	, pi2.today_delivery_standard_time as today_delivery_standard_time ")
				.append("\n 	, '' as todayDeliveryYn ")
				.append("\n 	, '' as slsDate ")
				.append("\n 	, pi2.product_weight ")
				.append("\n , ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = pi2.seller_id),'') as slsDateText ")
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
				.append("\n 	, product_desc, use_yn, product_category_code, seller_id,today_delivery_standard_time, create_datetime, update_datetime, create_user, update_user) ")
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
				.append("\n 	, now() ")
				.append("\n 	, now() ")
				.append("\n 	, ? ")
				.append("\n 	, ? ")
				.append("\n from lz_market.product_info b ")
				.append("\n where b.product_id = ? ");
		
		return _query.toString();
	}
}
