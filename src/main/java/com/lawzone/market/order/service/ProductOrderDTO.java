package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductOrderDTO {
	private String orderNo;
	private String orderDate;
	private BigDecimal orderCount;
	private BigDecimal productTotalPrice;
	private String orderStateCode;
	private String userId;
	private String zonecode;
	private String roadnameCode;
	private String address;
	private String detailAddress;
	private String buildingName;
	private String deliveryMessage;
	private String phoneNumber;
	private String subPhoneNumber;
	private String orderName;
	private String recipientName;
	private ArrayList<CustOrderItemListDTO> orderItemList;
}
