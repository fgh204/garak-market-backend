package com.lawzone.market.admin.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.CdDtlInfoId;
import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.product.service.PageInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductOrderItemBookIdInfoDTO {
	private String bookId;
	private String orderNo;
	private String productId;
	private String orderIdFromCorp;
	private String receiverAddress;
	private String receiverAddressBuilding;
	private String receiverAddressCleaned;
	private String receiverAddressRoadCleaned;
	private String receiverAddressRoadDetail;
	private String dongGroup;
	private String placePageUrl;
	private String recipientName;
	private String phoneNumber;
	private String subPhoneNumber;
	private String address;
	private String zonecode;
	private String printYn;
	private String deliveryMessage;
	private BigDecimal productPrice;
	private BigDecimal productCount;
	private String productName;
	private String orderDate;
	private String deliveryStateCode;
	private String sellerId;
	private String productCategoryCode;
	private String deliveryOrderId;
	private String accessMethodText;
	private String combinedDeliveryYn;
	
	public ProductOrderItemBookIdInfoDTO() {
		
	}
}
