package com.lawzone.market.category.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.category.service.ProductCategoryInfo;

public interface ProductCategoryInfoDAO extends JpaRepository<ProductCategoryInfo, Long> {
	
}
