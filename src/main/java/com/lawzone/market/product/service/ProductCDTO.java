package com.lawzone.market.product.service;

import java.math.BigDecimal;

import com.lawzone.market.review.service.ProductReviewInfoCDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter

public class ProductCDTO {
	private String productId;
	private String productName;
	private String productCategoryCode;
	private String pageCount;
	private String userId;
	private String maxPageCount;
	private String sellerSearchYn;
	private String sellerId;
	private String sellerIdYn;
	private String favoriteYn;
	private String useYn;
	private String eventId;
	private Boolean isSoldOutHidden;
	private String productSortCode;
	private String productBundleCfcd;
	
	public ProductCDTO() {
		
	}
}
