package com.lawzone.market.categories.service;

import java.util.Date;

import lombok.Data;

@Data
public class ProductCategoriesInfoDTO {
	private String productCategoryLargeCode;
	private String productCategoryLargeName;
	private String productCategoryMediumCode;
	private String productCategoryMediumName;
	private String productCategorySmallCode;
	private String productCategorySmallName;
	private Date createDate;
	private Date updateDate;
}
