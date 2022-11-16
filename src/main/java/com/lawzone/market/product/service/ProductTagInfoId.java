package com.lawzone.market.product.service;

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
public class ProductTagInfoId implements Serializable{
	private static final long serialVersionUID = 1L;

	@Comment("상품ID")
	@Column(columnDefinition = "varchar(12)")
	private String productId;

	@Comment("태그ID")
	private Long tagId;
}
