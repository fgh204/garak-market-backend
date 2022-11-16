package com.lawzone.market.order.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "ProductOrderItemInfo")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductOrderItemInfo extends BaseTimeEntity{
	@EmbeddedId
	private ProductOrderItemInfoId id;
	
	@Comment("상품수량")
	@NotNull
	@Column(columnDefinition = "DECIMAL (5)")
	private BigDecimal productCount;
	
	@Comment("상품가격")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal productPrice;
	
	@Comment("주문항목상태")
	@Column(columnDefinition = "varchar(3)")
	private String orderItemStateCode;
	
	@Comment("장바구니번호")
	private Long cartNumber;
}
