package com.lawzone.market.order.service;

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
public class CustOrderItemListDTO {
	private String productName;
	private String orderItemStateCode;
	private String orderItemStateName;
	private BigDecimal productCount;
	private BigDecimal productPrice;
	private String thumbnailImagePath;
	private String productId;
	private String orderItemDlngStateCode;
	private String orderItemDlngStateName;
	
	public CustOrderItemListDTO() {
		
	}
}
