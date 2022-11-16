package com.lawzone.market.common;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CdDtlInfo")
public class CdDtlInfo extends BaseTimeEntity{
	
	@EmbeddedId
	private CdDtlInfoId cdDtlInfoId;
	
	@Comment("상세코드명")
	@Column(columnDefinition = "varchar (500)")
	private String dtlCodeName;
	
	@Comment("코드설명")
	@Column(columnDefinition = "TEXT")
	private String dtlCodeText;
	
	@Comment("사용여부")
	@Column(columnDefinition = "char (1)")
	private String useYn;
}
