package com.lawzone.market.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductInfoDTO;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.dao.SellerInfoJdbcDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SellerInfoService {
	
	private final SellerInfoDAO sellerInfoDAO;
	private final SellerInfoJdbcDAO sellerInfoJdbcDAO;
	private final UserInfoDAO userInfoDAO;
	private final PasswordEncoder passwordEncoder;
	private final UtilService utilService;

	public List<UserInfo> getSellerInfo(String portalId, String password) {
		List<UserInfo> userInfo = new ArrayList<>();
		
		Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findByLoginId(portalId);
		
		//log.info("111====" + passwordEncoder.encode(password));
		
        if (sellerInfo.isPresent()) {
            if(passwordEncoder.matches(password, sellerInfo.get().getPassword())) {
            	userInfo = userInfoDAO.findByUserIdAndUseYn(sellerInfo.get().getSellerId(), "Y");
            }else {
            	return userInfo;
            }
        } 
        
        return userInfo;
    }
	
	public String getSellerYn(String sellerId) {
		String _sql = this.sellerInfoJdbcDAO.sellerYn();

		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, sellerId);
		
		return this.utilService.getQueryStringChk(_sql,_queryValue);
    }
	
	public Optional<SellerInfo> getSellerInfo(String sellerId) {
		return this.sellerInfoDAO.findBySellerId(sellerId);
    }
}
