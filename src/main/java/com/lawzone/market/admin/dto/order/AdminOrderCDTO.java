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
	private String orderDateGb;
	private String deliveryComplYn;
	private String orderStatCode;
	private String orderDlngStatCode;
	private String searchGb;
	private String searchValue;
	private String sellerId;
	private String pageCnt;
	private String maxPage;
	private String orderNo;
	private String productId;
	private String spotId;
	private String boilerplateName;
	private String useYn;
	private Integer userLvl;
	private String year;
	private String month;
	private String slsDayYn;
	private String spotName;
	private String businessAddress;
	private String sellerPhoneNumber;
	private String shopName;
	private String deliverYn;
	private String deliveryStateCode;
	
	public AdminOrderCDTO() {
		
	}
}
