package com.lawzone.market.common.service;

import com.lawzone.market.notice.service.NoticeInfoCDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class DtlCodeDTO {
	private String dtlCode;
	private String dtlCodeName;
	private String dtlCodeText;
	
	public DtlCodeDTO() {
		
	}
}
