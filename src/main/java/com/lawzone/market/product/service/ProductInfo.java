package com.lawzone.market.product.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.BaseDateToStringConverter;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class ProductInfo extends BaseTimeEntity{
	@Id
	@Comment("상품ID")
	@Column(columnDefinition = "varchar(12)")
	private String productId;
	
	@Comment("상품명")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String productName; 
	
	@Comment("상품가격")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal productPrice;
	
	@Comment("공급가액")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal supplyPrice;
	
	@Comment("상품수량")
	@NotNull
	@Column(columnDefinition = "DECIMAL (5)")
	private BigDecimal productStock;
	
	@Comment("상품무게")
	@Column(columnDefinition = "DECIMAL(7)")
	private BigDecimal productWeight;
	
	@Comment("상품설명")
	@Column(columnDefinition = "TEXT")
	private String productDesc;
	
	@Comment("상품카테고리코드")
	@Column(columnDefinition = "CHAR(9)")
	private String productCategoryCode;
	
	@Comment("판매시작일")
	@Column(name="begin_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String beginDate;
	
	@Comment("판매종료일")
	@Column(name="end_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String endDate;
	
	@Comment("판매여부")
	@NotNull
	@Column(columnDefinition = "CHAR (1)")
	private String useYn;
	
	@Comment("판매자ID")
	@Column(columnDefinition = "varchar(8)")
	private String sellerId;
	
	@Comment("오늘배송기준시간")
	@Column(columnDefinition = "varchar(4)")
    private String todayDeliveryStandardTime;
	
	@Comment("이벤트ID")
	@Column(columnDefinition = "varchar(30)")
	private String eventId;
}
