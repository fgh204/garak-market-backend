package com.lawzone.market.payment.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.payment.service.PaymentInfo;

public interface PaymentDAO extends JpaRepository<PaymentInfo, String>{
	List<PaymentInfo> findByReceiptId(String receiptId);
	List<PaymentInfo> findByOrderNo(String orderNo);
}
