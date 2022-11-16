package com.lawzone.market.answer;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AnswerForm {
	@NotEmpty(message = "내용은 필수항목입니다.")
	private String content;
}
