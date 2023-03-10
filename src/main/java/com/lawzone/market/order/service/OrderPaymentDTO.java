package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class OrderPaymentDTO {
	private String orderDate;
	private String orderNo;
	private String sellerId;
	private BigDecimal orderAmount;
	private BigDecimal cancelledOrderAmount;
	private BigDecimal deliveryAmount;
	
	public OrderPaymentDTO() {
		
	}
}

