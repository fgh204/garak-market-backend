package com.lawzone.market.order.service;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CustOrderListDTO {
	private UserOrderInfoDTO orderInfo;
	
	private ArrayList<CustOrderItemListDTO> orderItemList;
}
