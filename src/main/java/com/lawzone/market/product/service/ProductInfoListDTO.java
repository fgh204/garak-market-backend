package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductInfoListDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal supplyPrice;
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
	
	public ProductInfoListDTO() {
		
	}
}
