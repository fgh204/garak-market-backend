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
	private String userId;
	private String eventCode;
	private String eventId;
	private BigDecimal pointValue;
	private String pointCode;
	private String pointStateCode;
	private String pointRegistDate;
	private String pointExpirationDatetime;
	private Long pointSaveId;
	private Long pointOriginalId;
	private String expirationDateGb;
	private BigDecimal expirationDateValue;
	private String monthValue;
	private String maxPageCount;
	private String pageCount;
	
	public PointInfoCDTO() {
		
	}
}
