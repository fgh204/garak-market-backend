package com.lawzone.market.image.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.image.service.ProductImageInfo;

public interface ProductImageDAO extends JpaRepository<ProductImageInfo, BigDecimal>{
	List<ProductImageInfo> findByproductId(String productId);
}
