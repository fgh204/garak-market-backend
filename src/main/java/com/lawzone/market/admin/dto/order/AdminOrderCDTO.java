package com.lawzone.market.admin.dto.order;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminOrderCDTO {
	private String orderDateAf;
	private String orderDateBf;
	private String orderStatCode;
	private String searchGb;
	private String searchValue;
	private String sellerId;
	private String pageCnt;
	private String maxPage;
	private String orderNo;
	private String productId;
	private String spotId;
	
	public AdminOrderCDTO() {
		
	}
}
