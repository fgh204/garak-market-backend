package com.lawzone.market.image.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.product.service.ProductInfo;

import lombok.Data;

@Data
@Entity
//@Where(clause = "delegate_thumb_nail_yn = 'Y'")
public class ProductImageInfo extends BaseTimeEntity{
	@Id
	@Comment("이미지파일번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long imageFileNumber;
	
	@Comment("상품ID")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String productId;
	
	@Comment("주문번호")
	@NotNull
	@Column(columnDefinition = "varchar(12)")
	private String orderNo;
	
	@Comment("파일명")
	@NotNull
	@Column(columnDefinition = "varchar (200)")
	private String fileName;
	
	@Comment("원본파일명")
	@NotNull
	@Column(columnDefinition = "varchar (200)")
	private String originFileName;
	
	@Comment("썸네일이미지")
	@NotNull
	@Column(columnDefinition = "varchar (3000)")
	private String thumbnailImagePath;
	
	@Comment("이미지구분코드")
	@NotNull
	@Column(columnDefinition = "char(2)")
	private String imageCfcd;
	
	@Comment("대표썸네일이미지여부")
	@NotNull
	@Column(columnDefinition = "char (1)")
	private String delegateThumbnailYn;
	
	@Comment("파일크기")
	@NotNull
	private Long fileSize;
	
	//@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "product_id")
    //private ProductInfo productId;
}
