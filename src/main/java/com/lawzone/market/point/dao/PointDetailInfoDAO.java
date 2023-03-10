package com.lawzone.market.point.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.point.service.PointDetailInfo;

public interface PointDetailInfoDAO extends JpaRepository<PointDetailInfo, Long>{
	Optional<PointDetailInfo> findByPointDetailId(Long pointDetailId);
}
