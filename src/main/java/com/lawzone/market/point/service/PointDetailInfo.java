package com.lawzone.market.point.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class PointDetailInfo extends BaseTimeEntity{
	@Id
	@Comment("포인트상세ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pointDetailId;
	
	@Comment("socialId")
	@Column(columnDefinition = "varchar(100)")
	private String socialId;
	
	@Comment("포인트상태코드")
	@Column(columnDefinition = "varchar(3)")
	private String pointStateCode;
	
	@Comment("포인트값")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal pointValue;
	
	@Comment("포인트구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String pointCode;
	
	@Comment("포인트적립ID")
	private Long pointSaveId;
	
	@Comment("포인트원본ID")
	private Long pointOriginalId;
	
	@Comment("포인트 만료일시")
	@Column(name="point_expiration_datetime", length=20)
	@Convert(converter=DateToStringConverter.class)
    private String pointExpirationDatetime;
}
