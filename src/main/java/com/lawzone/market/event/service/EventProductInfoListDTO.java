package com.lawzone.market.event.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.lawzone.market.product.service.ProductInfoListDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter

public class EventProductInfoListDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal originalProductPrice;
	private BigDecimal productStock;
	private String productDesc;
	private String thumbnailImagePath;
	private BigDecimal cumulativeSalesCount;
	private String shopName;
	private String todayDeliveryStandardTime;
	private String productCategoryCode;
	private String productCategorySmallName;
	private String slsDateText;
	private Character useYn;
	private String eventId;
	private BigInteger combinedDeliveryStandardAmount;
	private BigInteger deliveryAmount;
	private String combinedDeliveryYn;
	private BigInteger paymentCount;
	private BigInteger productReviewCount;
	private String eventBeginTime;
	private String eventEndTime;
	private BigDecimal discountRate;
	private String eventBeginDate;
	private String eventEndDate;
	private String eventMstSeq;
	private String eventCfcd;
	private String eventTitle;
	private String eventStateCode;
	private BigDecimal eventCount;
	private String eventProceedCode;
	private String popupImagePath;
	private String popupDisplayCfcd;
	private String bannerImagePath;
	private String bannerDetailDisplayYn;
	private String bannerMyPageDisplayYn;
	private String bannerTodayDisplayYn;
	private String bannerPointHistDisplayYn;
	private String bannerCreateReviewDisplayYn;
	private String banner_reviewInfoDisplayYn;
	private String landingPageImagePath;
	private String landingPageUrl;
	private BigDecimal personBuyCount;
	private String benefitCfcd;
	private BigDecimal benefitAmount;
	private String benefitName;
	private String benefitDateCfcd;
	private String benefitDate;
	private String benefitDuplicatedYn;
	private String appPushImagePath;

	
	public EventProductInfoListDTO() {
		
	}
}
