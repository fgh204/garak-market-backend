package com.lawzone.market.admin.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Comment;

import com.lawzone.market.common.CdDtlInfoId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductOrderItemBookIdInfoId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Comment("운송장번호")
	@Column(columnDefinition = "varchar (20)")
	private String bookId;

	@Comment("주문번호")
	@Column(columnDefinition = "varchar (12)")
	private String orderNo;
	
	@Comment("상품코드")
	@Column(columnDefinition = "varchar (12)")
	private String productId;
}
