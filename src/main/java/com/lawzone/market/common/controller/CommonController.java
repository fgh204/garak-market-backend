package com.lawzone.market.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.cart.service.CartInfoDTO;
import com.lawzone.market.common.service.BoilerplateInfoDTO;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class CommonController {
	private final TelmsgLogService telmsgLogService;
	private final CommonService commonService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/boilerplateInfo/create")
	public String addBoilerplateInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		BoilerplateInfoDTO boilerplateInfoDTO = new BoilerplateInfoDTO();
		boilerplateInfoDTO = (BoilerplateInfoDTO) ParameterUtils.setDto(map, boilerplateInfoDTO, "insert", sessionBean);
		
		this.commonService.addBoilerplateInfo(boilerplateInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/boilerplateInfo/list")
	public String getBoilerplateList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		BoilerplateInfoDTO boilerplateInfoDTO = new BoilerplateInfoDTO();
		boilerplateInfoDTO = (BoilerplateInfoDTO) ParameterUtils.setDto(map, boilerplateInfoDTO, "insert", sessionBean);
		
		List boilerplateList = this.commonService.getBoilerplateList(boilerplateInfoDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("boilerplateList", boilerplateList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
}
