package com.lawzone.market.admin.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dao.AdminJdbcDAO;
import com.lawzone.market.admin.dao.ProductOrderItemBookIdInfoDAO;
import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.dto.user.AdminUserDTO;
import com.lawzone.market.common.service.BoilerplateInfo;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.DoobalHeroUtils;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderItemInfoId;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GarakAdminService {
	private final ModelMapper modelMapper;
	private final ProductOrderService productOrderService;
	private final DoobalHeroUtils doobalHeroUtils;
	private final TelmsgLogService telmsgLogService;
	private final ProductOrderItemBookIdInfoDAO productOrderItemBookIdInfoDAO;
	private final AdminJdbcDAO adminJdbcDAO;
	private final UtilService utilService;
	private final UserInfoDAO userInfoDAO;
	private final SellerInfoDAO sellerInfoDAO;
	private final PasswordEncoder passwordEncoder;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional(rollbackFor = Exception.class)
	public String addBookId(AdminOrderCDTO adminOrderCDTO) throws ClientProtocolException, IOException {
		String _rtnMsg = "";
		//주문정보
		List<CustOrderInfoDTO> custOrderInfo = this.productOrderService.getCustOrderInfoByOrderNo(adminOrderCDTO.getOrderNo(), "");
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getAdminOrderItemList(adminOrderCDTO.getOrderNo(), adminOrderCDTO.getProductId(), "");
		
		Map<Object, Object> req = new HashMap<>();
		Map<Object, Object> res = new HashMap<>();
		
		ProductOrderItemBookIdInfoDTO productOrderItemBookIdInfoDTO = new ProductOrderItemBookIdInfoDTO();
		ProductOrderItemBookIdInfo productOrderItemBookIdInfo = new ProductOrderItemBookIdInfo();
		ProductOrderItemBookIdInfoId productOrderItemBookIdInfoId = new ProductOrderItemBookIdInfoId();
		int _cnt = custOrderItemList.get(0).getProductCount().intValue();
		
		for (int i = 0; i < _cnt; i++ ) {
			req = new HashMap<>();
			
			req.put("spotCode", adminOrderCDTO.getSpotId());
			req.put("receiverName", custOrderInfo.get(0).getRecipientName());
			req.put("receiverMobile", custOrderInfo.get(0).getPhoneNumber());
			req.put("receiverAddress", custOrderInfo.get(0).getAddress() + " " + custOrderInfo.get(0).getDetailAddress());
			req.put("receiverAddressPostalCode", custOrderInfo.get(0).getZonecode());
			req.put("productName", custOrderItemList.get(0).getProductName());
			req.put("memoFromCustomer", custOrderInfo.get(0).getDeliveryMessage());
			req.put("productPrice", custOrderItemList.get(0).getProductPrice());
			req.put("orderIdFromCorp", custOrderInfo.get(0).getOrderNo() + "_" + ( i + 0));
			req.put("print", "r");
			
			res = new HashMap<>();
			this.telmsgLogService.addTelmsgLog("01", "01", "1", req);
			res = this.doobalHeroUtils.getDeliveryReception(req);
			this.telmsgLogService.addTelmsgLog("02", "01", "2", res);
			if(res.get("statusCode") == null) {
				productOrderItemBookIdInfoDTO = new ProductOrderItemBookIdInfoDTO();
				productOrderItemBookIdInfo = new ProductOrderItemBookIdInfo();
				productOrderItemBookIdInfoId = new ProductOrderItemBookIdInfoId();
				
				productOrderItemBookIdInfoDTO.setBookId((String) res.get("bookId"));
				productOrderItemBookIdInfoDTO.setOrderNo(custOrderInfo.get(0).getOrderNo());
				productOrderItemBookIdInfoDTO.setProductId(custOrderItemList.get(0).getProductId());
				productOrderItemBookIdInfoDTO.setOrderIdFromCorp((String) res.get("orderIdFromCorp"));
				productOrderItemBookIdInfoDTO.setReceiverAddress((String) res.get("receiverAddress"));
				productOrderItemBookIdInfoDTO.setReceiverAddressBuilding((String) res.get("receiverAddressBuilding"));
				productOrderItemBookIdInfoDTO.setReceiverAddressCleaned((String) res.get("receiverAddressCleaned"));
				productOrderItemBookIdInfoDTO.setReceiverAddressRoadCleaned((String) res.get("receiverAddressRoadCleaned"));
				productOrderItemBookIdInfoDTO.setReceiverAddressRoadDetail((String) res.get("receiverAddressRoadDetail"));
				productOrderItemBookIdInfoDTO.setDongGroup((String) res.get("dongGroup"));
				productOrderItemBookIdInfoDTO.setPlacePageUrl((String) res.get("placePageUrl"));
				
				productOrderItemBookIdInfoId = modelMapper.map(productOrderItemBookIdInfoDTO, ProductOrderItemBookIdInfoId.class);
				productOrderItemBookIdInfo = modelMapper.map(productOrderItemBookIdInfoDTO, ProductOrderItemBookIdInfo.class);
				productOrderItemBookIdInfo.setId(productOrderItemBookIdInfoId);
				
				this.productOrderItemBookIdInfoDAO.save(productOrderItemBookIdInfo);
			}else {
				_rtnMsg = (String) res.get("message");
			}
		}
		return _rtnMsg;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getBookIdList(AdminOrderCDTO adminOrderCDTO) throws ClientProtocolException, IOException {
		return this.productOrderItemBookIdInfoDAO.findByIdOrderNoAndIdProductId(adminOrderCDTO.getOrderNo(), adminOrderCDTO.getProductId());
	}
	
	public List getAdminUserListPageInfo(AdminUserCDTO adminUserCDTO) {
		String sql = this.adminJdbcDAO.adminUserListPageing(adminUserCDTO);
		String _sellerYn = adminUserCDTO.getSellerYn();
		String _useYn = adminUserCDTO.getUseYn();
		String _userName = adminUserCDTO.getUserName();
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		
		if(!"%".equals(_sellerYn)) {
			_queryValue.add(_queryValueIdx, _sellerYn);
			_queryValueIdx++;
		}
		
		if(!"%".equals(_useYn)) {
			_queryValue.add(_queryValueIdx, _useYn);
			_queryValueIdx++;
		}

		if(!(_userName == null || "".equals(_userName))) {
			_queryValue.add(_queryValueIdx, _userName);
			_queryValueIdx++;
		}
		
		return this.utilService.getQueryString(sql,adminPageInfoDTO,_queryValue);
	}
	
	public List getAdminUserList(AdminUserCDTO adminUserCDTO) {
		String sql = this.adminJdbcDAO.adminUserList(adminUserCDTO);
		String _sellerYn = adminUserCDTO.getSellerYn();
		String _useYn = adminUserCDTO.getUseYn();
		String _userName = adminUserCDTO.getUserName();
		
		AdminUserDTO adminUserDTO = new AdminUserDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		
		if(!"%".equals(_sellerYn)) {
			_queryValue.add(_queryValueIdx, _sellerYn);
			_queryValueIdx++;
		}
		
		if(!"%".equals(_useYn)) {
			_queryValue.add(_queryValueIdx, _useYn);
			_queryValueIdx++;
		}

		if(!(_userName == null || "".equals(_userName))) {
			_queryValue.add(_queryValueIdx, _userName);
			_queryValueIdx++;
		}
		
		return this.utilService.getQueryString(sql,adminUserDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyUserInfo(AdminUserDTO adminUserDTO) throws ClientProtocolException, IOException {
		//유저정보 수정
		String sql = this.adminJdbcDAO.modifyAdminUserInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, adminUserDTO.getUserLvl());
		_queryValue.add(1, adminUserDTO.getSellerYn().toString());
		_queryValue.add(2, adminUserDTO.getUseYn().toString());
		_queryValue.add(3, sessionBean.getUserId());
		_queryValue.add(4, adminUserDTO.getSellerId());
		
		this.utilService.getQueryStringUpdate(sql, _queryValue);
		//판매자정보 수정
		
		//비밀번호 체크
		if(adminUserDTO.getPasswordValue() == null || "".equals(adminUserDTO.getPasswordValue())) {
			adminUserDTO.setPassword("");
		}else {
			adminUserDTO.setPassword(passwordEncoder.encode(adminUserDTO.getPasswordValue()));
		}

		if("Y".equals(adminUserDTO.getSellerYn().toString())) {
			Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(adminUserDTO.getSellerId());
			
			if(sellerInfo.isPresent()) {
				String sql2 = this.adminJdbcDAO.modifyAdminSellerInfo();
				ArrayList<String> _queryValue2 = new ArrayList<>();
				_queryValue2.add(0, adminUserDTO.getShopName());
				_queryValue2.add(1, adminUserDTO.getLoginId());
				_queryValue2.add(2, adminUserDTO.getPassword());
				_queryValue2.add(3, adminUserDTO.getPassword());
				_queryValue2.add(4, adminUserDTO.getSpotId());
				_queryValue2.add(5, sessionBean.getUserId());
				_queryValue2.add(6, adminUserDTO.getSellerId());
				
				this.utilService.getQueryStringUpdate(sql2, _queryValue2);
			}else {
				SellerInfo _sellerInfo = new SellerInfo();
				_sellerInfo = modelMapper.map(adminUserDTO, SellerInfo.class);
				
				this.sellerInfoDAO.save(_sellerInfo);
			}
		}else {
			this.sellerInfoDAO.deleteById(adminUserDTO.getSellerId());
		}
	}
}
