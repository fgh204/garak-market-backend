package com.lawzone.market.product.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
	private String productId;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productStock;
	private String productDesc;
	private String productCategoriesCode;
	private String beginDate;
	private String page;
	private String endDate;
	private String useYn;
	private String sellerId;
}
