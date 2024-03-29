package com.lawzone.market.oAuth2;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.util.JwtTokenUtil;

import io.jsonwebtoken.io.SerialException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	@Value("${lzmarket.baseurl}") 
	private String url;
	
	@Value("${lzmarket.jwttoken}") 
	private String token;

	@Resource
	private SessionBean sessionBean;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest
			, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
		//log.info("성공!");
		//String[] path = httpServletRequest.getRequestURI().split("/");
		//Provider provider = Provider.valueOf(path[path.length - 1].toUpperCase());
		//String oauthId = authentication.getName();
		//log.info("authentication====" + authentication);
		//log.info("authentication====" + authentication.getName());
		String url = UriComponentsBuilder.fromUriString(this.url + "/product")
				//.queryParam("token", sessionBean.getToken())
				//.queryParam("oauthId", oauthId)
				.build().toString();

//		Cookie myCookie = new Cookie(this.token, sessionBean.getToken());
//		myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
//		myCookie.setHttpOnly(true);
//		myCookie.setSecure(true);
//		if("P".equals(this.service)) {
//			myCookie.setDomain("domaado.me");
//		}
		
		ResponseCookie accessTokenCookie;
		
		if("P".equals(this.service)) {
			accessTokenCookie = ResponseCookie.from(this.token, sessionBean.getToken())
	                .path("/")
	                .secure(true)
	                .httpOnly(true)
	                .sameSite("None")
	                .domain("domaado.me")
	                .build();
		}else {
			accessTokenCookie = ResponseCookie.from(this.token, sessionBean.getToken())
	                .path("/")
	                .secure(true)
	                .httpOnly(true)
	                .sameSite("None")
	                .build();
		}
		
		//httpServletResponse.addCookie(myCookie);
		httpServletResponse.setHeader("Set-Cookie", accessTokenCookie.toString());
		httpServletResponse.sendRedirect(url);
	}
}
