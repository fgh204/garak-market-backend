package com.lawzone.market.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.order.service.ProductOrderRevokeInfo;

public interface ProductOrderRevokeDAO extends JpaRepository<ProductOrderRevokeInfo, String>{

}
