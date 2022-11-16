package com.lawzone.market;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
//import lombok.Setter;

@RequiredArgsConstructor
@Getter
//@Setter
public class HelloLombok {
	
	private final String hello;
	private final int lombok;
	
	public static void main(String[] args) {
		HelloLombok helloLonbok = new HelloLombok("헬로",5);
		//helloLonbok.setHello("렣로");
		//helloLonbok.setLombok(5);
		
		System.out.println(helloLonbok.getHello());
		System.out.println(helloLonbok.getLombok());
	}
}
