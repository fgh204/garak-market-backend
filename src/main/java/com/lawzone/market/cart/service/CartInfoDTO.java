package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.util.Date;

import com.lawzone.market.payment.service.PaymentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CartInfoDTO {
	private Long cartNumber;
	private String productId;
	private String userId;
	private BigDecimal productCount;
	
	public CartInfoDTO() {
		
	}
}
