package com.lawzone.market.common.dao;

import org.springframework.stereotype.Component;

@Component
public class CommonJdbcDAO {
	public String menuInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n SELECT ")
		.append("\n 	M1.MENU_SEQ ")
		.append("\n 	, M1.MENU_NM ")
		.append("\n 	, M1.RSRC_PATH_VALUE ")
		.append("\n 	, M1.FULL_TITLE ")
		.append("\n 	, M1.DEPTH_PATH ")
		.append("\n 	, M1.ORDER_SEQ ")
		.append("\n 	, M1.MENU_LVL ")
		.append("\n 	, M1.FOLDER_YN ")
		.append("\n 	, M1.MENU_REF ")
		.append("\n 	, M1.MENU_CNT ")
		.append("\n FROM ( ")
		.append("\n   SELECT ")
		.append("\n     a.MENU_SEQ ")
		.append("\n     , a.MENU_NM ")
		.append("\n     , a.RSRC_PATH_VALUE ")
		.append("\n     , CONCAT_WS('>', c.MENU_NM, b.MENU_NM, a.MENU_NM) AS FULL_TITLE ")
		.append("\n     , CONCAT_WS('', c.MENU_SEQ, b.MENU_SEQ, a.MENU_SEQ) AS DEPTH_PATH ")
		.append("\n     , CONCAT_WS('', c.ORDER_SEQ , b.ORDER_SEQ, a.ORDER_SEQ) AS ORDER_SEQ ")
		.append("\n     , a.MENU_LVL ")
		.append("\n     , a.FOLDER_YN ")
		.append("\n     , a.MENU_REF ")
		.append("\n     , ((SELECT ")
		.append("\n 	      COUNT(1) ")
		.append("\n 	    FROM lz_market.menu_info MI ")
		.append("\n 	    WHERE MI.UP_MENU_SEQ = a.MENU_SEQ ")
		//.append("\n 	    AND MI.FOLDER_YN = 'N' ")
		.append("\n 	    AND MI.USE_YN = 'Y' ")
		.append("\n 	    AND MI.DISP_YN = 'Y' ")
		.append("\n 	    AND MI.rsrc_lvl <= (select  ")
		.append("\n 	    							ui.user_lvl  ")
		.append("\n 	    						from lz_market.user_info ui  ")
		.append("\n 								where ui.user_id = ?) ")
		.append("\n      	)+(SELECT ")
		.append("\n  		COUNT(1) ")
		.append("\n  		FROM lz_market.menu_info MI ")
		.append("\n 		WHERE MI.UP_MENU_SEQ = b.MENU_SEQ  ")
		.append("\n  		AND MI.FOLDER_YN = 'N' ")
		.append("\n  		AND MI.USE_YN = 'Y' ")
		.append("\n  		AND MI.DISP_YN = 'Y' ")
		.append("\n 	    AND MI.rsrc_lvl  <= (select  ")
		.append("\n 	    							ui.user_lvl  ")
		.append("\n 	    						from lz_market.user_info ui  ")
		.append("\n 								where ui.user_id = ?) ")
		.append("\n  		 )+(SELECT ")
		.append("\n  		     COUNT(1) ")
		.append("\n  		 FROM lz_market.menu_info MI ")
		.append("\n  		 WHERE MI.UP_MENU_SEQ = c.MENU_SEQ ")
		.append("\n  		 AND MI.FOLDER_YN = 'N' ")
		.append("\n  		 AND MI.USE_YN = 'Y' ")
		.append("\n  		 AND MI.DISP_YN = 'Y' ")
		.append("\n 	    AND MI.rsrc_lvl  <= (select  ")
		.append("\n 	    							ui.user_lvl  ")
		.append("\n 	    						from lz_market.user_info ui  ")
		.append("\n 								where ui.user_id = ?)) ")
		.append("\n  		) AS MENU_CNT ")
		.append("\n   FROM lz_market.menu_info a ")
		.append("\n   LEFT OUTER JOIN lz_market.menu_info b ON a.UP_MENU_SEQ = b.MENU_SEQ ")
		.append("\n   LEFT OUTER JOIN lz_market.menu_info c ON b.UP_MENU_SEQ = c.MENU_SEQ ")
		.append("\n   WHERE a.USE_YN = 'Y' ")
		.append("\n   AND a.DISP_YN = 'Y' ")		
		.append("\n   and a.menu_seq not in( select menu_seq ")
		.append("\n   from lz_market.menu_info mi ")
		.append("\n   where mi.rsrc_lvl > ( select ui.user_lvl ")
		.append("\n   from lz_market.user_info ui ")
		.append("\n   where ui.user_id = ?)  ")
		.append("\n   and IFNULL(mi.rsrc_path_value, '') <> '') ")
		.append("\n )M1 ")
		.append("\n WHERE M1.MENU_CNT > 0 ")
		.append("\n ORDER BY M1.ORDER_SEQ ");
		
		return _query.toString();
	}
	
	public String dateInfoList(String slsDayYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n SELECT ")
			.append("\n 	DATE_FORMAT(sdi.sls_date, '%Y-%m-%d') as sls_date ")
			.append("\n 	, sdi.sls_day_yn ")
			.append("\n 	, sdi.fstdy_yn ")
			.append("\n 	, sdi.wdcd ")
			.append("\n 	, sdi.week_dgre_yymm ")
			.append("\n 	, sdi.calnd_week_dgre ")
			.append("\n from lz_market.sls_date_info sdi ")
			.append("\n where sdi.week_dgre_yymm = ? ");
			if(slsDayYn != null) {
				_query.append("\n and sls_day_yn = ? ");
			}
		
		return _query.toString();
	}
	
	public String externalLinkInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n SELECT ")
			.append("\n eli.access_token  ")
			.append("\n , eli.refresh_token ")
			.append("\n , case when DATE_ADD(eli.update_datetime, INTERVAL 8 MINUTE) > now() then 'Y' else 'N' end ")
			.append("\n from lz_market.external_link_info eli ")
			.append("\n where eli.external_link_company_code = ? ");
		
		return _query.toString();
	}
	
	public String modiifyExternalLinkInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.external_link_info eli  ")
			.append("\n set eli.access_token = ? ")
			.append("\n , eli.update_datetime = now() ")
			.append("\n where eli.external_link_company_code = ? ");
		
		return _query.toString();
	}
	
	public String insertCdDtlInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n INSERT INTO lz_market.cd_dtl_info ")
			.append("\n (code_no, dtl_code, create_datetime, create_user, update_datetime, update_user, dtl_code_name, dtl_code_text, use_yn) ")
			.append("\n VALUES(?, ?, now(), '99999999', now(), '99999999', ?, ?, ?) ");
		
		return _query.toString();
	}
	
	public String removeCdDtlInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n DELETE FROM lz_market.cd_dtl_info ")
			.append("\n WHERE code_no = ? ");
		
		return _query.toString();
	}
	
	public String selectCdDtlInfo(String listCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n	 dtl_code_name ")
			.append("\n	 , dtl_code_text ")
			.append("\n from lz_market.cd_dtl_info ")
			.append("\n where code_no = ? ")
			.append("\n order by dtl_code ")
			.append("\n limit " + listCnt);
		
		return _query.toString();
	}
}
