package com.lawzone.market.product.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductTagJdbcDAO	{
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
	
	public String removeProductTgaInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n delete from lz_market.product_tag_info pti ")
			.append("\n	where product_id = ? ");
		
		return _query.toString();
	}
	
	public String productTagCopyOrigin() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n insert INTO lz_market.product_tag_info(product_id, tag_id, create_datetime, update_datetime, create_user, update_user) ")
				.append("\n select ")
				.append("\n 	? ")
				.append("\n 	, b.tag_id ")
				.append("\n 	, now() ")
				.append("\n 	, now() ")
				.append("\n 	, ? ")
				.append("\n 	, ? ")
				.append("\n from lz_market.product_tag_info b ")
				.append("\n where b.product_id = ? ");
		
		return _query.toString();
	}
}
