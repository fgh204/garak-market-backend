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
public class UserOrderInfoDTO {
	private String orderNo;
	private String orderDate;
	private String orderStateCode;
	private String orderStateName;
//	private String orderDlngStateCode;
//	private String orderDlngStateName;
	
	public UserOrderInfoDTO() {
		
	}
}
