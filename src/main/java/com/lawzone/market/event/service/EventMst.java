package com.lawzone.market.event.service;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Data;

@Data
@Entity
public class EventMst extends BaseTimeEntity{
	@Id
	@Comment("이벤트원부일련번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventMstSeq;
	
	@Comment("이벤트ID")
	@Column(columnDefinition = "varchar(30)")
	private String eventId;
	
	@Comment("이벤트구분코드")
	@Column(columnDefinition = "varchar(3)")
	private String eventCfcd;
	
	@Comment("이벤트제목")
	@Column(columnDefinition = "varchar(30)")
	private String eventTitle;
	
	@Comment("이벤트시작일자")
	@Column(name="event_begin_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String eventBeginDate;
	
	@Comment("이벤트종료일자")
	@Column(name="event_end_date", length=20)
	@Convert(converter=DateToStringConverter.class)
	private String eventEndDate;
	
	@Comment("이벤트시작시간")
	@Column(columnDefinition = "varchar(4)")
    private String eventBeginTime;
	
	@Comment("이벤트종료시간")
	@Column(columnDefinition = "varchar(4)")
    private String eventEndTime;
	
	@Comment("이벤트진행갯수")
	//@NotNull
	@Column(columnDefinition = "DECIMAL (5)")
	private BigDecimal eventCount;
	
	@Comment("이벤트진행방식코드")
	@Column(columnDefinition = "varchar(3)")
	private String eventProceedCode;
	
	@Comment("인당구매갯수")
	@Column(columnDefinition = "DECIMAL (5)")
	private BigDecimal personBuyCount;
	
	@Comment("혜택지급구분코드")
	@Column(columnDefinition = "varchar(3)")
    private String benefitCfcd;
	
	@Comment("혜택지급금액")
	@Column(columnDefinition = "DECIMAL (15,2)")
    private String benefitAmount;
	
	@Comment("혜택지급금액")
	@Column(columnDefinition = "varchar(300)")
    private String benefitName;
	
	@Comment("혜택지급기간구분코드")
	@Column(columnDefinition = "varchar(3)")
    private String benefitDateCfcd;
	
	@Comment("이벤트상태코드")
	@Column(columnDefinition = "varchar(3)")
    private String eventStateCode;
	
	@Comment("혜택지급기간일자")
	@Column(columnDefinition = "varchar(8)")
    private String benefitDate;
	
	@Comment("혜택중복사용여부")
	@Column(columnDefinition = "varchar(1)")
    private String benefitDuplicatedYn;
	
	@Comment("랜딩페이지URL")
	@Column(columnDefinition = "varchar(300)")
    private String landingPageUrl;
	
	@Comment("팝업이미지PATH")
	@Column(columnDefinition = "varchar(300)")
    private String landingPageImagePath;
	
	@Comment("팝업이미지PATH")
	@Column(columnDefinition = "varchar(300)")
    private String popupImagePath;
	
	@Comment("배너이미지PATH")
	@Column(columnDefinition = "varchar(300)")
    private String bannerImagePath;
	
	@Comment("팝업노출구분코드")
	@Column(columnDefinition = "varchar(3)")
    private String popupDisplayCfcd;
	
	@Comment("배너상세표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerDetailDisplayYn;
	
	@Comment("배너마이페이지표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerMyPageDisplayYn;
	
	@Comment("배너오늘상품표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerTodayDisplayYn;
	
	@Comment("배너포인트내역표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerPointHistDisplayYn;
	
	@Comment("배너고객후기더보기표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerReviewInfoDisplayYn;
	
	@Comment("배너후기작성하기표시여부")
	@Column(columnDefinition = "varchar(1)")
    private String bannerCreateReviewDisplayYn;
	
	@Comment("앱푸시이미지PATH")
	@Column(columnDefinition = "varchar(300)")
    private String appPushImagePath;
}
