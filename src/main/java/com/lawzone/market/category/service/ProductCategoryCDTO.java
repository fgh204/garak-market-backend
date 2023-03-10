package com.lawzone.market.category.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductCategoryCDTO {
	private String productCategoryLargeCode;
	private String productCategoryMediumCode;
	private String productCategorySmallCode;
	
	public ProductCategoryCDTO() {
		
	}
	
}
