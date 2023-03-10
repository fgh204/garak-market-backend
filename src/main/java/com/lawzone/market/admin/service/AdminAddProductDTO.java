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
public class AdminAddProductDTO {
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productStock;
	private BigDecimal productWeight;
	private String productDesc;
	private String productTag1;
	private String productTag2;
	private String productTag3;
	
	public AdminAddProductDTO() {
		
	}
}
