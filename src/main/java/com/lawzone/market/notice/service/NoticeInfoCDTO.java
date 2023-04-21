package com.lawzone.market.notice.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class NoticeInfoCDTO {
	private Long noticeNumber;
	private String noticeCfcd;
	private String searchGb;
	private String searchValue;
	private String postYn;
	private String noticeBeginDate;
	private String noticeEndDate;
	private String cdNo;
	private String pageCnt;
	private String maxPage;
	private String maxPageCount;
	private String pageCount;
	private String dateGb;
	
	public NoticeInfoCDTO() {
		
	}
}
