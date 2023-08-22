package com.lawzone.market.send.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.send.service.SendInfo;

public interface SendInfoDAO extends JpaRepository<SendInfo, Long>{
	List<SendInfo> findBySendId(String sendId);
	List<SendInfo> findBySendIdAndSendYn(String sendId, String sendYn);
}
