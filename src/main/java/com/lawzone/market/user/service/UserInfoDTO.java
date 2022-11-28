package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class UserInfoDTO {
	private String userId;
	private String username;
	private String email;
	private String password;
	private String socialId;
	private String phoneNumber;
	private String sellerYn;
	private String useYn;
}
