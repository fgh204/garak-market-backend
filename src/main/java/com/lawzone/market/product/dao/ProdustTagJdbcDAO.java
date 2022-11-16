package com.lawzone.market.product.dao;

import org.springframework.stereotype.Component;

@Component
public class ProdustTagJdbcDAO	{
	public String productTgaList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n	ti.tag_id ")
			.append("\n	, ti.tag_name ")
			.append("\n	, ti.use_yn ")
			.append("\n	, ti.color_id ")
			.append("\n from lz_market.product_tag_info pti ")
			.append("\n 	, lz_market.tag_info ti")
			.append("\n where pti.tag_id = ti.tag_id")
			.append("\n and pti.product_id = ? ")
			.append("\n and ti.use_yn = ? ");
		
		return _query.toString();
	}
}
