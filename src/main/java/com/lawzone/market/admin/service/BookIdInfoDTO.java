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
public class BookIdInfoDTO {
	private String orderIdFromCorp;
	private String orderNo;
	private String productId;
	private String bookId;
	private String productName;
	private String recipientName;
	private String address;
	private String orderDate;
	private String todayDeliveryStandardTime;
		
	public BookIdInfoDTO() {
		
	}
}
