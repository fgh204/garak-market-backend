package com.lawzone.market.review.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductReviewInfoCDTO {
	private Long reviewNumber;
	private String productId;
	private String userId;
	private String reviewTitle;
	private String reviewText;
	private BigDecimal productScore;
	private BigDecimal freshScore;
	private BigDecimal tasteScore;
	private BigDecimal packagingScore;
	private BigDecimal shippingScore;
	private String pageCount;
	private String maxPageCount;
	private String orderCode;
	
	public ProductReviewInfoCDTO() {
		
	}
}
