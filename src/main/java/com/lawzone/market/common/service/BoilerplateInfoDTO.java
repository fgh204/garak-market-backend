package com.lawzone.market.common.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class BoilerplateInfoDTO {
	private BigDecimal boilerplateNumber;
	private String userId;
	private String boilerplateName;
	private String boilerplateText;
	private String useYn;
	
	public BoilerplateInfoDTO() {
		
	}
}
