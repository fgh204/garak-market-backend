package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TagInfoDTO {
	private BigInteger tagId;
	private String tagName;
	private Character useYn;
	private BigDecimal colorId;
	
	public TagInfoDTO() {
		
	}
}
