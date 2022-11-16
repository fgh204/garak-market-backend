package com.lawzone.market.config;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import groovy.transform.ToString;
import lombok.Data;

@Data
@Component
@ToString
public class SessionBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String userNm;
	private String sellerYn;
	private String email;
	private String phoneNumber;
	private String socialId;
	private String token;
	private String svcUrl;
	private String controller;
	private String method;
	private String sessionId;
	private String userIp;
	private String agent;
}
