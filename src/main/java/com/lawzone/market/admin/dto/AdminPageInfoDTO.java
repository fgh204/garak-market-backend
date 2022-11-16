package com.lawzone.market.admin.dto;

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
public class AdminPageInfoDTO {
	private BigInteger totalCount;
	private BigDecimal totalPages;
	private String hasNext;
	private String hasPrevious;
	private BigInteger number;
	
	public AdminPageInfoDTO() {
		
	}
}
