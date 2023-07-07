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
	
	public EventProductInfoListDTO() {
		
	}
}
