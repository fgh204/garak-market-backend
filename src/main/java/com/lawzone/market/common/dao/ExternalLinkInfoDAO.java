package com.lawzone.market.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.common.service.ExternalLinkInfo;

public interface ExternalLinkInfoDAO extends JpaRepository<ExternalLinkInfo, String>{

}
