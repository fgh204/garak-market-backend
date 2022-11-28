package com.lawzone.market.common.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class MenuInfo extends BaseTimeEntity{
	@Id
	@Comment("메뉴일련번호")
	private Long menuSeq;
	
	@Comment("메뉴REF")
	private Long menuRef;
	
	@Comment("메뉴명")
	@Column(columnDefinition = "varchar (200)")
	private String menuNm;
	
	@Comment("메뉴설명")
	@Column(columnDefinition = "varchar (200)")
	private String menuDesc;
	
	@Comment("폴더여부")
	@Column(columnDefinition = "varchar (1)")
	private String folderYn;
	
	@Comment("자원일련번호")
	private Long rsrcSeq;
	
	@Comment("자원경로값")
	@Column(columnDefinition = "varchar (100)")
	private String rsrcPathValue;
	
	@Comment("표시여부")
	@Column(columnDefinition = "varchar (1)")
	private String dispYn;
	
	@Comment("사용여부")
	@Column(columnDefinition = "varchar (1)")
	private String useYn;
	
	@Comment("상위매뉴일련번호")
	@Column(columnDefinition = "varchar (500)")
	private Long upMenuSeq;
	
	@Comment("메뉴레벨")
	private Integer menuLvl;
	
	@Comment("메뉴레벨")
	private Integer rsrcLvl;
	
	@Comment("정렬")
	private Long orderSeq;
}
