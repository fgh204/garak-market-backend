package com.lawzone.market.point.service;

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
public class PointChkDTO {
	private String pointYn;
	private BigDecimal pointAmount;
	
	public PointChkDTO() {
		
	}
}
