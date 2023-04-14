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
	private String noticeId;
	private String noticeCfcd;
	private String noticeTitle;
	private String noticeDesc;
	private String postYn;
	private String noticeBeginDate;
	private String noticeEndDate;
	
	public NoticeInfoCDTO() {
		
	}
}
