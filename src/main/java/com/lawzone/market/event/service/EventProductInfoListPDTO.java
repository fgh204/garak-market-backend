package com.lawzone.market.event.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.lawzone.market.product.service.TagInfoDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter

public class EventProductInfoListPDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productStock;
	private String productDesc;
	private String thumbnailImagePath;
	private BigDecimal cumulativeSalesCount;
	private String shopName;
	private String todayDeliveryStandardTime;
	private String todayDeliveryYn;
	private String slsDate;
	private Character useYn;
	private String eventBeginTime;
	private String eventEndTime;
	private BigDecimal personBuyCount;
	private BigDecimal eventCount;
	private String eventId;
	private String eventBeginDate;
	private String eventEndDate;
	private BigInteger combinedDeliveryStandardAmount;
	private BigInteger deliveryAmount;
	private String combinedDeliveryYn;
	private BigDecimal discountRate;
	private BigDecimal originalProductPrice;
	
	private ArrayList<TagInfoDTO> productTagList;
	
	public EventProductInfoListPDTO() {
		// TODO Auto-generated constructor stub
	}
}
