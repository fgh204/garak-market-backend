package com.lawzone.market.admin.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BookIInfoCDTO {
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
	
	public BookIInfoCDTO() {
		
	}
}
