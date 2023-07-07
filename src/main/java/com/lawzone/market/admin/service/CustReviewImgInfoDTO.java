package com.lawzone.market.admin.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class CustReviewImgInfoDTO {
	private String productId;
	private String orderNo;
	private String thumbnailImagePath;
		
	public CustReviewImgInfoDTO() {
		
	}
}
