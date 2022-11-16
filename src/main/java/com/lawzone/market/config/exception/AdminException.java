package com.lawzone.market.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "siteuser not found")
public class AdminException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public AdminException(String message){
        super(message);
    }
}