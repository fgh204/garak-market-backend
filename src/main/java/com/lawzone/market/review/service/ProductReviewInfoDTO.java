package com.lawzone.market.review.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductReviewInfoDTO {
	private String userName;
	private BigInteger reviewNumber;
	private BigDecimal productScore;
	private BigDecimal freshScore;
	private BigDecimal tasteScore;
	private BigDecimal packagingScore;
	private BigDecimal shippingScore;
	private String productId;
	private String productName;
	private String reviewDate;
	private String reviewTitle;
	private String reviewText;
	private String sellerName;
	
	public ProductReviewInfoDTO() {
		
	}
}
