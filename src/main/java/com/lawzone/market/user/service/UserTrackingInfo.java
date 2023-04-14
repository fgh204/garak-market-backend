package com.lawzone.market.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserTrackingInfo extends BaseTimeEntity{	
	@Id
	@Comment("사용자ID")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("서비스URL")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String svcUrl;
}
