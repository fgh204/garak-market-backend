package com.lawzone.market.question;

import java.util.Date;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Data;

@Data
public class QuestionDTO {
	@NotNull
	private Integer id;
	
	private String subject;
	private String content;
	private Date createDate;
}
