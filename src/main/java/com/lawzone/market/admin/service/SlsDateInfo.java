package com.lawzone.market.admin.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Entity
@Data
public class SlsDateInfo {
	@Id
	@Comment("기준일자")
	@Column(columnDefinition = "date")
	private Date slsDate;
	
	@Comment("영업일여부")
	@NotNull
	@Column(columnDefinition = "char(1)")
	private String slsDayYn;
	
	@Comment("임시공휴일여부")
	@NotNull
	@Column(columnDefinition = "char(1)")
	private String fstdyYn;
	
	@Comment("요일코드")
	@NotNull
	@Column(columnDefinition = "varchar(2)")
	private BigDecimal wdcd;
	
	@Comment("주수년월")
	@NotNull
	@Column(columnDefinition = "varchar(6)")
	private String weekDgreYymm;
	
	@Comment("주수")
	@NotNull
	@Column(columnDefinition = "decimal(5,0)")
	private BigDecimal calndWeekDgre;
}
