package com.lawzone.market.user.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class SearchWordDTO {
	private String searchWord;
	private BigInteger searchCount;
	
	public SearchWordDTO() {
		
	}
	
	public SearchWordDTO(String searchWord, String searchCount) {
		this.searchWord = searchWord;
		this.searchCount = new BigInteger(searchCount);
	}
}
