package com.lawzone.market.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class TempPaymentInfo extends BaseTimeEntity{
	@Id
	@Comment("tempPaymentNumber")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tempPaymentNumber;
	
	@Comment("사용자ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("paymentText")
	@Column(columnDefinition = "LONGTEXT")
	private String paymentText;
}
