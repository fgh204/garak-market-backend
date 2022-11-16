package com.lawzone.market.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.lawzone.market.oAuth2.OAuth2Attributes;
import com.lawzone.market.user.service.UserInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	private final UserInfoService userInfoService;
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	private String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("bindingResult=====================");
		System.out.println(bindingResult);
		System.out.println("bindingResult=====================");
		Cookie[] myCookies = request.getCookies();
		
		for(int i = 0; i < myCookies.length; i++) {
			System.out.println(i + "번째 쿠키 이름: " + myCookies[i].getName());
			System.out.println(i + "번째 쿠키 값: " + myCookies[i].getValue());
			    }
		
		if(bindingResult.hasErrors()) {
			return "signup_form";
		}
		
		if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
		}
		try {
		userService.create(userCreateForm.getUsername(), 
                userCreateForm.getEmail(), userCreateForm.getPassword1());
		}catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            
            Cookie myCookie = new Cookie("qqwwee", "223344");
    		myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
            
            response.addCookie(myCookie);
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
		return "redirect:/";
	}
	
	@RequestMapping("/index")
    public String login2() {
        return "index";
    }
	
	@GetMapping("/login")
    public String login(UserLoginForm userLoginForm) {
        return "th/login_form";
    }
	
	@PostMapping("/login")
    public String userLogin(@Valid UserLoginForm userLoginForm, BindingResult bindingResult) {
		System.out.println("UserCreateForm" + userLoginForm);
		System.out.println("UserCreateForm" + userLoginForm.getPassword());
		
		this.userService.getLogInInfo(userLoginForm.getEmail(), userLoginForm.getPassword());
		
        return "th/login_form";
    }
	
	@ResponseBody
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("request=============================" + request.getCookies());
		Cookie[] myCookies = request.getCookies();

	    for(int i = 0; i < myCookies.length; i++) {
	System.out.println(i + "번째 쿠키 이름: " + myCookies[i].getName());
	System.out.println(i + "번째 쿠키 값: " + myCookies[i].getValue());
	    }
    }
}
