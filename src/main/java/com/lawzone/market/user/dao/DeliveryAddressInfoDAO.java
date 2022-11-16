package com.lawzone.market.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.DeliveryAddressInfo;

public interface DeliveryAddressInfoDAO extends JpaRepository<DeliveryAddressInfo, Long>{
	List<DeliveryAddressInfo> findByUserId(String userId);
}
