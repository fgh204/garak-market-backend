package com.lawzone.market.categories.service;

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
public class ProductCategoriesInfo extends BaseTimeEntity{
	@Id
	@Comment("상품카테고리번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productCategoriesNumber;
	
	@Comment("상품카테고리대분류코드")
	@NotNull
	@Column(columnDefinition = "varchar(3)")
	private String productCategoryLargeCode;
	
	@Comment("상품카테고리대분류명")
	@NotNull
	@Column(columnDefinition = "varchar(100)")
	private String productCategoryLargeName;
	
	@Comment("상품카테고리중분류코드")
	@NotNull
	@Column(columnDefinition = "varchar(3)")
	private String productCategoryMediumCode;
	
	@Comment("상품카테고리중분류명")
	@NotNull
	@Column(columnDefinition = "varchar(100)")
	private String productCategoryMediumName;
	
	@Comment("상품카테고리소분류코드")
	@NotNull
	@Column(columnDefinition = "varchar(9)")
	private String productCategorySmallCode;
	
	@Comment("상품카테고리소분류명")
	@NotNull
	@Column(columnDefinition = "varchar(100)")
	private String productCategorySmallName;
}
