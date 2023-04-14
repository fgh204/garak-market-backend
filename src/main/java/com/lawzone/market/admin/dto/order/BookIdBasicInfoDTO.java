package com.lawzone.market.admin.dto.order;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BookIdBasicInfoDTO {
	private String recipientName;
	private String phoneNumber;
	private String address;
	private String zonecode;
	private String productName;
	private String deliveryMessage;
	private BigDecimal productPrice;
	private String orderNo;
	private BigDecimal productCount;
	private String productId;
	private String orderDate;
	private String sellerId;
	private String productCategoryCode;
	private String accessMethodText;
	private String combinedDeliveryYn;
	
	public BookIdBasicInfoDTO() {
		
	}
}
