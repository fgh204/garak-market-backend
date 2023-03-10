package com.lawzone.market.admin.dto.order;

import java.math.BigDecimal;

import com.lawzone.market.order.service.CustOrderItemListDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class AdminCustOrderItemListDTO {
	private String productName;
	private String orderItemStateCode;
	private String orderItemStateName;
	private BigDecimal productCount;
	private BigDecimal productPrice;
	private BigDecimal pointAmount;
	private BigDecimal totalProductPrice;
	private String thumbnailImagePath;
	private String productId;
	private String orderItemDlngStateCode;
	private String orderItemDlngStateName;
	private String shopName;
	
	public AdminCustOrderItemListDTO() {
		
	}
}
