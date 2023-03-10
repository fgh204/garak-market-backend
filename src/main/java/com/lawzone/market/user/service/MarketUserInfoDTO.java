package com.lawzone.market.user.service;

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
	
	public MarketUserInfoDTO() {
		
	}
}
