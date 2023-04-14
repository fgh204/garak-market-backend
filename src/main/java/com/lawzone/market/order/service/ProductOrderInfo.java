package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.user.SiteUser;
import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductOrderInfo extends BaseTimeEntity{
	@Id
	@Comment("주문번호")
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("주문일자")
	@Column(name="order_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String orderDate;
	
	@Comment("주문수량")
	@NotNull
	@Column(columnDefinition = "DECIMAL(5)")
	private BigDecimal orderCount;
	
	@Comment("총상품가격")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal productTotalPrice;
	
	@Comment("주문금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal paymentAmount;
	
	@Comment("배송금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal deliveryAmount;
	
	@Comment("포인트금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal pointAmount;
	
	@Comment("주문상태")
	@Column(columnDefinition = "varchar(3)")
	private String orderStateCode;
	
//	@Comment("주문처리상태코드")
//	@Column(columnDefinition = "varchar(3)")
//	private String orderDlngStateCode;
	
	@Comment("사용자ID")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("지역코드")
	@NotNull
	@Column(columnDefinition = "varchar(5)")
	private String zonecode;
	
	@Comment("도로명코드")
	//@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String roadnameCode;
	
	@Comment("주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String address;
	
	@Comment("상세주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String detailAddress;
	
	@Comment("건물명")
	@Column(columnDefinition = "varchar(300)")
	private String buildingName;
	
	@Comment("배송메세지")
	@Column(columnDefinition = "text")
	private String deliveryMessage;
	
	@Comment("배송연락처")
	@Column(columnDefinition = "varchar(11)")
	private String phoneNumber;
	
	@Comment("배송서브연락처")
	@Column(columnDefinition = "varchar(11)")
	private String subPhoneNumber;
	
	@Comment("주문명")
	@Column(columnDefinition = "varchar(300)")
	private String orderName;
	
	@Comment("수령인명")
	@Column(columnDefinition = "varchar(100)")
	private String recipientName;
	
	@Comment("배송장소구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String deliveryLocationCfcd;
	
	@Comment("출입방법구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String accessMethodCfcd;
	
	@Comment("출입방법내용")
	@Column(columnDefinition = "text")
	private String accessMethodText;
}
