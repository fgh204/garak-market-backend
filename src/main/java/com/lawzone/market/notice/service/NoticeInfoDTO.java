package com.lawzone.market.notice.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class NoticeInfoDTO {
	private BigInteger noticeNumber;
	private String noticeCfcd;
	private String noticeCfnm;
	private String noticeTitle;
	private String noticeDesc;
	private String noticeImagesPath;
	private String noticeBeginDate;
	private String noticeEndDate;
	private Character postYn;
	
	public NoticeInfoDTO() {
		
	}
}
