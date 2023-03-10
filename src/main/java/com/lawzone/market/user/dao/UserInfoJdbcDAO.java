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
			  .append("\n   , ui.background_image_path ")
			  .append("\n   , si.introduction_text ")
			  .append("\n   , ui.social_name ")
			  .append("\n from lz_market.user_info ui ")
			  .append("\n 	left outer join lz_market.seller_info si ")
			  .append("\n 	on ui.user_id = si.seller_id  ")
			  .append("\n where ui.user_id = ? ");
		
		return _query.toString();
	}
	
	public String storePageInfo(String maxPageCnt, String productCategoryCode) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_count ")
			  .append("\n   , count(1) AS total_count ")
			  .append("\n from lz_market.seller_info si ")
			  .append("\n where IFNULL(si.spot_id,'') <> ''");
		if(!("".equals(productCategoryCode) || productCategoryCode == null)) {
			_query.append("\n and si.product_category_code = ? ");
		}
		
		return _query.toString();
	}
	
	public String storeList(String pageCnt, String maxPageCnt, String _userId, String _productCategoryCode) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	si.product_category_code ")
			  .append("\n	, si.seller_id ")
			  .append("\n   , si.shop_name ");
			  if( _userId == null) {
				  _query.append("\n   , 'N' ");
			  }else {
				  _query.append("\n   , (select case when count(1) > 0 then 'Y' else 'N' end from lz_market.seller_favorite_info sfi  ")
				  .append("\n   where sfi.user_id = ? ")
				  .append("\n   and sfi.seller_id = si.seller_id) ");
			  }
			  _query.append("\n from lz_market.seller_info si ")
			  .append("\n where IFNULL(si.spot_id,'') <> ''");
		  if(!("".equals(_productCategoryCode) || _productCategoryCode == null)) {
				_query.append("\n and si.product_category_code = ? ");
		  }
		  _query.append("\n limit " + pageCnt + "," + maxPageCnt);
		
		return _query.toString();
	}
	
	public String storeImgList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  //.append("\n	pi2.seller_id  ")
			  .append("\n 	pii.thumbnail_image_path ")
			  .append("\n 	, pi2.product_id ")
			  .append("\n from lz_market.product_info pi2 ")
			  .append("\n   	, lz_market.product_image_info pii ")
			  .append("\n where pi2.product_id = pii.product_id ")
			  .append("\n and pii.image_cfcd = ? ")
			  .append("\n and pii.delegate_thumbnail_yn = ? ")
			  .append("\n and pi2.seller_id = ? ")
			  .append("\n and pi2.use_yn = ? ")
			  .append("\n order by pi2.product_id desc ")
			  .append("\n limit 5 ");
		
		return _query.toString();
	}
}
