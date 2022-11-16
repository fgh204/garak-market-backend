package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class CartInfo extends BaseTimeEntity{
	@Id
	@Comment("장바구니번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartNumber;
	
	@Comment("상품ID")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String productId;
	
	@Comment("사용자ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("수량")
	@NotNull
	@Column(columnDefinition = "decimal(3,0)")
	private BigDecimal productCount;
}
