package com.lawzone.market.user.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lawzone.market.aws.service.S3Upload;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.payment.service.PaymentInfo;
import com.lawzone.market.user.dao.DeliveryAddressInfoDAO;
import com.lawzone.market.user.dao.DeliveryAddressInfoJdbcDAO;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.dao.UserInfoJdbcDAO;
import com.lawzone.market.util.AES256Util;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoService {
	private final UserInfoDAO userInfoDAO;
	private final SellerInfoDAO sellerInfoDAO;
	private final UserInfoJdbcDAO userInfoJdbcDAO;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final DeliveryAddressInfoDAO deliveryAddressInfoDAO;
	private final DeliveryAddressInfoJdbcDAO deliveryAddressInfoJdbcDAO;
	private final UtilService utilService;
	private final S3Upload s3Upload;
	
	public UserInfo create(Map<String, Object> userMap) {
		UserInfo user = new UserInfo();
		user.setUserName(userMap.get("").toString());
		user.setEmail(userMap.get("").toString());
		//user.setPassword(passwordEncoder.encode(userMap.get("").toString()));
		this.userInfoDAO.save(user);
		return user;
	}
	
	public UserInfo loadUserBySocialId(String socialId) throws UsernameNotFoundException {
        return userInfoDAO.findBySocialId(socialId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
	
	public Optional<UserInfo> marketLogin(String emailId, String password) {
		
		AES256Util aES256Util = new AES256Util();
		
		Optional<UserInfo> userInfo = userInfoDAO.findByEmail(emailId);
		
		if (userInfo.isEmpty()) {
			return userInfo;
		}else {
		
			UserInfo _userinfo = userInfo.get();
			return java.util.Optional.empty();
			//if(password.equals(aES256Util.decryptedText(_userinfo.getPassword()))) {
			//	return userInfo;
			//}else {
			//	return java.util.Optional.empty();
			//}
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
	
	public List getUserInfo(String userId) {
		String _query = this.userInfoJdbcDAO.marketUserInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, userId);
		
		MarketUserInfoDTO marketUserInfoDTO = new MarketUserInfoDTO();
		
		return this.utilService.getQueryString(_query, marketUserInfoDTO, _queryValue);
    }
	
	@Transactional(rollbackFor = Exception.class)
	public String modifyMarketUserInfo(MarketUserDTO marketUserDTO) {
		//유저정보수정
		String _rtnMsg = "";
		String _nickname = marketUserDTO.getNickname();
		String _shopName = marketUserDTO.getShopName();
		
		if("".equals(_nickname) || _nickname == null) {
			return _rtnMsg = "수정할 별칭을 입력하세요.";
		}else {
			Optional<UserInfo> userInfo = this.userInfoDAO.findByNickname(marketUserDTO.getNickname());
			
			if(userInfo.isPresent()) {
				if(marketUserDTO.getUserId().equals(userInfo.get().getUserId())) {
					//return _rtnMsg = "";
				}else {
					//이미 존재하는 닉네임
					return _rtnMsg = "이미 사용중인 별칭입니다.";
				}
			}else {
				//수정가능
				List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(marketUserDTO.getUserId());
				
				_userInfo.get(0).setNickname(marketUserDTO.getNickname());
			}
		}
		
		//판매자정보수정
		if("".equals(_shopName) || _shopName == null) {
			return _rtnMsg = "수정할 상호를 입력하세요.";
		}else {
			Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(marketUserDTO.getUserId());
			if(sellerInfo.isPresent()) {
				sellerInfo.get().setShopName(marketUserDTO.getShopName());
			}
		}
		return _rtnMsg;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean getUserNicknameChk(MarketUserDTO marketUserDTO) {
		//유저정보수정
		Boolean _rtnValue = false;
		String _nickname = marketUserDTO.getNickname();
		
		Optional<UserInfo> userInfo = this.userInfoDAO.findByNickname(_nickname);
		
		if(userInfo.isPresent()) {
			//if(marketUserDTO.getUserId().equals(userInfo.get().getUserId())) {
				//return _rtnValue = "";
			//}else {
				return _rtnValue = true;
			//}
		}
		
		return _rtnValue;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void addProfileImages(MultipartFile[] uploadFile, String userId) throws IOException {
		String _userId = userId;
		String _fileName = "";
		String imeageUrl = "";
		List<UserInfo> userInfo = this.userInfoDAO.findByUserId(_userId);
		
		if(userInfo.size() > 0) {
			for(MultipartFile file : uploadFile) {
				
				if(!file.isEmpty()) {
					
					if(file.getOriginalFilename().indexOf("HEIC") > -1 ) {
						try {
							File _file = new File(file.getOriginalFilename());
							
							file.transferTo(_file);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
					
					_fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

					imeageUrl = this.s3Upload.upload(file, _fileName);
				}
			}
			
			UserInfo _userInfo = new UserInfo();
			
			_userInfo = userInfo.get(0);
			_userInfo.setProfileImagesPath(imeageUrl);
			
			this.userInfoDAO.save(_userInfo);
		}	
    }
}