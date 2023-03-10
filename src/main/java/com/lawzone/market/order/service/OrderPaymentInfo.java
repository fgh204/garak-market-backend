package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.user.SiteUser;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
public class OrderPaymentInfo extends BaseTimeEntity{
	@Id
	@Comment("주문금액번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderPaymentNumber;
	
	@Comment("주문일자")
	@Column(name="order_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String orderDate;
	
	@Comment("주문번호")
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("판매자ID")
	@Column(columnDefinition = "varchar(8)")
	private String sellerId;
	
	@Comment("주문금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal orderAmount;
	
	@Comment("주문취소금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal cancelledOrderAmount;
	
	@Comment("배송금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal deliveryAmount;
}
