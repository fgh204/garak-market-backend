package com.lawzone.market.admin.dto.order;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminOrderListDTO {
	private String orderDate;
	private String orderNo;
	private String productId;
	private String productName;
	private String orderCount;
	private String productPrice;
	private String orderPrice;
	private String orderItemStateCode;
	private String orderItemStateName;
	private String orderItemDlngStateCode;
	private String orderItemDlngStateName;
	private String userName;
	private String shopName;
	private String deliveryStateCode;
	private String deliveryStateCodeName;
	
	public AdminOrderListDTO() {
		
	}
}
