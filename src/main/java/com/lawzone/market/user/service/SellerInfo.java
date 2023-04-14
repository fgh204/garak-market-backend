package com.lawzone.market.user.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class SellerInfo extends BaseTimeEntity{
	@Id
	@Comment("판매자ID")
	@Column(unique = true, columnDefinition = "varchar(8)")
	private String sellerId;
	
	@Comment("상호")
	//@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String shopName; 
	
	@Comment("사업장주소")
	//@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String businessAddress;

	@Comment("사업자번호")
	//@NotNull
	@Column(columnDefinition = "varchar(30)")
	private String businessNumber;

	@Comment("로그인ID")
	@Column(columnDefinition = "varchar(100)")
	private String loginId;

	@Comment("비밀번호")
	@Column(columnDefinition = "varchar(300)")
	private String password;

	@Comment("SPOTID")
	@Column(columnDefinition = "varchar(50)")
	private String spotId;
	
	@Comment("SPOTNAME")
	@Column(columnDefinition = "varchar(300)")
	private String spotName;
	
	@Comment("소개글")
	@Column(unique = true, columnDefinition = "MEDIUMTEXT")
	private String introductionText;
	
	@Comment("판매자전화번호")
	@Column(columnDefinition = "varchar (11)")
	private String sellerPhoneNumber;
	
	@Comment("상품카테고리코드")
	@Column(columnDefinition = "CHAR(9)")
	private String productCategoryCode;
	
	@Comment("배송금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal deliveryAmount;
	
	@Comment("지역코드")
	//@NotNull
	@Column(columnDefinition = "varchar(5)")
	private String zonecode;
	
	@Comment("판매자지역코드")
	//@NotNull
	@Column(columnDefinition = "varchar(5)")
	private String sellerZonecode;
	
	@Comment("오늘배송기준시간")
	@Column(columnDefinition = "varchar(4)")
    private String todayDeliveryStandardTime;
	
	@Comment("합배송여부")
	@Column(columnDefinition = "varchar(1)")
    private String combinedDeliveryYn;
	
	@Comment("마켓노출여부")
	@Column(columnDefinition = "varchar(1)")
    private String MarketExposureYn;
}
