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
public class AdminOrderDTO {
	private String orderNo;
	private String custNm;
	private String productId;
	private String productNm;
	private BigDecimal productCount;
	
	public AdminOrderDTO() {
		
	}
}
