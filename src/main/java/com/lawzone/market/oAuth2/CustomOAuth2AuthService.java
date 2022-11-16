package com.lawzone.market.oAuth2;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2AuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
	
	//private final UserInfoDAO userInfoDAO;
	//private final PasswordEncoder passwordEncoder;
	
	@Resource
	private SessionBean sessionBean;
	
	private final UserInfoDAO userInfoDAO;
	private final UtilService utilService;
	private final JwtTokenUtil jwtTokenUtil;
	
	@SneakyThrows
	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
		log.info("CustomOAuth2AuthService");
		OAuth2UserService delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(request);
		String reistrationId = request.getClientRegistration().getRegistrationId();
		String userNameAttributeName = request.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
		OAuth2Attributes attributes = OAuth2Attributes.of(reistrationId, userNameAttributeName, oAuth2User.getAttributes());
		//this.userInfoService.create((Map<String, Object>) attributes.getAttributes());		
		
		String userId = "";
		String userNm = "";
		String sellerYn = "";
		String email = "";
		String phoneNumber = "";
		String socialId = "";
		
		UserInfo user = new UserInfo();
		List<UserInfo> userInfo = 
				this.userInfoDAO
				.findBySocialIdAndEmailAndUserName(
						attributes.getOauthId()
						,attributes.getEmail()
						,attributes.getName());
		
		if(userInfo.size() == 0) {
			String userNumber = utilService.getNextVal("USER_ID");
			
			user.setUserId(StringUtils.leftPad(userNumber, 8,"0"));
			user.setUserName(attributes.getName());
			user.setEmail(attributes.getEmail());
			user.setSocialId(attributes.getOauthId());
			user.setPhoneNumber(attributes.getPhone_number());
			user.setSellerYn("N");
			user.setUseYn("Y");
			this.userInfoDAO.save(user).getUserId();
			
			List<UserInfo> userInfo2 = 
					this.userInfoDAO
					.findBySocialIdAndEmailAndUserName(
							attributes.getOauthId()
							,attributes.getEmail()
							,attributes.getName());
			userInfo = userInfo2;
		}
		
		userId = userInfo.get(0).getUserId();
		userNm = userInfo.get(0).getUserName();
		sellerYn = userInfo.get(0).getUserName();
		email = userInfo.get(0).getEmail();
		phoneNumber = userInfo.get(0).getPhoneNumber();
		socialId = userInfo.get(0).getSocialId();

		sessionBean.setToken(jwtTokenUtil.generateToken(userInfo.get(0)));
		
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes.getAttributes(), attributes.getNameAttributeKeys());
	}
}

