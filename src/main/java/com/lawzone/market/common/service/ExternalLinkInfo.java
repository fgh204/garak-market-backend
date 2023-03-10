package com.lawzone.market.common.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Entity
@Data
public class ExternalLinkInfo extends BaseTimeEntity{
	@Id
	@Comment("외부연계코드")
	@Column(columnDefinition = "varchar(5)")
	private String externalLinkCompanyCode;
	
	@Comment("accessToken")
	@Column(columnDefinition = "varchar(3000)")
	private String accessToken;
	
	@Comment("refreshToken")
	@Column(columnDefinition = "varchar(3000)")
	private String refreshToken;
	
}
