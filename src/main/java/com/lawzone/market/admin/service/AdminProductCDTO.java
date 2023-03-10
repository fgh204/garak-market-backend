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
public class AdminProductCDTO {
	private String productCategoryCode;
	private Integer userLvl;
	private String sellerId;
	private String useYn;
	private String imgRegstYn;
	
	public AdminProductCDTO() {
		
	}
}
