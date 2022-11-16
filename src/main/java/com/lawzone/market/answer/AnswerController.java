package com.lawzone.market.answer;


import lombok.RequiredArgsConstructor;
import net.sf.json.*;
import net.sf.json.util.CycleDetectionStrategy;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lawzone.market.question.Question;
import com.lawzone.market.question.QuestionDTO;
import com.lawzone.market.question.QuestionService;
import com.lawzone.market.user.SiteUser;
import com.lawzone.market.user.UserService;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
    	QuestionDTO questionDTO = new QuestionDTO();
    	questionDTO.setId(id);
    	Question question = this.questionService.getQuestion(questionDTO);
    	SiteUser siteUser = this.userService.getUser(principal.getName());
    	 if (bindingResult.hasErrors()) {
             model.addAttribute("question", question);
             return "question_detail";
         }
    	 Answer answer = this.answerService.create(question, 
                 answerForm.getContent(), siteUser);
         return String.format("redirect:/question/detail/%s#answer_%s", 
                 answer.getQuestion().getId(), answer.getId());
    }
    
    @ResponseBody
    @PostMapping("/create2")
    public String createAnswer2(@RequestParam Map<String, String> name) {
    	
    	List<Question> questionList = this.questionService.getList();
    	System.out.println("obj1===================");
    	JSONArray obj = new JSONArray();
    	System.out.println("obj2===================" + questionList.toString());
    	
    	JsonConfig config = new JsonConfig();
    	config.setIgnoreDefaultExcludes(false); 
    	config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); 
    	JSONArray result = JSONArray.fromObject(questionList, config);
    	
    	
    	System.out.println("result===================" + result.toString());
        return result.toString();
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
            @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", 
        	    answer.getQuestion().getId(), answer.getId());
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s#answer_%s", 
        	    answer.getQuestion().getId(), answer.getId());
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.answerService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", 
        	    answer.getQuestion().getId(), answer.getId());
    }
}
