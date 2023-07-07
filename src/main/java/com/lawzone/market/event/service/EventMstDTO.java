package com.lawzone.market.event.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class EventMstDTO {
	private BigInteger eventMstSeq;
	private String eventId;
	private String eventCfcd;
	private String eventTitle;
	private String eventBeginDate;
	private String eventEndDate;
	private String eventBeginTime;
	private String eventEndTime;
	private String eventCount;
	private String eventProceedCode;
	private String personBuyCount;
	private String benefitCfcd;
	private String benefitAmount;
	private String benefitName;
	private String benefitDateCfcd;
	private String eventStateCode;
	private String benefitDate;
	private String benefitDuplicatedYn;
	private String landingPageUrl;
	private String popupImagePath;
	private String bannerImagePath;
	private String landingPageImagePath;
	private String appPushImagePath;
	private String popupDisplayCfcd;
	private String bannerDetailDisplayYn;
	private String bannerMyPageDisplayYn;
	private String bannerTodayDisplayYn;
	private String bannerPointHistDisplayYn;
	private String bannerReviewInfoDisplayYn;
	private String bannerCreateReviewDisplayYn;
	
	public EventMstDTO() {
		
	}
}
