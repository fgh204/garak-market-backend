package com.lawzone.market.admin.dto.user;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class KakaoTargetPointDTO {
	private BigDecimal pointAmount;
	private String userId;
	private String pointExpirationDate;
	private String stnDate;
	private String phoneNumber;
	private String userName;
	
	public KakaoTargetPointDTO() {
		
	}
}
