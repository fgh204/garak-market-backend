package com.lawzone.market.event.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class EventInfoCDTO {
	private Long eventMstSeq;
	private String eventCfcd;
	private String searchDateGb;
	private String eventBeginDate;
	private String eventEndDate;
	private String createDate;
	private String eventStateCode;
	private String searchGb;
	private String searchValue;
	private String pageCnt;
	private String maxPage;
	private String maxPageCount;
	private String pageCount;
	private String eventId;
	private String userId;
	
	public EventInfoCDTO() {
		
	}
}
