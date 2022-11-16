package com.lawzone.market.categories.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.categories.service.ProductCategoriesInfo;

public interface ProductCategoriesInfoDAO extends JpaRepository<ProductCategoriesInfo, Long> {
	//List<ProductCategoriesInfo> findBy(String Lccd, String Mccd, String Sccd);
}
