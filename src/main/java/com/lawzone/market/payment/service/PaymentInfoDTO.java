package com.lawzone.market.payment.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class PaymentInfoDTO {
	private String pgPayCoNm;
	private String cardQuota;
	private BigInteger paymentAmount;
	private BigInteger deliveryAmount;
	private BigInteger cancelledPaymentAmount;
	private String paymentMethod;
	private BigInteger finalPaymentAmount;
	private BigInteger pointAmount;
	private BigInteger cancelledPointAmount;
	private BigInteger finalPointAmount;
	private BigDecimal finalDeliveryAmount;
	private BigDecimal cancelledDeliveryAmount;
	
	public PaymentInfoDTO() {
		
	}
}
