package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.lawzone.market.review.service.ProductReviewInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class PageInfoDTO {
	private BigDecimal totalPageCount;
	private BigInteger totalCount;
	
	public PageInfoDTO() {
		
	}
}
