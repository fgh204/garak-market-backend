package com.lawzone.market.payment.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class PaymentDTO {
	private String receiptId;
	private String orderNo;
	private String orderName;
	private String orderDate;
	private BigDecimal paymentAmount;
	private BigDecimal cancelledPaymentAmount;
	private String pgName;
	private String paymentGb;
	private String paymentName;
	private String paymentDttm;
	private String cancelledPaymentDttm;
	private String PaymentReqDttm;
	private String receiptUrl;
	private String pgId;
	private String approveNo;
	private String custPaymentNo;
	private String pgPayCoCd;
	private String pgPayCoNm;
	private String pgCustNm;
	private String cardQuota;
	private BigDecimal pointAmount;
	private BigDecimal cancelledPointAmount;
	
	public PaymentDTO() {
		
	}
}
