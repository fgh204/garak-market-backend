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
	
	public CartInfoListDTO() {
		
	}
}
