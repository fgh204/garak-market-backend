package com.lawzone.market.payment.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class PaymentCancleDTO {
	private String orderNo;
	private String productId;
	private BigDecimal productTotalAmt;
	private BigDecimal orderTotalAmt;
	private String receiptId;	
	private BigDecimal paymentAmount;
	private BigDecimal cancelledPaymentAmount;
	private String message;
	
	public PaymentCancleDTO() {
		
	}
}
