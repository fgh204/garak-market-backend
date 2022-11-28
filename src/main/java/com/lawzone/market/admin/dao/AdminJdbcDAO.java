package com.lawzone.market.admin.dao;

import org.springframework.stereotype.Component;

import com.lawzone.market.admin.dto.user.AdminUserCDTO;

@Component
public class AdminJdbcDAO {
	public String adminUserList(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	ui.user_id ")
				.append("\n 	, ui.user_name ")
				.append("\n 	, ui.nickname ") 
				.append("\n 	, concat(ui.user_lvl) ") 
				.append("\n 	, ui.email ")
				.append("\n 	, ui.phone_number ")
				.append("\n 	, si.shop_name ")
				.append("\n 	, si.login_id ")
				.append("\n 	, if(ifnull(si.password,'') = '', 'N', 'Y') as password ") 
				.append("\n 	, '' as passwordValue ")
				.append("\n 	, si.spot_id ")
				.append("\n 	, ui.seller_yn ")
				.append("\n 	, ui.use_yn ")
				.append("\n from lz_market.user_info ui ")
				.append("\n 	left outer join lz_market.seller_info si ")
				.append("\n 	on ui.user_id = si.seller_id ")
				.append("\n where 1 = 1 ");
				if(!"%".equals(adminUserCDTO.getSellerYn())) {
					_query.append("\n and ui.seller_yn = ? ");
				}
				if(!"%".equals(adminUserCDTO.getUseYn())) {
					_query.append("\n and ui.use_yn = ? ");
				}
				if(!(adminUserCDTO.getUserName() == null || "".equals(adminUserCDTO.getUserName()))) {
					_query.append("\n and ui.user_name = ? ");
				}
				_query.append("\n order by ui.user_id ")
				.append("\n limit " + adminUserCDTO.getPageCnt() + ", 10 ");
		
		return _query.toString();
	}
	
	public String adminUserListPageing(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     page.cnt as total_count ")
				.append("\n     , page.p_cnt as total_pages ")
				.append("\n     , if(page.p_cnt > " + adminUserCDTO.getPageCnt() + ", 'Y', 'N') as has_next ")
				.append("\n     , if(1 < " + adminUserCDTO.getPageCnt() + ", 'Y', 'N') as has_previous ")
				.append("\n     , " + adminUserCDTO.getPageCnt() + " as page_number ")
				.append("\n from ")
				.append("\n     (select ")
				.append("\n 		    COUNT(1) as cnt ")
				.append("\n         , CEIL (COUNT(1)/10) as p_cnt ")
				.append("\n     from lz_market.user_info ui ")
				.append("\n 	left outer join lz_market.seller_info si ")
				.append("\n 		on ui.user_id = si.seller_id ")
				.append("\n     where 1 = 1 ");
				if(!"%".equals(adminUserCDTO.getSellerYn())) {
					_query.append("\n and ui.seller_yn = ? ");
				}
				if(!"%".equals(adminUserCDTO.getUseYn())) {
					_query.append("\n and ui.use_yn = ? ");
				}
				if(!(adminUserCDTO.getUserName() == null || "".equals(adminUserCDTO.getUserName()))) {
					_query.append("\n and ui.user_name = ? ");
				}
				_query.append("\n 	)page ");
					  		
		return _query.toString();
	}
	
	public String modifyAdminUserInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.user_info ui ")
				.append("\n set ui.user_lvl  = ? ")
				.append("\n 	, ui.seller_yn  = ? ")
				.append("\n 	, ui.use_yn  = ? ")
				.append("\n 	, ui.update_date = now() ")
				.append("\n 	, ui.update_user = ? ")
				.append("\n where ui.user_id = ? ");
					  		
		return _query.toString();
	}
	
	public String modifyAdminSellerInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.seller_info si ")
				.append("\n set si.shop_name = ? ")
				.append("\n 	, si.login_id = ? ")
				.append("\n 	, si.password = case when IFNULL(?,'') = '' then si.password else ? end ")
				.append("\n 	, si.spot_id = ? ")
				.append("\n 	, si.update_date = now() ")
				.append("\n 	, si.update_user = ? ")
				.append("\n where si.seller_id = ? ");
					  		
		return _query.toString();
	}
}
