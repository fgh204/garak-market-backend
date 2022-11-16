package com.lawzone.market.order.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lawzone.market.admin.dto.AdminProductOrderInfoDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.question.Question;

public interface ProductOrderDAO extends JpaRepository<ProductOrderInfo, String>{
	List<ProductOrderInfo> findByUserIdAndOrderStateCode(String userId, String orderStateCode);
	List<ProductOrderInfo> findByOrderNo(String orderNo);
	
	@Query("select "
            + "distinct q.orderNo "
            + "from ProductOrderInfo q " 
            + "where "
            + "q.orderStateCode = :kw ")
    Page<AdminProductOrderInfoDTO> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
