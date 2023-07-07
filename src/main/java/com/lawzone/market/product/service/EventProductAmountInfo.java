package com.lawzone.market.product.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class EventProductAmountInfo extends BaseTimeEntity{
	@Id
	@Comment("이벤트상품금액번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventProductAmountNumber;
	
	@Comment("이벤트ID")
	@Column(columnDefinition = "varchar(30)")
	private String eventId;
	
	@Comment("이벤트상품명")
	@Column(columnDefinition = "varchar(300)")
	private String eventProductName; 
	
	@Comment("이벤트상품할인구분코드")
	@Column(columnDefinition = "varchar(2)")
	private String eventProductDiscountCfCode;
	
	@Comment("이벤트상품할인구분값")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private String eventProductDiscountCfValue;
	
	@Comment("이벤트상품할인률")
	@Column(columnDefinition = "DECIMAL (4,2)")
	private String eventProductDiscountRate;
	
	@Comment("이벤트시작시간")
	@Column(columnDefinition = "varchar(4)")
    private String beginTime;
	
	@Comment("이벤트종료시간")
	@Column(columnDefinition = "varchar(4)")
    private String endTime;
}
