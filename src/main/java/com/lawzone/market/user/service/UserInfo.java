package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class UserInfo extends BaseTimeEntity{
	@Id
	@Column(unique = true, columnDefinition = "varchar(8)")
	private String userId;
	
	@Column(unique = true, columnDefinition = "varchar (100)")
	private String userName;
	
	@Column(unique = true, columnDefinition = "varchar (100)")
	private String email;
	
	private String password;
	
	@Column(unique = true, columnDefinition = "varchar (100)")
	private String socialId;
	
	@Column(columnDefinition = "varchar (11)")
	private String phoneNumber;
	
	@Column(columnDefinition = "char (1)")
	private String sellerYn;
	
	@Column(columnDefinition = "char (1)")
	private String useYn;
}
