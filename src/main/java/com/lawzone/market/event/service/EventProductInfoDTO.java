package com.lawzone.market.event.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class EventProductInfoDTO {
	private BigDecimal personBuyCount;
	private BigDecimal eventCount;
	private String eventBeginTime;
	private String eventEndTime;
	private String eventBeginDate;
	private String eventEndDate;
	private BigInteger personPaymentCount;
	private BigInteger eventPaymentCount;
	
	public EventProductInfoDTO() {
		
	}
	
	public EventProductInfoDTO(BigDecimal personBuyCount, BigDecimal eventCount, String eventBeginTime, String eventEndTime
			, String eventBeginDate, String eventEndDate				
			, BigDecimal personPaymentCount, BigDecimal eventPaymentCount) {
		this.personBuyCount = personBuyCount;
		this.eventCount = eventCount;
		this.eventBeginTime = eventBeginTime;
		this.eventEndTime = eventEndTime;
		this.eventBeginDate = eventBeginDate;
		this.eventEndDate = eventEndDate;
		this.personPaymentCount = new BigInteger(personPaymentCount.toString());
		this.eventPaymentCount = new BigInteger(eventPaymentCount.toString());
		}
}
