package com.lawzone.market.product.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.product.service.ProductTagInfo;
import com.lawzone.market.product.service.ProductTagInfoDTO;

public interface ProdustTagDAO extends JpaRepository<ProductTagInfo, String>{
	List<ProductTagInfoDTO> findByproductTagInfoIdProductId(String productId);
}
