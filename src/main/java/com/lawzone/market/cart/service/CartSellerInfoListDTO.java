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
public class CartSellerInfoListDTO {
	private String sellerId;
	private BigDecimal productPrice;
	private String combinedDeliveryYn;
	private String shopName;
	private BigDecimal deliveryAmount;
	private BigDecimal combinedDeliveryStandardAmount;
	
	public CartSellerInfoListDTO() {
		
	}
}
