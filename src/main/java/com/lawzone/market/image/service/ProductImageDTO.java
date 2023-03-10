package com.lawzone.market.image.service;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductImageDTO {
	private Long imageFileNumber;
	private String productId;
	private String orderNo;
	private String fileName;
	private String originFileName;
	private String thumbnailImagePath;
	private String delegateThumbnailYn;
	private String imageCfcd;
	private Long fileSize;
	private Date createDatetime;
	private Date updateDatetime;
	
	public ProductImageDTO() {
		
	}
}
