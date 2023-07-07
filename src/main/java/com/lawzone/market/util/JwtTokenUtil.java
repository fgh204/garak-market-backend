package com.lawzone.market.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.user.UserRole;
import com.lawzone.market.user.service.UserInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.json.simple.JSONObject;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil{
	private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 168;// 일주일
	//private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 10;// 10시간
	//private final long TOKEN_VALID_MILISECOND = 1000L * 10;
	
	@Value("${spring.jwt.secret}")
    private String secretKey;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Value("${lzmarket.jwttoken}") 
	private String jwtTokenName;
	
    /**
     * 토큰 생성
     */
    public String generateToken(UserInfo userInfo, Long tokenValidTime) {
    	Map userMap = new HashMap<>();
		userMap.put("userId", userInfo.getUserId());
		userMap.put("userNm", userInfo.getUserName());
		userMap.put("sellerYn", userInfo.getSellerYn());
		userMap.put("email", userInfo.getEmail());
		userMap.put("phoneNumber", userInfo.getPhoneNumber());
		userMap.put("socialId", userInfo.getSocialId());
		userMap.put("userLvl", userInfo.getUserLvl());
    	
		JSONObject jsonObject = new JSONObject(userMap);
		
        Claims claims = Jwts.claims().setSubject((String) userMap.get("socialId"));
        claims.put("userForm", jsonObject.toString());
        return createToken(claims, tokenValidTime); // socialIdf를 subject로 해서 token 생성
    }

    @SuppressWarnings("deprecation")
	private String createToken(Claims claims, Long _tokenValidTime) {
    	Long tokenValidTime = TOKEN_VALID_MILISECOND;
    	if(_tokenValidTime == null) {
    		tokenValidTime = TOKEN_VALID_MILISECOND;
    	}else {
    		tokenValidTime = _tokenValidTime;
    	}

        return Jwts.builder()
        		.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256,Base64.getEncoder().encodeToString(this.secretKey.getBytes()))
                .compact();
    }

    /**
     * 토큰 유효여부 확인
     */
    public Boolean isValidToken(String token, String socialId) {
        //log.info("isValidToken token = {}", token);
        String subject = getSocialIdFromToken(token);
        return (subject.equals(socialId) && !isTokenExpired(token));
    }

    /**
     * 토큰의 Claim 디코딩
     */
    @SuppressWarnings("deprecation")
	private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(this.secretKey.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Claim 에서 socialId 가져오기
     */
    public String getSocialIdFromToken(String token) {
        String socialId = String.valueOf(getAllClaims(token).get("userForm"));
        return socialId;
    }
    
    /**
     * 토큰 만료기한 가져오기
     */
    public Date getExpirationDate(String token) {
        Claims claims = getAllClaims(token);
        return claims.getExpiration();
    }

    /**
     * 토큰이 만료되었는지
     */
    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }
    
    public String resolveToken(HttpServletRequest req) {
        String _resolveToken = req.getHeader("Authorization");
        
        if(_resolveToken == null) {
        	Cookie[] myCookies = req.getCookies();
        	
        	if(myCookies != null) {
        		for(int i = 0; i < myCookies.length; i++) {
        			//log.error("myCookies[i].getName() ====== " + myCookies[i].getName());
        			//log.error("myCookies[i].getValue() ====== " + myCookies[i].getValue());
            		if(this.jwtTokenName.equals(myCookies[i].getName())) {
            			_resolveToken = myCookies[i].getValue();
            			break;
            		}
        		}
        	}
        }
        
        if("D".equals(this.service) && _resolveToken == null) {
        	//_resolveToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMjEyMzE4NjExIiwidXNlckZvcm0iOiJ7XCJ1c2VyTm1cIjpcIuq5gO2YgeyImFwiLFwicGhvbmVOdW1iZXJcIjpcIjAxMDMzNTI0NTA5XCIsXCJzb2NpYWxJZFwiOlwiMjIxMjMxODYxMVwiLFwidXNlcklkXCI6XCIwMDAwMDAwMlwiLFwic2VsbGVyWW5cIjpcIllcIixcImVtYWlsXCI6XCJmZ2gyMDRAa2FrYW8uY29tXCJ9IiwiaWF0IjoxNjY3NzE1MjI1LCJleHAiOjE3OTkxMTUyMjV9.8awIlGay-A_gNib8iVSAc9LYPA5-p3vNuOSHCN6d2JI";
    	}
    	return _resolveToken;
    }
    
 // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) throws JsonMappingException, JsonProcessingException {
//        String userInfo = this.userInfoDAO
//				.findBySocialId(this.getSocialIdFromToken(token)).toString();
//        
//        String _stream = userInfo;
//        _stream = _stream.replace("Optional[UserInfo(", "").replace(")]", "");
//        
    	String _stream = this.getSocialIdFromToken(token).toString();
        //Collection<? extends GrantedAuthority> authorities =
        //		Arrays.stream(_stream.split(","))
        //               .map(SimpleGrantedAuthority::new)
        //                .collect(Collectors.toList());
    	
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, String> userForm = mapper.readValue(_stream, Map.class);
    	
    	List<GrantedAuthority> authorities = new ArrayList<>();
        if ("Y".equals(userForm.get("sellerYn"))) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
    	
        return new UsernamePasswordAuthenticationToken(_stream, "", authorities);
    }
    
 // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(Base64.getEncoder().encodeToString(this.secretKey.getBytes())).build().parseClaimsJws(token).getBody();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
        	log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
        	log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
        	log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    
    private byte[] readPrivateKey(String keyPath) {

    	Resource resource = new ClassPathResource(keyPath);
        byte[] content = null;

        try (FileReader keyReader = new FileReader(resource.getFile());
             PemReader pemReader = new PemReader(keyReader)) {
            {
                PemObject pemObject = pemReader.readPemObject();
                content = pemObject.getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}