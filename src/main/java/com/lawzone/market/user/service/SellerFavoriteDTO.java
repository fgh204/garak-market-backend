package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class SellerFavoriteDTO {
	private String userId;
	private String sellerId;
	private String shopName;
	private String createDatetime;
	private String updateDatetime;
	
	public SellerFavoriteDTO() {
		
	}
}
