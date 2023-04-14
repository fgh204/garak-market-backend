package com.lawzone.market.admin.dto.order;

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
public class BookIdListInfoDTO {
	private String orderDate;
	private String orderDttm;
	private String productId;
	private String bookId;
	private String dongGroup;
	private String receiverAddressRoadCleaned;
	private String receiverAddressRoadDetail;
	private String receiverAddress;
	private String productName;
	private String deliveryMessage;
	private String phoneNumber;
	private String recipientName;
	private String orderNo;
	private String orderIdFromCorp;
	private String zonecode;
	private BigDecimal productPrice;
	private String address;
	private String printYn;
	private String chkValue;
	private String updateDatetime;
	private String todayDeliveryStandardTime;
	private String spotName;
	private String businessAddress;
	private String shopName;
	private String resultMessage;
	private String deliverCompleteYn;
	private String sellerId;
	private String productCategoryCode;
	private String deliveryOrderId;
	private String accessMethodText;
	private String combinedDeliveryYn;
	private String imgBase64;
	
	public BookIdListInfoDTO() {
		
	}
}
