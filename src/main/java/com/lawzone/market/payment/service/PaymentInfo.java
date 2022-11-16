package com.lawzone.market.payment.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentInfo extends BaseTimeEntity{
	@Id
	@Comment("부트페이 영수증ID")
	@Column(columnDefinition = "varchar(50)")
	private String receiptId;
	
	@Comment("주문번호")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("주문명")
	@Column(columnDefinition = "varchar(300)")
	private String orderName;
	
	@Comment("주문일자")
	@NotNull
	@Column(name="order_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String orderDate;
	
	@Comment("결제금액")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal paymentAmount;
	
	@Comment("결제취소금액")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal cancelledPaymentAmount;
	
	@Comment("결제PG사명")
	@NotNull
	@Column(columnDefinition = "varchar(100)")
	private String pgName;
	
	@Comment("결제수단")
	@NotNull
	@Column(columnDefinition = "varchar(50)")
	private String paymentGb;
	
	@Comment("결제완료일시")
	@NotNull
	@Column(name="payment_dttm", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String paymentDttm;
	
	@Comment("결제취소일시")
	@Column(name="cancelled_payment_dttm", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String cancelledPaymentDttm;
	
	@Comment("결제요청일시")
	@Column(name="payment_req_dttm", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String paymentReqDttm;
	
	@Comment("결제영수증")
	@Column(columnDefinition = "varchar(300)")
	private String receiptUrl;
	
	@Comment("PG사고유ID")
	@Column(columnDefinition = "varchar(100)")
	private String pgId;
	
	@Comment("PG사고유승인번호")
	@Column(columnDefinition = "varchar(100)")
	private String approveNo;
	
	@Comment("고객결제인이용번호")
	@Column(columnDefinition = "varchar(100)")
	private String custPaymentNo;
	
	@Comment("PG사정의결제사코드")
	@Column(columnDefinition = "varchar(30)")
	private String pgPayCoCd;
	
	@Comment("PG사정의결제사명")
	@Column(columnDefinition = "varchar(100)")
	private String pgPayCoNm;
	
	@Comment("PG사정의결제고객명")
	@Column(columnDefinition = "varchar(100)")
	private String pgCustNm;
	
	@Comment("PG사정의할부개월수")
	@Column(columnDefinition = "varchar(100)")
	private String cardQuota;
}
