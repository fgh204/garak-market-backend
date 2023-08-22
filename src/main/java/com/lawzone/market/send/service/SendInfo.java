package com.lawzone.market.send.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class SendInfo extends BaseTimeEntity{
	@Id
	@Comment("발송정보일련번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sendInfoSeq;
	
	@Comment("발송등록ID")
	@Column(columnDefinition = "varchar(100)")
	private String sendId;
	
	@Comment("발송서식코드")
	@Column(columnDefinition = "CHAR(8)")
	private String sendFormCode;
	
	@Comment("서식명")
	@Column(columnDefinition = "varchar(150)")
	private String sendFormName;
	
	@Comment("푸쉬서비스서식코드")
	@Column(columnDefinition = "varchar(40)")
	private String pushSvcFormCode;
	
	@Comment("푸쉬서비스서식명")
	@Column(columnDefinition = "varchar(100)")
	private String pushSvcFormName;
	
	@Comment("발송내용")
	@Column(columnDefinition = "text")
	private String sendText;
	
	@Comment("phone")
	@Column(columnDefinition = "varchar(15)")
	private String phone;
	
	@Comment("발송여부")
	@Column(columnDefinition = "varchar(1)")
	private String sendYn;
	
	@Comment("등록결과코드")
	@Column(columnDefinition = "varchar(10)")
	private String regResponseCode;
	
	@Comment("발송결과코드")
	@Column(columnDefinition = "varchar(10)")
	private String sendResponseCode;
	
	@Comment("수신고객번호")
	@Column(columnDefinition = "varchar(8)")
	private String receiveUserId;
}
