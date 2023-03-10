package com.lawzone.market.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.SellerFavoriteInfo;
import com.lawzone.market.user.service.SellerInfo;

public interface SellerFavoriteInfoDAO extends JpaRepository<SellerFavoriteInfo, Long>{
	Optional<SellerFavoriteInfo> findByUserId(String userId);
	Optional<SellerFavoriteInfo> findByUserIdAndSellerId(String userId, String sellerID);
}
