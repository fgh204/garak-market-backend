package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.order.AdminCustOrderItemListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.order.AdminOrderItemListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderStatCountInfoDTO;
import com.lawzone.market.order.dao.OrderPaymentDAO;
import com.lawzone.market.order.dao.ProductOrderDAO;
import com.lawzone.market.order.dao.ProductOrderItemInfoDAO;
import com.lawzone.market.order.dao.ProductOrderJdbcDAO;
import com.lawzone.market.order.dao.ProductOrderRevokeDAO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.user.SiteUser;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductOrderService {
	private final ProductOrderDAO productOrderDAO;
	private final ProductOrderItemInfoDAO productOrderItemInfoDAO;
	private final ProductOrderRevokeDAO productOrderRevokeDAO;
	private final ProductOrderJdbcDAO productOrderJdbcDAO;
	
	private final OrderPaymentDAO orderPaymentDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	
	@Transactional
	public String addProductOrderInfo(ProductOrderDTO productOrderDTO) {
		if("".equals(productOrderDTO.getOrderNo()) || productOrderDTO.getOrderNo() == null) {
			
			String orderNo = utilService.getNextVal("ORDER_NO");
			
			productOrderDTO.setOrderNo(orderNo);
		}
		
		ProductOrderInfo productOrderInfo = new ProductOrderInfo();
		productOrderInfo = modelMapper.map(productOrderDTO, ProductOrderInfo.class);
		
		return this.productOrderDAO.save(productOrderInfo).getOrderNo();
	}
	
	@Transactional
	public List getProductOrderInfoByUser(ProductOrderDTO productOrderDTO) {
		return this.productOrderDAO.findByUserIdAndOrderStateCode(productOrderDTO.getUserId(), "003");
	}
	
	@Transactional
	public void addProductOrderItemInfo(ProductOrderItemDTO productOrderItemDTO) {
		ProductOrderItemInfo productOrderItemInfo = new ProductOrderItemInfo();
		ProductOrderItemInfoId productOrderItemInfoId = new ProductOrderItemInfoId();

		productOrderItemInfoId = modelMapper.map(productOrderItemDTO, ProductOrderItemInfoId.class);
		
		productOrderItemInfo = modelMapper.map(productOrderItemDTO, ProductOrderItemInfo.class);
		
		productOrderItemInfo.setId(productOrderItemInfoId);
		
		this.productOrderItemInfoDAO.save(productOrderItemInfo);
	}
	
	@Transactional
	public List getCustOrderItemList(String orderNo, String statCd, String userId, String payYn) {
		String sql = this.productOrderJdbcDAO.custProductItemByOrderId(statCd, userId, payYn);
		
		CustOrderItemListDTO custOrderItemListDTO = new CustOrderItemListDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, statCd);
		
		if(!"".equals(userId)) {
			_queryValue.add(2, userId);
		}
		
		return this.utilService.getQueryString(sql,custOrderItemListDTO,_queryValue);
	}
	
	@Transactional
	public List getAdminOrderItemList(String orderNo, String productId, String statCd) {
		String sql = this.productOrderJdbcDAO.adminProductItemInfo(statCd);
		
		AdminCustOrderItemListDTO adminCustOrderItemListDTO = new AdminCustOrderItemListDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, productId);
		_queryValue.add(2, statCd);
		
		return this.utilService.getQueryString(sql,adminCustOrderItemListDTO,_queryValue);
	}
	
	@Transactional
	public List getCustOrderInfoByOrderNo(String orderNo, String statCd, String userId, String payYn) {
		String sql = this.productOrderJdbcDAO.custProductInfoByOrderId(statCd, userId, payYn);
		
		CustOrderInfoDTO custOrderInfoDTO = new CustOrderInfoDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, statCd);
		
		if(!"".equals(userId)) {
			_queryValue.add(2, userId);
		}
		
		return this.utilService.getQueryString(sql,custOrderInfoDTO,_queryValue);
	}
	
	@Transactional
	public List getProductOrderItemInfoByOrderNo(String orderNo) {
		return this.productOrderItemInfoDAO.findByIdOrderNo(orderNo);
	}
	
	@Transactional
	public List getProductOrderItemInfoByOrderNoAndProductId(String _orderNo, String _productId) {
		return this.productOrderItemInfoDAO.findByIdOrderNoAndIdProductId(_orderNo, _productId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Integer modifyProductOrderInfoStat(String statCd, String orderNo) {
		String sql = this.productOrderJdbcDAO.modifyOrderInfoStat();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, statCd);
		_queryValue.add(1, orderNo);
		
		CustOrderInfoDTO custOrderInfoDTO = new CustOrderInfoDTO();
		
		return this.utilService.getQueryStringUpdate(sql,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Integer modifyProductOrderItemInfoStat(String statCd, String orderNo) {
		String sql = this.productOrderJdbcDAO.modifyOrderItemInfoStat("N");
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, statCd);
		_queryValue.add(1, orderNo);
		
		return this.utilService.getQueryStringUpdate(sql,_queryValue);
	}
	
	public List getAdminOrderList(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _userName = adminOrderCDTO.getSearchValue();
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _searchGb = adminOrderCDTO.getSearchGb();
		String _statCd = adminOrderCDTO.getOrderStatCode();
		String _deliveryStateCode = adminOrderCDTO.getDeliveryStateCode();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();
		String _dlngStatCd = adminOrderCDTO.getOrderDlngStatCode();
		
		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderList(_statCd, _dlngStatCd,_deliveryStateCode, _sellerIdYn, _searchGb, _page);
		
		AdminOrderListDTO adminOrderListDTO = new AdminOrderListDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;
		
		if(!"000".equals(_statCd)) {
			_queryValue.add(_queryValueIdx, _statCd);
			_queryValueIdx++;
		}
		
		if(!"000".equals(_dlngStatCd)) {
			_queryValue.add(_queryValueIdx, _dlngStatCd);
			_queryValueIdx++;
		}
		
		if(!"000".equals(_deliveryStateCode)) {
			_queryValue.add(_queryValueIdx, _deliveryStateCode);
			_queryValueIdx++;
		}
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if("01".equals(_searchGb)) {
			_queryValue.add(_queryValueIdx, _userName);
			_queryValueIdx++;
		}
		
		return this.utilService.getQueryString(sql,adminOrderListDTO,_queryValue);
	}
	
	public List getAdminOrderListPageInfo(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _userName = adminOrderCDTO.getSearchValue();
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _searchGb = adminOrderCDTO.getSearchGb();
		String _statCd = adminOrderCDTO.getOrderStatCode();
		String _dlngStatCd = adminOrderCDTO.getOrderDlngStatCode();
		String _deliveryStateCode = adminOrderCDTO.getDeliveryStateCode();
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderItemPageInfo(_statCd, _dlngStatCd, _deliveryStateCode, _sellerIdYn, _searchGb, _page);
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _beginDate);
		_queryValueIdx++;
		_queryValue.add(_queryValueIdx, _endDate);
		_queryValueIdx++;
		
		if(!"000".equals(_statCd)) {
			_queryValue.add(_queryValueIdx, _statCd);
			_queryValueIdx++;
		}
		
		if(!"000".equals(_dlngStatCd)) {
			_queryValue.add(_queryValueIdx, _dlngStatCd);
			_queryValueIdx++;
		}
		
		if(!"000".equals(_deliveryStateCode)) {
			_queryValue.add(_queryValueIdx, _deliveryStateCode);
			_queryValueIdx++;
		}
		
		if("Y".equals(_sellerIdYn)) {
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if("01".equals(_searchGb)) {
			_queryValue.add(_queryValueIdx, _userName);
			_queryValueIdx++;
		}

		return this.utilService.getQueryString(sql,adminPageInfoDTO,_queryValue);
	}
	
	public List getAdminOrderStatCountInfo(AdminOrderCDTO adminOrderCDTO) {
		String _sellerIdYn = "Y";
		String _beginDate = adminOrderCDTO.getOrderDateAf();
		String _endDate = adminOrderCDTO.getOrderDateBf();
		String _sellerId = adminOrderCDTO.getSellerId();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderCountInfo( _sellerIdYn);
		
		AdminOrderStatCountInfoDTO adminOrderStatCountInfoDTO = new AdminOrderStatCountInfoDTO();
		
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
		
		return this.utilService.getQueryString(sql,adminOrderStatCountInfoDTO,_queryValue);
	}
	
	public List getCustOrderList(Map aMap) {
		String _userId = (String) aMap.get("userId");
		String _pageCount = (String) aMap.get("pageCount");
		String _maxPageCount = (String) aMap.get("maxPageCount");

		String sql = this.productOrderJdbcDAO.custOrderList(_pageCount, _maxPageCount);
		
		UserOrderInfoDTO userOrderInfoDTO = new UserOrderInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		
		return this.utilService.getQueryString(sql,userOrderInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyOrderItemStatInfo(String _orderNo, String _productId, String _orderItemDlngStateCode) {
		String sqlModifyOrderInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoDlngStat();
		ArrayList<String> _queryValue1 = new ArrayList<>();
		
		List<ProductOrderInfo> productOrderInfo = this.productOrderDAO.findByOrderNo(_orderNo);
		String productOrderStatCode = productOrderInfo.get(0).getOrderStateCode();
		_queryValue1.add(0, _orderItemDlngStateCode);
		_queryValue1.add(1, _orderNo);
		_queryValue1.add(2, _productId);
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyOrderItemStatInfoDeliveryBySellerId(String _orderNo, String _sellerId, String _orderItemDlngStateCode, String _deliveryStateCode) {
		String sqlModifyOrderInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoDlngStatDeliveryBySellerId();
		ArrayList<String> _queryValue1 = new ArrayList<>();
		
		List<ProductOrderInfo> productOrderInfo = this.productOrderDAO.findByOrderNo(_orderNo);
		String productOrderStatCode = productOrderInfo.get(0).getOrderStateCode();
		_queryValue1.add(0, _orderItemDlngStateCode);
		_queryValue1.add(1, _orderItemDlngStateCode);
		_queryValue1.add(2, _orderNo);
		_queryValue1.add(3, _sellerId);
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyOrderItemStatInfoDeliveryByProduct(String _orderNo, String _productId, String _orderItemDlngStateCode, String _deliveryStateCode) {
		String sqlModifyOrderInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoDlngStatDeliveryByProduct();
		ArrayList<String> _queryValue1 = new ArrayList<>();
		
		List<ProductOrderInfo> productOrderInfo = this.productOrderDAO.findByOrderNo(_orderNo);
		String productOrderStatCode = productOrderInfo.get(0).getOrderStateCode();
		_queryValue1.add(0, _orderItemDlngStateCode);
		_queryValue1.add(1, _orderItemDlngStateCode);
		_queryValue1.add(1, _orderNo);
		_queryValue1.add(2, _productId);
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
	}
	
	public List getCustOrderListPageInfo(Map aMap) {
		String _userId = (String) aMap.get("userId");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		
		String sql = this.productOrderJdbcDAO.custOrderListPageInfo(_maxPageCount);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	@Transactional
	public List getProductOrderUserInfo(String _orderNo) {
		String _query = this.productOrderJdbcDAO.getProductOrderUserInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _orderNo);
		
		ProductOrderUserInfoDTO productOrderUserInfoDTO = new ProductOrderUserInfoDTO();
		
		return this.utilService.getQueryString(_query,productOrderUserInfoDTO,_queryValue);
	}
	
	@Transactional
	public void addOrderPaymentInfo(String _orderNo) {
		String _query = this.productOrderJdbcDAO.getOrderPaymentInfo();
		Map orderItemPaymentMap = new HashMap<>();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _orderNo);
		
		OrderItemPaymentDTO orderItemPaymentDTO = new OrderItemPaymentDTO();
		
		List<OrderItemPaymentDTO> orderItemPaymentList = this.utilService.getQueryString(_query,orderItemPaymentDTO,_queryValue);
		
		OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
		OrderPaymentInfo orderPaymentInfo = new OrderPaymentInfo();
		
		int _cnt = orderItemPaymentList.size();
		BigDecimal _cancelledOrderAmount = new BigDecimal("0");
		
		for(int i = 0; i < _cnt; i++) {
			orderPaymentDTO = new OrderPaymentDTO();

			orderPaymentDTO.setOrderDate("now()");
			orderPaymentDTO.setOrderNo(orderItemPaymentList.get(i).getOrderNo());
			orderPaymentDTO.setSellerId(orderItemPaymentList.get(i).getSellerId());
			orderPaymentDTO.setOrderAmount(orderItemPaymentList.get(i).getOrderAmount());
			orderPaymentDTO.setCancelledOrderAmount(_cancelledOrderAmount);
			orderPaymentDTO.setDeliveryAmount(orderItemPaymentList.get(i).getDeliveryAmount());
			
			orderPaymentInfo = new OrderPaymentInfo();
			orderPaymentInfo = modelMapper.map(orderPaymentDTO, OrderPaymentInfo.class);
			
			orderPaymentDAO.save(orderPaymentInfo);
		}
	}
	
	@Transactional
	public Optional<OrderPaymentInfo> getOrderPaymentInfo(String _orderNo, String sellerId) {
		return this.orderPaymentDAO.findByOrderNoAndSellerId(_orderNo, sellerId);
	}
	
	@Transactional
	public void modifyOrderPaymentInfo(OrderPaymentDTO orderPaymentDTO) {
		String _query = this.productOrderJdbcDAO.modifyOrderPaymentInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderPaymentDTO.getCancelledOrderAmount().toString());
		_queryValue.add(1, orderPaymentDTO.getOrderNo());
		_queryValue.add(2, orderPaymentDTO.getSellerId());
		
		this.utilService.getQueryStringUpdate(_query, _queryValue);
	}
}
