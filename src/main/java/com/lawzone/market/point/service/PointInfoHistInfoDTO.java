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
public class PointInfoHistInfoDTO {
	private BigInteger pointId;
	private String pointName;
	private BigDecimal pointAmount;
	private String pointDate;
	private String expirationYn;
	private String pointStatCode;
	private String pointStatCodeName;
	private String updateDatetime;
	private String createUserName;
	
	public PointInfoHistInfoDTO() {
		
	}
}
