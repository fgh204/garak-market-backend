package com.lawzone.market.image.service;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ProductImageDTO {
	private Long imageFileNumber;
	private String productId;
	private String fileName;
	private String originFileName;
	private String thumbnailImagePath;
	private String delegateThumbnailYn;
	private String imageCfcd;
	private Long fileSize;
	private Date createDate;
	private Date updateDate;
	
	public ProductImageDTO(String productId, String fileName, String originFileName
			, String thumbnailImagePath, String delegateThumbnailYn, String imageCfcd,Long fileSize) {
		this.productId = productId;
		this.fileName = fileName;
		this.originFileName = originFileName;
		this.thumbnailImagePath = thumbnailImagePath;
		this.delegateThumbnailYn = delegateThumbnailYn;
		this.imageCfcd = imageCfcd;
		this.fileSize = fileSize;
	}
}
