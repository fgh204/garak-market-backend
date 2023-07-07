package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CartInfoListDTO {
	private String productId;
	private BigInteger cartNumber;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productCount;
	private String thumbnailImagePath;
	private String shopName;
	private String sellerName;
	private String todayDeliveryStandardTime;
	private String todayDeliveryYn;
	private String slsDate;
	private BigInteger productStock;
	private String productCategoryCode;
	private String productCategorySmallName;
	private String sellerId;
	private String slsDateText;
	private String eventBeginTime;
	private String eventEndTime;
	private String eventBeginDate;
	private String eventEndDate;
	private BigInteger personBuyCount;
	private BigInteger eventCount;
	private String eventId;
	private String createDatetime;
	private String updateDatetime;
	
	public CartInfoListDTO() {
		
	}
}
