package com.lawzone.market.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CdDtlInfoId implements Serializable{
	@Comment("코드번호")
	@Column(columnDefinition = "varchar (20)")
	private String codeNo;

	@Comment("상세코드")
	@Column(columnDefinition = "varchar (20)")
	private String dtlCode;
}
