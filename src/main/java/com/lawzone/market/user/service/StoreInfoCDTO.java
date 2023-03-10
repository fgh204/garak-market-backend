package com.lawzone.market.user.service;

import java.util.ArrayList;

import com.lawzone.market.product.service.TagInfoDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class StoreInfoCDTO {
	private String maxPageCount;
	private String pageCount;
	private String userId;
	private String productCategoryCode;
	
	public StoreInfoCDTO() {
		
	}
}
