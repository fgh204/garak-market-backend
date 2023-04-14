package com.lawzone.market.user.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class PointConfirmDTO {
	private String dtlCodeName;
	private String dtlCodeText;
	private String userId;
	//private String updateDatetime;
	private String isConfirmed;
	
	public PointConfirmDTO() {
		
	}
}
