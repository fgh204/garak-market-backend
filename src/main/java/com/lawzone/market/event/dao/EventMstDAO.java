package com.lawzone.market.event.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.event.service.EventMst;

public interface EventMstDAO extends JpaRepository<EventMst, Long>{
	List<EventMst> findByeventMstSeq(Long eventMstSeq);
}
