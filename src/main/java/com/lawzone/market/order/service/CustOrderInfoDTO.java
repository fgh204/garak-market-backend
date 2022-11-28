package com.lawzone.market.order.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class CustOrderInfoDTO {
	private String orderNo;
	private String userName;
	private String zonecode;
	private BigDecimal totalPrice;
	private String address;
	private String detailAddress;
	private String orderName;
	private String recipientName;
	private String orderDate;
	private String phoneNumber;
	private String subPhoneNumber;
	private String deliveryMessage;
	//private String orderDlngStateCode;
	//private String orderDlngStateName;
	
	public CustOrderInfoDTO() {
		
	}
}
