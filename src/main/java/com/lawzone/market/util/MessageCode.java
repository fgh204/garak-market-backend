package com.lawzone.market.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageCode {
	M0001(0000,"I","조회되었습니다."),
	M0002(0000,"I","조회결과가없습니다."),
	M0003(0000,"I","저장되었습니다."),
	M0004(0000,"I","수정되었습니다."),
    ;

    private int messageCode;
    private String messageStatus;
    private String message;
	int getMessageCode() {
		// TODO Auto-generated method stub
		return this.messageCode;
	}
	String getMessageStatus() {
		// TODO Auto-generated method stub
		return this.messageStatus;
	}
	String getMessage() {
		// TODO Auto-generated method stub
		return this.message;
	}
}
