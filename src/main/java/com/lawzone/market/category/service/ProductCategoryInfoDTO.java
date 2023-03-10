package com.lawzone.market.category.service;

import java.util.Date;

import lombok.Data;

@Data
public class ProductCategoryInfoDTO {
	private String productCategoryLargeCode;
	private String productCategoryLargeName;
	private String productCategoryMediumCode;
	private String productCategoryMediumName;
	private String productCategorySmallCode;
	private String productCategorySmallName;
	private Date createDatetime;
	private Date updateDatetime;
}
