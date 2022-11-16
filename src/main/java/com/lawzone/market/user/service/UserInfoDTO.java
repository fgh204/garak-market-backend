package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
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
