package com.lawzone.market.user.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.user.UserLoginForm;
import com.lawzone.market.user.service.DeliveryAddressInfoDTO;
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
	
	@Resource
	private SessionBean sessionBean;
	
	@GetMapping("/login")
    public String login(UserLoginForm userLoginForm) {
        return "market_login_form";
    }
	
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
		
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		this.userInfoService.addDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/deliveryAddress/remove")
	public String removeDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		this.userInfoService.removeDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/deliveryAddress/list")
	public String getDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert", sessionBean);
		List<DeliveryAddressInfoDTO> _deliveryAddressInfoDTO = this.userInfoService.getDeliveryAddressInfo(deliveryAddressInfoDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("deliveryAddressList", _deliveryAddressInfoDTO);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
}
