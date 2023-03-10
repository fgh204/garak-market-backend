package com.lawzone.market.common.service;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Comment;

import com.lawzone.market.config.BaseTimeEntity;
import com.lawzone.market.externalLink.util.AppPushDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ExternalLinkInfoDTO {
	private String accessToken;
	private String refreshToken;
	private String useYn;

	public ExternalLinkInfoDTO() {
		
	}
	
}
