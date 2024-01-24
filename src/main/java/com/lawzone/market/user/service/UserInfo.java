package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class UserInfo extends BaseTimeEntity{
	@Id
	@Column(unique = true, columnDefinition = "varchar(8)")
	private String userId;
	
	@Column(columnDefinition = "varchar (100)")
	private String userName;
	
	@Column(columnDefinition = "varchar (100)")
	private String email;
	
	//private String password;
	
	//@Column(unique = true, columnDefinition = "varchar (100)")
	@Column(columnDefinition = "varchar (100)")
	private String socialId;
	
	@Column(columnDefinition = "varchar (20)")
	private String phoneNumber;
	
	@Column(columnDefinition = "char (1)")
	private String sellerYn;
	
	@Column(columnDefinition = "char (1)")
	private String useYn;
	
	@Comment("유저레벨")
	private Integer userLvl;
	
	//@Column(unique = true, columnDefinition = "varchar (100)")
	@Column(columnDefinition = "varchar (100)")
	private String nickname;
	
	@Comment("프로필 이미지 경로")
	@Column(columnDefinition = "varchar (3000)")
	private String profileImagesPath;
	
	@Comment("배경 이미지 경로")
	@Column(columnDefinition = "varchar (3000)")
	private String backgroundImagePath;
	
	@Comment("비밀번호")
	@Column(columnDefinition = "varchar(300)")
	private String password;
	
	@Comment("로그인ID")
	@Column(columnDefinition = "varchar(100)")
	private String loginId;
	
	@Comment("accessToken")
	@Column(columnDefinition = "varchar(300)")
	private String accessToken;
	
	@Comment("token")
	@Column(columnDefinition = "varchar(500)")
	private String token;
	
	@Comment("pushId")
	@Column(columnDefinition = "varchar(100)")
	private String pushId;
	
	@Column(columnDefinition = "varchar (10)")
	private String socialName;
	
	@Comment("탈퇴사유코드")
	@Column(columnDefinition = "varchar(3)")
	private String withdrawalReasonCode;
	
	@Comment("탈퇴사유내용")
	@Column(columnDefinition = "text")
	private String withdrawalReasonText;
	
	@Comment("탈퇴일시")
	@Column(name="withdrawal_date_time", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String withdrawalDateTime;
	
	@Comment("법인여부")
	@Column(columnDefinition = "varchar(1)")
	private String corpYn;
	
	@Comment("앱버전")
	@Column(columnDefinition = "varchar(30)")
	private String appVersion;
	
	@Comment("푸쉬동의여부")
	@Column(columnDefinition = "varchar(1)")
	private String pushAgreeYn;
}
