package com.lawzone.market.product.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lawzone.market.product.service.TagInfo;

public interface TagDAO extends JpaRepository<TagInfo, Long>{
	Optional<TagInfo> findByTagName(String tagName);
	Optional<TagInfo> findByTagId(Long tagId);
	List<TagInfo> findByUseYn(String useYn);
	
//	@Query(nativeQuery = true, value ="select "
//            + " t.tagId "
//            + ", t.tagName "
//            + ", t.useYn "
//            + ", t.colorId "
//            + "from TagInfo t "
//            + ", ProductTagInfo p "
//            + "where t.tagId = p.productTagInfoId.tagId "
//            + "and p.productTagInfoId.productId IN (:productId) "
//            + "and t.useYn = (:useYn) ")
//	List<TagInfo> findByTagIdAndUseYn(@Param("productId") List productId, @Param("useYn") String useYn);
}
