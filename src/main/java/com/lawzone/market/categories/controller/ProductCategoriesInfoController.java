package com.lawzone.market.categories.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.categories.service.ProductCategoriesInfoDTO;
import com.lawzone.market.categories.service.ProductCategoriesInfoService;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/cate")
public class ProductCategoriesInfoController {
	private final ProductCategoriesInfoService productCategoriesInfoService;
	private final TelmsgLogService telmsgLogService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addCartInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCategoriesInfoDTO productCategoriesInfoDTO = new ProductCategoriesInfoDTO();
		productCategoriesInfoDTO = (ProductCategoriesInfoDTO) ParameterUtils.setDto(map, productCategoriesInfoDTO, "insert", sessionBean);
		this.productCategoriesInfoService.addProductCategoriesInfo(productCategoriesInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/cateList")
	public String getList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		List cateList = this.productCategoriesInfoService.getProductCategoriesInfo();
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("cateList", cateList);
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
}
