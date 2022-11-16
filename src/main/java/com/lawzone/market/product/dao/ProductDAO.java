package com.lawzone.market.product.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.question.Question;

public interface ProductDAO extends JpaRepository<ProductInfo, String>{
	List<ProductInfo> findByproductName(String productName);
	List<ProductInfo> findByuseYn(String useYn);
	List<ProductInfo> findByProductId(String productId);
	
	@Query("select "
            + "distinct q "
            + "from ProductInfo q "
            + "where 1=1 ")
    Page<ProductInfo> findAllByPage(@Param("kw") String kw,Pageable pageable);
}
