package com.lawzone.market.admin.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.admin.service.ProductOrderItemBookIdInfo;
import com.lawzone.market.admin.service.SlsDateInfo;

public interface SlsDateInfoDAO extends JpaRepository<SlsDateInfo, String> {
	Optional<SlsDateInfo> findBySlsDate(Date slsDate);
	List<SlsDateInfo> findByWeekDgreYymm(String weekDgreYymm);
	List<SlsDateInfo> findByWeekDgreYymmAndSlsDayYn(String weekDgreYymm, String slsDayYn);
}
