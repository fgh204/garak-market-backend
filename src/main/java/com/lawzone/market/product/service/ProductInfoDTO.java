package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductInfoDTO {
	private String productId;
	private String beginDate;
	private String endDate;
	private String productName;
	private BigDecimal productPrice;
	private BigInteger supplyPrice;
	private BigInteger productStock;
	private String productDesc;
	private Character useYn;
	private String productCategoryCode;
	private String sellerId;
	private String createDatetime;
	private String updateDatetime;
	private String shopName;
	private String sellerName;
	private String thumbnailImagePath;
	private String sellerProfileImagePath;
	private String todayDeliveryStandardTime;
	private String todayDeliveryYn;
	private String slsDate;
	private BigDecimal productWeight;
	private String slsDateText;
	private String eventBeginTime;
	private String eventEndTime;
	private String eventBeginDate;
	private String eventEndDate;
	private BigInteger personBuyCount;
	private BigInteger eventCount;
	private String eventId;
	private BigInteger combinedDeliveryStandardAmount;
	private BigInteger deliveryAmount;
	private String combinedDeliveryYn;
	
	public ProductInfoDTO() {
		
	}
}
