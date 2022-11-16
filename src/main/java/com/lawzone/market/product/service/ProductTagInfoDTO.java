package com.lawzone.market.product.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductTagInfoDTO {
	private String productId;
	private BigInteger tagId;
	
	public ProductTagInfoDTO() {
		
	}
}
