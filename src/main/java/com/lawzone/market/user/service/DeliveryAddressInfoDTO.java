package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class DeliveryAddressInfoDTO {
	private Long deliveryAddressNumber;
	private String userId;
	private String addressName;
	private String recipientName;
	private String zonecode;
	private String roadnameCode;
	private String address;
	private String detailAddress;
	private String buildingName;
	private String baseShippingYn;
	private String phoneNumber;
	private String subPhoneNumber;
	private String bcode;
	private String deliveryLocationCfcd;
	private String accessMethodCfcd;
	private String accessMethodText;
	private String deliveryMessage;
	
	public DeliveryAddressInfoDTO() {
		
	}
}
