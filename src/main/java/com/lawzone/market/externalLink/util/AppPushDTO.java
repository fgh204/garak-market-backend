package com.lawzone.market.externalLink.util;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AppPushDTO {
	private String title;
	private String content;
	private String url;
	private String allYn;
	private ArrayList<String> pushList;
	
	public AppPushDTO() {
		
	}
}