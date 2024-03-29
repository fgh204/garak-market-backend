package com.lawzone.market.order.service;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductOrderItemDTO {
	
	private BigDecimal orderItemNo;
	private String orderNo;
	private String productId;
	private BigDecimal productCount;
	private BigDecimal productPrice;
	private BigDecimal totalProductPrice;
	private BigDecimal pointAmount;
	private String orderItemStateCode;
	private String orderItemDlngStateCode;
	private String deliveryStateCode;
	private String sellerId;
	private Long cartNumber;
	
	public ProductOrderItemDTO() {
		
	}
}
