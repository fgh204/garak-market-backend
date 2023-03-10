package com.lawzone.market.order.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.order.service.OrderPaymentInfo;

public interface OrderPaymentDAO extends JpaRepository<OrderPaymentInfo, Long>{
	Optional<OrderPaymentInfo> findByOrderNoAndSellerId(String orderNo, String sellerId);
}
