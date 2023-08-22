package com.lawzone.market.event.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class EventInfo extends BaseTimeEntity{
	@Id
	@Comment("이벤트일련번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventSeq;
	
	@Comment("이벤트ID")
	@Column(columnDefinition = "varchar(30)")
	private String eventId;
	
	@Comment("이벤트명")
	@Column(columnDefinition = "varchar(300)")
	private String eventName;
	
	@Comment("중복제한여부")
	@Column(columnDefinition = "char(1)")
	private String duplicateLimitYn;
	
	@Comment("중복제한건수")
	@Column(columnDefinition = "DECIMAL(3)")
	private String duplicateLimitCount;
	
	@Comment("포인트 금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
	private BigDecimal pointAmount;
	
	@Comment("만료일자구분")
	@Column(columnDefinition = "char(1)")
    private String expirationDateGb;
	
	@Comment("포인트금액수정여부")
	@Column(columnDefinition = "char(1)")
    private String pointAmountModificationYn;
	
	@Comment("만료일자구분값")
	@Column(columnDefinition = "DECIMAL(3)")
    private BigDecimal expirationDateValue;
	
	@Comment("사용여부")
	@Column(columnDefinition = "char(1)")
    private String useYn;
}
