package com.lawzone.market.cart.dao;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.cart.service.CartInfo;

public interface CartInfoDAO extends JpaRepository<CartInfo, Long>{
	List<CartInfo> findByUserIdAndProductId(String userId, String productId);
	List<CartInfo> findByCartNumber(Long cartNumber);
}
