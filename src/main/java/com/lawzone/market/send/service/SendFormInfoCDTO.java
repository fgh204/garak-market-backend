package com.lawzone.market.send.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class SendFormInfoCDTO {
	private String sendFormCode;
	private String recipient;
	private String productName;
	private String orderNo;
	private String totalAmount;
	private String cancelledPaymentAmount;
	private String bookId;
	private String deliveryName;
	private String subPoint;
	private String addPoint;
	private String subDate;
	private String sendDate;
	private String receiveUserId;
	
	public SendFormInfoCDTO() {
		
	}
}
