package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class SellerInfo extends BaseTimeEntity{
	@Id
	@Comment("판매자ID")
	@Column(unique = true, columnDefinition = "varchar(8)")
	private String sellerId;
	
	@Comment("상호")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String shopName; 
	
	@Comment("사업장주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String businessAddress;

	@Comment("사업자번호")
	@NotNull
	@Column(columnDefinition = "varchar(30)")
	private String businessNumber;

	@Comment("로그인ID")
	@Column(unique = true, columnDefinition = "varchar(100)")
	private String loginId;

	@Comment("비밀번호")
	@Column(columnDefinition = "varchar(300)")
	private String password;

	@Comment("SPOTID")
	@Column(unique = true, columnDefinition = "varchar(300)")
	private String soptId;
}
