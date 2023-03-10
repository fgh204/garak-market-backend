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
public class ProductReviewScoreCountDTO {
	private Double tasteScore3;
	private Double tasteScore2;
	private Double tasteScore1;
	private Double freshScore3;
	private Double freshScore2;
	private Double freshScore1;
	private Double deliveryScore3;
	private Double deliveryScore2;
	private Double deliveryScore1;
	private Double reorderScore3;
	private Double reorderScore2;
	private Double reorderScore1;
	private Double tasteScore3rate;
	private Double tasteScore2rate;
	private Double tasteScore1rate;
	private Double freshScore3rate;
	private Double freshScore2rate;
	private Double freshScore1rate;
	private Double deliveryScore3rate;
	private Double deliveryScore2rate;
	private Double deliveryScore1rate;
	private Double reorderScore3rate;
	private Double reorderScore2rate;
	private Double reorderScore1rate;
	
	public ProductReviewScoreCountDTO() {
		
	}
}
