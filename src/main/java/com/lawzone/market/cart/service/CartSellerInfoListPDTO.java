package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.lawzone.market.product.service.TagInfoDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CartSellerInfoListPDTO {
	private String sellerId;
	private BigDecimal productPrice;
	private String combinedDeliveryYn;
	private String shopName;
	private BigDecimal deliveryAmount;
	private BigDecimal combinedDeliveryStandardAmount;
	
	private ArrayList<CartInfoListDTO> cartProductList;
	
	public CartSellerInfoListPDTO() {
		
	}
}
