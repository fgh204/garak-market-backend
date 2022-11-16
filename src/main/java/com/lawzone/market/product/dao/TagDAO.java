package com.lawzone.market.product.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.product.service.TagInfo;

public interface TagDAO extends JpaRepository<TagInfo, Long>{
	Optional<TagInfo> findByTagName(String tagName);
	Optional<TagInfo> findByTagId(Long tagId);
	List<TagInfo> findByUseYn(String useYn);
}
