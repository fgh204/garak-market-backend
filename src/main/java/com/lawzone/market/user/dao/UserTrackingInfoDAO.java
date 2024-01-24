package com.lawzone.market.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.UserTrackingInfo;

public interface UserTrackingInfoDAO extends JpaRepository<UserTrackingInfo, Long>{
	Optional<UserTrackingInfo> findByIdUserIdAndIdSvcUrl(String userId, String svcUrl);
}
