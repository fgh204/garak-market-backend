package com.lawzone.market.telmsgLog.service;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class TelmsgLogInfo {
	@Id
	@Comment("전문로그번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long telmsgNo;
	
	@Comment("처리일시")
	@NotNull
	@Column(name="dlng_dttm", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String dlngDttm;
	
	@Comment("서비스URL")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String svcUrl;
	
	@Comment("controller")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String controller;
	
	@Comment("method")
	@NotNull
	@Column(columnDefinition = "varchar(200)")
	private String method;
	
	@Comment("사용자ID")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("세션ID")
	@Column(columnDefinition = "varchar(200)")
	private String sessionId;
	
	@Comment("전문DATA")
	@Column(columnDefinition = "LONGTEXT")
	private String telmsgDtaInfo;
	
	@Comment("사용자IP")
	@NotNull
	@Column(columnDefinition = "varchar(100)")
	private String userIp;
	
	@Comment("유입경로코드")
	@NotNull
	@Column(columnDefinition = "varchar(2)")
	private String ingrsPathCd;
	
	@Comment("처리유형코드")
	@NotNull
	@Column(columnDefinition = "varchar(2)")
	private String dlngTpcd;
	
	@Comment("device")
	@NotNull
	@Column(columnDefinition = "varchar(1)")
	private String device;
	
	@Comment("송수신구분코드")
	@NotNull
	@Column(columnDefinition = "varchar(1)")
	private String trnrcvCfcd;
}
