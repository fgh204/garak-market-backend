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
public class KakaoTargetReviewDTO {
	private String orderDate;
	private String userName;
	private String phoneNumber;
	private String reviewYn;
	private String stnDate;
	
	public KakaoTargetReviewDTO() {
		
	}
}
