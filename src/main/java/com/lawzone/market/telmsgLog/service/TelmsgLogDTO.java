package com.lawzone.market.telmsgLog.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TelmsgLogDTO {
	private String ingrsPathCd;
	private String dlngTpcd;
	private String telmsgDtaInfo;
	
	public TelmsgLogDTO() {
		
	}
}
