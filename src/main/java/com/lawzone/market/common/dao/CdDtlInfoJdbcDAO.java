package com.lawzone.market.common.dao;

import org.springframework.stereotype.Component;

@Component
public class CdDtlInfoJdbcDAO {
	public String selectCdDtlInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			  .append("\n	 cdi.dtl_code ")
			  .append("\n	 , cdi.dtl_code_name ")
			  .append("\n	 , cdi.dtl_code_text ")
			  .append("\n from lz_market.cd_dtl_info cdi ")
			  .append("\n where code_no = ? ")
			  .append("\n and use_yn = ? ");
		
		return _query.toString();
	}
}
