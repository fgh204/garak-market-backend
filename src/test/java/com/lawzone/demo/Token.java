package com.lawzone.demo;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Token {
	private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 10 * 3650; // 10시간

    private String secretKey = "56465465849847698889922123123456";
    
    /**
     * 토큰 생성
     */
    public String generateToken() {
    	Map userMap = new HashMap<>();
		userMap.put("userId", "00000002");
		userMap.put("userNm", "김혁수");
		userMap.put("sellerYn", "Y");
		userMap.put("email", "fgh204@kakao.com");
		userMap.put("phoneNumber", "01033524509");
		userMap.put("socialId", "2212318611");
    	
		JSONObject jsonObject = new JSONObject(userMap);
		
        Claims claims = Jwts.claims().setSubject((String) userMap.get("socialId"));
        claims.put("userForm", jsonObject.toString());
        return createToken(claims); // socialIdf를 subject로 해서 token 생성
    }

    @SuppressWarnings("deprecation")
	private String createToken(Claims claims) {
        return Jwts.builder()
        		.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_MILISECOND))
                .signWith(SignatureAlgorithm.HS256,Base64.getEncoder().encodeToString(this.secretKey.getBytes()))
                .compact();
    }

    /**
     * 토큰 유효여부 확인
     */
    public Boolean isValidToken(String token, String socialId) {
        log.info("isValidToken token = {}", token);
        String subject = getSocialIdFromToken(token);
        return (subject.equals(socialId) && !isTokenExpired(token));
    }

    /**
     * 토큰의 Claim 디코딩
     */
    @SuppressWarnings("deprecation")
	private Claims getAllClaims(String token) {
        log.info("getAllClaims token = {}", token);
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
        log.info("getUsernameFormToken subject = {}", socialId);
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
            		if("Authorization".equals(myCookies[i].getName())) {
            			_resolveToken = myCookies[i].getValue();
            			break;
            		}
        		}
        	}
        }
        
    	return _resolveToken;
    }
    
 // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
//        String userInfo = this.userInfoDAO
//				.findBySocialId(this.getSocialIdFromToken(token)).toString();
//        
//        String _stream = userInfo;
//        _stream = _stream.replace("Optional[UserInfo(", "").replace(")]", "");
//        
    	String _stream = this.getSocialIdFromToken(token).toString();
        Collection<? extends GrantedAuthority> authorities =
        		Arrays.stream(_stream.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
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
    @Test
    public void main() {
    	log.info("generateToken(userInfo) " + generateToken());
    }
}
