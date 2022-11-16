package com.lawzone.market.common;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class CdInfo extends BaseTimeEntity{
	@Id
	@Comment("코드번호")
	@Column(columnDefinition = "varchar (20)")
	private String codeNo;
	
	@Comment("코드명")
	@Column(columnDefinition = "varchar (500)")
	private String codeName;
	
	@Comment("사용여부")
	@Column(columnDefinition = "char (1)")
	private String useYn;
}
