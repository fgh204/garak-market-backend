package com.lawzone.market.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lawzone.market.common.dao.BoilerplateInfoDAO;
import com.lawzone.market.common.dao.CommonJdbcDAO;
import com.lawzone.market.product.service.TagInfo;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonService {
	private final BoilerplateInfoDAO boilerplateInfoDAO;
	private final CommonJdbcDAO commonJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	
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
	
	//메뉴정보
	public List getMenuInfo(String userId) {
		String sql = this.commonJdbcDAO.menuInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, userId);
		_queryValue.add(1, userId);
		_queryValue.add(2, userId);
		
		MenuDTO menuDTO = new MenuDTO();
		
		return this.utilService.getQueryString(sql,menuDTO,_queryValue);
	}
}
