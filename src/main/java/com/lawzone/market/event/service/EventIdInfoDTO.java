package com.lawzone.market.event.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class EventIdInfoDTO {
	private String eventId;
	private String eventName;
	private Character expirationDateGb;
	private BigInteger expirationDateValue;
	private BigInteger pointAmount;
	
	public EventIdInfoDTO() {
		
	}
}
