package com.lawzone.market.review.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.review.service.ProductReviewInfo;

public interface ProductReviewInfoDAO extends JpaRepository<ProductReviewInfo, Long>{
	Optional<ProductReviewInfo> findByProductIdAndUserId(String productId, String userId);
	Optional<ProductReviewInfo> findByProductIdAndUserIdAndOrderNo(String productId, String userId, String orderNo);
}
