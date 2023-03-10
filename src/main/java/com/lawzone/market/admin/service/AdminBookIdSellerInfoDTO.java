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
public class AdminBookIdSellerInfoDTO {
	private String spotId;
	private String shopName;
	private String sellerPhoneNumber;
	private String businessAddress;
	private String spotName;
	private String deliveryTime;
	private String phoneNumber;
	private String recipientName;
	private String zonecode;
	private BigDecimal deliveryAmount;
	private String categoryName;
	private String orderDttm;
	
	public AdminBookIdSellerInfoDTO() {
		
	}
}
