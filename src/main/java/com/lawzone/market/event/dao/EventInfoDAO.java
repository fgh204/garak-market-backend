package com.lawzone.market.event.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.event.service.EventInfo;

public interface EventInfoDAO extends JpaRepository<EventInfo, Long>{
	List<EventInfo> findByeventId(String eventId);
	List<EventInfo> findByeventIdAndUseYn(String eventId, String useYn);
}
