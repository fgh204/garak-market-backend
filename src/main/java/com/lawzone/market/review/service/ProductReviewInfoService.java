package com.lawzone.market.review.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.cart.service.CartInfo;
import com.lawzone.market.cart.service.CartInfoDTO;
import com.lawzone.market.order.service.UserOrderInfoDTO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.review.dao.ProductReviewInfoDAO;
import com.lawzone.market.review.dao.ProductReviewInfoJdbcDAO;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductReviewInfoService {
	private final ProductReviewInfoDAO productReviewInfoDAO;
	private final ProductReviewInfoJdbcDAO productReviewInfoJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	
	@Transactional
	public String addProductReviewInfo(ProductReviewInfoCDTO productReviewInfoCDTO) {
		String _productId = productReviewInfoCDTO.getProductId();
		String _userId = productReviewInfoCDTO.getUserId();
		String sql = this.productReviewInfoJdbcDAO.chkReviewYn();
		String _rtnMsg = "";
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		_queryValue.add(1, _productId);
		
		if("Y".equals(this.utilService.getQueryStringChk(sql,_queryValue))){
			Optional<ProductReviewInfo> _productReviewInfo = this.productReviewInfoDAO.findByProductIdAndUserId(_productId, _userId);
			
			if (_productReviewInfo.isPresent()) {
				productReviewInfoCDTO.setReviewNumber(_productReviewInfo.get().getReviewNumber());
			}
			
			ProductReviewInfo productReviewInfo = new ProductReviewInfo();
			productReviewInfo = modelMapper.map(productReviewInfoCDTO, ProductReviewInfo.class);
			
			this.productReviewInfoDAO.save(productReviewInfo).getProductId();
			
			_rtnMsg = "저장되었습니다";
		}else {
			_rtnMsg = "구입한 상품만 리뷰를 등록할 수 있습니다";
		}
		return _rtnMsg;
	}
	
	@Transactional
	public void modifyProductReviewInfo(ProductReviewInfoDTO productReviewInfoDTO) {
		ProductReviewInfo productReviewInfo = new ProductReviewInfo();
		productReviewInfo = modelMapper.map(productReviewInfoDTO, ProductReviewInfo.class);
		
		this.productReviewInfoDAO.save(productReviewInfo).getProductId();
	}
	
	@Transactional
	public void removeProductReviewInfo(ProductReviewInfoCDTO productReviewInfoCDTO) {
		ProductReviewInfo productReviewInfo = new ProductReviewInfo();
		productReviewInfo = modelMapper.map(productReviewInfoCDTO, ProductReviewInfo.class);
		
		this.productReviewInfoDAO.delete(productReviewInfo);
	}
	
	@Transactional
	public List getProductReviewList(Map aMap) {
		String _productId = (String) aMap.get("productId");
		String _pageCount = (String) aMap.get("pageCount");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _orderCd = (String) aMap.get("orderCd");
		
		String _productScore = "";
		
		if(aMap.get("productScore") != null) {
			_productScore = aMap.get("productScore").toString();
		}

		String sql = this.productReviewInfoJdbcDAO.getProductReviewList(_pageCount, _productScore, _maxPageCount, _orderCd);
		
		ProductReviewInfoDTO productReviewInfoDTO = new ProductReviewInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _productId);
		
		if(!"".equals(_productScore)) {
			_queryValue.add(1, _productScore);
		}
		
		return this.utilService.getQueryString(sql,productReviewInfoDTO,_queryValue);
	}
	
	@Transactional
	public List getProductReviewPageInfo(Map aMap) {
		
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _productId = (String) aMap.get("productId");
		
		String _productScore = "";
		
		if(aMap.get("reviewRank") != null) {
			_productScore = aMap.get("productScore").toString();
		}
		
		String sql = this.productReviewInfoJdbcDAO.getProductReviewPageInfo(_maxPageCount, _productScore);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _productId);
		
		if(!"".equals(_productScore)) {
			_queryValue.add(1, _productScore);
		}
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	public List getSellerAverageScoreInfo(ProductCDTO productCDTO) {
		String sql = this.productReviewInfoJdbcDAO.getAverageScoreInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productCDTO.getSellerId());
		
		ProductReviewAverageScoreDTO productReviewAverageScoreDTO = new ProductReviewAverageScoreDTO();
		
		return this.utilService.getQueryString(sql,productReviewAverageScoreDTO,_queryValue);
	}
}
