package com.lawzone.market.order.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductOrderItemInfoId implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//@Comment("주문항목번호")
	//@Column(columnDefinition = "DECIMAL(12,0)")
	//private Integer orderItemNo;
	
	@Comment("주문번호")
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("상품ID")
	@Column(columnDefinition = "varchar(12)")
	private String productId;
}
