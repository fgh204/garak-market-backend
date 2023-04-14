package com.lawzone.market.admin.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TodayProductDTO {
	private String productName;
	private String productCategory;
	private BigDecimal productPrice;
	private String productSellerName;
	private String productId;
	private String orderDttm;
	private BigDecimal productCount;
	
	public TodayProductDTO() {
		
	}
}
