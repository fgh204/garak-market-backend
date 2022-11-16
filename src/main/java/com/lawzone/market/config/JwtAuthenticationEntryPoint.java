package com.lawzone.market.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401(인증 실패)
    	
    	if("/".equals(request.getRequestURI()) || request.getRequestURI().indexOf("/admin/") > -1) {
    		response.sendRedirect("/admin/login");
    	}else {
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    	}
    }
}