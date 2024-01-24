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
public class SettlementListDTO {
	private String shopName;
	private BigDecimal productAmt;
	private BigDecimal deliveryCharge;
	private BigDecimal salesAmount;
	private BigDecimal netProfit;
	private BigDecimal fee;
	private String saleBeginDate;
	private String saleEndDate;
	private BigDecimal productCount;
	private BigDecimal supplyPrice;
	private BigDecimal subSupplyPrice;
	private BigDecimal productAmt2;
		
	public SettlementListDTO() {
		
	}
}
