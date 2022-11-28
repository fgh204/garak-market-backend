package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductInfoDTO {
	private String productId;
	private String beginDate;
	private String endDate;
	private String productName;
	private BigInteger productPrice;
	private BigInteger productStock;
	private String productDesc;
	private Character useYn;
	private String productCategoriesCode;
	private String sellerId;
	private String createDate;
	private String updateDate;
	private String shopName;
	private String sellerName;
	private String thumbnailImagePath;
	
	public ProductInfoDTO() {
		
	}
}
