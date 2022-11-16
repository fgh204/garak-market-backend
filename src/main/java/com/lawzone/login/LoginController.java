package com.lawzone.login;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lawzone.market.question.QuestionForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class LoginController {
	@GetMapping("/login/oauth2/code/kakao")
	public String getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		System.out.println("attributes=====================" + attributes);
		return "";
    }
	
	@GetMapping("/login/create")
    public String questionCreate(QuestionForm questionForm) {
		System.out.println("attributes=====================");
        return "";
    }
}
