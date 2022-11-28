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
	private String orderItemStateCode;
	private String orderItemDlngStateCode;
	private Long cartNumber;
	
	public ProductOrderItemDTO() {
		
	}
}
