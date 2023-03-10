package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.lawzone.market.image.service.ProductImageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductOrderUserInfoDTO {
	private String userName;
	
	public ProductOrderUserInfoDTO() {
		
	}
}
