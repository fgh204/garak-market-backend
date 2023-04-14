package com.lawzone.market.admin.dto.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminUserCDTO {
	private String userName;
	private String sellerYn;
	private String useYn;
	private String pageCnt;
	private String maxPage;
	private String adminUserId;
	private String beginDate;
	private String endDate;
	private String searchGb;
	private String reviewYn;
	
	public AdminUserCDTO() {
		
	}
}
