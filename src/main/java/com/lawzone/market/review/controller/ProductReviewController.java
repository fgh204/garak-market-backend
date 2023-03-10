package com.lawzone.market.review.controller;

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
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.image.service.ProductImageListDTO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.review.service.ProductReviewInfoCDTO;
import com.lawzone.market.review.service.ProductReviewInfoDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/review")
public class ProductReviewController {
	private final ProductReviewInfoService productReviewInfoService;
	private final TelmsgLogService telmsgLogService;
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addProductReviewInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		Map productReviewMap = new HashMap<>();
		Map reviewMap = new HashMap<>();
		
		productReviewMap = (Map) map.get("dataset");
		reviewMap.put("dataset", productReviewMap.get("productReviewInfo"));
		
		ProductReviewInfoCDTO productReviewInfoCDTO = new ProductReviewInfoCDTO();
		productReviewInfoCDTO = (ProductReviewInfoCDTO) ParameterUtils.setDto(reviewMap, productReviewInfoCDTO, "insert", sessionBean);
		
		List<ProductImageListDTO> reviewImageList = (List) productReviewMap.get("productReviewImageList");
		
		String _msg = this.productReviewInfoService.addProductReviewInfo(productReviewInfoCDTO, reviewImageList);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", _msg, rtnMap).toString();	
	}
	
	@ResponseBody
	@PostMapping("/list")
	public String getProductReviewInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);		
		ProductReviewInfoCDTO productReviewInfoCDTO = new ProductReviewInfoCDTO();
		productReviewInfoCDTO = (ProductReviewInfoCDTO) ParameterUtils.setDto(map, productReviewInfoCDTO, "insert", sessionBean);
		
		if("".equals(productReviewInfoCDTO.getMaxPageCount()) || productReviewInfoCDTO.getMaxPageCount() == null) {
			productReviewInfoCDTO.setMaxPageCount("10");
    	}
    	
    	if("".equals(productReviewInfoCDTO.getPageCount()) || productReviewInfoCDTO.getPageCount() == null) {
    		productReviewInfoCDTO.setPageCount("0");
    	}else {
    		int _currentCnt = Integer.parseInt(productReviewInfoCDTO.getPageCount());
    		int _limitCnt = Integer.parseInt(productReviewInfoCDTO.getMaxPageCount());
    		
    		productReviewInfoCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
    	}
		
		Map orderMap = new HashMap<>();
		orderMap.put("productId", productReviewInfoCDTO.getProductId());
		orderMap.put("sellerId", productReviewInfoCDTO.getSellerId());
    	orderMap.put("pageCount", productReviewInfoCDTO.getPageCount());
    	orderMap.put("maxPageCount", productReviewInfoCDTO.getMaxPageCount());
    	orderMap.put("orderCd", productReviewInfoCDTO.getOrderCode());
    	orderMap.put("productScore", productReviewInfoCDTO.getProductScore());
    	
    	List<PageInfoDTO> pageInfo = this.productReviewInfoService.getProductReviewPageInfo(orderMap);
    	List productReviewList = this.productReviewInfoService.getProductReviewList(orderMap);
    	// 상품리뷰건수
    	
    	ProductCDTO productCDTO = new ProductCDTO();
    	productCDTO.setSellerId(productReviewInfoCDTO.getSellerId());
    	List productReviewScoreCountInfo = this.productReviewInfoService.getProductReviewCountInfo(productCDTO);
		Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("productReviewCountInfo", productReviewScoreCountInfo.get(0));
		rtnMap.put("productReviewList", productReviewList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();

		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/myList")
	public String getMyProductReviewInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);		
		ProductReviewInfoCDTO productReviewInfoCDTO = new ProductReviewInfoCDTO();
		productReviewInfoCDTO = (ProductReviewInfoCDTO) ParameterUtils.setDto(map, productReviewInfoCDTO, "insert", sessionBean);
		
		if("".equals(productReviewInfoCDTO.getMaxPageCount()) || productReviewInfoCDTO.getMaxPageCount() == null) {
			productReviewInfoCDTO.setMaxPageCount("10");
    	}
    	
    	if("".equals(productReviewInfoCDTO.getPageCount()) || productReviewInfoCDTO.getPageCount() == null) {
    		productReviewInfoCDTO.setPageCount("0");
    	}else {
    		int _currentCnt = Integer.parseInt(productReviewInfoCDTO.getPageCount());
    		int _limitCnt = Integer.parseInt(productReviewInfoCDTO.getMaxPageCount());
    		
    		productReviewInfoCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
    	}
		
		Map orderMap = new HashMap<>();
		orderMap.put("productId", productReviewInfoCDTO.getProductId());
    	orderMap.put("pageCount", productReviewInfoCDTO.getPageCount());
    	orderMap.put("maxPageCount", productReviewInfoCDTO.getMaxPageCount());
    	orderMap.put("orderCd", productReviewInfoCDTO.getOrderCode());
    	orderMap.put("productScore", productReviewInfoCDTO.getProductScore());
    	orderMap.put("productId", productReviewInfoCDTO.getProductId());
    	orderMap.put("orderNo", productReviewInfoCDTO.getOrderNo());
    	orderMap.put("userId", productReviewInfoCDTO.getUserId());
    	
    	List<PageInfoDTO> pageInfo = this.productReviewInfoService.getMyProductReviewPageInfo(orderMap);
    	List productReviewList = this.productReviewInfoService.getMyProductReviewList(orderMap);
    	
		Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("productReviewList", productReviewList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();

		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/modify")
	public String modifyProductReviewInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductReviewInfoDTO productReviewInfoDTO = new ProductReviewInfoDTO();
		productReviewInfoDTO = (ProductReviewInfoDTO) ParameterUtils.setDto(map, productReviewInfoDTO, "insert", sessionBean);
		this.productReviewInfoService.modifyProductReviewInfo(productReviewInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/remove")
	public String removeProductReviewInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductReviewInfoCDTO productReviewInfoCDTO = new ProductReviewInfoCDTO();
		productReviewInfoCDTO = (ProductReviewInfoCDTO) ParameterUtils.setDto(map, productReviewInfoCDTO, "insert", sessionBean);
		this.productReviewInfoService.removeProductReviewInfo(productReviewInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}
}
