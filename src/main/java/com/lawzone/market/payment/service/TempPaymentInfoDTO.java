package com.lawzone.market.payment.service;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TempPaymentInfoDTO{
	private Long tempPaymentNumber;
	private String userId;
	private String paymentText;
	
	public TempPaymentInfoDTO() {
		
	}
}
