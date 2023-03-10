package com.lawzone.market.admin.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.lawzone.market.admin.dao.ProductOrderItemBookIdInfoJdbcDAO;
import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.order.BookIdBasicInfoDTO;
import com.lawzone.market.admin.dto.order.BookIdListInfoDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.dto.user.AdminUserDTO;
import com.lawzone.market.admin.dto.user.BoilerplateDTO;
import com.lawzone.market.common.dao.BoilerplateInfoJdbcDAO;
import com.lawzone.market.common.dao.ExternalLinkInfoDAO;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.common.service.ExternalLinkInfo;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.DoobalHeroUtils;
import com.lawzone.market.externalLink.util.TodayUtils;
import com.lawzone.market.order.dao.ProductOrderItemInfoDAO;
import com.lawzone.market.order.dao.ProductOrderJdbcDAO;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.product.dao.ProductTagDAO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.product.service.ProductTagInfo;
import com.lawzone.market.product.service.ProductTagInfoDTO;
import com.lawzone.market.product.service.ProductTagInfoId;
import com.lawzone.market.review.dao.ProductReviewInfoJdbcDAO;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.user.service.SellerInfoService;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class GarakAdminService {
	private final ModelMapper modelMapper;
	private final ProductTagDAO productTagDAO;
	private final ProductOrderService productOrderService;
	private final ProductService productService;
	private final DoobalHeroUtils doobalHeroUtils;
	private final TelmsgLogService telmsgLogService;
	private final SellerInfoService sellerInfoService;
	private final ProductOrderItemBookIdInfoDAO productOrderItemBookIdInfoDAO;
	private final AdminJdbcDAO adminJdbcDAO;
	private final UtilService utilService;
	private final UserInfoDAO userInfoDAO;
	private final SellerInfoDAO sellerInfoDAO;
	private final ProductOrderItemInfoDAO productOrderItemInfoDAO;
	private final PasswordEncoder passwordEncoder;
	private final ProductOrderItemBookIdInfoJdbcDAO productOrderItemBookIdInfoJdbcDAO;
	private final BoilerplateInfoJdbcDAO boilerplateInfoJdbcDAO;
	private final ProductReviewInfoJdbcDAO productReviewInfoJdbcDAO;
	private final ProductOrderJdbcDAO productOrderJdbcDAO;
	private final TodayUtils todayUtils;
	private final CommonService commonService;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional(rollbackFor = Exception.class)
	public String addBookId(AdminOrderCDTO adminOrderCDTO) throws ClientProtocolException, IOException {
		String _rtnMsg = "";
		//주문정보
		List<CustOrderInfoDTO> custOrderInfo = this.productOrderService.getCustOrderInfoByOrderNo(adminOrderCDTO.getOrderNo(), "", "","N");
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
			this.telmsgLogService.addTelmsgLog("02", "01", "1", req,"");
			res = this.doobalHeroUtils.getDeliveryReception(req);
			this.telmsgLogService.addTelmsgLog("02", "01", "2", res,"");
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
	public String modifyUserInfo(AdminUserDTO adminUserDTO, String userId) throws ClientProtocolException, IOException {
		//유저정보 수정
		String _rtnMsg = "";
		String sql = this.adminJdbcDAO.modifyAdminUserInfo();
		String sellerId = adminUserDTO.getSellerId();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, adminUserDTO.getUserLvl());
		_queryValue.add(1, adminUserDTO.getSellerYn().toString());
		_queryValue.add(2, adminUserDTO.getUseYn().toString());
		_queryValue.add(3, userId);
		_queryValue.add(4, adminUserDTO.getSellerId());
		
		this.utilService.getQueryStringUpdate(sql, _queryValue);
		//판매자정보 수정
		
		//비밀번호 체크
		if(adminUserDTO.getPasswordValue() == null || "".equals(adminUserDTO.getPasswordValue())) {
			adminUserDTO.setPassword("");
		}else {
			adminUserDTO.setPassword(passwordEncoder.encode(adminUserDTO.getPasswordValue()));
		}
		Optional<SellerInfo> _sellerLoginInfo = this.sellerInfoDAO.findByLoginId(adminUserDTO.getLoginId());
		if(_sellerLoginInfo.isPresent() && !_sellerLoginInfo.get().getSellerId().equals(sellerId)) {
			_rtnMsg = "사용중인 ID 입니다.";
		}else {
			if(adminUserDTO.getSpotId() == null || "".equals(adminUserDTO.getSpotId())) {
				adminUserDTO.setSpotId("");
			}
			
			Optional<SellerInfo> _sellerSpotIdInfo = this.sellerInfoDAO.findBySpotId(adminUserDTO.getSpotId());
			if(_sellerSpotIdInfo.isPresent() && !_sellerSpotIdInfo.get().getSellerId().equals(sellerId)) {
				_rtnMsg = "사용중인 SPOTID 입니다.";
			}else {
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
						_queryValue2.add(5, adminUserDTO.getSpotName());
						_queryValue2.add(6, adminUserDTO.getBusinessAddress());
						_queryValue2.add(7, adminUserDTO.getSellerPhoneNumber());
						_queryValue2.add(8, adminUserDTO.getProductCategoryCode());
						_queryValue2.add(9, userId);
						_queryValue2.add(10, adminUserDTO.getSellerId());
						
						this.utilService.getQueryStringUpdate(sql2, _queryValue2);
					}else {
						SellerInfo _sellerInfo = new SellerInfo();
						_sellerInfo = modelMapper.map(adminUserDTO, SellerInfo.class);
						
						this.sellerInfoDAO.save(_sellerInfo);
					}
				}else {
					Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(adminUserDTO.getSellerId());
					if(sellerInfo.isPresent()) {
						this.sellerInfoDAO.deleteById(adminUserDTO.getSellerId());
					}
				}
			_rtnMsg = "저장되었습니다.";
			}
		}
		return _rtnMsg;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String getorderInfoConfirmAll(AdminOrderCDTO adminOrderCDTO) throws ClientProtocolException, IOException {
		String _sellerIdYn = "Y";
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _sellerId = adminOrderCDTO.getSellerId();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.adminJdbcDAO.adminBookIdBasicInfo( _sellerIdYn);
		
		BookIdBasicInfoDTO bookIdBasicInfoDTO = new BookIdBasicInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, "100");
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, "0");
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		_queryValue.add(_queryValueIdx, "100");
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, "0");
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		List<BookIdBasicInfoDTO> bookIdBasicInfoList = this.utilService.getQueryString(sql,bookIdBasicInfoDTO,_queryValue);
		
		ProductOrderItemBookIdInfoDTO productOrderItemBookIdInfoDTO = new ProductOrderItemBookIdInfoDTO();
		ProductOrderItemBookIdInfo productOrderItemBookIdInfo = new ProductOrderItemBookIdInfo();
		ProductOrderItemBookIdInfoId productOrderItemBookIdInfoId = new ProductOrderItemBookIdInfoId();
		
		int bookCnt = bookIdBasicInfoList.size();
		int bookItemCnt = 0;
		
		if(bookCnt > 0) {
			for(int i = 0; i < bookCnt; i++) {
				bookItemCnt = bookIdBasicInfoList.get(i).getProductCount().intValue();
				
				for(int j = 0; j < bookItemCnt; j++) {
					productOrderItemBookIdInfoDTO = new ProductOrderItemBookIdInfoDTO();
					productOrderItemBookIdInfo = new ProductOrderItemBookIdInfo();
					productOrderItemBookIdInfoId = new ProductOrderItemBookIdInfoId();
					
					productOrderItemBookIdInfoDTO.setOrderNo(bookIdBasicInfoList.get(i).getOrderNo());
					productOrderItemBookIdInfoDTO.setProductId(bookIdBasicInfoList.get(i).getProductId());
					productOrderItemBookIdInfoDTO.setOrderIdFromCorp(bookIdBasicInfoList.get(i).getOrderNo() + "_" + ( j + 1));
					
					productOrderItemBookIdInfoDTO.setRecipientName(bookIdBasicInfoList.get(i).getRecipientName());
					productOrderItemBookIdInfoDTO.setPhoneNumber(bookIdBasicInfoList.get(i).getPhoneNumber());
					
					productOrderItemBookIdInfoDTO.setAddress(bookIdBasicInfoList.get(i).getAddress());
					productOrderItemBookIdInfoDTO.setZonecode(bookIdBasicInfoList.get(i).getZonecode());
					productOrderItemBookIdInfoDTO.setDeliveryMessage(bookIdBasicInfoList.get(i).getDeliveryMessage());
					productOrderItemBookIdInfoDTO.setProductPrice(bookIdBasicInfoList.get(i).getProductPrice());
					productOrderItemBookIdInfoDTO.setProductCount(bookIdBasicInfoList.get(i).getProductCount());
					productOrderItemBookIdInfoDTO.setProductName(bookIdBasicInfoList.get(i).getProductName());
					productOrderItemBookIdInfoDTO.setOrderDate(bookIdBasicInfoList.get(i).getOrderDate());
					productOrderItemBookIdInfoDTO.setPrintYn("N");
					productOrderItemBookIdInfoDTO.setDeliveryStateCode("100");
					productOrderItemBookIdInfoDTO.setSellerId(bookIdBasicInfoList.get(i).getSellerId());
					productOrderItemBookIdInfoDTO.setProductCategoryCode(bookIdBasicInfoList.get(i).getProductCategoryCode());
					
					productOrderItemBookIdInfoId = modelMapper.map(productOrderItemBookIdInfoDTO, ProductOrderItemBookIdInfoId.class);
					productOrderItemBookIdInfo = modelMapper.map(productOrderItemBookIdInfoDTO, ProductOrderItemBookIdInfo.class);
					productOrderItemBookIdInfo.setId(productOrderItemBookIdInfoId);
					
					this.productOrderItemBookIdInfoDAO.save(productOrderItemBookIdInfo);
				}
			}
			String updateSql = this.adminJdbcDAO.adminOrderStatConfirmAllUpdate( _sellerIdYn);
			
			int _queryValueIdx2 = 0;
			ArrayList<String> _queryValue2 = new ArrayList<>();
			_queryValue2.add(_queryValueIdx2, "200");
			_queryValueIdx2++;
			_queryValue2.add(_queryValueIdx2, "100");
			_queryValueIdx2++;
			_queryValue2.add(_queryValueIdx2, "100");
			_queryValueIdx2++;
			_queryValue2.add(_queryValueIdx2, _beginDate);
			_queryValueIdx2++;
			_queryValue2.add(_queryValueIdx2, _endDate);
			_queryValueIdx2++;		
			if("Y".equals(_sellerIdYn)) {
				_queryValue2.add(_queryValueIdx2, _sellerId);
				_queryValueIdx2++;
			}
			this.utilService.getQueryStringUpdate(updateSql, _queryValue2);
			return "등록 되었습니다";
		}
		
		return "접수건이 없습니다.";
	}
	
	public List getAdminBookIdList(AdminOrderCDTO adminOrderCDTO) throws ClientProtocolException, IOException, ParseException {
		String _sellerIdYn = "Y";
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();
		String _deliverYn = adminOrderCDTO.getDeliverYn();
		String _img = "";
		
		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderItemBookIdInfoJdbcDAO.adminBookIdList(_sellerIdYn, _deliverYn, _page);
		
		BookIdListInfoDTO bookIdListInfoDTO = new BookIdListInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		List<BookIdListInfoDTO> bookIdListInfoList = this.utilService.getQueryString(sql,bookIdListInfoDTO,_queryValue);
		
		String access_token = this.commonService.getTodayAccessToken();
		Map req = new HashMap<>();
		req.put("access_token",access_token);
		
		int _cnt = bookIdListInfoList.size();
		
		for(int i = 0; i < _cnt; i++) {
			if(bookIdListInfoList.get(i).getDeliveryOrderId() != null) {
				req.put("order_id",bookIdListInfoList.get(i).getDeliveryOrderId());
				_img = this.todayUtils.getDeliveryInvoiceImeag(req);
				
				bookIdListInfoList.get(i).setImgBase64("data:image/jpeg;base64," + _img);
			}
		}
		
		return bookIdListInfoList;
	}
	
	public List getAdminBookIdListPageInfo(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _userName = adminOrderCDTO.getSearchValue();
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _searchGb = adminOrderCDTO.getSearchGb();
		String _statCd = adminOrderCDTO.getOrderStatCode();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();
		String _deliverYn = adminOrderCDTO.getDeliverYn();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderItemBookIdInfoJdbcDAO.adminBookIdPageInfo(_sellerIdYn, _deliverYn, _page);
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		return this.utilService.getQueryString(sql,adminPageInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addBookIdList(List<BookIdListInfoDTO> bookIdList, AdminOrderCDTO adminOrderCDTO)throws ClientProtocolException, IOException, ParseException {
		String _rtnMsg = "";
		Map<Object, Object> req = new HashMap<>();
		Map<Object, Object> res = new HashMap<>();
		Map listMap = new HashMap<>();
		
		int _cnt = bookIdList.size();
		
		String _orderIdFromCorp = "";
		String _orderNo = "";
		String _productId = "";
		String _bookId = "";
		String _todayDeliveryStandardTime = "";
		
		//영업일여부
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
		Date date = new Date();
		String _date = sf.format(date);
		String _dateArray[] =  _date.split(" ");
		
		String _toDate = _dateArray[0];
		String _toTime = _dateArray[1];
		int _time = Integer.parseInt(_toTime);
		int _todayDeliveryTime = 1030;
		String slsDayYn = this.utilService.getSlsDayYn(_toDate);
		String _spotId = "";
		String _spotName = "";
		String _zondcode = "";
		String _businessAddress = "";
		String _sellerPhoneNumber = "";
		String _shopName = "";
		String _deliveryTime = "";
		String _productCategoryCode = "";
		String _sellerId = "";
		String _phoneNumber = "";
		String _recipientName = "";
		String _categoryName = "";
		String _orderDttm = "";
		String resultMessage = "";
		BigDecimal deliveryAmount = new BigDecimal(0);
		BigDecimal amountZero = new BigDecimal(0);
		String _sql = this.productOrderItemBookIdInfoJdbcDAO.adminBookIdSellerInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		
		AdminBookIdSellerInfoDTO adminBookIdSellerInfoDTO = new AdminBookIdSellerInfoDTO();
		
		String access_token = this.commonService.getTodayAccessToken();
		req.put("access_token",access_token);
		
		Map shippingMap = this.todayUtils.getDeliveryShippingList(req);
		
		List shippingList = (List) shippingMap.get("shipping_places");
		
		Map shippinInfo = (Map) shippingList.get(0);
		
		for (int i = 0; i < _cnt; i++ ) {
			listMap = (Map) bookIdList.get(i);
			_bookId = (String) listMap.get("bookId");
			
			if(!"".equals(_bookId)) {
				continue;
			}
			
			_todayDeliveryStandardTime = (String) listMap.get("todayDeliveryStandardTime");
			//_todayDeliveryTime = Integer.parseInt(_todayDeliveryStandardTime);
			
			_orderIdFromCorp = (String) listMap.get("orderIdFromCorp");
			_orderNo = (String) listMap.get("orderNo");
			_productId = (String) listMap.get("productId");
			_productCategoryCode = (String) listMap.get("productCategoryCode");
			_sellerId = (String) listMap.get("sellerId");
			_queryValue = new ArrayList<>();
			_queryValue.add(0, _orderNo);
			_queryValue.add(1, _productId);

			List<AdminBookIdSellerInfoDTO> adminBookIdSellerInfo = this.utilService.getQueryString(_sql,adminBookIdSellerInfoDTO,_queryValue);
			
			_spotId = adminBookIdSellerInfo.get(0).getSpotId();
			_spotName = adminBookIdSellerInfo.get(0).getSpotName();
			_businessAddress = adminBookIdSellerInfo.get(0).getBusinessAddress();
			_sellerPhoneNumber = adminBookIdSellerInfo.get(0).getSellerPhoneNumber();
			_shopName = adminBookIdSellerInfo.get(0).getShopName();
			_deliveryTime = adminBookIdSellerInfo.get(0).getDeliveryTime();
			_phoneNumber = adminBookIdSellerInfo.get(0).getPhoneNumber();
			_recipientName = adminBookIdSellerInfo.get(0).getRecipientName();
			_zondcode = adminBookIdSellerInfo.get(0).getZonecode();
			_categoryName = adminBookIdSellerInfo.get(0).getCategoryName();
			_orderDttm = adminBookIdSellerInfo.get(0).getOrderDttm();
			deliveryAmount = adminBookIdSellerInfo.get(0).getDeliveryAmount();
			
			//_time = Integer.parseInt(_deliveryTime);
			resultMessage = "";
			if("".equals(_spotId) || _spotId == null) {
				//수거지 ID가 없음
				resultMessage = "수거지 정보가 없습니다.";
			}else {
				if( _todayDeliveryTime >= _time || adminOrderCDTO.getUserLvl() >= 6) {
					req = new HashMap<>();
					//두발 히어로
//					req.put("spotCode", _spotId);
//					req.put("receiverName", _recipientName);
//					req.put("receiverMobile", _phoneNumber);
//					req.put("receiverAddress", listMap.get("address"));
//					req.put("receiverAddressPostalCode", listMap.get("zonecode"));
//					req.put("productName", listMap.get("productName"));
//					req.put("memoFromCustomer", listMap.get("deliveryMessage"));
//					req.put("productPrice", listMap.get("productPrice"));
//					req.put("orderIdFromCorp", listMap.get("orderIdFromCorp"));
//					req.put("print", "r");
					
					req.put("sendName", _spotName);
					req.put("sendPhone", _sellerPhoneNumber);
					req.put("sendAddress", _businessAddress);
					req.put("sendPostalCode", _zondcode);
					
					req.put("receiverName", _recipientName);
					req.put("receiverPhone", _phoneNumber);
					req.put("receiverAddress", listMap.get("address"));
					req.put("receiverPostalCode", listMap.get("zonecode"));
					req.put("receiverMemo", listMap.get("deliveryMessage"));
					
					req.put("productName", listMap.get("productName"));
					req.put("productCategory", _categoryName);
					req.put("productPrice", listMap.get("productPrice"));
					req.put("productSellerName", _spotName);
					req.put("orderNo", _orderNo);
					req.put("orderDttm", _orderDttm);
					req.put("shippingPlaceId", shippinInfo.get("id"));
					req.put("access_token",access_token);
					
					res = new HashMap<>();
					this.telmsgLogService.addTelmsgLog("02", "00", "1", req,"");
					//res = this.doobalHeroUtils.getDeliveryReception(req);
					res = this.todayUtils.addDeliveryReception(req);
					this.telmsgLogService.addTelmsgLog("02", "00", "2", res,"");
					//두발 히어로
					//if(res.get("statusCode") == null) {
					if(res.get("order_id") != null) {
						Optional<ProductOrderItemBookIdInfo> productOrderItemBookIdInfo 
							= this.productOrderItemBookIdInfoDAO.findByIdOrderIdFromCorpAndIdOrderNoAndIdProductId(_orderIdFromCorp, _orderNo, _productId);
						
						if(productOrderItemBookIdInfo.isPresent()) {
							Map todayReceiverMap = new HashMap<>();
							Map todayReceiverAddressMap = new HashMap<>(); 
							todayReceiverMap = (Map) res.get("receiver");
							todayReceiverAddressMap = (Map) todayReceiverMap.get("address");
							//두발 히어로
//							productOrderItemBookIdInfo.get().setBookId((String) res.get("bookId"));
//							productOrderItemBookIdInfo.get().setReceiverAddress((String) res.get("receiverAddress"));
//							productOrderItemBookIdInfo.get().setReceiverAddressBuilding((String) res.get("receiverAddressBuilding"));
//							productOrderItemBookIdInfo.get().setReceiverAddressCleaned((String) res.get("receiverAddressCleaned"));
//							productOrderItemBookIdInfo.get().setReceiverAddressRoadCleaned((String) res.get("receiverAddressRoadCleaned"));
//							productOrderItemBookIdInfo.get().setReceiverAddressRoadDetail((String) res.get("receiverAddressRoadDetail"));
//							productOrderItemBookIdInfo.get().setDongGroup((String) res.get("dongGroup"));
//							productOrderItemBookIdInfo.get().setPlacePageUrl((String) res.get("placePageUrl"));
							
							productOrderItemBookIdInfo.get().setBookId((String) res.get("invoice_number"));
							productOrderItemBookIdInfo.get().setDeliveryOrderId((String) res.get("order_id"));
							
							productOrderItemBookIdInfo.get().setReceiverAddress((String) todayReceiverMap.get("raw_address"));
							productOrderItemBookIdInfo.get().setReceiverAddressBuilding((String) todayReceiverAddressMap.get("region_base_address"));
							productOrderItemBookIdInfo.get().setReceiverAddressCleaned((String) todayReceiverAddressMap.get("region_detail_address"));
							productOrderItemBookIdInfo.get().setReceiverAddressRoadCleaned((String) todayReceiverAddressMap.get("street_base_address"));
							productOrderItemBookIdInfo.get().setReceiverAddressRoadDetail((String) todayReceiverAddressMap.get("street_detail_address"));
							//productOrderItemBookIdInfo.get().setDongGroup((String) res.get("dongGroup"));
							//productOrderItemBookIdInfo.get().setPlacePageUrl((String) res.get("placePageUrl"));
							
							productOrderItemBookIdInfo.get().setSpotName(_shopName);
							productOrderItemBookIdInfo.get().setBusinessAddress(_businessAddress);
							productOrderItemBookIdInfo.get().setSellerPhoneNumber(_sellerPhoneNumber);
							productOrderItemBookIdInfo.get().setShopName(_spotName);
							productOrderItemBookIdInfo.get().setResultMessage(resultMessage);
							productOrderItemBookIdInfo.get().setZonecode(_zondcode);
							productOrderItemBookIdInfo.get().setDeliveryStateCode("300");
							//_productCategoryCode
							
							if(deliveryAmount.compareTo(amountZero) == 0) {
								//this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "300");
								this.productOrderService.modifyOrderItemStatInfoDeliveryByProduct(_orderNo, _productId, "300", "300");
							} else {
								//this.productOrderService.modifyOrderItemStatInfo2(_orderNo, _sellerId, "300");
								this.productOrderService.modifyOrderItemStatInfoDeliveryBySellerId(_orderNo, _sellerId, "300", "300");
							}
							
//							if("002000000".equals(_productCategoryCode)) {
//								this.productOrderService.modifyOrderItemStatInfoDeliveryBySellerId(_orderNo, _sellerId, "300", "300");
//							}else {
//								this.productOrderService.modifyOrderItemStatInfoDeliveryByProduct(_orderNo, _productId, "300", "300");
//							}
							
						}
					}else {
						//등록오류
						resultMessage = (String) res.get("message");
					}
				}else {
					//등록 가능시간이 아님
					resultMessage = "당일배송 시간이 아닙니다.( " + _todayDeliveryTime + ")";
				}
			}
			
			if(!"".equals(resultMessage)) {
				Optional<ProductOrderItemBookIdInfo> productOrderItemBookIdInfo 
				= this.productOrderItemBookIdInfoDAO.findByIdOrderIdFromCorpAndIdOrderNoAndIdProductId(_orderIdFromCorp, _orderNo, _productId);
				
				productOrderItemBookIdInfo.get().setResultMessage(resultMessage);
			}
		}
	}
	
	public List getBoilerplateListPageInfo(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _useYn = adminOrderCDTO.getUseYn();
		String _boilerplateName = adminOrderCDTO.getBoilerplateName();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		if(_useYn == null) {
			_useYn = "";
		}
		
		if(_boilerplateName == null) {
			_boilerplateName = "";
		}
		
		String sql = this.boilerplateInfoJdbcDAO.adminBoilerplatePageInfo(_useYn, _sellerIdYn, _boilerplateName, _page);
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		if(!"".equals(_useYn)) {
			_queryValue.add(_queryValueIdx, _useYn);
			_queryValueIdx++;
		}
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if(!"".equals(_boilerplateName)) {
			_queryValue.add(_queryValueIdx, _boilerplateName);
			_queryValueIdx++;
		}

		return this.utilService.getQueryString(sql,adminPageInfoDTO,_queryValue);
	}
	
	public List getBoilerplateList(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _useYn = adminOrderCDTO.getUseYn();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();
		String _boilerplateName = adminOrderCDTO.getBoilerplateName();
		
		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		if(_useYn == null) {
			_useYn = "";
		}
		
		if(_boilerplateName == null) {
			_boilerplateName = "";
		}
		
		String sql = this.boilerplateInfoJdbcDAO.adminBoilerplateList(_useYn, _sellerIdYn, _boilerplateName, _page);
		
		BoilerplateDTO boilerplateDTO = new BoilerplateDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		if(!"".equals(_useYn)) {
			_queryValue.add(_queryValueIdx, _useYn);
			_queryValueIdx++;
		}
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if(!"".equals(_boilerplateName)) {
			_queryValue.add(_queryValueIdx, _boilerplateName);
			_queryValueIdx++;
		}
		return this.utilService.getQueryString(sql,boilerplateDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyBookIdPrintYn(List bookIdPrintList) {
		String sql = this.productOrderItemBookIdInfoJdbcDAO.modifyBookIdPrintYn();
		Map listMap = new HashMap<>();
		int listCnt = bookIdPrintList.size();
		ArrayList<String> _queryValue = new ArrayList<>();
		
		String orderIdFromCorp = "";
		String orderNo = "";
		String productId = "";
		
		for(int i = 0; i < listCnt; i++ ) {
			listMap = (Map) bookIdPrintList.get(i);
			
			orderIdFromCorp = listMap.get("orderIdFromCorp").toString();
			orderNo = listMap.get("orderNo").toString();
			productId = listMap.get("productId").toString();
			
			_queryValue = new ArrayList<>();
			_queryValue.add(0, "Y");
			_queryValue.add(1, orderIdFromCorp);
			_queryValue.add(2, orderNo);
			_queryValue.add(3, productId);
			
			this.utilService.getQueryStringUpdate(sql, _queryValue);
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getBookIdPrintImg(List bookIdPrintList) throws ClientProtocolException, IOException, ParseException {
		String sql = this.productOrderItemBookIdInfoJdbcDAO.modifyBookIdPrintYn();
		Map listMap = new HashMap<>();
		Map req = new HashMap<>();
		int listCnt = bookIdPrintList.size();
		ArrayList<String> _queryValue = new ArrayList<>();
		
		String orderIdFromCorp = "";
		String orderNo = "";
		String deliveryOrderId = "";
		String productId = "";
		String _img = "";
		
		String access_token = this.commonService.getTodayAccessToken();
		req.put("access_token",access_token);
		
		for(int i = 0; i < listCnt; i++ ) {
			listMap = (Map) bookIdPrintList.get(i);
			
			orderIdFromCorp = listMap.get("orderIdFromCorp").toString();
			orderNo = listMap.get("orderNo").toString();
			productId = listMap.get("productId").toString();
			deliveryOrderId = listMap.get("deliveryOrderId").toString();
			
			req.put("order_id",deliveryOrderId);
			_img = this.todayUtils.getDeliveryInvoiceImeag(req);
			
			if(!"".equals(_img)) {
				_queryValue = new ArrayList<>();
				_queryValue.add(0, "Y");
				_queryValue.add(1, orderIdFromCorp);
				_queryValue.add(2, orderNo);
				_queryValue.add(3, productId);
				
				this.utilService.getQueryStringUpdate(sql, _queryValue);
				
				listMap.put("img", "data:image/jpeg;base64," + _img);
			}
			
		}
		return bookIdPrintList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void getDeliveryStaus() throws ClientProtocolException, IOException, ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String _date = sf.format(date);
		String slsDayYn = this.utilService.getSlsDayYn(_date);
		if("N".equals(slsDayYn)) {
			return;
		}
		
		String sql = this.productOrderItemBookIdInfoJdbcDAO.orderDeliveryBookIdInfo();
		Map listMap = new HashMap<>();
		
		OrderDeliveryBookIdInfoDTO orderDeliveryBookIdInfoDTO = new OrderDeliveryBookIdInfoDTO();
		OrderBookIdInfoDTO orderBookIdInfo = new OrderBookIdInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		List<OrderDeliveryBookIdInfoDTO> orderBookIdList = this.utilService.getQueryString(sql, orderDeliveryBookIdInfoDTO, _queryValue);
		int _cnt = orderBookIdList.size();
		
		String _orderIdFromCorp = "";
		String _orderNo = "";
		String _productId = "";
		String _bookId = "";
		String _orderDate = "";
		String _sellerId = "";
		String _productCategoryCode = "";
		BigDecimal _deliveryAmount = new BigDecimal(0);
		BigDecimal _deliveryAmountZero = new BigDecimal(0);
		
		String _sql = this.productOrderJdbcDAO.modifyOrderItemDeliveryInfoStat();
		ArrayList<String> _modifyQueryValue = new ArrayList<>();
		
		String _sqlDeliveryBySellerId = this.productOrderJdbcDAO.modifyOrderItemInfoDeliveryBySellerId();
		String _sqlDeliveryByProduct = this.productOrderJdbcDAO.modifyOrderItemInfoDeliveryByProduct();
		ArrayList<String> _modifyQueryValueDelivery = new ArrayList<>();
		String access_token = this.commonService.getTodayAccessToken();
		
		int _compCnt = 0;
		for(int i = 0; i < _cnt; i++) {
			_orderIdFromCorp = orderBookIdList.get(i).getOrderIdFromCorp();
			_orderNo = orderBookIdList.get(i).getOrderNo();
			_productId = orderBookIdList.get(i).getProductId();
			_bookId = orderBookIdList.get(i).getBookId();
			_orderDate = orderBookIdList.get(i).getOrderDate();
			_deliveryAmount = orderBookIdList.get(i).getDeliveryAmount();
			_sellerId = orderBookIdList.get(i).getSellerId();
			_productCategoryCode = orderBookIdList.get(i).getProductCategoryCode();
			
			if("".equals(_bookId) || _bookId == null) {
				continue;
			}
			
			orderBookIdInfo = new OrderBookIdInfoDTO();
			orderBookIdInfo.setBookId(_bookId);
			orderBookIdInfo.setOrderDate(_orderDate);
			
			Map map = new HashMap<>();
			map.put("bookid", _bookId);
			map.put("orderDate", _orderDate);
			this.telmsgLogService.addTelmsgLog("02", "01", "1", map,"");
			//Map deliveryStausMap = this.doobalHeroUtils.getDeliveryStaus(orderBookIdInfo);
			
			Map req = new HashMap<>();
			req.put("access_token", access_token);
			req.put("invoice_number", _bookId);
			Map deliveryStausMap = this.todayUtils.getDeliveryStatus(req);
			this.telmsgLogService.addTelmsgLog("02", "01", "2", deliveryStausMap,"");
			if(deliveryStausMap.get("error_code") == null) {
				String status = deliveryStausMap.get("state").toString();
				
				if("DELIVERED".equals(status)) {
					_compCnt++;
					_modifyQueryValue = new ArrayList<>();
					_modifyQueryValue.add(0, "400");
					_modifyQueryValue.add(1, _orderIdFromCorp);
					_modifyQueryValue.add(2, _orderNo);
					_modifyQueryValue.add(3, _productId);
					_modifyQueryValue.add(4, "300");
					
					this.utilService.getQueryStringUpdate(_sql, _modifyQueryValue);
					
					_modifyQueryValueDelivery = new ArrayList<>();
					_modifyQueryValueDelivery.add(0, "400");
					_modifyQueryValueDelivery.add(1, _orderNo);
					
					if(_deliveryAmount.compareTo(_deliveryAmountZero) == 0) {
						_modifyQueryValueDelivery.add(2, _productId);
						
						this.utilService.getQueryStringUpdate(_sqlDeliveryByProduct, _modifyQueryValueDelivery);
					} else {
						_modifyQueryValueDelivery.add(2, _sellerId);
						
						this.utilService.getQueryStringUpdate(_sqlDeliveryBySellerId, _modifyQueryValueDelivery);
					}
					
//					z
				}
			}	
		}
		//log.error("배송요청 : " + _cnt + "건") ;
		//log.error("배송완료 : " + _compCnt + "건") ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void setTodayToken() throws ClientProtocolException, IOException {
		Map todayTokenMap = this.todayUtils.getTodayToken();
		
		String accessToken = (String) todayTokenMap.get("access_token");
		String refreshToken = (String) todayTokenMap.get("refresh_token");
		
		String _sql = this.adminJdbcDAO.addExternalLinkInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "00001");
		_queryValue.add(1, accessToken);
		_queryValue.add(2, refreshToken);
		_queryValue.add(3, "99999999");
		_queryValue.add(4, "99999999");
		
		this.utilService.getQueryStringUpdate(_sql, _queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getAdminCustReviewInfoList(AdminOrderCDTO adminOrderCDTO) {
		String sql = this.productReviewInfoJdbcDAO.adminCustReviewInfoList(adminOrderCDTO.getPageCnt(), adminOrderCDTO.getMaxPage());
		String _bfDate = adminOrderCDTO.getOrderDateBf();
		String _afDate = adminOrderCDTO.getOrderDateAf();
		
		CustReviewInfoDTO custReviewInfoDTO = new CustReviewInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _bfDate);
		_queryValue.add(1, _afDate);
		
		return this.utilService.getQueryString(sql,custReviewInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getAdminCustReviewPageInfo(AdminOrderCDTO adminOrderCDTO) {
		String sql = this.productReviewInfoJdbcDAO.adminCustReviewPageInfo(adminOrderCDTO.getPageCnt(), adminOrderCDTO.getMaxPage());
		String _bfDate = adminOrderCDTO.getOrderDateBf();
		String _afDate = adminOrderCDTO.getOrderDateAf();
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _bfDate);
		_queryValue.add(1, _afDate);
		
		return this.utilService.getQueryString(sql,adminPageInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getSettlementList(AdminOrderCDTO adminOrderCDTO) {
		String sql = this.adminJdbcDAO.settlementList();
		
		SettlementListDTO settlementListDTO = new SettlementListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		return this.utilService.getQueryString(sql,settlementListDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String addProductList(AdminOrderCDTO adminOrderCDTO, List<AdminAddProductDTO> adminAddProductDTO) {
		
		Optional<SellerInfo> sellerInfo = this.sellerInfoService.getSellerInfo(adminOrderCDTO.getSellerId());
		
		if(sellerInfo.isEmpty()) {
			return "등록된 판매자가 없습니다.";
		}
		
		String _productCategoryCode = sellerInfo.get().getProductCategoryCode();
		String _productId = "";
		String _tag1 = "";
		String _tag2 = "";
		String _tag3 = "";
		
		Map addProductMap = new HashMap<>();
		int cnt = adminAddProductDTO.size();
		int tagIndex = 0;
		
		ProductDTO productDTO = new ProductDTO();
		
		List<ProductTagInfoDTO> productTagInfoList = new ArrayList<ProductTagInfoDTO>();
		
		ProductTagInfoDTO productTagInfoDTO = new ProductTagInfoDTO();
		
		for(int i = 0; i < cnt; i++) {
			addProductMap = (Map) adminAddProductDTO.get(i);
			
			_tag1 = (String) addProductMap.get("productTag1");
			_tag2 = (String) addProductMap.get("productTag2");
			_tag3 = (String) addProductMap.get("productTag3");
			
			productDTO = new ProductDTO();
			productDTO.setProductName(addProductMap.get("productName").toString());
			productDTO.setProductPrice(new BigDecimal(addProductMap.get("productPrice").toString()));
			productDTO.setProductStock(new BigDecimal(addProductMap.get("productStock").toString()));
			productDTO.setProductWeight(new BigDecimal(addProductMap.get("productWeight").toString()));
			productDTO.setProductDesc(addProductMap.get("productDesc").toString());
			productDTO.setProductCategoryCode(_productCategoryCode);
			productDTO.setSellerId(adminOrderCDTO.getSellerId());
			productDTO.setBeginDate("now()");
			productDTO.setUseYn("Y");
			productDTO.setTodayDeliveryStandardTime("0930");
			
			_productId = this.productService.addProductInfo(productDTO);
			
			tagIndex = 0;
			
			if("Y".equals(_tag1)) {
				productTagInfoDTO = new ProductTagInfoDTO();
				
				productTagInfoDTO.setProductId(_productId);
				productTagInfoDTO.setTagId(new BigInteger("7"));
				
				ProductTagInfo productTagInfo = new ProductTagInfo();
				ProductTagInfoId productTagInfoId = new ProductTagInfoId();
				
				productTagInfoId = modelMapper.map(productTagInfoDTO, ProductTagInfoId.class);
				
				productTagInfo.setProductTagInfoId(productTagInfoId);
				
				this.productTagDAO.save(productTagInfo);
			}
			
			if("Y".equals(_tag2)) {
				productTagInfoDTO = new ProductTagInfoDTO();
				
				productTagInfoDTO.setProductId(_productId);
				productTagInfoDTO.setTagId(new BigInteger("8"));
				
				ProductTagInfo productTagInfo = new ProductTagInfo();
				ProductTagInfoId productTagInfoId = new ProductTagInfoId();
				
				productTagInfoId = modelMapper.map(productTagInfoDTO, ProductTagInfoId.class);
				
				productTagInfo.setProductTagInfoId(productTagInfoId);
				
				this.productTagDAO.save(productTagInfo);
			}
			
			if("Y".equals(_tag3)) {
				productTagInfoDTO = new ProductTagInfoDTO();
				
				productTagInfoDTO.setProductId(_productId);
				productTagInfoDTO.setTagId(new BigInteger("9"));
				
				ProductTagInfo productTagInfo = new ProductTagInfo();
				ProductTagInfoId productTagInfoId = new ProductTagInfoId();
				
				productTagInfoId = modelMapper.map(productTagInfoDTO, ProductTagInfoId.class);
				
				productTagInfo.setProductTagInfoId(productTagInfoId);
				
				this.productTagDAO.save(productTagInfo);
			}
		}
		return "저장되었습니다";
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getProductList(AdminProductCDTO adminProductCDTO) {
		String sql = this.adminJdbcDAO.adminProductList(adminProductCDTO);
		
		AdminProductDTO adminProductDTO = new AdminProductDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		if(adminProductCDTO.getUserLvl() < 5) {
			_queryValue.add(adminProductCDTO.getSellerId());
		}else if(!"000".equals(adminProductCDTO.getProductCategoryCode())){
			_queryValue.add(adminProductCDTO.getProductCategoryCode());
		}
		
		if(!"000".equals(adminProductCDTO.getUseYn())) {
			_queryValue.add(adminProductCDTO.getUseYn());
		}
		
		if(!"000".equals(adminProductCDTO.getImgRegstYn())) {
			_queryValue.add(adminProductCDTO.getImgRegstYn());
		}
		
		return this.utilService.getQueryString(sql,adminProductDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getAdminUserInfo(String portalId) {
		String sql = this.adminJdbcDAO.adminUserInfo();
		
		AdminUserInfoDTO adminUserInfoDTO = new AdminUserInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "Y");
		_queryValue.add(1, portalId);
		
		return this.utilService.getQueryString(sql,adminUserInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getOrderListInfo(List<BookIdListInfoDTO> bookIdList, AdminOrderCDTO adminOrderCDTO)throws ClientProtocolException, IOException {
		int _cnt = bookIdList.size();
		Map listMap = new HashMap<>();
		String _orderNo = "";
		String _sellerId = "";
		
		String _sql = this.adminJdbcDAO.getOrderListInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		ArrayList<AdminOrderDTO> adminOrderList = new ArrayList<>();
		AdminOrderDTO adminOrderInDTO = new AdminOrderDTO();
		for(int i = 0; i < _cnt; i ++) {
			listMap = (Map) bookIdList.get(i);
			
			if("002000000".equals(listMap.get("productCategoryCode"))) {
				_orderNo = (String) listMap.get("orderNo");
				_sellerId = (String) listMap.get("sellerId");
				
				adminOrderInDTO = new AdminOrderDTO();
				
				_queryValue = new ArrayList<>();
				_queryValue.add(0, _orderNo);
				_queryValue.add(1, _sellerId);
				
				List<AdminOrderDTO> adminOrderInfo = this.utilService.getQueryString(_sql,adminOrderInDTO,_queryValue);
				
				int orderCnt = adminOrderInfo.size();
				
				for(int j = 0; j < orderCnt; j++) {
					adminOrderList.add(adminOrderInfo.get(j));
				}
			}
		}
	return adminOrderList;
	}
}
