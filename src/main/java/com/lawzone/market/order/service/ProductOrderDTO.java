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
	private BigDecimal pointAmount;
	private BigDecimal paymentAmount;
	private BigDecimal deliveryAmount;
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
	private String deliveryLocationCfcd;
	private String accessMethodCfcd;
	private String accessMethodText;
	private ArrayList<CustOrderItemListDTO> orderItemList;
}
