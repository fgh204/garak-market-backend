package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class StorethumbnailImagePathInfoDTO {
	//private String sellerId;
	private String thumbnailImagePath;
	private String productId;
	
	public StorethumbnailImagePathInfoDTO() {
		
	}
}
