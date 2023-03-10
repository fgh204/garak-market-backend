package com.lawzone.market.payment.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.payment.service.TempPaymentInfo;

public interface TempPaymentInfoDAO extends JpaRepository<TempPaymentInfo, Long>{
	Optional<TempPaymentInfo> findTopByUserIdOrderByTempPaymentNumberDesc(String userId);
	List<TempPaymentInfo> findByUserId(String userId);
}
