package com.lawzone.market.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.lawzone.market.user.service.SellerFavoriteInfo;
import com.lawzone.market.user.service.SellerInfo;

@Component
public class SellerFavoriteInfoJdbcDAO {
	public String sellerFavorite() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n 	sfi.user_id  ")
			  .append("\n 	, sfi.seller_id ")
			  .append("\n 	, si.shop_name ")
			  .append("\n 	, DATE_FORMAT(sfi.create_datetime, '%Y-%m-%d %H:%i:%s') ")
			  .append("\n 	, DATE_FORMAT(sfi.update_datetime, '%Y-%m-%d %H:%i:%s') ")
			  .append("\n from lz_market.seller_favorite_info sfi  ")
			  .append("\n , lz_market.seller_info si ")
			  .append("\n where sfi.seller_id = si.seller_id ")
			  .append("\n and sfi.user_id = ? ");	
		return _query.toString();
	}
}
