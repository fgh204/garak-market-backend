package com.lawzone.market.order.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductOrderRevokeInfoId implements Serializable{
	private static final long serialVersionUID = 1L;

	@Comment("주문번호")
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("상품ID")
	@Column(columnDefinition = "varchar(12)")
	private String productId;
}
