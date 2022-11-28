package com.lawzone.market.config;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig implements AuditorAware<String>{
	
	@Resource
	private SessionBean sessionBean;
	
	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(sessionBean.getUserId());
	}
	

}
