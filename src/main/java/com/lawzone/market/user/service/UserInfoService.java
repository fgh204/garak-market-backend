package com.lawzone.market.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lawzone.market.payment.service.PaymentInfo;
import com.lawzone.market.user.dao.DeliveryAddressInfoDAO;
import com.lawzone.market.user.dao.DeliveryAddressInfoJdbcDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.util.AES256Util;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoService {
	private final UserInfoDAO userInfoDAO;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final DeliveryAddressInfoDAO deliveryAddressInfoDAO;
	private final DeliveryAddressInfoJdbcDAO deliveryAddressInfoJdbcDAO;
	private final UtilService utilService;
	
	public UserInfo create(Map<String, Object> userMap) {
		UserInfo user = new UserInfo();
		user.setUserName(userMap.get("").toString());
		user.setEmail(userMap.get("").toString());
		user.setPassword(passwordEncoder.encode(userMap.get("").toString()));
		this.userInfoDAO.save(user);
		return user;
	}
	
	public UserInfo loadUserBySocialId(String socialId) throws UsernameNotFoundException {
        return userInfoDAO.findBySocialId(socialId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
	
	public Optional<UserInfo> marketLogin(String emailId, String password) {
		
		AES256Util aES256Util = new AES256Util();
		
		Optional<UserInfo> userInfo = userInfoDAO.findByEmailAndPasswordIsNotNull(emailId);
		
		if (userInfo.isEmpty()) {
			return userInfo;
		}else {
		
			UserInfo _userinfo = userInfo.get();
	
			if(password.equals(aES256Util.decryptedText(_userinfo.getPassword()))) {
				return userInfo;
			}else {
				return java.util.Optional.empty();
			}
		}
    }
	
	public List getUserInfoByUserId(String userId) throws UsernameNotFoundException {
        return userInfoDAO.findByUserId(userId);
    }
	
	@Transactional
	public void addDeliveryAddressInfo(DeliveryAddressInfoDTO deliveryAddressInfoDTO) throws UsernameNotFoundException {
		if("Y".equals(deliveryAddressInfoDTO.getBaseShippingYn())) {
			String _query = this.deliveryAddressInfoJdbcDAO.deliveryAddressBaseShippingYn();
			
			ArrayList<String> _queryValue = new ArrayList<>();
			_queryValue.add(0, deliveryAddressInfoDTO.getUserId());
			
			this.utilService.getQueryStringUpdate(_query, _queryValue);
		}
		
		DeliveryAddressInfo deliveryAddressInfo = new DeliveryAddressInfo();
		deliveryAddressInfo = modelMapper.map(deliveryAddressInfoDTO, DeliveryAddressInfo.class);
		
		this.deliveryAddressInfoDAO.save(deliveryAddressInfo);
    }
	
	@Transactional
	public void removeDeliveryAddressInfo(DeliveryAddressInfoDTO deliveryAddressInfoDTO) throws UsernameNotFoundException {
		DeliveryAddressInfo deliveryAddressInfo = new DeliveryAddressInfo();
		deliveryAddressInfo = modelMapper.map(deliveryAddressInfoDTO, DeliveryAddressInfo.class);
		
		this.deliveryAddressInfoDAO.delete(deliveryAddressInfo);
    }
	
	public List getDeliveryAddressInfo(DeliveryAddressInfoDTO deliveryAddressInfoDTO) throws UsernameNotFoundException {
		
		return this.deliveryAddressInfoDAO.findByUserId(deliveryAddressInfoDTO.getUserId());
    }
}