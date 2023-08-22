package com.lawzone.market.send.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Data
@Entity
public class SendFormInfo extends BaseTimeEntity{
	@Id
	@Comment("발송서식정보일련번호")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sendFormInfoSeq;
	
	@Comment("발송서식코드")
	@NotNull
	@Column(columnDefinition = "CHAR(8)")
	private String sendFormCode;
	
	@Comment("서식명")
	@Column(columnDefinition = "varchar(150)")
	private String sendFormName;
	
	@Comment("푸쉬서비스서식코드")
	@Column(columnDefinition = "varchar(40)")
	private String pushSvcFormCode;
	
	@Comment("푸쉬서비스서식명")
	@Column(columnDefinition = "varchar(100)")
	private String pushSvcFormName;
	
	@Comment("발송내용")
	@Column(columnDefinition = "text")
	private String sendText;
	
	@Comment("서식항목1명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm1Name;
	
	@Comment("서식항목2명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm2Name;
	
	@Comment("서식항목3명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm3Name;
	
	@Comment("서식항목4명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm4Name;
	
	@Comment("서식항목5명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm5Name;
	
	@Comment("서식항목6명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm6Name;
	
	@Comment("서식항목7명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm7Name;
	
	@Comment("서식항목8명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm8Name;
	
	@Comment("서식항목9명")
	@Column(columnDefinition = "varchar(100)")
	private String formItm9Name;
	
	@Comment("서식항목1값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm1Value;
	
	@Comment("서식항목2값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm2Value;
	
	@Comment("서식항목3값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm3Value;
	
	@Comment("서식항목4값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm4Value;
	
	@Comment("서식항목5값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm5Value;
	
	@Comment("서식항목6값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm6Value;
	
	@Comment("서식항목7값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm7Value;
	
	@Comment("서식항목8값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm8Value;
	
	@Comment("서식항목9값")
	@Column(columnDefinition = "varchar(300)")
	private String formItm9Value;
	
	@Comment("이미지여부")
	@Column(columnDefinition = "CHAR(1)")
	private String imageYn;
	
	@Comment("버튼여부")
	@Column(columnDefinition = "CHAR(1)")
	private String buttonYn;
	
	@Comment("사용여부")
	@Column(columnDefinition = "CHAR(1)")
	private String useYn;
}
