package com.lawzone.market.admin.dto.user;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BoilerplateDTO {
	private String shopName;
	private BigInteger boilerplateNumber;
	private String boilerplateName;
	private String boilerplateText;
	private String useYn;
	
	public BoilerplateDTO() {
		
	}
}
