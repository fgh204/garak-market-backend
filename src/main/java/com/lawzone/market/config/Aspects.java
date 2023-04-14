package com.lawzone.market.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.sf.json.JSONArray;
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
	
	@Autowired
	private TelmsgLogService telmsgLogService;
	
	public void setTelmsgLogService(TelmsgLogService telmsgLogService) {
        this.telmsgLogService = telmsgLogService;
    }
	
	@Pointcut("execution(* com.lawzone.market.*.controller.*.*(..))")
	public void controllerPointcut() {		
	}
	
	@Around("controllerPointcut()")
	public Object doLogging(ProceedingJoinPoint joinPoint ) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		//String userAgent = request.getHeader("User-Agent").toUpperCase();
		
		String userAgent = request.getHeader("User-Agent");
		
		String agent = "";
		
		Object[] args = joinPoint.getArgs();
		Map resultMap = new HashMap<>();
		
		String _svcUrl = request.getRequestURI();
		String _controller = joinPoint.getSignature().getDeclaringType().getName();
		String _method = joinPoint.getSignature().getName();
		
		for(Object obj : args) {
			if(obj != null) {
				if("LinkedHashMap".equals(obj.getClass().getSimpleName())) {
					resultMap.put("data", obj);
					break;
				}
			}
        }
		
		//log.error("userAgent ================== " + userAgent);
		//log.error("userAgent ================== " + request.getRemoteAddr());
		//log.error("userAgent ================== " + request.getRemoteHost());
		//log.error("userAgent ================== " + request.getServerName());
		
		log.info("svcUrl============" + _svcUrl);
		boolean isMobile = userAgent.matches(".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*");
		
		if(userAgent.indexOf("iosapp") > -1){
			agent = "I";
		}else if( userAgent.indexOf("androidapp") >-1){
			agent = "A";
		}else if(isMobile){
			agent = "M";
		}else {
			agent = "W";
		}
		
//		if(userAgent.indexOf(IS_MOBILE) > -1){
//			if(userAgent.indexOf(IS_PHONE) == -1) {
//				agent = "M";
//			}else {
//				agent = "P";
//			}
//		}else {
//			agent = "W";
//		}
		
		String json =  (String) authentication.getPrincipal();
		
		if(!"anonymousUser".equals(json)) {
			ObjectMapper mapper = new ObjectMapper();
	    	Map userForm = mapper.readValue(json, Map.class);
			
	    	sessionBean.setUserId((String) userForm.get("userId"));
			sessionBean.setUserNm((String) userForm.get("userNm"));
			sessionBean.setSellerYn((String) userForm.get("sellerYn"));
			sessionBean.setEmail((String) userForm.get("email"));
			sessionBean.setPhoneNumber((String) userForm.get("phoneNumber"));
			sessionBean.setSocialId((String) userForm.get("socialId"));
			
			if(userForm.get("userLvl") == null) {
				sessionBean.setUserLvl("1");
			}else {
				sessionBean.setUserLvl(userForm.get("userLvl").toString());
			}
			
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
			sessionBean.setAgent(agent);
//			sessionBean.setSvcUrl(request.getRequestURI());
//			resultMap.put("svcUrl", request.getRequestURI());
//			sessionBean.setController(joinPoint.getSignature().getDeclaringType().getName());
//			resultMap.put("controller", joinPoint.getSignature().getDeclaringType().getName());
//			sessionBean.setMethod(joinPoint.getSignature().getName());
//			resultMap.put("method", joinPoint.getSignature().getName());
			
			sessionBean.setSvcUrl(_svcUrl);
			resultMap.put("svcUrl", _svcUrl);
			sessionBean.setController(_controller);
			resultMap.put("controller", _controller);
			sessionBean.setMethod(_method);
			resultMap.put("method", _method);
			
			sessionBean.setSessionId(request.getSession().getId());
			sessionBean.setUserIp(getClientIP(request));
		}catch (Exception e) {
			log.error("LoggerAspect error" , e);
		}
		
		//TelmsgLogService telmsgLogService = new TelmsgLogService(null);
		
		//this.telmsgLogService = new TelmsgLogService(null);
		
		this.telmsgLogService.addTelmsgLog1("00", "00", "1", resultMap);
		
		//log.info("log : {}" + params);
		
		Object result = joinPoint.proceed();
		
		return result;
	}
	
//	@Around("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
//    public void handleRequestRejectedException (ProceedingJoinPoint pjp) throws Throwable {
//        try {
//            pjp.proceed();
//        } catch (RequestRejectedException exception) {
//            HttpServletResponse response = (HttpServletResponse) pjp.getArgs()[1];
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//        }
//    }
	
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
	
	@AfterReturning(value = "controllerPointcut()", returning = "obj")
    public void afterReturn(JoinPoint joinPoint, Object obj) throws ParseException {
        JSONParser parser = new JSONParser();
        
        if(obj != null) {
        	Object obj2 = parser.parse( obj.toString());
        	
        	if(obj2.getClass().getName().indexOf("JSONObject") > -1) {
        		Map resultMap = new HashMap<>();
                resultMap = (Map) obj2;
                
                if("9999".equals(resultMap.get("msgCd"))){
                	Map logMap = new HashMap<>();
                	logMap.put("msgNm", resultMap.get("msgNm"));
                	
            		this.telmsgLogService.addTelmsgLog("99", "00", "2", logMap,"");
                }
        	} else {
        		log.error("Aspects error!! === " + obj2.toString());
        	}
        }
    }
}