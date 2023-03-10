package com.lawzone.market.category.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductCategoryInfoJdbcDAO {
	public String largeList() {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select")
			  .append("\n	a.product_category_large_name as categoryName ")
			  .append("\n	, product_category_code as categoryCode ")
			  .append("\n from lz_market.product_category_info a ")
			  .append("\n where a.product_category_large_code <> ? ")
			  .append("\n and a.product_category_medium_code = ? ")
			  .append("\n and a.product_category_small_code = ? ");
		return _query.toString();
	}
}
