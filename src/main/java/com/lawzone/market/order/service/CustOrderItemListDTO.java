package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Convert;

import com.lawzone.market.util.BooleanToYNConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class CustOrderItemListDTO {
	private String productName;
	private String orderItemStateCode;
	private String orderItemStateName;
	private BigDecimal productCount;
	private BigDecimal productPrice;
	private BigDecimal pointAmount;
	private BigDecimal totalProductPrice;
	private String thumbnailImagePath;
	private String productId;
	private String orderItemDlngStateCode;
	private String orderItemDlngStateName;
	private Boolean isReviewRegistered;
	
	public CustOrderItemListDTO(
			String productName
			, String orderItemStateCode
			, String orderItemStateName
			, BigDecimal productCount
			, BigDecimal productPrice
			, BigDecimal pointAmount
			, BigDecimal totalProductPrice
			, String thumbnailImagePath
			, String productId
			, String orderItemDlngStateCode
			, String orderItemDlngStateName
			, String isReviewRegistered
			) {
		this.productName = productName;
		this.orderItemStateCode = orderItemStateCode;
		this.orderItemStateName = orderItemStateName;
		this.productCount = productCount;
		this.productPrice = productPrice;
		this.pointAmount = pointAmount;
		this.totalProductPrice = totalProductPrice;
		this.thumbnailImagePath = thumbnailImagePath;
		this.productId = productId;
		this.orderItemDlngStateCode = orderItemDlngStateCode;
		this.orderItemDlngStateName = orderItemDlngStateName;
		this.isReviewRegistered = "Y".equalsIgnoreCase(isReviewRegistered);
	}
	
	public CustOrderItemListDTO() {
		
	}
}
