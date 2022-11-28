package com.lawzone.market.user.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.review.service.ProductReviewAverageScoreDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.UserLoginForm;
import com.lawzone.market.user.service.DeliveryAddressInfoDTO;
import com.lawzone.market.user.service.MarketUserDTO;
import com.lawzone.market.user.service.MarketUserInfoDTO;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/market")
public class UserInfoController {
	private final UserInfoService userInfoService;
	
	private final JwtTokenUtil jwtTokenUtil;
	private final ProductService productService;
	private final ProductReviewInfoService productReviewInfoService;
	
	@Resource
	private SessionBean sessionBean;
	
	@GetMapping("/login")
    public String login(UserLoginForm userLoginForm) {
        return "market_login_form";
    }
	
	private final TelmsgLogService telmsgLogService;
	
	@PostMapping("/login")
    public String login(@Valid UserLoginForm userLoginForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
		if(bindingResult.hasErrors()) {
			return "market_login_form";
		}
		
		Optional<UserInfo> _userInfo = this.userInfoService.marketLogin(userLoginForm.getEmail(), userLoginForm.getPassword());
		
		if (_userInfo.isEmpty()) {
			bindingResult.rejectValue("", "", 
	                "사용자정보가 없습니다.");
			return "market_login_form";
        }
		
//		List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		
		//Cookie myCookie = new Cookie("Authorization", jwtTokenUtil.generateToken(_userInfo.get().getSocialId()));
		//myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
		//response.addCookie(myCookie);
		return "market_login_form";
    }
	
	@ResponseBody
	@PostMapping("/deliveryAddress/create")
	public String addDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		
		deliveryAddressInfoDTO.setDeliveryAddressNumber(null);
		
		this.userInfoService.addDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/deliveryAddress/modify")
	public String modifyDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		this.userInfoService.addDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/deliveryAddress/remove")
	public String removeDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		this.userInfoService.removeDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/deliveryAddress/list")
	public String getDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		List<DeliveryAddressInfoDTO> _deliveryAddressInfoDTO = this.userInfoService.getDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("deliveryAddressList", _deliveryAddressInfoDTO);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/userInfo")
	public String getUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		
		List<MarketUserInfoDTO> marketUserInfo = this.userInfoService.getUserInfo(sessionBean.getUserId());
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("userInfo", marketUserInfo.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/userInfo/modify")
	public String modifyUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		MarketUserDTO marketUserDTO = new MarketUserDTO();
		marketUserDTO = (MarketUserDTO) ParameterUtils.setDto(map, marketUserDTO, "insert", sessionBean);
		
		String _msg = this.userInfoService.modifyMarketUserInfo(marketUserDTO);
		
		Map rtnMap = new HashMap<>();
		if("".equals(_msg)) {
			return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
		}else {
			return JsonUtils.returnValue("9999", _msg, rtnMap).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/userInfo/nicknameduplicated")
	public String getUserNicknameChk(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		MarketUserDTO marketUserDTO = new MarketUserDTO();
		marketUserDTO = (MarketUserDTO) ParameterUtils.setDto(map, marketUserDTO, "insert", sessionBean);
		
		Boolean chkValue = this.userInfoService.getUserNicknameChk(marketUserDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("isDuplicated",chkValue);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/userInfo/addProfileImages")
	public String addProfileImages(HttpServletRequest request, @RequestPart(value="file",required = false) MultipartFile[] uploadFile) throws IOException {
		Map rtnMap = new HashMap<>();
		this.telmsgLogService.addTelmsgLog("01", "00", "1", rtnMap);
		
		this.userInfoService.addProfileImages(uploadFile, sessionBean.getUserId());
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@RequestMapping("/seller/productList")
    public String getSellerProductList(HttpServletRequest request
    		, HttpServletResponse response
    		, @RequestBody() Map map) throws IllegalAccessException, InvocationTargetException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();
		
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
    	
    	if("".equals(productCDTO.getMaxPageCount()) || productCDTO.getMaxPageCount() == null) {
    		productCDTO.setMaxPageCount("10");
    	}
    	
    	if("".equals(productCDTO.getPageCount()) || productCDTO.getPageCount() == null) {
    		productCDTO.setPageCount("0");
    	}else {
    		int _currentCnt = Integer.parseInt(productCDTO.getPageCount());
    		int _limitCnt = Integer.parseInt(productCDTO.getMaxPageCount());
    		
    		productCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
    	}
    	
    	productCDTO.setSellerSearchYn("Y");
    	productCDTO.setSellerIdYn("Y");
    	
    	List<PageInfoDTO> pageInfo = this.productService.getPageInfo(productCDTO);
    	
    	List<ProductInfoListDTO> productList = this.productService.getList2(productCDTO);
    	
    	Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("productList", productList);
    	
    	String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
        return rtnValue;
    }
	
	@ResponseBody
	@RequestMapping("/seller/productMainInfo")
    public String getSellerAverageScoreInfo(HttpServletRequest request
    		, HttpServletResponse response
    		, @RequestBody() Map map) throws IllegalAccessException, InvocationTargetException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
    	
		if("".equals(productCDTO.getMaxPageCount()) || productCDTO.getMaxPageCount() == null) {
    		productCDTO.setMaxPageCount("10");
    	}
    	
    	if("".equals(productCDTO.getPageCount()) || productCDTO.getPageCount() == null) {
    		productCDTO.setPageCount("0");
    	}else {
    		int _currentCnt = Integer.parseInt(productCDTO.getPageCount());
    		int _limitCnt = Integer.parseInt(productCDTO.getMaxPageCount());
    		
    		productCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
    	}
    	
    	productCDTO.setSellerSearchYn("Y");
    	productCDTO.setSellerIdYn("Y");
		
    	List<MarketUserInfoDTO> marketUserInfo = this.userInfoService.getUserInfo(productCDTO.getSellerId());
    	List<ProductReviewAverageScoreDTO> productReviewAverageScoreDTO = this.productReviewInfoService.getSellerAverageScoreInfo(productCDTO);
    	List<ProductInfoListDTO> productList = this.productService.getList2(productCDTO);
    	
    	Map rtnMap = new HashMap<>();
    	rtnMap.put("sellerInfo", marketUserInfo.get(0));
    	rtnMap.put("averageScoreInfo", productReviewAverageScoreDTO.get(0));
    	rtnMap.put("productList", productList);
    	String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
        return rtnValue;
    }
}
