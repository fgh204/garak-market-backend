package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DeliveryAddressInfo extends BaseTimeEntity{
	@Id
	@Comment("배송주소번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long deliveryAddressNumber;
	
	@Comment("사용자ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("주소명")
	@NotNull
	@Column(columnDefinition = "varchar(200)")
	private String addressName;
	
	@Comment("수령인명")
	@Column(columnDefinition = "varchar(100)")
	private String recipientName;
	
	@Comment("지역코드")
	@NotNull
	@Column(columnDefinition = "varchar(5)")
	private String zonecode;
	
	@Comment("도로명코드")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String roadnameCode;
	
	@Comment("법정동/리 코드")
	@NotNull
	@Column(columnDefinition = "varchar(20)")
	private String bcode;
	
	@Comment("주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String address;
	
	@Comment("상세주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String detailAddress;
	
	@Comment("건물명")
	@Column(columnDefinition = "varchar(300)")
	private String buildingName;
	
	@Comment("기본배송지여부")
	@NotNull
	@Column(columnDefinition = "char(1)")
	private String baseShippingYn;
	
	@Comment("배송연락처")
	@Column(columnDefinition = "varchar(20)")
	private String phoneNumber;
	
	@Comment("배송서브연락처")
	@Column(columnDefinition = "varchar(20)")
	private String subPhoneNumber;
	
	@Comment("배송장소구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String deliveryLocationCfcd;
	
	@Comment("출입방법구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String accessMethodCfcd;
	
	@Comment("출입방법내용")
	@Column(columnDefinition = "text")
	private String accessMethodText;
	
	@Comment("배송메세지")
	@Column(columnDefinition = "text")
	private String deliveryMessage;
}
