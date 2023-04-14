package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MarketSignupDTO {
	private String userName;
	private Character sellerYn;
	private String phoneNumber;
	private String shopName;
	private String email;
	private String nickname;
	private String userId;
	private String password;
	private String loginId;
	private String previousUrl;
	private String withdrawalReasonCode;
	private String withdrawalReasonText;
	
	public MarketSignupDTO() {
		
	}
}
