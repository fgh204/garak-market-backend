package com.lawzone.market.admin.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.CdDtlInfoId;
import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.product.service.PageInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class CustReviewInfoDTO {
	private String productName;
	private String orderNo;
	private String userId;
	private BigDecimal tasteScore;
	private BigDecimal freshScore;
	private BigDecimal deliveryScore;
	private BigDecimal reorderScore;
	private BigDecimal productScore;
	private BigDecimal packagingScore;
	private String reviewTitle;
	private String reviewText;
	private String userName;
	private String updateDatetime;
	private String phoneNumber;
		
	public CustReviewInfoDTO() {
		
	}
}
