package com.lawzone.market.user.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class SearchWordCDTO {
	private Integer wordListCount;
	
	public SearchWordCDTO() {
		
	}
}
