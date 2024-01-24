package com.lawzone.market.event.service;

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
public class EventInfoDTO {
	private BigInteger eventMstSeq;
	private String eventId;
	private String eventCfcd;
	private String eventCfnm;
	private String eventTitle;
	private String eventDate;
	private String landingPageUrl;
	private String popupImagePath;
	private String bannerImagePath;
	private String landingPageImagePath;
	private String appPushImagePath;
	private String eventRgstDate;
	private String eventStateCodeName;
	private String eventStateCode;
	private BigInteger dateInterval;
	private String eventBeginDate;
	private String eventEndDate;
	private BigDecimal productPrice;
	private String eventBeginTime;
	private String eventEndTime;
	private BigDecimal eventCount;
	private String eventProceedCode;
	private String popupDisplayCfcd;
	private String bannerDetailDisplayYn;
	private String bannerMyPageDisplayYn;
	private String bannerTodayDisplayYn;
	private String bannerPointHistDisplayYn;
	private String bannerCreateReviewDisplayYn;
	private String bannerReviewInfoDisplayYn;
	private BigDecimal personBuyCount;
	private String benefitCfcd;
	private BigInteger benefitAmount;
	private String benefitName;
	private String benefitDateCfcd;
	private String benefitDate;
	private String benefitDuplicatedYn;
	
	public EventInfoDTO() {
		
	}
}
