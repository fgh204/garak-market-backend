package com.lawzone.market.admin.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderListForm {
	@NotEmpty(message="제목은 필수항목입니다.")
	private String beginDate;
	
	@NotEmpty(message="내용은 필수항목입니다.")
	private String endDate;
}
