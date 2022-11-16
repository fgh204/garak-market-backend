package com.lawzone.market.product.service;

import java.math.BigDecimal;

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
public class TagInfo extends BaseTimeEntity{
	@Id
	@Comment("태그ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tagId;
	
	@Comment("태그명")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String tagName;
	
	@Comment("색상ID")
	@NotNull
	@Column(columnDefinition = "Decimal(2,0)")
	private Integer colorId;
	
	@Comment("사용여부")
	@NotNull
	@Column(columnDefinition = "CHAR (1)")
	private String useYn;
}
