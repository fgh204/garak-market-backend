package com.lawzone.market.admin.dto.order;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminOrderItemListDTO {
	private String orderNo;
	private String orderName;
	private String orderDate;
	private String statName;
	private String orderCount;
	private String productPrice;
	private String userName;
	
	public AdminOrderItemListDTO() {
		
	}
}
