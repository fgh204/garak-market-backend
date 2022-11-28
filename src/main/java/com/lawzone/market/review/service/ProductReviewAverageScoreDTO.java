package com.lawzone.market.review.service;

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
public class ProductReviewAverageScoreDTO {
	private BigDecimal productAverageScore;
	private BigDecimal freshAverageScore;
	private BigDecimal tasteAverageScore;
	private BigDecimal packagingAverageScore;
	private BigDecimal shippingAverageScore;
	private BigInteger sellerReviewCount;
	
	public ProductReviewAverageScoreDTO() {
		
	}
}
