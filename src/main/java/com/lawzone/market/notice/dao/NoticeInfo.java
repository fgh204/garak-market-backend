package com.lawzone.market.notice.dao;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class NoticeInfo extends BaseTimeEntity{
	@Id
	@Comment("공지사항ID")
	@Column(columnDefinition = "varchar(10)")
	private String noticeId;
	
	@Comment("공지사항구분코드")
	@NotNull
	@Column(columnDefinition = "CHAR(3)")
	private String noticeCfcd;
	
	@Comment("공지사항제목")
	@Column(columnDefinition = "varchar(300)")
	private String noticeTitle;
	
	@Comment("공지사항내용")
	@Column(columnDefinition = "TEXT")
	private String noticeDesc;
	
	@Comment("게시여부")
	@Column(columnDefinition = "CHAR(1)")
	private String postYn;
	
	@Comment("공지시작일")
	@Column(name="begin_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String noticeBeginDate;
	
	@Comment("공지종료일")
	@Column(name="end_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String noticeEndDate;
}
