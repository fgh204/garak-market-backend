package com.lawzone.market.user.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserTrackingInfoId implements Serializable{
	@Comment("사용자ID")
	@Column(columnDefinition = "varchar(8)")
	private String userId;
	
	@Comment("서비스URL")
	@NotNull
	@Column(columnDefinition = "varchar(300)")
	private String svcUrl;
}
