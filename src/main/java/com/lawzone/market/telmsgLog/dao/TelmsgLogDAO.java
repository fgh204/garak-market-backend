package com.lawzone.market.telmsgLog.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.telmsgLog.service.TelmsgLogInfo;

public interface TelmsgLogDAO extends JpaRepository<TelmsgLogInfo, Long>{

}
