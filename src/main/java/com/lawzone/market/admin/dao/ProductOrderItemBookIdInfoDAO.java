package com.lawzone.market.admin.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.admin.service.ProductOrderItemBookIdInfo;

public interface ProductOrderItemBookIdInfoDAO extends JpaRepository<ProductOrderItemBookIdInfo, String>{
	List<ProductOrderItemBookIdInfo> findByIdOrderNoAndIdProductId(String orderNo, String productId);
	Optional<ProductOrderItemBookIdInfo> findByIdOrderIdFromCorpAndIdOrderNoAndIdProductId(String orderIdFromCorp, String orderNo, String productId);
}
