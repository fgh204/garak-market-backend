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
public class ProductReviewInfoDTO {
	private String userName;
	private String nickname;
	private BigInteger reviewNumber;
	private BigDecimal productScore;
	private BigDecimal freshScore;
	private BigDecimal tasteScore;
	private BigDecimal packagingScore;
	private BigDecimal deliveryScore;
	private BigDecimal reorderScore;
	private String productId;
	private String productName;
	private String reviewDate;
	private String reviewTitle;
	private String reviewText;
	private String sellerName;
	private String orderNo;
	private String createDatetime;
	private String updateDatetime;
	private String profileImagesPath;
	
	public ProductReviewInfoDTO() {
		
	}
}
