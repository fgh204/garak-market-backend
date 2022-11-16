package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductInfoListDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productStock;
	private String productDesc;
	private String thumbnailImagePath;
	private BigDecimal cumulativeSalesCount;
	private String shopName;
	
	public ProductInfoListDTO() {
		// TODO Auto-generated constructor stub
	}
}
