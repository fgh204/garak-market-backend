package com.lawzone.market.question;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.lawzone.market.answer.AnswerForm;
import com.lawzone.market.user.SiteUser;
import com.lawzone.market.user.UserService;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {
	
	private final QuestionService questionService;
	private final UserService userService;
	private final UtilService utilService;
	
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") 
	private String redirect;
	
    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        //List<Question> questionList = this.questionService.getList();
        //model.addAttribute("questionList", questionList);
    	//log.info("page:{}, kw:{}", page, kw);
    	
		//UtilComponent utilComponent = new UtilComponent();
    	//log.info("111111111111111111111===" + utilService.getNextVal("CART_NO"));
    	
    	Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        System.out.println("redirect==============" + redirect);
        return "th/question_list";
    }
    
    @RequestMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
    	QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
    	Question question = this.questionService.getQuestion(questionDTO);
        model.addAttribute("question", question);
    	return "th/question_detail";
    }
    
    @ResponseBody
    @RequestMapping("/list2")
    public String list2(Model model) {
    	Map<Object, Object> mapInfo = new HashMap<>();
    	System.out.println("result===================");
    	
        //List<Question> questionList = this.questionService.getList();
        
        Page<Question> paging = this.questionService.getList(0, "20");
        model.addAttribute("paging", paging);
        model.addAttribute("kw", "20");
        
        
        
    	JsonConfig config = new JsonConfig();
    	config.setIgnoreDefaultExcludes(false); 
    	config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); 
    	mapInfo.put("result", paging);
    	JSONArray result = JSONArray.fromObject(mapInfo, config);
    	
    	System.out.println("result===================" + result.toString());
        return result.toString();
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        System.out.println("LocalDateTime.now()================" + LocalDateTime.now());
    	this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
    	return "redirect:/question/list";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
    	QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
    	Question question = this.questionService.getQuestion(questionDTO);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, 
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
    	Question question = this.questionService.getQuestion(questionDTO);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
    	QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
        Question question = this.questionService.getQuestion(questionDTO);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
    	QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
        Question question = this.questionService.getQuestion(questionDTO);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
