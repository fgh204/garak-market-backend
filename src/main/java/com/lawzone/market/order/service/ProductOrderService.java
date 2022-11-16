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

import com.lawzone.market.admin.dto.AdminOrderItemListDTO;
import com.lawzone.market.admin.dto.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.AdminProductOrderInfoDTO;
import com.lawzone.market.admin.dto.AdminProductOrderListDTO;
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
	public List getCustOrderItemList(String productId, String statCd) {
		String sql = this.productOrderJdbcDAO.custProductItemByOrderId(statCd);
		
		CustOrderItemListDTO custOrderItemListDTO = new CustOrderItemListDTO();
		
		if("".equals(statCd)) {
			statCd = "001";
		}
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		_queryValue.add(1, statCd);
		
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
	public List getProductOrderInfoByOrderNo(String orderNo) {
		return this.productOrderDAO.findByOrderNo(orderNo);
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
	
	public List getAdminOrderList(Map aMap) {
		String _userNameYn = "Y";
		String _userName = (String) aMap.get("userNm");
		String _beginDate = (String) aMap.get("beginDate");
		String _endDate = (String) aMap.get("endDate");
		String _statCd = (String) aMap.get("statCd");
		String _sellerId = (String) aMap.get("sellerId");
		String _page = (String) aMap.get("pageCount");
		
		if("".equals(_userName) || _userName == null) {
			_userNameYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderItemList(_userNameYn, _page);
		
		AdminOrderItemListDTO adminOrderItemListDTO = new AdminOrderItemListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _beginDate);
		_queryValue.add(1, _endDate);
		_queryValue.add(2, _statCd);
		_queryValue.add(3, _sellerId);
		
		if("Y".equals(_userNameYn)) {
			_queryValue.add(4, _userName);
		}
		
		return this.utilService.getQueryString(sql,adminOrderItemListDTO,_queryValue);
	}
	
	public List getAdminOrderListPageInfo(Map aMap) {
		String _userNameYn = "Y";
		String _userName = (String) aMap.get("userNm");
		String _beginDate = (String) aMap.get("beginDate");
		String _endDate = (String) aMap.get("endDate");
		String _statCd = (String) aMap.get("statCd");
		String _sellerId = (String) aMap.get("sellerId");
		String _page = (String) aMap.get("page");
		
		if("".equals(_userName) || _userName == null) {
			_userNameYn = "N";
		}
		
		String sql = this.productOrderJdbcDAO.adminOrderItemPageInfo(_userNameYn, _page);
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _beginDate);
		_queryValue.add(1, _endDate);
		_queryValue.add(2, _statCd);
		_queryValue.add(3, _sellerId);
		
		if("Y".equals(_userNameYn)) {
			_queryValue.add(4, _userName);
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
	
	public List getCustOrderListPageInfo(Map aMap) {
		String _userId = (String) aMap.get("userId");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		
		String sql = this.productOrderJdbcDAO.custOrderListPageInfo(_maxPageCount);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	public Page<AdminProductOrderListDTO> getList(int page, String kw) {
		if("".equals(kw)) {
			kw = "003";
		}
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("orderDate"));
		Pageable pageable = PageRequest.of(page,  10, Sort.by(sorts));
		Specification<ProductOrderInfo> spec = search(kw);
		Page<AdminProductOrderInfoDTO> aa = this.productOrderDAO.findAllByKeyword(kw, pageable);
		return aa.map(post ->
            new AdminProductOrderListDTO(
                post.getOrderNo()));
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
