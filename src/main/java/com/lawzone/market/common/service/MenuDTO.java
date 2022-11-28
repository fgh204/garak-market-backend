package com.lawzone.market.common.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MenuDTO {
	private BigInteger menuSeq;
	private String menuNm;
	private String rsrcPathValue;
	private String fullTitle;
	private String depthPath;
	private String orderSeq;
	private Integer menuLvl;
	private String folderYn;
	private BigInteger menuRef;
	private BigInteger menuCnt;
	
	public MenuDTO() {
		
	}
}
