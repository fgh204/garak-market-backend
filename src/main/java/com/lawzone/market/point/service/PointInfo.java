package com.lawzone.market.point.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.BaseDateToStringConverter;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class PointInfo extends BaseTimeEntity{
	@Id
	@Comment("포인트ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pointId;
	
	@Comment("userId")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("이벤트코드 001 : 통상, 002 : 특정이벤트, 003 : 결제, 004 : 쿠폰")
	@Column(columnDefinition = "varchar(10)")
	private String eventCode;
	
	@Comment("이벤트ID 이벤트 코드가 001 : '' , 002 : 특정이벤트ID, 003 : 주문번호, 004 : 쿠폰번호")
	@Column(columnDefinition = "varchar(30)")
	private String eventId;
	
	@Comment("포인트상태코드 001 : 적립, 002 : 사용, 003 : 만료")
	@Column(columnDefinition = "varchar(3)")
	private String pointStateCode;
	
	@Comment("포인트값")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal pointValue;
	
	@Comment("포인트구분코드 001 : 금액, 002 : 쿠폰(%)")
	@Column(columnDefinition = "varchar(3)")
	private String pointCode;
	
	@Comment("포인트등록일")
	@Column(name="point_regist_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String pointRegistDate;
	
	@Comment("포인트 만료일시")
	@Column(name="point_expiration_datetime", length=20)
	@Convert(converter=DateToStringConverter.class)
    private String pointExpirationDatetime;
}
