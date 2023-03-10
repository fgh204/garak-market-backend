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
public class AdminUserInfoDTO {
	private String shopName;
	private String productCategoryCode;
	private String userName;
	private Integer userLvl;
	
	public AdminUserInfoDTO() {
		
	}
}
