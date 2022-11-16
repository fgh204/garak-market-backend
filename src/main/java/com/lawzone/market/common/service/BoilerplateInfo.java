package com.lawzone.market.common.service;

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

@Data
@Entity
public class BoilerplateInfo extends BaseTimeEntity{
	@Id
	@Comment("상용구번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long boilerplateNumber;
	
	@Comment("유저ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("상용구명")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String boilerplateName;
	
	@Comment("상용구내용")
	@NotNull
	@Column(columnDefinition = "text")
	private String boilerplateText;
	
	@Comment("사용여부")
	@NotNull
	@Column(columnDefinition = "varchar(1)")
	private String useYn;
}
