package com.lawzone.market.answer;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;

import com.lawzone.market.question.Question;
import com.lawzone.market.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@CreatedDate
	private LocalDateTime createDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;
	
	@ManyToOne
    private SiteUser author;
	
	private LocalDateTime modifyDate;
	
	@ManyToMany
    Set<SiteUser> voter;
}
