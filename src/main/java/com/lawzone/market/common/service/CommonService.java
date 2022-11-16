package com.lawzone.market.common.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lawzone.market.common.dao.BoilerplateInfoDAO;
import com.lawzone.market.product.service.TagInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonService {
	private final BoilerplateInfoDAO boilerplateInfoDAO;
	private final ModelMapper modelMapper;
	
	public void addBoilerplateInfo(BoilerplateInfoDTO boilerplateInfoDTO) {
		//기존 상용구 검색
		Optional<BoilerplateInfo> _boilerplateInfo = this.boilerplateInfoDAO.findByUserIdAndBoilerplateName(boilerplateInfoDTO.getUserId(), boilerplateInfoDTO.getBoilerplateName());
		
		if(_boilerplateInfo.isPresent()) {
			boilerplateInfoDTO.setBoilerplateNumber( new BigDecimal(_boilerplateInfo.get().getBoilerplateNumber()));
		}
		
		if("".equals(boilerplateInfoDTO.getUseYn()) || boilerplateInfoDTO.getUseYn() == null) {
			boilerplateInfoDTO.setUseYn("Y");
		}
		
		BoilerplateInfo boilerplateInfo = new BoilerplateInfo();
		boilerplateInfo = modelMapper.map(boilerplateInfoDTO, BoilerplateInfo.class);
		
		this.boilerplateInfoDAO.save(boilerplateInfo);
	}
	
	public List getBoilerplateList(BoilerplateInfoDTO boilerplateInfoDTO) {
		return this.boilerplateInfoDAO.findByUserIdAndUseYn(boilerplateInfoDTO.getUserId(), "Y");
	}
}
