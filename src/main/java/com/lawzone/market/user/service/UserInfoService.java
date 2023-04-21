package com.lawzone.market.user.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lawzone.market.admin.service.SettlementListDTO;
import com.lawzone.market.aws.service.S3Controller;
import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.dao.CdDtlInfoDAO;
import com.lawzone.market.common.dao.CommonJdbcDAO;
import com.lawzone.market.image.dao.ProductImageDAO;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.image.service.ProductImageInfo;
import com.lawzone.market.payment.service.PaymentInfo;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductInfoListPDTO;
import com.lawzone.market.user.dao.DeliveryAddressInfoDAO;
import com.lawzone.market.user.dao.DeliveryAddressInfoJdbcDAO;
import com.lawzone.market.user.dao.SellerFavoriteInfoDAO;
import com.lawzone.market.user.dao.SellerFavoriteInfoJdbcDAO;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.dao.UserInfoJdbcDAO;
import com.lawzone.market.util.AES256Util;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoService {
	private final UserInfoDAO userInfoDAO;
	private final ProductImageDAO productImageDAO;
	private final SellerInfoDAO sellerInfoDAO;
	private final UserInfoJdbcDAO userInfoJdbcDAO;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final DeliveryAddressInfoDAO deliveryAddressInfoDAO;
	private final DeliveryAddressInfoJdbcDAO deliveryAddressInfoJdbcDAO;
	private final UtilService utilService;
	private final S3Controller s3Upload;
	private final SellerFavoriteInfoDAO sellerFavoriteInfoDAO;
	private final SellerFavoriteInfoJdbcDAO sellerFavoriteInfoJdbcDAO;
	private final JwtTokenUtil jwtTokenUtil;
	private final CdDtlInfoDAO cdDtlInfoDAO;
	private final PointService pointService;
	private final CommonJdbcDAO commonJdbcDAO;
	
	public UserInfo create(Map<String, Object> userMap) {
		UserInfo user = new UserInfo();
		user.setUserName(userMap.get("").toString());
		user.setEmail(userMap.get("").toString());
		//user.setPassword(passwordEncoder.encode(userMap.get("").toString()));
		this.userInfoDAO.save(user);
		return user;
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
		String _socialName = "";
		boolean nick = false;
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(marketUserDTO.getUserId());
		
		if(_userInfo.size() > 0) {
			_socialName = _userInfo.get(0).getSocialName();
			
			if("".equals(_nickname) || _nickname == null) {
				return _rtnMsg = "수정할 별칭을 입력하세요.";
			}
			
			if("Y".equals(_userInfo.get(0).getSellerYn())) {
				//판매자정보수정
				if("".equals(_shopName) || _shopName == null) {
					return _rtnMsg = "수정할 상호를 입력하세요.";
				}else {
					Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(marketUserDTO.getUserId());
					if(sellerInfo.isPresent()) {
						sellerInfo.get().setShopName(marketUserDTO.getShopName());
					}
				}
			}
			
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
				//List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(marketUserDTO.getUserId());
				nick = true;
			}
			
			if(nick) {
				_userInfo.get(0).setNickname(marketUserDTO.getNickname());
			}
			
			if("apple".equals(_socialName)) {
				_userInfo.get(0).setUserName(marketUserDTO.getUserName());
				_userInfo.get(0).setEmail(marketUserDTO.getEmail());
			}
			
		}
		
//		if("".equals(_nickname) || _nickname == null) {
//			return _rtnMsg = "수정할 별칭을 입력하세요.";
//		}else {
//			Optional<UserInfo> userInfo = this.userInfoDAO.findByNickname(marketUserDTO.getNickname());
//			
//			if(userInfo.isPresent()) {
//				if(marketUserDTO.getUserId().equals(userInfo.get().getUserId())) {
//					//return _rtnMsg = "";
//				}else {
//					//이미 존재하는 닉네임
//					return _rtnMsg = "이미 사용중인 별칭입니다.";
//				}
//			}else {
//				//수정가능
//				List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(marketUserDTO.getUserId());
//				
//				_userInfo.get(0).setNickname(marketUserDTO.getNickname());
//			}
//		}
		
//		//판매자정보수정
//		if("".equals(_shopName) || _shopName == null) {
//			return _rtnMsg = "수정할 상호를 입력하세요.";
//		}else {
//			Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(marketUserDTO.getUserId());
//			if(sellerInfo.isPresent()) {
//				sellerInfo.get().setShopName(marketUserDTO.getShopName());
//			}
//		}
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

					imeageUrl = this.s3Upload.upload(file, _fileName, "");
				}
			}
			
			//UserInfo _userInfo = new UserInfo();
			
			//_userInfo = userInfo.get(0);
			userInfo.get(0).setProfileImagesPath(imeageUrl);
			
			//this.userInfoDAO.save(_userInfo);
		}	
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void removeProfileImage( String userId) {
		String _userId = userId;
		List<UserInfo> userInfo = this.userInfoDAO.findByUserId(_userId);
		
		//this.s3Upload.delete(userInfo.get(0).getProfileImagesPath());
		userInfo.get(0).setProfileImagesPath("");
    }
	
	@Transactional(rollbackFor = Exception.class)
	public String addProfileImage(ProductImageDTO productImageDTO, String userId) {
		String rtnMsg = "";
		
		Optional<ProductImageInfo> productImageInfo = productImageDAO.findByImageFileNumber(productImageDTO.getImageFileNumber());
		
		if(productImageInfo.isPresent()) {
			List<UserInfo> userInfo = this.userInfoDAO.findByUserId(userId);
			if(userInfo.size() > 0) {
				userInfo.get(0).setProfileImagesPath(productImageInfo.get().getThumbnailImagePath());
				rtnMsg = "저장되었습니다.";
			} else {
				rtnMsg = "고객정보가 없습니다.";
			}
		} else {
			rtnMsg = "등록된 이미지가 없습니다.";
		}
		
	return rtnMsg;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Map appleLogin(Map _map) {
		Map rtnMap = new HashMap<>();
		String token = "";
		String newYn = "";

		String userId = "";
		String userNm = (String) _map.get("name");
		String sellerYn = "N";
		String nickname = "";
		String email = (String) _map.get("email");
		String phoneNumber = "";
		String oauthId = _map.get("sub").toString();
		String socialAccessToken = (String) _map.get("access_token");
		String loginId = "";
		String loginPw = "";
		String socialName= "apple";
		
		UserInfo user = new UserInfo();
		
		List<UserInfo> userInfo = this.userInfoDAO
				.findBySocialIdAndUseYn(
						oauthId
						, "Y");
		if(userInfo.size() == 0) {
			newYn = "Y";
			String userNumber = utilService.getNextVal("USER_ID");
			
			loginId = UUID.randomUUID().toString();
			loginPw = UUID.randomUUID().toString();
			
			user.setUserId(StringUtils.leftPad(userNumber, 8,"0"));
			user.setUserName(userNm);
			user.setNickname(nickname);
			user.setEmail(email);
			user.setSocialId(oauthId);
			user.setPhoneNumber(phoneNumber);
			user.setUserLvl(1);
			user.setSellerYn(sellerYn);
			user.setUseYn("Y");
			user.setAccessToken(socialAccessToken);
			user.setPassword(passwordEncoder.encode(loginPw));
			user.setLoginId(loginId);
			user.setSocialName(socialName);
			
			token = jwtTokenUtil.generateToken(user, null);
			
			user.setToken(token);
			
			this.userInfoDAO.save(user).getUserId();
			
			List<UserInfo> userInfo2 = 
					this.userInfoDAO
					.findBySocialIdAndUseYn(
							oauthId
							, "Y");
			userInfo = userInfo2;
		}else {
			newYn = "N";
			token = jwtTokenUtil.generateToken(userInfo.get(0), null);
			
			if("".equals(userInfo.get(0).getLoginId()) || userInfo.get(0).getLoginId() == null) {
				loginId = UUID.randomUUID().toString();
				loginPw = UUID.randomUUID().toString();
				
				userInfo.get(0).setLoginId(loginId);
				userInfo.get(0).setPassword(passwordEncoder.encode(loginPw));
			}
			
			userInfo.get(0).setAccessToken(socialAccessToken);
			userInfo.get(0).setToken(token);
		}
		
		boolean isRegistered = true;
		
		if(userInfo.get(0).getPushId() == null || "".equals(userInfo.get(0).getPushId())) {
			isRegistered = false;
		}
		
		rtnMap.put("token" , token);
		rtnMap.put("loginId" , userInfo.get(0).getLoginId());
		rtnMap.put("password" , userInfo.get(0).getPassword());
		rtnMap.put("isRegistered" , isRegistered);
		
	return rtnMap;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public String getDomaadoUserSignup(MarketSignupDTO marketSignupDTO) {
		String _rtnCd = "9999";
		
		Optional<UserInfo> userInfo = this.userInfoDAO.findByLoginId(marketSignupDTO.getLoginId());
		
		if(userInfo.isPresent()) {
			_rtnCd = "9999";
		} else {
			UserInfo user = new UserInfo();
			
			String userNumber = utilService.getNextVal("USER_ID");
			
			user.setUserId(StringUtils.leftPad(userNumber, 8,"0"));
			user.setLoginId(marketSignupDTO.getLoginId());
			user.setUserName(marketSignupDTO.getUserName());
			user.setPassword(passwordEncoder.encode(marketSignupDTO.getPassword()));
			user.setUserLvl(1);
			user.setSellerYn("N");
			user.setUseYn("Y");
			
			this.userInfoDAO.save(user);
			_rtnCd = "0000";
		}
		
	return _rtnCd;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Optional getDomaadoUserSignin(MarketSignupDTO marketSignupDTO) {
		Boolean _rtn = false;
		
		Optional<UserInfo> userInfo = this.userInfoDAO.findByLoginIdAndUseYn(marketSignupDTO.getLoginId(), "Y");
		
		if(userInfo.isPresent()) {
			if(passwordEncoder.matches(marketSignupDTO.getPassword(), userInfo.get().getPassword())) {
				return userInfo;
            }else {
            	return userInfo.empty();
            }
		}
	return userInfo;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean getDomaadoUserEmailDuplicated(MarketSignupDTO marketSignupDTO) {
		//유저정보수정
		Boolean _rtnValue = false;
		String _email = marketSignupDTO.getEmail();
		Optional<UserInfo> userInfo = this.userInfoDAO.findByEmail(_email);
		
		if(userInfo.isPresent()) {
			return _rtnValue = true;
		}
		
		return _rtnValue;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean getDomaadoUserphoneNumberDuplicated(MarketSignupDTO marketSignupDTO) {
		//유저정보수정
		Boolean _rtnValue = false;
		String _phoneNumber = marketSignupDTO.getPhoneNumber();
		Optional<UserInfo> userInfo = this.userInfoDAO.findByPhoneNumber(_phoneNumber);
		
		if(userInfo.isPresent()) {
			return _rtnValue = true;
		}
		
		return _rtnValue;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean getDomaadoUserLoginIdDuplicated(MarketSignupDTO marketSignupDTO) {
		//유저정보수정
		Boolean _rtnValue = false;
		String _loginId = marketSignupDTO.getLoginId();
		Optional<UserInfo> userInfo = this.userInfoDAO.findByLoginId(_loginId);
		
		if(userInfo.isPresent()) {
			return _rtnValue = true;
		}
		
		return _rtnValue;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getDomaadoUserMembershipWithdrawal(MarketSignupDTO marketSignupDTO) {
		Boolean _rtn = false;
		
		List<UserInfo> userInfo = this.userInfoDAO.findByUserId(marketSignupDTO.getUserId());
		
		if(userInfo.size() > 0) {
			userInfo.get(0).setUseYn("N");
			userInfo.get(0).setWithdrawalReasonCode(marketSignupDTO.getWithdrawalReasonCode());
			userInfo.get(0).setWithdrawalReasonText(marketSignupDTO.getWithdrawalReasonText());
			_rtn = true;
			
			return userInfo;
		}
		return userInfo;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getDomaadoUserInfo(String userId) {

		List<UserInfo> userInfo = this.userInfoDAO.findByUserId(userId);
		
	return userInfo;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Map getKakaoLogin(Map map) {
		String token = "";
		String newYn = "";
		
		Map accountMap = new HashMap<>();
		Map rtnMap = new HashMap<>();
		Map profile = new HashMap<>();
		accountMap = (Map) map.get("kakao_account");
		profile = (Map) accountMap.get("profile");
		String userId = "";
		String userNm = (String) accountMap.get("name");
		String sellerYn = "N";
		String nickname = (String) profile.get("nickname");
		String email = (String) accountMap.get("email");
		String phoneNumber = "0" + accountMap.get("phone_number").toString().substring(4).replaceAll("-", "");
		String oauthId = map.get("id").toString();
		String socialAccessToken = (String) map.get("socialAccessToken");
		String loginId = "";
		String loginPw = "";
		String socialName = "kakao";
		
		UserInfo user = new UserInfo();
		
		List<UserInfo> userInfo = this.userInfoDAO
				.findBySocialIdAndEmailAndUseYn(
						oauthId
						,email
						, "Y");
		if(userInfo.size() == 0) {
			newYn = "Y";
			String userNumber = utilService.getNextVal("USER_ID");
			
			loginId = UUID.randomUUID().toString();
			loginPw = UUID.randomUUID().toString();
			
			user.setUserId(StringUtils.leftPad(userNumber, 8,"0"));
			user.setUserName(userNm);
			user.setNickname(nickname);
			user.setEmail(email);
			user.setSocialId(oauthId);
			user.setPhoneNumber(phoneNumber);
			user.setUserLvl(1);
			user.setSellerYn(sellerYn);
			user.setUseYn("Y");
			user.setAccessToken(socialAccessToken);
			user.setPassword(passwordEncoder.encode(loginPw));
			user.setLoginId(loginId);
			user.setSocialName(socialName);
			
			token = jwtTokenUtil.generateToken(user, null);
			
			user.setToken(token);
			
			this.userInfoDAO.save(user).getUserId();
			
			List<UserInfo> userInfo2 = 
					this.userInfoDAO
					.findBySocialIdAndEmailAndUseYn(
							oauthId
							,email
							, "Y");
			userInfo = userInfo2;
		}else {
			newYn = "N";
			token = jwtTokenUtil.generateToken(userInfo.get(0), null);
			
			if("".equals(userInfo.get(0).getLoginId()) || userInfo.get(0).getLoginId() == null) {
				loginId = UUID.randomUUID().toString();
				loginPw = UUID.randomUUID().toString();
				
				userInfo.get(0).setLoginId(loginId);
				userInfo.get(0).setPassword(passwordEncoder.encode(loginPw));
			}
			
			userInfo.get(0).setAccessToken(socialAccessToken);
			userInfo.get(0).setToken(token);
		}
		
		boolean isRegistered = true;
		
		if(userInfo.get(0).getPushId() == null || "".equals(userInfo.get(0).getPushId())) {
			isRegistered = false;
		}
		
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO.setUserId(userInfo.get(0).getUserId());
		pointInfoCDTO.setEventCode("002");
		pointInfoCDTO.setEventId("00001");
		this.pointService.addPoint(pointInfoCDTO);
		
		List<PointConfirmDTO> pointConfirmInfo = getPointConfirmInfo(userInfo.get(0).getUserId());
		
		Boolean isConfirmed = false;
		
		if(pointConfirmInfo.size() > 0) {
			if("Y".equals(pointConfirmInfo.get(0).getIsConfirmed())) {
				isConfirmed = true;
			}
		}
		rtnMap.put("isPointConfirmed", isConfirmed);
		
		rtnMap.put("token" , token);
		rtnMap.put("socialAccessToken" , socialAccessToken);
		rtnMap.put("loginId" , userInfo.get(0).getLoginId());
		rtnMap.put("password" , userInfo.get(0).getPassword());
		rtnMap.put("isRegistered" , isRegistered);
		
	return rtnMap;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void addPushid(MarketUserDTO marketUserDTO) {

		List<UserInfo> userInfo = this.userInfoDAO.findByUserId(marketUserDTO.getUserId());
		
		if(userInfo.size() > 0) {
			userInfo.get(0).setPushId(marketUserDTO.getPushId());
		}
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void addSellerFavorite(SellerFavoriteCDTO sellerFavoriteCDTO) {

		Optional<SellerFavoriteInfo> sellerFavoriteInfo = this.sellerFavoriteInfoDAO.findByUserIdAndSellerId(sellerFavoriteCDTO.getUserId(), sellerFavoriteCDTO.getSellerId());
		
		if(sellerFavoriteInfo.isPresent()) {
			//
		}else {
			SellerFavoriteInfo _sellerFavoriteInfo = new SellerFavoriteInfo();
			_sellerFavoriteInfo = modelMapper.map(sellerFavoriteCDTO, SellerFavoriteInfo.class);
			
			this.sellerFavoriteInfoDAO.save(_sellerFavoriteInfo);
		}
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void removeSellerFavorite(SellerFavoriteCDTO sellerFavoriteCDTO) {

		Optional<SellerFavoriteInfo> sellerFavoriteInfo = this.sellerFavoriteInfoDAO.findByUserIdAndSellerId(sellerFavoriteCDTO.getUserId(), sellerFavoriteCDTO.getSellerId());
		
		if(sellerFavoriteInfo.isPresent()) {
			this.sellerFavoriteInfoDAO.delete(sellerFavoriteInfo.get());
		}
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getSellerFavorite(SellerFavoriteCDTO sellerFavoriteCDTO) {
		String sql = this.sellerFavoriteInfoJdbcDAO.sellerFavorite();
		
		SellerFavoriteDTO _sellerFavoriteDTO = new SellerFavoriteDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, sellerFavoriteCDTO.getUserId());
		
		return this.utilService.getQueryString(sql,_sellerFavoriteDTO,_queryValue);
    }
	
	@Transactional(rollbackFor = Exception.class)
	public Map autoSignin(MarketSignupDTO marketSignupDTO) {
		String token = "";
		String accessToken = "";
		String userId = "";
		Map userMap = new HashMap<>();
		Optional<UserInfo> userInfo = this.userInfoDAO.findByLoginIdAndPasswordAndUseYn(
				marketSignupDTO.getLoginId(),marketSignupDTO.getPassword(), "Y");
			
		if(userInfo.isPresent()) {
			token = userInfo.get().getToken();
			accessToken = userInfo.get().getAccessToken();
			userId = userInfo.get().getUserId();
		}else {
			
		}
		userMap.put("token", token);
		userMap.put("userId", userId);
		userMap.put("accessToken", accessToken);
	return userMap;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public String getdomaadoVersion() {
		List<CdDtlInfo> cdDtlInfo = this.cdDtlInfoDAO.findByIdCodeNoAndIdDtlCodeAndUseYn("6","001","Y");
		return cdDtlInfo.get(0).getDtlCodeName();
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getStorePageInfo(StoreInfoCDTO storeInfoCDTO) {
		String _productCategoryCode = storeInfoCDTO.getProductCategoryCode();
		String sql = this.userInfoJdbcDAO.storePageInfo(storeInfoCDTO.getMaxPageCount(), _productCategoryCode);
		//and si.product_category_code = ?
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		ArrayList<String> _queryValue = new ArrayList<>();
		if(!("".equals(_productCategoryCode) || _productCategoryCode == null)) {
			_queryValue.add(0,_productCategoryCode);
		}
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getStoreInfoList(StoreInfoCDTO storeInfoCDTO) {
		String _userId = storeInfoCDTO.getUserId();
		String _productCategoryCode = storeInfoCDTO.getProductCategoryCode();
		String storeInfoListSql = this.userInfoJdbcDAO.storeList(storeInfoCDTO.getPageCount(), storeInfoCDTO.getMaxPageCount(), _userId, _productCategoryCode);
		String storeImgListSql = this.userInfoJdbcDAO.storeImgList();
		
		StoreInfoDTO storeInfo = new StoreInfoDTO();
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		if(_userId != null) {
			_queryValue.add(index, storeInfoCDTO.getUserId());
			index++;
		}
		
		if(!("".equals(_productCategoryCode) || _productCategoryCode == null)) {
			_queryValue.add(index,_productCategoryCode);
			index++;
		}
		
		List<StoreInfoDTO> storeInfoList = this.utilService.getQueryString(storeInfoListSql,storeInfo,_queryValue);
		
		int _cnt = storeInfoList.size();
		
		StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
		StoreInfoPDTO storeInfoPDTO = new StoreInfoPDTO();
		ArrayList<StoreInfoPDTO> productInfoList = new ArrayList<StoreInfoPDTO>();
		StorethumbnailImagePathInfoDTO storethumbnailImagePathInfoDTO = new StorethumbnailImagePathInfoDTO();
		String _sellerFavorite = "";
		Boolean isFavorite = true;
		
		ArrayList<String> _queryValue2 = new ArrayList<>();
		
		for(int i = 0; i < _cnt; i++) {
			storeInfoDTO = new StoreInfoDTO();
			storeInfoPDTO = new StoreInfoPDTO();
			storeInfoDTO = storeInfoList.get(i);
			
			_sellerFavorite = storeInfoDTO.getIsFavoriteSeller();
			
			storeInfoPDTO.setProductCategoryCode(storeInfoDTO.getProductCategoryCode());
			storeInfoPDTO.setShopName(storeInfoDTO.getShopName());
			storeInfoPDTO.setSellerId(storeInfoDTO.getSellerId());
			
			if("Y".equals(_sellerFavorite)) {
				isFavorite = true;
			}else {
				isFavorite = false;
			}
			storeInfoPDTO.setIsFavoriteSeller(isFavorite);
			
			productInfoList.add(i,storeInfoPDTO);
			
			storethumbnailImagePathInfoDTO = new StorethumbnailImagePathInfoDTO();
			_queryValue2 = new ArrayList<>();
			
			_queryValue2.add(0, "01");
			_queryValue2.add(1, "Y");
			_queryValue2.add(2, storeInfoDTO.getSellerId());
			_queryValue2.add(3, "Y");
			
			List<StorethumbnailImagePathInfoDTO> storethumbnailImagePathInfoList = this.utilService.getQueryString(storeImgListSql,storethumbnailImagePathInfoDTO,_queryValue2);

			productInfoList.get(i).setStoreThumbnailImagePathList((ArrayList<StorethumbnailImagePathInfoDTO>) storethumbnailImagePathInfoList);
			
		}
		
		return productInfoList;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public List getSearchWord(SearchWordCDTO searchWordCDTO) {
		int listCnt = 0;
		
		if(searchWordCDTO.getWordListCount() != null) {
			listCnt = searchWordCDTO.getWordListCount();
		}
		
		if(listCnt == 0) {
			listCnt = 5;
		}
		String _listCnt = Integer.toString(listCnt);
		String sql = this.commonJdbcDAO.selectCdDtlInfo(_listCnt);
		
		//and si.product_category_code = ?
		SearchWordDTO searchWordDTO = new SearchWordDTO();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "14");
		
		List<SearchWordDTO> searchWordList = this.utilService.getQueryString(sql,searchWordDTO,_queryValue);
		
		return searchWordList;
    }
	
	@Transactional(rollbackFor = Exception.class)
	public void setSearchWord() {
		String sql = this.userInfoJdbcDAO.searchWord();
		String removeSearchWord = this.commonJdbcDAO.removeCdDtlInfo();
		String addSearchWord = this.commonJdbcDAO.insertCdDtlInfo();
	
		//and si.product_category_code = ?
		SearchWordDTO searchWordDTO = new SearchWordDTO();
		ArrayList<String> _queryValue = new ArrayList<>();
		
		List<SearchWordDTO> searchWordList = this.utilService.getQueryString(sql,searchWordDTO,_queryValue);
		
		if(searchWordList.size() > 0) {
			Comparator<SearchWordDTO> compareByCnt = Comparator.comparing( SearchWordDTO::getSearchCount );
			
			List<SearchWordDTO> sortedList = searchWordList.stream()
					.sorted(compareByCnt.reversed())
					.collect(Collectors.toList());
			
			//코드삭제
			ArrayList<String> _removeQueryValue = new ArrayList<>();
			_removeQueryValue.add(0,"14");
			this.utilService.getQueryStringUpdate(removeSearchWord, _removeQueryValue);
			//코드등록
			int cnt = sortedList.size();
			String searchCntText = "";
			ArrayList<String> _addQueryValue = new ArrayList<>();
			for(int i = 0; i < cnt; i++) {
				searchCntText = Integer.toString(i + 1);
				
				_addQueryValue = new ArrayList<>();
				_addQueryValue.add(0, "14");
				_addQueryValue.add(1, StringUtils.leftPad(searchCntText, 5, "0"));
				_addQueryValue.add(2, sortedList.get(i).getSearchWord());
				_addQueryValue.add(3, sortedList.get(i).getSearchCount().toString());
				_addQueryValue.add(4, "Y");
				
				this.utilService.getQueryStringUpdate(addSearchWord, _addQueryValue);
			}
		}
		
    }
	
	public List getPointConfirmInfo(String userId) {
		String _query = this.userInfoJdbcDAO.pointConfirmInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, userId);
		_queryValue.add(1, "15");
		_queryValue.add(2, "Y");
		
		PointConfirmDTO pointConfirmDTO = new PointConfirmDTO();
		
		return this.utilService.getQueryString(_query, pointConfirmDTO, _queryValue);
    }
}