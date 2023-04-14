package com.lawzone.market.review.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.cart.service.CartInfo;
import com.lawzone.market.cart.service.CartInfoDTO;
import com.lawzone.market.image.service.ProductImageListDTO;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.order.service.UserOrderInfoDTO;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductInfoListPDTO;
import com.lawzone.market.product.service.TagInfoDTO;
import com.lawzone.market.review.dao.ProductReviewInfoDAO;
import com.lawzone.market.review.dao.ProductReviewInfoJdbcDAO;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductReviewInfoService {
	private final ProductReviewInfoDAO productReviewInfoDAO;
	private final ProductReviewInfoJdbcDAO productReviewInfoJdbcDAO;
	private final ProductImageService productImageService;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final PointService pointService;
	
	@Transactional
	public String addProductReviewInfo(ProductReviewInfoCDTO productReviewInfoCDTO, List reviewImageList) {
		String _productId = productReviewInfoCDTO.getProductId();
		String _userId = productReviewInfoCDTO.getUserId();
		String _orderNo = productReviewInfoCDTO.getOrderNo();
		String sql = this.productReviewInfoJdbcDAO.chkReviewYn();
		String _rtnMsg = "";
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _userId);
		_queryValue.add(1, _productId);
		_queryValue.add(2, _orderNo);
		
		if("Y".equals(this.utilService.getQueryStringChk(sql,_queryValue))){
			Optional<ProductReviewInfo> _productReviewInfo = this.productReviewInfoDAO.findByProductIdAndUserIdAndOrderNo(_productId, _userId, _orderNo);
			
			if (_productReviewInfo.isPresent()) {
				//수정
				productReviewInfoCDTO.setReviewNumber(_productReviewInfo.get().getReviewNumber());
			} else {
				//신규
				PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
				pointInfoCDTO.setUserId(_userId);
				pointInfoCDTO.setEventCode("002");
				if(reviewImageList.size() == 0) {
					pointInfoCDTO.setEventId("00002");
				} else {
					pointInfoCDTO.setEventId("00003");
				}
				
				this.pointService.addPoint(pointInfoCDTO);
			}
			
			ProductReviewInfo productReviewInfo = new ProductReviewInfo();
			productReviewInfo = modelMapper.map(productReviewInfoCDTO, ProductReviewInfo.class);
			
			this.productReviewInfoDAO.save(productReviewInfo).getProductId();
			
			this.productImageService.mergeProductImageInfoList(reviewImageList, _productId, _orderNo);
			
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
		String _sellerId = (String) aMap.get("sellerId");
		String _pageCount = (String) aMap.get("pageCount");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _orderCd = (String) aMap.get("orderCd");
		
		String _productScore = "";
		
		if(aMap.get("productScore") != null) {
			_productScore = aMap.get("productScore").toString();
		}

		String sql = this.productReviewInfoJdbcDAO.getProductReviewList(_pageCount, _productScore, _maxPageCount, _orderCd, _productId, _sellerId);
		
		ProductReviewInfoDTO productReviewInfoDTO = new ProductReviewInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		
		if(!("".equals(_productId) || _productId == null)){
			_queryValue.add(_queryValueIdx, _productId);
			_queryValueIdx++;
		}
		
		if(!("".equals(_sellerId) || _sellerId == null)){
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if(!("".equals(_productScore) || _productScore == null)) {
			_queryValue.add(_queryValueIdx, _productScore);
			_queryValueIdx++;
		}
		
		List<ProductReviewInfoDTO> productReviewList = this.utilService.getQueryString(sql,productReviewInfoDTO,_queryValue);
		
		int cnt = productReviewList.size();
		
		ProductReviewInfoDTO productReviewInfo = new ProductReviewInfoDTO();
		ArrayList<ProductReviewInfoPDTO> productReviewInfoList = new ArrayList<ProductReviewInfoPDTO>();
		ProductReviewInfoPDTO productReviewInfoListInfo = new ProductReviewInfoPDTO();
		
		String reviewImageSql = "";
		
		for(int i = 0; i < cnt; i++) {
			productReviewInfo = new ProductReviewInfoDTO();
			productReviewInfoListInfo = new ProductReviewInfoPDTO();
			productReviewInfo = productReviewList.get(i);
			
//			productReviewInfoListInfo.setUserName(productReviewInfo.getUserName());
//			productReviewInfoListInfo.setNickname(productReviewInfo.getNickname());
//			productReviewInfoListInfo.setReviewNumber(productReviewInfo.getReviewNumber());
//			productReviewInfoListInfo.setProductScore(productReviewInfo.getProductScore());
//			productReviewInfoListInfo.setFreshScore(productReviewInfo.getFreshScore());
//			productReviewInfoListInfo.setTasteScore(productReviewInfo.getTasteScore());
//			productReviewInfoListInfo.setPackagingScore(productReviewInfo.getPackagingScore());
//			productReviewInfoListInfo.setDeliveryScore(productReviewInfo.getDeliveryScore());
//			productReviewInfoListInfo.setReorderScore(productReviewInfo.getReorderScore());
//			productReviewInfoListInfo.setProductId(productReviewInfo.getProductId());
//			productReviewInfoListInfo.setProductName(productReviewInfo.getProductName());
//			productReviewInfoListInfo.setReviewDate(productReviewInfo.getReviewDate());
//			productReviewInfoListInfo.setReviewTitle(productReviewInfo.getReviewTitle());
//			productReviewInfoListInfo.setReviewText(productReviewInfo.getReviewText());
//			productReviewInfoListInfo.setSellerName(productReviewInfo.getSellerName());
//			productReviewInfoListInfo.setOrderNo(productReviewInfo.getOrderNo());
//			productReviewInfoListInfo.setCreateDatetime(productReviewInfo.getCreateDatetime());
//			productReviewInfoListInfo.setUpdateDatetime(productReviewInfo.getUpdateDatetime());
//			productReviewInfoListInfo.setProfileImagesPath(productReviewInfo.getProfileImagesPath());
			
			//productReviewInfoListInfo = modelMapper.map(productReviewInfo, ProductReviewInfoPDTO.class);
			
			productReviewInfoListInfo = modelMapper.map(productReviewInfo, ProductReviewInfoPDTO.class);
			
			productReviewInfoList.add(i, productReviewInfoListInfo);
			
			List<ProductImageListDTO> productImageList = this.productImageService.getProductReviewImageInfoList(productReviewInfo.getProductId(), productReviewInfo.getOrderNo());
			
			productReviewInfoList.get(i).setProductReviewImageList((ArrayList<ProductImageListDTO>) productImageList);
		}
		
		return productReviewInfoList;
	}
	
	public Object model(Object obj1, Object obj2) {
		ModelMapper modelMapper = new ModelMapper();
		//modelMapper.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE).setFieldMatchingEnabled(true).setMatchingStrategy(MatchingStrategies.LOOSE);

		return modelMapper.map(obj1,obj2.getClass());
	}
	
	@Transactional
	public List getMyProductReviewList(Map aMap) {
		String _pageCount = (String) aMap.get("pageCount");
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _orderCd = (String) aMap.get("orderCd");
		String _userId = (String) aMap.get("userId");
		String _productId = "";
		String _productIdYn = "N";
		String _orderNo = "";
		String _orderNoYn = "N";
		
		if(aMap.get("productId") != null) {
			_productIdYn = "Y";
			_productId = (String) aMap.get("productId");
		}
		
		if(aMap.get("orderNo") != null) {
			_orderNoYn = "Y";
			_orderNo = (String) aMap.get("orderNo");
		}
		
		String sql = this.productReviewInfoJdbcDAO.getMyProductReviewList(_pageCount, _maxPageCount, _orderCd, _productIdYn, _orderNoYn);
		
		ProductReviewInfoDTO productReviewInfoDTO = new ProductReviewInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _userId);
		_queryValueIdx++;
		if("Y".equals(_productIdYn)) {
			_queryValue.add(_queryValueIdx, _productId);
			_queryValueIdx++;
		}
		
		if("Y".equals(_orderNoYn)) {
			_queryValue.add(_queryValueIdx, _orderNo);
			_queryValueIdx++;
		}
		
		List<ProductReviewInfoDTO> productReviewList = this.utilService.getQueryString(sql,productReviewInfoDTO,_queryValue);
		
		int cnt = productReviewList.size();
		
		ProductReviewInfoDTO productReviewInfo = new ProductReviewInfoDTO();
		ArrayList<ProductReviewInfoPDTO> productReviewInfoList = new ArrayList<ProductReviewInfoPDTO>();
		ProductReviewInfoPDTO productReviewInfoListInfo = new ProductReviewInfoPDTO();
		
		String reviewImageSql = "";
		
		for(int i = 0; i < cnt; i++) {
			productReviewInfo = new ProductReviewInfoDTO();
			productReviewInfoListInfo = new ProductReviewInfoPDTO();
			productReviewInfo = productReviewList.get(i);
			
//			productReviewInfoListInfo.setUserName(productReviewInfo.getUserName());
//			productReviewInfoListInfo.setNickname(productReviewInfo.getNickname());
//			productReviewInfoListInfo.setReviewNumber(productReviewInfo.getReviewNumber());
//			productReviewInfoListInfo.setProductScore(productReviewInfo.getProductScore());
//			productReviewInfoListInfo.setFreshScore(productReviewInfo.getFreshScore());
//			productReviewInfoListInfo.setTasteScore(productReviewInfo.getTasteScore());
//			productReviewInfoListInfo.setPackagingScore(productReviewInfo.getPackagingScore());
//			productReviewInfoListInfo.setDeliveryScore(productReviewInfo.getDeliveryScore());
//			productReviewInfoListInfo.setReorderScore(productReviewInfo.getReorderScore());
//			productReviewInfoListInfo.setProductId(productReviewInfo.getProductId());
//			productReviewInfoListInfo.setProductName(productReviewInfo.getProductName());
//			productReviewInfoListInfo.setReviewDate(productReviewInfo.getReviewDate());
//			productReviewInfoListInfo.setReviewTitle(productReviewInfo.getReviewTitle());
//			productReviewInfoListInfo.setReviewText(productReviewInfo.getReviewText());
//			productReviewInfoListInfo.setSellerName(productReviewInfo.getSellerName());
//			productReviewInfoListInfo.setOrderNo(productReviewInfo.getOrderNo());
//			productReviewInfoListInfo.setCreateDatetime(productReviewInfo.getCreateDatetime());
//			productReviewInfoListInfo.setUpdateDatetime(productReviewInfo.getUpdateDatetime());
//			productReviewInfoListInfo.setProfileImagesPath(productReviewInfo.getProfileImagesPath());
			
			//productReviewInfoListInfo = modelMapper.map(productReviewInfo, ProductReviewInfoPDTO.class);
			
			productReviewInfoListInfo = modelMapper.map(productReviewInfo, ProductReviewInfoPDTO.class);
			
			productReviewInfoList.add(i, productReviewInfoListInfo);
			
			List<ProductImageListDTO> productImageList = this.productImageService.getProductReviewImageInfoList(productReviewInfo.getProductId(), productReviewInfo.getOrderNo());
			
			productReviewInfoList.get(i).setProductReviewImageList((ArrayList<ProductImageListDTO>) productImageList);
		}
		
		return productReviewInfoList;
	}
	
	@Transactional
	public List getProductReviewPageInfo(Map aMap) {
		
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _productId = (String) aMap.get("productId");
		String _sellerId = (String) aMap.get("sellerId");
		String _pageCount = (String) aMap.get("pageCount");
		String _orderCd = (String) aMap.get("orderCd");
		String _productScore = "";
		
		if(aMap.get("productScore") != null) {
			_productScore = aMap.get("productScore").toString();
		}
		
		String sql = this.productReviewInfoJdbcDAO.getProductReviewPageInfo(_maxPageCount, _productId, _sellerId, _productScore);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		
		if(!("".equals(_productId) || _productId == null)){
			_queryValue.add(_queryValueIdx, _productId);
			_queryValueIdx++;
		}
		
		if(!("".equals(_sellerId) || _sellerId == null)){
			_queryValue.add(_queryValueIdx, _sellerId);
			_queryValueIdx++;
		}
		
		if(!("".equals(_productScore) || _productScore ==null )) {
			_queryValue.add(_queryValueIdx, _productScore);
			_queryValueIdx++;
		}
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	@Transactional
	public List getMyProductReviewPageInfo(Map aMap) {		
		String _maxPageCount = (String) aMap.get("maxPageCount");
		String _userId = (String) aMap.get("userId");
		String _productId = "";
		String _productIdYn = "N";
		String _orderNo = "";
		String _orderNoYn = "N";
		
		if(aMap.get("productId") != null) {
			_productIdYn = "Y";
			_productId = (String) aMap.get("productId");
		}
		
		if(aMap.get("orderNo") != null) {
			_orderNoYn = "Y";
			_orderNo = (String) aMap.get("orderNo");
		}
		
		String sql = this.productReviewInfoJdbcDAO.getMyProductReviewPageInfo(_maxPageCount, _productIdYn, _orderNoYn);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		int _queryValueIdx = 0;
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(_queryValueIdx, _userId);
		_queryValueIdx++;
		if("Y".equals(_productIdYn)) {
			_queryValue.add(_queryValueIdx, _productId);
			_queryValueIdx++;
		}
		
		if("Y".equals(_orderNoYn)) {
			_queryValue.add(_queryValueIdx, _orderNo);
			_queryValueIdx++;
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
	
	public List getProductReviewCountInfo(ProductCDTO productCDTO) {
		String sql = this.productReviewInfoJdbcDAO.getProductReviewCountInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productCDTO.getSellerId());
		
		ProductReviewScoreCountDTO productReviewScoreCountDTO = new ProductReviewScoreCountDTO();
		
		return this.utilService.getQueryString(sql,productReviewScoreCountDTO,_queryValue);
	}
}
