package com.lawzone.market.payment.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class PushIdDTO {
	private String pushId;
	private String productName;
	private String userId;
	
	public PushIdDTO() {
		
	}
}
