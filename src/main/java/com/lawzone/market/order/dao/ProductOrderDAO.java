package com.lawzone.market.order.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.order.service.ProductOrderInfo;

public interface ProductOrderDAO extends JpaRepository<ProductOrderInfo, String>{
	List<ProductOrderInfo> findByUserIdAndOrderStateCode(String userId, String orderStateCode);
	List<ProductOrderInfo> findByOrderNo(String orderNo);
}
