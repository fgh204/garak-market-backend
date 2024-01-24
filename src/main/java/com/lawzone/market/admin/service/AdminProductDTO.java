package com.lawzone.market.admin.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminProductDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal supplyPrice;
	private BigDecimal productStock;
	private BigDecimal productWeight;
	private String productDesc;
	private String shopName;
	private String todayDeliveryStandardTime;
	private String productCategoryCode;
	private String productCategorySmallName;
	private String imgRegstYn;
	private BigInteger changProductPrice;
	private String deliveryOrderId;
	private String combinedDeliveryYn;
	private BigDecimal deliveryAmount;
	
	public AdminProductDTO() {
		
	}
}
