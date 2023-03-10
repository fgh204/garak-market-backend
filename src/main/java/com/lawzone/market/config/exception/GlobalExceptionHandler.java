package com.lawzone.market.config.exception;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.telmsgLog.dao.TelmsgLogDAO;
import com.lawzone.market.telmsgLog.service.TelmsgLogInfo;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private TelmsgLogService telmsgLogService;
	
	@Autowired
	public GlobalExceptionHandler(TelmsgLogService telmsgLogService) {
		this.telmsgLogService = telmsgLogService;
	}
	
	@Resource
	private SessionBean sessionBean;
	
    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicateException(EmailDuplicateException ex){
        //log.error("handleEmailDuplicateException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request){
        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        Map rtnMap = new HashMap<>();
        
        rtnMap.put("exception", ExceptionUtils.getStackTrace(ex));
        
        this.telmsgLogService.addTelmsgLog("99", "00", "1", rtnMap, ex.toString());
        return JsonUtils.returnValue("9999","시스템 에러입니다.",rtnMap);
    }
    
    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ErrorResponse> handleAdminException(AdminException ex){
        //log.error("handleException",ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}