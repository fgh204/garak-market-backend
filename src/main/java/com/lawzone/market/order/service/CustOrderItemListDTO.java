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
	private Boolean isReviewWritable;
	private String shopName;
	private String sellerId;
	private String todayDeliveryStandardTime;
	private String orderCompletionDate;
	private String estimatedDeliveryDate;
	private String orderCompletionTime;
	private String orderCancellationDate;
	private String deliveryCompletionDate;
	private String slsDateText;
	private String orderNo;
	
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
			, String isReviewWritable
			, String shopName
			, String sellerId
			, String todayDeliveryStandardTime
			, String orderCompletionDate
			, String estimatedDeliveryDate
			, String orderCompletionTime
			, String orderCancellationDate
			, String deliveryCompletionDate
			, String slsDateText
			, String orderNo
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
		this.isReviewWritable = "Y".equalsIgnoreCase(isReviewWritable);
		this.shopName = shopName;
		this.sellerId = sellerId;
		this.todayDeliveryStandardTime = todayDeliveryStandardTime;
		this.orderCompletionDate = orderCompletionDate;
		this.estimatedDeliveryDate = estimatedDeliveryDate;
		this.orderCompletionTime = orderCompletionTime;
		this.orderCancellationDate = orderCancellationDate;
		this.deliveryCompletionDate = deliveryCompletionDate;
		this.slsDateText = slsDateText;
		this.orderNo = orderNo;
	}
	
	public CustOrderItemListDTO() {
		
	}
}
