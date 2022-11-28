package com.lawzone.market.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.order.AdminOrderItemListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderListDTO;
import com.lawzone.market.answer.Answer;
import com.lawzone.market.order.dao.ProductOrderDAO;
import com.lawzone.market.order.dao.ProductOrderItemInfoDAO;
import com.lawzone.market.order.dao.ProductOrderJdbcDAO;
import com.lawzone.market.order.dao.ProductOrderRevokeDAO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.question.Question;
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
	public List getCustOrderItemList(String orderNo, String statCd) {
		String sql = this.productOrderJdbcDAO.custProductItemByOrderId(statCd);
		
		CustOrderItemListDTO custOrderItemListDTO = new CustOrderItemListDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, statCd);
		
		return this.utilService.getQueryString(sql,custOrderItemListDTO,_queryValue);
	}
	
	@Transactional
	public List getAdminOrderItemList(String orderNo, String productId, String statCd) {
		String sql = this.productOrderJdbcDAO.adminProductItemInfo(statCd);
		
		CustOrderItemListDTO custOrderItemListDTO = new CustOrderItemListDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, productId);
		_queryValue.add(2, statCd);
		
		return this.utilService.getQueryString(sql,custOrderItemListDTO,_queryValue);
	}
	
	@Transactional
	public List getCustOrderInfoByOrderNo(String orderNo, String statCd) {
		String sql = this.productOrderJdbcDAO.custProductInfoByOrderId(statCd);
		
		CustOrderInfoDTO custOrderInfoDTO = new CustOrderInfoDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, statCd);
		
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
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();
		
		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderList(_statCd, _sellerIdYn, _searchGb, _page);
		
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
		String _sellerId = adminOrderCDTO.getSellerId();
		String _page = adminOrderCDTO.getPageCnt();

		if("".equals(_sellerId) || _sellerId == null) {
			_sellerIdYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderItemPageInfo(_statCd, _sellerIdYn, _searchGb, _page);
		
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
	
	public List getCustOrderListPageInfo(Map aMap) {
		String _userId = (String) aMap.get("userId");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		
		String sql = this.productOrderJdbcDAO.custOrderListPageInfo(_maxPageCount);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	private Specification<ProductOrderInfo> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<ProductOrderInfo> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.and(cb.equal(q.get("orderStateCode"), kw)); 
            }
        };
    }
}
