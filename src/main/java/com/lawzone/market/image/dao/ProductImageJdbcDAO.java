package com.lawzone.market.image.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductImageJdbcDAO {
	public String delegateFileNumber() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n	min(p.image_file_number) ")
				.append("\n	from  lz_market.product_image_info p ")
				.append("\n	where p.product_id = ? ")
				.append("\n	and p.image_cfcd = ? ");
		
		return _query.toString();
	}
	
	public String setDelegateFileNumber() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_image_info pii ")
				.append("\n	set pii.delegate_thumbnail_yn = if( ? = pii.image_file_number,'Y','N') ")
				.append("\n	where pii.product_id = ? ");
		
		return _query.toString();
	}
}
