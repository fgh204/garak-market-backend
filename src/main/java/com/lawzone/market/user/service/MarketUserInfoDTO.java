package com.lawzone.market.user.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MarketUserInfoDTO {
	private String userName;
	private Character sellerYn;
	private String phoneNumber;
	private String shopName;
	private String email;
	private String nickname;
	private String profileImagesPath;
	private String backgroundImagePath;
	private String introductionText;
	private String socialName;
	private String productCategoryCode;
	private String combinedDeliveryYn;
	private BigInteger deliveryAmount;
	private BigInteger combinedDeliveryStandardAmount;
	private String appVersion;
	
	public MarketUserInfoDTO() {
		
	}
}
