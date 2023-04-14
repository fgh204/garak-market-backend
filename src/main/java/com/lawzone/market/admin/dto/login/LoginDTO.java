package com.lawzone.market.admin.dto.login;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class LoginDTO {
	private String portalId;
	private String portalPw;
	private String oauthCode;
	private String previousUrl;
	
	public LoginDTO() {
		
	}
}
