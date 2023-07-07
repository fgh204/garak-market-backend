package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.persistence.Convert;

import com.lawzone.market.product.service.TagInfoDTO;
import com.lawzone.market.util.BooleanToYNConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class CustOrderItemListPDTO {
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
	private Boolean isReviewRegistered;
	private Boolean isReviewWritable;
	private String shopName;
	private String sellerId;
	private String todayDeliveryStandardTime;
	private String orderCompletionDate;
	private String estimatedDeliveryDate;
	private String orderCompletionTime;
	private String orderCancellationDate;
	private String deliveryCompletionDate;
	private String slsDateText;
	private String orderNo;
	
	private ArrayList<TagInfoDTO> productTagList;
	
	public CustOrderItemListPDTO() {
		
	}
}
