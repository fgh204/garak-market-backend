package com.lawzone.market.image.service;

import java.math.BigInteger;
import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProductImageListDTO {
	private BigInteger imageFileNumber;
	private String productId;
	private String fileName;
	private String originFileName;
	private String thumbnailImagePath;
	private Character delegateThumbnailYn;
	private String imageCfcd;
	private BigInteger fileSize;
	private String delYn;
	private String newYn;
	
	public ProductImageListDTO() {
		
	}
}
