package com.lawzone.market.order.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

import com.lawzone.market.order.service.ProductOrderItemInfo;

public interface ProductOrderItemInfoDAO extends JpaRepository<ProductOrderItemInfo, Key>{
	List<ProductOrderItemInfo> findByIdOrderNo(String orderNo);
	List<ProductOrderItemInfo> findByIdOrderNoAndIdProductId(String orderNo, String productId);
	List<ProductOrderItemInfo> findByIdOrderNoAndOrderItemStateCodeAndCartNumberIsNotNull(String orderNo, String orderItemStateCode);
}
