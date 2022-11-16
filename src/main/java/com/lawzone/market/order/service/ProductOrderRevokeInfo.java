package com.lawzone.market.order.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductOrderRevokeInfo extends BaseTimeEntity{
	@EmbeddedId
	private ProductOrderRevokeInfoId productOrderRevokeInfoId;
	
	@Comment("사용자ID")
	@NotNull
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("취소구분코드")
	@NotNull
	@Column(columnDefinition = "varchar(3)")
	private String revokeCd;
	
	@Comment("취소사유코드")
	@NotNull
	@Column(columnDefinition = "varchar(3)")
	private String revokeReasonCd;
	
	@Comment("취소내용")
	@Column(columnDefinition = "varchar(3)")
	private String revokeText;
	
	@Comment("취소일자")
	@NotNull
	@Column(name="revoke_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String revokeDate;
	
	@Comment("취소금액")
	@NotNull
	@Column(columnDefinition = "DECIMAL (15,2)")
	private String revokeAmount;
}
