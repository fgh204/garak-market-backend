package com.lawzone.market.util;

import lombok.Data;

@Data
public class MessageResponse {
	private int messageCode;
    private String messageStatus;
    private String message;
	
	public void MessageResponse(MessageCode messageCode){
		this.messageCode = messageCode.getMessageCode();
        this.messageStatus = messageCode.getMessageStatus();
        this.message = messageCode.getMessage();
    }
}
