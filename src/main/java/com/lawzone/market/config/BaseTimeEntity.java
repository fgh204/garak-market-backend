package com.lawzone.market.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
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
	//@Column(updatable = false)
	@Column(name="create_date", length=20 ,updatable = false)
	@Convert(converter=BaseDateToStringConverter.class)
    private String createDate;
	
    @LastModifiedDate
    @Comment("수정일자")
    @Column(name="update_date", length=20)
    @Convert(converter=BaseDateToStringConverter.class)
    private String updateDate;
}
