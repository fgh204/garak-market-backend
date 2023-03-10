package com.lawzone.market.category.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductCategoryDTO {
	private String categoryName;
	private String categoryCode;

	public ProductCategoryDTO() {
		
	}
}
