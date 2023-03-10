package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class SellerFavoriteInfo extends BaseTimeEntity{
	@Id
	@Comment("판매자즐겨찾기번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long favoriteNo;
	
	@Comment("사용자ID")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("판매자ID")
	@Column(columnDefinition = "varchar(8)")
	private String sellerId;
}
