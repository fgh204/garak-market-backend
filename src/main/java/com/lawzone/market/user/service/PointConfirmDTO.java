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
	private String dtlCodeEngName;
	//private String updateDatetime;
	private Boolean isConfirmed;
	
	public PointConfirmDTO() {
		
	}
	
	public PointConfirmDTO(String dtlCodeName, String dtlCodeText, String userId
							, String dtlCodeEngName, String isConfirmed) 
	{
		this.dtlCodeName = dtlCodeName;
		this.dtlCodeText = dtlCodeText;
		this.userId = userId;
		this.dtlCodeEngName = dtlCodeEngName;
		this.isConfirmed = ("Y".equals(isConfirmed))?true:false;
	}
}
