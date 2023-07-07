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
public class StoreInfoPDTO {
	private String productCategoryCode;
	private String shopName;
	private String sellerId;
	private Boolean isFavoriteSeller;
	private String todayDeliveryStandardTime;
	private String todayDeliveryYn;
	private String slsDate;
	
	private ArrayList<StorethumbnailImagePathInfoDTO> productList;
	//private ArrayList<StorethumbnailImagePathInfoDTO> storeThumbnailImagePathList;
	
	public StoreInfoPDTO() {
		
	}
}
