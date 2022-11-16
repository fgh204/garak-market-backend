package com.lawzone.market.cart.controller;

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
import com.lawzone.market.cart.service.CartInfoDTO;
import com.lawzone.market.cart.service.CartService;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/cart")
public class CartController {
	private final CartService cartService;
	private final TelmsgLogService telmsgLogService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addCartInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		CartInfoDTO cartInfoDTO = new CartInfoDTO();
		cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(map, cartInfoDTO, "insert", sessionBean);
		Long cartNumber =  this.cartService.addCartInfo(cartInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		if(cartNumber == 0) {
			return JsonUtils.returnValue("9999", "재고가 없습니다", rtnMap).toString();
		}else {
			return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/list")
	public String getCartInfoList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		CartInfoDTO cartInfoDTO = new CartInfoDTO();
		cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(map, cartInfoDTO, "insert", sessionBean);
		
		List cartList = null;
		cartList = this.cartService.getCartInfoList(cartInfoDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("cartList", cartList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();

		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/modify")
	public String modifyCartInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		CartInfoDTO cartInfoDTO = new CartInfoDTO();
		cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(map, cartInfoDTO, "insert", sessionBean);
		
		this.cartService.modifyCartInfo(cartInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/modifys")
	public String modifyCartInfoList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map cartItemListMap = (Map) map.get("dataset");
		
		List<CartInfoDTO> cartInfoList = (List) cartItemListMap.get("cartProductInfoList");
		
		this.cartService.modifyCartInfos(cartInfoList);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/remove")
	public String removeCartInfoCartInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		CartInfoDTO cartInfoDTO = new CartInfoDTO();
		cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(map, cartInfoDTO, "insert", sessionBean);
		this.cartService.removeCartInfo(cartInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}
}
