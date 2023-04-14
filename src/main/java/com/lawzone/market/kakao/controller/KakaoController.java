package com.lawzone.market.kakao.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.admin.dto.login.LoginDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/kakao")
public class KakaoController {
	@Resource
	private SessionBean sessionBean;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String secretKey;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Value("${lzmarket.jwttoken}") 
	private String token;
	
	@Value("${lzmarket.baseurl}") 
	private String baseurl;
	
	private final UserInfoService userInfoService;
	
	@GetMapping(value = "/oauth")
    public String kakaoConnect() {
        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?");
        url.append("client_id=" + this.clientId);
        url.append("&redirect_uri=http://localhost:8080/login/oauth2/code/kakao");
        url.append("&response_type=code");
        url.append("&scope=name,account_email,phone_number,friends");

        return "redirect:" + url;
    }
	 
	@ResponseBody
	@GetMapping("/login/oauth")
	public String getOauth(HttpServletRequest request, @RequestParam(required = true)String  code) throws JsonMappingException, JsonProcessingException {
		//String accessToken = getKakaoAccessToken(code);
		
		//getKakaoUserInfo(accessToken);
		return null;
	}
	@ResponseBody
	@PostMapping("/authorization/callback")
	public String getLogin(HttpServletRequest request
			, HttpServletResponse httpResponse
			, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		LoginDTO loginDTO = new LoginDTO();
		loginDTO = (LoginDTO) ParameterUtils.setDto(map, loginDTO, "insert", sessionBean);
		
		String oauthCode = loginDTO.getOauthCode();
		String previousUrl = loginDTO.getPreviousUrl();
		
		WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        // 카카오 서버에 요청 보내기 & 응답 받기
		JSONObject response = new JSONObject();
		
		try {
			response = webClient.post()
		            .uri(uriBuilder -> uriBuilder
		                .path("/oauth/token")
		                .queryParam("grant_type", "authorization_code")
		                .queryParam("client_id", this.clientId)
		                .queryParam("redirect_uri", baseurl + "/intro")
		                .queryParam("code", oauthCode)
		                .build())
		            .retrieve().bodyToMono(JSONObject.class).block();
		}catch (Exception e) {
			return "";
		}
        
        Map kakaoMap = getKakaoUserInfo((String) response.get("access_token"));
        
        String token = (String) kakaoMap.get("token");
        
        if(token != null) {
        	ResponseCookie accessTokenCookie;
    		
    		if("P".equals(this.service)) {
    			accessTokenCookie = ResponseCookie.from(this.token, token)
    	                .path("/")
    	                .secure(true)
    	                .httpOnly(true)
    	                .sameSite("None")
    	                .domain("domaado.me")
    	                .build();
    		} else if("T".equals(this.service)){
    			accessTokenCookie = ResponseCookie.from(this.token, token)
    	                .path("/")
    	                .secure(true)
    	                .httpOnly(true)
    	                .sameSite("None")
    	                .domain("test.domaado.me")
    	                .build();
    		} else {
    			accessTokenCookie = ResponseCookie.from(this.token, token)
    	                .path("/")
    	                .secure(true)
    	                .httpOnly(true)
    	                .sameSite("None")
    	                .build();
    		}
    		
    		httpResponse.setHeader("Set-Cookie", accessTokenCookie.toString());
        }
        Map rtnMap = new HashMap<>();
        rtnMap.put("isPointConfirmed", kakaoMap.get("isPointConfirmed"));
        rtnMap.put("isPushIdExist", kakaoMap.get("isRegistered"));
        rtnMap.put("token", kakaoMap.get("socialAccessToken"));
        rtnMap.put("loginId", kakaoMap.get("loginId"));
        rtnMap.put("password", kakaoMap.get("password"));
        rtnMap.put("previousUrl", previousUrl);
        return JsonUtils.returnValue("0000", "로그인되었습니다.", rtnMap).toString();
    }
	
    private Map getKakaoUserInfo(String accessToken) {
        // 카카오에 요청 보내기 및 응답 받기
        WebClient webClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
            
        JSONObject response = webClient.post()
            .uri(uriBuilder -> uriBuilder.path("/v2/user/me").build())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve().bodyToMono(JSONObject.class).block();
        
        //Integer id = (Integer) response.get("id");
        Map<String, Object> map = (Map<String, Object>) response;
        map.put("socialAccessToken",accessToken);
        
        return this.userInfoService.getKakaoLogin(map);
    }
}
