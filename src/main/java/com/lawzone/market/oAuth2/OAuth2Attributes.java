package com.lawzone.market.oAuth2;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OAuth2Attributes {
	private final Map<String, Object> attributes;
	private final String nameAttributeKeys;
	private final String oauthId;
	private final String name;
	private final String nickname;
	private final String email;
	private final String picture;
	private final String phone_number;
	private final Provider provider;
	
	@Builder
	public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKeys
			, String oauthId, String name, String email, String picture
			, String phone_number, Provider provider, String nickname) {
		this.attributes = attributes;
		this.nameAttributeKeys = nameAttributeKeys;
		this.oauthId = oauthId;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.phone_number = phone_number;
		this.provider = provider;
		this.nickname = nickname;
	}
	
	@SneakyThrows
	public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		//log.info("userNameAttributeName = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userNameAttributeName));
		//log.info("attributes = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(attributes));
		
		String registrationIdToLower = registrationId.toLowerCase();
		
		switch (registrationIdToLower) {
			case "naver" : return ofNaver(userNameAttributeName, attributes);
			case "kakao" : return ofKakao(userNameAttributeName, attributes);
			case "google" : return ofGoogle(userNameAttributeName, attributes);
			case "facebook" : return ofFacebook(userNameAttributeName, attributes);
			case "github" : return ofGithub(userNameAttributeName, attributes);
			default : throw new OAuth2RegistrationException("해당 소셜 로그인은 지원하지 않습니다");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes;
		
		return OAuth2Attributes.builder()
				.oauthId((String) response.get("id"))
				.name((String) response.get("name"))
				.email((String) response.get("email"))
				.picture((String) response.get("id"))
				.provider(Provider.NAVER)
				.attributes(response)
				.nameAttributeKeys("id")
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> basicInfo = (Map<String, Object>) attributes;
		Map<String, Object> accountInfo = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) accountInfo.get("profile");
		
//		log.error("basicInfo_id = {}", basicInfo.get("id"));
//		log.error("basicInfo = {}", basicInfo);
//		log.error("accountInfo = {}", accountInfo);
//		log.error("profile = {}", profile);
//		log.error("accountInfo_email = {}", accountInfo.get("email"));
//		log.error("accountInfo_email = {}", accountInfo.get("phone_number"));
		
		String name = "";
		String nickname = "";
		String email = "";
		String phone_number = "";
		
		if(profile != null) {
			if(profile.get("nickname") != null) {
				nickname = profile.get("nickname").toString();
			}
		}
		
		if(accountInfo.get("name") != null) {
			name = accountInfo.get("name").toString();
		}
		
		if(accountInfo.get("email") != null) {
			email = accountInfo.get("email").toString();
		}
		
		if(accountInfo.get("phone_number") != null) {
			phone_number = "0" + accountInfo.get("phone_number").toString().substring(4).replaceAll("-", "");
		}
		
		return OAuth2Attributes.builder()
				.oauthId(basicInfo.get("id").toString())
				.name(name)
				.email(email)
				.phone_number(phone_number)
				.provider(Provider.KAKAO)
				.nickname(nickname)
				.attributes(basicInfo)
				.nameAttributeKeys("id")
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		
		return OAuth2Attributes.builder()
				.oauthId((String) response.get("id"))
				.name((String) response.get("id"))
				.email((String) response.get("id"))
				.picture((String) response.get("id"))
				.provider(Provider.GOOGLE)
				.attributes(response)
				.nameAttributeKeys("id")
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuth2Attributes ofFacebook(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		
		return OAuth2Attributes.builder()
				.oauthId((String) response.get("id"))
				.name((String) response.get("id"))
				.email((String) response.get("id"))
				.picture((String) response.get("id"))
				.provider(Provider.FACEBOOK)
				.attributes(response)
				.nameAttributeKeys("id")
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuth2Attributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		
		return OAuth2Attributes.builder()
				.oauthId((String) response.get("id"))
				.name((String) response.get("id"))
				.email((String) response.get("id"))
				.picture((String) response.get("id"))
				.provider(Provider.GITHUB)
				.attributes(response)
				.nameAttributeKeys("id")
				.build();
	}
}
