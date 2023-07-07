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
public class StoreInfoDTO {
	private String productCategoryCode;
	private String sellerId;
	private String shopName;
	private String todayDeliveryStandardTime;
	private String slsDateText;
	private String isFavoriteSeller;
	
	public StoreInfoDTO() {
		
	}
}
