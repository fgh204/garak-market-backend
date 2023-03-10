package com.lawzone.market.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.lawzone.market.util.BaseDateToStringConverter;
import com.lawzone.market.util.DateToStringConverter;

import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
	@CreatedDate
	@Comment("등록일자")
	@Column(name="create_datetime", length=20 ,updatable = false)
	@Convert(converter=BaseDateToStringConverter.class)
    private String createDatetime;
	
    @LastModifiedDate
    @Comment("수정일자")
    @Column(name="update_datetime", length=20)
    @Convert(converter=BaseDateToStringConverter.class)
    private String updateDatetime;
    
    @CreatedBy
	@Comment("등록자")
	@Column(name="create_user", length=8 ,updatable = false)
    private String createUser;
	
    @LastModifiedBy
    @Comment("수정자")
    @Column(name="update_user", length=8)
    private String updateUser;
}
