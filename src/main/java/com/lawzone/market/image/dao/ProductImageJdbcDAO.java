package com.lawzone.market.image.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductImageJdbcDAO {
	public String delegateFileNumber() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n	p.image_file_number ")
				.append("\n	from  lz_market.product_image_info p ")
				.append("\n	where p.product_id = ? ")
				.append("\n	and p.image_cfcd = ? ")
				.append("\n	order by create_datetime  ")
				.append("\n	limit 1 ");
		
		return _query.toString();
	}
	
	public String setDelegateFileNumber() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_image_info pii ")
				.append("\n	set pii.delegate_thumbnail_yn = if( ? = pii.image_file_number,'Y','N') ")
				.append("\n	where pii.product_id = ? ");
		
		return _query.toString();
	}
	
	public String productImeageCopyOrigin() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n insert INTO lz_market.product_image_info(image_cfcd, origin_file_name, file_name, file_size, thumbnail_image_path ")
				.append("\n 	, delegate_thumbnail_yn, product_id, create_datetime, update_datetime, create_user, update_user) ")
				.append("\n select ")
				.append("\n 	image_cfcd ")
				.append("\n 	, origin_file_name ")
				.append("\n 	, file_name ")
				.append("\n 	, file_size ")
				.append("\n 	, thumbnail_image_path ")
				.append("\n 	, delegate_thumbnail_yn ")
				.append("\n 	, ? ")
				.append("\n 	, create_datetime ")
				.append("\n 	, now() ")
				.append("\n 	, ? ")
				.append("\n 	, ? ")
				.append("\n from lz_market.product_image_info b ")
				.append("\n where b.product_id = ? ");
		
		return _query.toString();
	}
	
	public String productImeageList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	pii.image_file_number ")
				.append("\n 	, pii.product_id  ")
				.append("\n 	, pii.file_name ")
				.append("\n 	, pii.origin_file_name ")
				.append("\n 	, pii.thumbnail_image_path ")
				.append("\n 	, pii.delegate_thumbnail_yn ")
				.append("\n 	, pii.image_cfcd ")
				.append("\n 	, pii.file_size ")
				.append("\n 	, 'N' as delYn ")
				.append("\n 	, 'N' as newYn ")
				.append("\n from lz_market.product_image_info pii ")
				.append("\n where pii.product_id = ? ")
				.append("\n order by pii.create_datetime ");
		
		return _query.toString();
	}
	
	public String productReviewImeageList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	pii.image_file_number ")
				.append("\n 	, pii.product_id  ")
				.append("\n 	, pii.file_name ")
				.append("\n 	, pii.origin_file_name ")
				.append("\n 	, pii.thumbnail_image_path ")
				.append("\n 	, pii.delegate_thumbnail_yn ")
				.append("\n 	, pii.image_cfcd ")
				.append("\n 	, pii.file_size ")
				.append("\n 	, 'N' as delYn ")
				.append("\n 	, 'N' as newYn ")
				.append("\n from lz_market.product_image_info pii ")
				.append("\n where pii.product_id = ? ")
				.append("\n and pii.order_no = ? ")
				.append("\n order by pii.create_datetime ");
		
		return _query.toString();
	}
}
