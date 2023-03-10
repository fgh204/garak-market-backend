package com.lawzone.market.point.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class PointInfoCDTO {
	private String socialId;
	private String eventCode;
	private String eventId;
	private String userId;
	private BigDecimal pointValue;
	private String pointCode;
	private String pointStateCode;
	private String pointRegistDate;
	private String pointExpirationDatetime;
	private Long pointSaveId;
	private Long pointOriginalId;
	
	public PointInfoCDTO() {
		
	}
}
