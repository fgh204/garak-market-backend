package com.lawzone.market.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.SellerInfo;

public interface SellerInfoDAO extends JpaRepository<SellerInfo, String>{
	Optional<SellerInfo> findByLoginId(String loginId);
	Optional<SellerInfo> findBySellerId(String sellerId);
	Optional<SellerInfo> findBySpotId(String spotId);
}
