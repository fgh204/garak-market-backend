package com.lawzone.market.config;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Aspect
@Component
@Slf4j
public class Aspects {
	
	public static final String IS_MOBILE = "MOBILE";
	private static final String IS_PHONE = "PHONE";
	
	@Resource
	private SessionBean sessionBean;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Pointcut("execution(* com.lawzone.market.*.controller.*.*(..))")
	public void controllerPointcut() {		
	}
	
	@Around("controllerPointcut()")
	public Object doLogging(ProceedingJoinPoint joinPoint ) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String userAgent = request.getHeader("User-Agent").toUpperCase();
		String agent = "";
		
		log.info("userAgent ================== " + userAgent);
		
		if(userAgent.indexOf(IS_MOBILE) > -1){
			if(userAgent.indexOf(IS_PHONE) == -1) {
				agent = "M";
			}else {
				agent = "M";
			}
		}else {
			agent = "W";
		}
		
		String json =  (String) authentication.getPrincipal();
		
		if(!"anonymousUser".equals(json)) {
			ObjectMapper mapper = new ObjectMapper();
	    	Map<String, String> userForm = mapper.readValue(json, Map.class);
			
	    	sessionBean.setUserId((String) userForm.get("userId"));
			sessionBean.setUserNm((String) userForm.get("userNm"));
			sessionBean.setSellerYn((String) userForm.get("sellerYn"));
			sessionBean.setEmail((String) userForm.get("email"));
			sessionBean.setPhoneNumber((String) userForm.get("phoneNumber"));
			sessionBean.setSocialId((String) userForm.get("socialId"));
			sessionBean.setAgent(agent);
		}
		
		Map params = new HashMap<>();
		
		try {
//			String controllerName = joinPoint.getSignature().getDeclaringType().getName();
//			String methodName = joinPoint.getSignature().getName();
//			String userIp = getClientIP(request);
//			String sessionId = request.getSession().getId();
//			params.put("controller", controllerName);
//			params.put("method", methodName);
//			params.put("params", getParams(request));
//			params.put("log_time", LocalDateTime.now());
//			params.put("request_url", request.getRequestURI());
//			params.put("http_method", request.getMethod());
//			params.put("user_ip", userIp);
//			params.put("session_id", sessionId);

			sessionBean.setSvcUrl(request.getRequestURI());
			sessionBean.setController(joinPoint.getSignature().getDeclaringType().getName());
			sessionBean.setMethod(joinPoint.getSignature().getName());
			sessionBean.setSessionId(request.getSession().getId());
			sessionBean.setUserIp(getClientIP(request));
		}catch (Exception e) {
			log.error("LoggerAspect error" , e);
		}
		
		//log.info("log : {}" + params);
		
		Object result = joinPoint.proceed();
		
		return result;
	}
	
	private static JSONObject getParams(HttpServletRequest request) {
		JSONObject jSONObject = new JSONObject();
		Enumeration<String> params = request.getParameterNames();
		
		while(params.hasMoreElements()) {
			String param = params.nextElement();
			String replaceParam = param.replaceAll("\\.", "-");
			jSONObject.put(replaceParam, request.getParameter(param));
		}
		return jSONObject;
	}
	
	private static String getClientIP(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_CLIENT_IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-Real-IP"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-RealIP"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
        }
		
		return ip;
	}
}