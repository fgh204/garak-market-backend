package com.lawzone.market.admin.dto.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminUserDTO {
	private String sellerId;
	private String userName;
	private String nickname;
	private String userLvl;
	private String email;
	private String phoneNumber;
	private String shopName;
	private String loginId;
	private String password;
	private String passwordValue;
	private String spotId;
	private Character sellerYn;
	private Character useYn;
	
	public AdminUserDTO() {
		
	}
}
