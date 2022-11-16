package com.lawzone.market.order.service;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class CustOrderInfoListDTO {
	private String orderNo;
	private String username;
	private String zonecode;
	private String totalPrice;
	private String address;
	private String detailAddress;
	private ArrayList<CustOrderItemListDTO> orderItemList;
	
	public CustOrderInfoListDTO() {
		
	}	
}
