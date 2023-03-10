package com.lawzone.market.order.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class OrderItemPaymentDTO {
	private String orderNo;
	private BigDecimal orderAmount;
	private String sellerId;
	private BigDecimal deliveryAmount;
	
	public OrderItemPaymentDTO() {
		
	}
}
