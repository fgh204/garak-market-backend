package com.lawzone.market.common.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.service.BoilerplateInfo;

public interface CdDtlInfoDAO extends JpaRepository<CdDtlInfo, String>{
	Optional<CdDtlInfo> findByIdCodeNoAndIdDtlCode(String codeNo, String dtlCode);
	List<CdDtlInfo> findByIdCodeNoAndIdDtlCodeAndUseYn(String codeNo, String dtlCode, String useYn);
	List<CdDtlInfo> findByIdCodeNoAndUseYn(String codeNo, String useYn);
	List<CdDtlInfo> findByIdCodeNoAndDtlCodeNameAndUseYn(String codeNo, String dtlCodeName, String useYn);
}
