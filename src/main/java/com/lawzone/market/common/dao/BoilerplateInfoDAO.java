package com.lawzone.market.common.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.common.service.BoilerplateInfo;

public interface BoilerplateInfoDAO extends JpaRepository<BoilerplateInfo, Long>{
	Optional<BoilerplateInfo> findByUserIdAndBoilerplateName(String userId, String boilerplateName);
	List<BoilerplateInfo> findByUserIdAndUseYn(String userId, String useYn);
}
