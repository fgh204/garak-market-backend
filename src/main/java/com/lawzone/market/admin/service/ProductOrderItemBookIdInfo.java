package com.lawzone.market.admin.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.CdDtlInfoId;
import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProductOrderItemBookIdInfo")
public class ProductOrderItemBookIdInfo extends BaseTimeEntity{
	@EmbeddedId
	private ProductOrderItemBookIdInfoId id;
	
	@Comment("운송장번호")
	@Column(columnDefinition = "varchar (20)")
	private String bookId;
	
	@Comment("receiverAddress")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddress;
	
	@Comment("receiverAddressBuilding")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressBuilding;
	
	@Comment("receiverAddressCleaned")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressCleaned;
	
	@Comment("receiverAddressRoadCleaned")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressRoadCleaned;
	
	@Comment("receiverAddressRoadDetail")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressRoadDetail;
	
	@Comment("dongGroup")
	@Column(columnDefinition = "varchar (300)")
	private String dongGroup;
	
	@Comment("placePageUrl")
	@Column(columnDefinition = "varchar (300)")
	private String placePageUrl;
	
	@Comment("수령인명")
	@Column(columnDefinition = "varchar(100)")
	private String recipientName;
	
	@Comment("배송연락처")
	@Column(columnDefinition = "varchar(11)")
	private String phoneNumber;
	
	@Comment("배송서브연락처")
	@Column(columnDefinition = "varchar(11)")
	private String subPhoneNumber;
	
	@Comment("주소")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String address;
	
	@Comment("지역코드")
	@NotNull
	@Column(columnDefinition = "varchar(5)")
	private String zonecode;
	
	@Comment("배송메세지")
	@Column(columnDefinition = "text")
	private String deliveryMessage;
	
	@Comment("출입방법내용")
	@Column(columnDefinition = "text")
	private String accessMethodText;
	
	@Comment("합배송여부")
	@Column(columnDefinition = "varchar(1)")
    private String combinedDeliveryYn;
	
	@Comment("상품가격")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal productPrice;
	
	@Comment("상품수량")
	@NotNull
	@Column(columnDefinition = "DECIMAL (5)")
	private BigDecimal productCount;
	
	@Comment("상품명")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String productName; 
	
	@Comment("주문일자")
	@Column(name="order_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String orderDate;
	
	@Comment("출력여부")
	@Column(columnDefinition = "varchar(1)")
	private String printYn; 
	
	@Comment("SPOTNAME")
	@Column(columnDefinition = "varchar(300)")
	private String spotName;
	
	@Comment("판매자전화번호")
	@Column(columnDefinition = "varchar (11)")
	private String sellerPhoneNumber;
	
	@Comment("사업장주소")
	//@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String businessAddress;
	
	@Comment("상호")
	@Column(columnDefinition = "varchar(300)")
	private String shopName;
	
	@Comment("결과")
	@Column(columnDefinition = "varchar(1000)")
	private String resultMessage; 
	
	@Comment("배송상태코드")
	@Column(columnDefinition = "varchar(3)")
	private String deliveryStateCode;
	
	@Comment("판매자ID")
	@Column(columnDefinition = "varchar(8)")
	private String sellerId;
	
	@Comment("상품카테고리코드")
	@Column(columnDefinition = "CHAR(9)")
	private String productCategoryCode;
	
	@Comment("배송주문ID")
	@Column(columnDefinition = "VARCHAR(100)")
	private String deliveryOrderId;
}
