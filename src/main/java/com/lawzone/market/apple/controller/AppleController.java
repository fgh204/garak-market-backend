package com.lawzone.market.apple.controller;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.admin.dto.login.LoginDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/apple")
public class AppleController {
	@Resource
	private SessionBean sessionBean;
	
	public static final String TEAM_ID = "H645RW4U34";
	public static final String REDIRECT_URL = "https://trans.domaado.me/login/oauth2/code/apple";
	public static final String CLIENT_ID = "domaado.me.service";
	public static final String KEY_ID = "65S86F3A58";
	public static final String AUTH_URL = "https://appleid.apple.com";
	public static final String KEY_PATH = "65S86F3A58.p8";
	private final UserInfoService userInfoService;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Value("${lzmarket.jwttoken}") 
	private String token;
	
	@Value("${lzmarket.baseurl}") 
	private String baseurl;
	
	/**
	 * Controller
	 */
	
	@RequestMapping(value = "/login/getAppleAuthUrl")
	public @ResponseBody String getAppleAuthUrl(
			HttpServletRequest request) throws Exception {
		
	    String reqUrl = 
	    		AUTH_URL 
	    		+ "/auth/authorize?client_id=" 
	    	    + CLIENT_ID
	    	    + "&redirect_uri=" 
	    	    + REDIRECT_URL
	            + "&response_type=code id_token&scope=name email&response_mode=form_post";

	    return reqUrl;
	}

	@RequestMapping(value = "/login")
	public void oauth_apple(
			HttpServletRequest request
			, @RequestParam(value = "code", required= false) String code
			, @RequestParam(value = "id_token", required= false) String id_token
			, @RequestParam(value = "data", required= false) String data
			, @RequestParam(value = "name", required= false) String name
			, @RequestParam(value = "email", required= false) String email
			, HttpServletResponse response
			) throws Exception {
		
	    String client_id = CLIENT_ID;
	    String client_secret = createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);
	    // 토큰 검증 및 발급
	    String reqUrl = AUTH_URL + "/auth/token";
	    Map<String, String> tokenRequest = new HashMap<>();
	    tokenRequest.put("client_id", client_id);
	    tokenRequest.put("client_secret", client_secret);
	    tokenRequest.put("code", code);
	    tokenRequest.put("grant_type", "authorization_code");
	    JSONObject data1 = decodeFromIdToken(id_token);
	    String apiResponse = doPost(reqUrl, tokenRequest);
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    JSONObject tokenResponse = objectMapper.readValue(apiResponse, JSONObject.class);
	    if (tokenResponse.get("error") == null ) {
	    	JSONObject appleInfo = decodeFromIdToken(tokenResponse.getString("id_token"));
	    	log.error("tokenResponse===============" + tokenResponse);
	    	log.error("appleInfo===============" + appleInfo);
	    	//log.error("userName===============" + map);
	    	//log.error("userName===============" + data);
	    	//log.error("userName===============" + name);
	    	//log.error("userName===============" + email);
	    	Map res = new HashMap<>();

		    res = new ObjectMapper().readValue(appleInfo.toString(), HashMap.class);
            
		    Map appleMap = this.userInfoService.appleLogin(res);
	    	
		    String token = (String) appleMap.get("token");
		    
			ResponseCookie accessTokenCookie;
			
			if("P".equals(this.service)) {
				accessTokenCookie = ResponseCookie.from(this.token, token)
		                .path("/")
		                .secure(true)
		                .httpOnly(true)
		                .sameSite("None")
		                .domain("domaado.me")
		                .build();
			} else if("T".equals(this.service)) {
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
			
			response.setHeader("Set-Cookie", accessTokenCookie.toString());	
	    }
	    response.sendRedirect("https://domaado.me/product");
	}
	
	@ResponseBody
	@PostMapping("/authorization/callback")
	public String getLogin(HttpServletRequest request
			, HttpServletResponse httpResponse
			, @RequestBody(required = true) Map map) throws IOException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		LoginDTO loginDTO = new LoginDTO();
		loginDTO = (LoginDTO) ParameterUtils.setDto(map, loginDTO, "insert", sessionBean);
		
		String oauthCode = loginDTO.getOauthCode();
		
		String client_id = CLIENT_ID;
	    String client_secret = createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);
	    // 토큰 검증 및 발급
	    String reqUrl = AUTH_URL + "/auth/token";
	    Map<String, String> tokenRequest = new HashMap<>();
	    tokenRequest.put("client_id", client_id);
	    tokenRequest.put("client_secret", client_secret);
	    tokenRequest.put("code", oauthCode);
	    tokenRequest.put("grant_type", "authorization_code");
	    String apiResponse = doPost(reqUrl, tokenRequest);
            
	    ObjectMapper objectMapper = new ObjectMapper();
	    JSONObject tokenResponse = objectMapper.readValue(apiResponse, JSONObject.class);
	    
	    if (tokenResponse.get("error") == null ) {
	    	//log.error("tokenResponse===========" + tokenResponse);
	    	JSONObject appleInfo = decodeFromIdToken(tokenResponse.getString("id_token"));
	    	//log.error("appleInfo===========" + appleInfo);
	    	Map res = new HashMap<>();

		    res = new ObjectMapper().readValue(appleInfo.toString(), HashMap.class);
		    res.put("access_token", tokenResponse.getString("access_token"));
		    Map appleMap = this.userInfoService.appleLogin(res);
	    	
		    String token = (String) appleMap.get("token");
		    
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
	        rtnMap.put("isPushIdExist", appleMap.get("isRegistered"));
	        //rtnMap.put("token", token);
	        rtnMap.put("loginId", appleMap.get("loginId"));
	        rtnMap.put("password", appleMap.get("password"));
	        return JsonUtils.returnValue("0000", "로그인되었습니다.", rtnMap).toString();
	    }else {
	    	Map rtnMap = new HashMap<>();
	        return JsonUtils.returnValue("0000", "로그인 오류입니다.", rtnMap).toString();
	    }
    }
	
	
	/**
	 * Util
	 * @throws IOException 
	 */
	
    public String createClientSecret(String teamId, String clientId, String keyId, String keyPath, String authUrl) throws IOException {
    	
    	ClassPathResource resource = new ClassPathResource(keyPath);
    	InputStream is = new BufferedInputStream(resource.getInputStream());
    	String privateKey = IOUtils.toString(is);
    	is.close();
    	Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        log.info("pKey===============");
        PrivateKey pKey = converter.getPrivateKey(object);
        log.info("pKey===============" + pKey);
    	
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                   .setHeaderParam("kid", keyId)
                   .setHeaderParam("alg", "ES256")
                   .setIssuer(teamId)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(expirationDate)
                   .setAudience("https://appleid.apple.com")
                   .setSubject(clientId)
                   .signWith(SignatureAlgorithm.ES256, pKey)
                   .compact();
    }   
    
    public String doPost(String url, Map<String, String> param) {
        String result = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        Integer statusCode = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> nvps = new ArrayList<>();
            Set<Map.Entry<String, String>> entrySet = param.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                nvps.add(new BasicNameValuePair(fieldName, fieldValue));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(formEntity);
            response = httpclient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            //log.error("response=============" + response);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");

            if (statusCode != 200) {
                System.out.println("애러");
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    

    public JSONObject decodeFromIdToken(String id_token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
    	    String appleInfo = getPayload.toJSONObject().toJSONString();
    	    ObjectMapper objectMapper = new ObjectMapper();
    	    JSONObject payload = objectMapper.readValue(appleInfo, JSONObject.class);
    	    
    	   // JSONObject payload = new JSONObject(appleInfo);

            if (payload != null) {
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
