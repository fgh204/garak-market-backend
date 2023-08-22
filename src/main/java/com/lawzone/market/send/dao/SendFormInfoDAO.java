package com.lawzone.market.send.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.send.service.SendFormInfo;

public interface SendFormInfoDAO extends JpaRepository<SendFormInfo, Long>{
	List<SendFormInfo> findBySendFormCode(String sendFormCode);
	List<SendFormInfo> findBySendFormCodeAndUseYn(String sendFormCode, String useYn);
}
