package com.lawzone.market.review.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Entity
@Data
public class ProductReviewInfo extends BaseTimeEntity{
	@Id
	@Comment("리뷰번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewNumber;
	
	@Comment("상품ID")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String productId;
	
	@Comment("사용자ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("리뷰제목")
	@Column(columnDefinition = "varchar(300)")
	private String reviewTitle;
	
	@Comment("리뷰내용")
	@Column(columnDefinition = "TEXT")
	private String reviewText;
	
	@Comment("상품점수")
	@Column(columnDefinition = "DECIMAL(2,1)")
	private String productScore;
	
	@Comment("신선도점수")
	@NotNull
	@Column(columnDefinition = "DECIMAL(1,0)")
	private String freshScore;
	
	@Comment("재구매점수")
	@NotNull
	@Column(columnDefinition = "DECIMAL(1,0)")
	private String reorderScore;
	
	@Comment("맛점수")
	@NotNull
	@Column(columnDefinition = "DECIMAL(1,0)")
	private String tasteScore;
	
	@Comment("포장점수")
	@Column(columnDefinition = "DECIMAL(1,0)")
	private String packagingScore;
	
	@Comment("배송점수")
	@NotNull
	@Column(columnDefinition = "DECIMAL(1,0)")
	//private String shippingScore;
	private String deliveryScore;
	
	@Comment("주문번호")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	//private String shippingScore;
	private String orderNo;
}
