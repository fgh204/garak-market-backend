package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductInfoListPDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productStock;
	private String productDesc;
	private String thumbnailImagePath;
	private BigDecimal cumulativeSalesCount;
	private String shopName;
	private String todayDeliveryStandardTime;
	private String todayDeliveryYn;
	private String slsDate;
	private Character useYn;
	
	private ArrayList<TagInfoDTO> productTagList;
	
	public ProductInfoListPDTO() {
		// TODO Auto-generated constructor stub
	}
}
