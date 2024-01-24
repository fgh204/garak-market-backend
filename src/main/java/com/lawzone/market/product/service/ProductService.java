package com.lawzone.market.product.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.service.AdminProductDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.event.dao.EventMstJdbcDAO;
import com.lawzone.market.event.service.EventInfoCDTO;
import com.lawzone.market.event.service.EventInfoDTO;
import com.lawzone.market.event.service.EventMstService;
import com.lawzone.market.event.service.EventProductInfoDTO;
import com.lawzone.market.event.service.EventProductInfoListDTO;
import com.lawzone.market.event.service.EventProductInfoListPDTO;
import com.lawzone.market.image.dao.ProductImageDAO;
import com.lawzone.market.image.dao.ProductImageJdbcDAO;
import com.lawzone.market.image.service.ProductImageInfo;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.order.service.ProductOrderItemInfoId;
import com.lawzone.market.product.controller.ProductController;
import com.lawzone.market.product.dao.ProductDAO;
import com.lawzone.market.product.dao.ProductJdbcDAO;
import com.lawzone.market.product.dao.ProductTagDAO;
import com.lawzone.market.product.dao.ProductTagJdbcDAO;
import com.lawzone.market.product.dao.TagDAO;
import com.lawzone.market.user.dao.SellerInfoDAO;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

import lombok.Builder.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
	private final ProductDAO productDAO;
	private final TagDAO tagDAO;
	private final ProductTagDAO productTagDAO;
	private final ProductTagJdbcDAO produstTagJdbcDAO;
	private final ProductJdbcDAO productJdbcDAO;
	private final SellerInfoDAO sellerInfoDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final ProductImageService productImageService;
	private final ProductImageJdbcDAO productImageJdbcDAO;
	private final ProductImageDAO productImageDAO;
	private final EventMstJdbcDAO eventMstJdbcDAO;
	private final EntityManager em;
	private final EventMstService eventMstService;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional
	public String addProductInfo(ProductDTO productDTO) {
		if("".equals(productDTO.getProductId()) || productDTO.getProductId() == null) {
			
			String productId = utilService.getNextVal("PRODUCT_ID");
			
			productDTO.setProductId(productId);
			
			Optional<SellerInfo> sellerInfo = this.sellerInfoDAO.findBySellerId(productDTO.getSellerId());
			
			if(sellerInfo.isPresent()) {
				productDTO.setProductCategoryCode(sellerInfo.get().getProductCategoryCode());
				productDTO.setTodayDeliveryStandardTime(sellerInfo.get().getTodayDeliveryStandardTime());
			}
			
		}
		
		ProductInfo productInfo = new ProductInfo();
		productInfo = modelMapper.map(productDTO, ProductInfo.class);
		return this.productDAO.save(productInfo).getProductId();
	}
	
	@Transactional
	public List getProductList() {
//		String sql = "select\r\n"
//				+ "        product_id  ,\r\n"
//				+ "        product_name  ,\r\n"
//				+ "        product_price  ,\r\n"
//				+ "        product_stock  ,\r\n"
//				+ "        product_desc  ,\r\n"
//				+ "        grade_cd  ,\r\n"
//				+ "        DATE_FORMAT(begin_date,'%y-%m-%d')  ,\r\n"
//				+ "        DATE_FORMAT(end_date,'%y-%m-%d')  ,\r\n"
//				+ "        use_yn AS useYn   ,\r\n"
//				+ "        created_date  ,\r\n"
//				+ "        update_date  \r\n"
//				+ "    from\r\n"
//				+ "        lz_market.product_info";
//		ProductDTO productDTO = new ProductDTO();
//		return this.utilService.getQueryString(sql,productDTO);
		return this.productDAO.findByuseYn("Y");
	}
	
	public List getProductDetailInfo(String productId) {
		String sql = this.productJdbcDAO.productInfo();
		String userId = sessionBean.getUserId();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		
		ProductInfoDTO productInfoDTO = new ProductInfoDTO();
		
		List<ProductInfoDTO> _productList = this.utilService.getQueryString(sql,productInfoDTO,_queryValue);
		
		//영업일여부
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
		Date date = new Date();
		String _date = sf.format(date);
		String _dateArray[] =  _date.split(" ");
		
		String _toDate = _dateArray[0];
		String _toTime = _dateArray[1];
		String slsDateList[];
		String slsDateText = "";
		int _time = Integer.parseInt(_toTime);
		int _todayDeliveryTime = 0;
		String slsDayYn = "N";
		
		String _eventId = "";
		
		String eventInfoSql = this.eventMstJdbcDAO.eventProductInfo();
		ArrayList<String> _eventInfoQueryValue = new ArrayList<>();
		EventProductInfoDTO eventProductInfoDTO = new EventProductInfoDTO();
		
		BigDecimal productStock = new BigDecimal(0);
		BigDecimal _zero = new BigDecimal(0);
		BigDecimal personBuyCount = new BigDecimal(0);
		BigDecimal eventCount = new BigDecimal(0);
		BigDecimal personPaymentCount = new BigDecimal(0);
		BigDecimal eventPaymentCount = new BigDecimal(0);
		
		slsDateText = _productList.get(0).getSlsDateText();
		_eventId = _productList.get(0).getEventId();
		
		if(slsDateText.indexOf(_toDate) > -1) {
			slsDayYn = "N";
		} else {
			slsDayYn = this.utilService.getSlsDayYn(_toDate);
		}
		
		slsDateList = slsDateText.split(";");
		
		if(_productList.get(0).getTodayDeliveryStandardTime() == null) {
			_productList.get(0).setTodayDeliveryStandardTime("1000");
		}
		
		_todayDeliveryTime = Integer.parseInt((String) _productList.get(0).getTodayDeliveryStandardTime());
		
		if(_todayDeliveryTime >= _time && "Y".equals(slsDayYn)) {
			_productList.get(0).setTodayDeliveryYn("Y");
		}else {
			_productList.get(0).setTodayDeliveryYn("N");
		}
		
		//if("N".equals(_productList.get(0).getTodayDeliveryYn())) {
			_productList.get(0).setSlsDate(this.utilService.getSlsDate(_toDate , _productList.get(0).getTodayDeliveryYn(), slsDateList));
		//}else {
		//	_productList.get(0).setSlsDate(this.utilService.getSlsDate(_toDate , "Y"));
		//}
		
		if(!("".equals(_eventId) || _eventId == null)) {
			if("".equals(userId) || userId == null) {
				userId = "99999999";
			}
			
			_eventInfoQueryValue = new ArrayList<>();
			_eventInfoQueryValue.add(0, userId);
			_eventInfoQueryValue.add(1, "003");
			_eventInfoQueryValue.add(2, productId);
			_eventInfoQueryValue.add(3, productId);
			_eventInfoQueryValue.add(4, "003");
			_eventInfoQueryValue.add(5, _eventId);
		
			List<EventProductInfoDTO> eventProductInfo = this.utilService.getQueryString(eventInfoSql,eventProductInfoDTO,_eventInfoQueryValue);
			
			productStock = new BigDecimal(_productList.get(0).getProductStock().toString());
			personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
			eventCount = eventProductInfo.get(0).getEventCount();
			personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
			eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
			
			if(personBuyCount.compareTo(_zero) == 0) {
				personBuyCount = productStock;
			} else {
				personBuyCount = personBuyCount.subtract(personPaymentCount);
				
				if(personBuyCount.compareTo(_zero) < 0) {
					personBuyCount = _zero;
				}
			}
			
			if(eventCount.compareTo(_zero) == 0) {
				eventCount = productStock;
			} else {
				eventCount = eventCount.subtract(eventPaymentCount);
				
				if(eventCount.compareTo(_zero) < 0) {
					eventCount = _zero;
				}
			}
		
			_productList.get(0).setEventBeginTime(eventProductInfo.get(0).getEventBeginTime());
			_productList.get(0).setEventEndTime(eventProductInfo.get(0).getEventEndTime());
			_productList.get(0).setPersonBuyCount(new BigInteger(personBuyCount.toString()));
			_productList.get(0).setEventCount(new BigInteger(eventCount.toString()));
			_productList.get(0).setEventId(_eventId);
			_productList.get(0).setEventBeginDate(eventProductInfo.get(0).getEventBeginDate());
			_productList.get(0).setEventEndDate(eventProductInfo.get(0).getEventEndDate());
		}
			
		return _productList;
	}
	
	public List getList2(ProductCDTO productCDTO) throws IllegalAccessException, InvocationTargetException {
		String categoryCodeYn = "N";
		String productIdYn = "N";
		String productNameYn = "N";
		String favoriteYn = productCDTO.getFavoriteYn();
		String sellerId = productCDTO.getSellerId();
		String sellerYn = productCDTO.getSellerSearchYn();
		String sellerIdYn = productCDTO.getSellerIdYn();
		String useYn = productCDTO.getUseYn();
		String eventId = productCDTO.getEventId();
		Boolean isSoldOutHidden = productCDTO.getIsSoldOutHidden();
		String productSortCode = productCDTO.getProductSortCode();
		String productBundleCfcd = productCDTO.getProductBundleCfcd();
		
		if(
			("Y".equals(sellerYn) && "Y".equals(sellerIdYn))
			|| (sellerYn == null && sellerIdYn == null)
			) {
			useYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductCategoryCode()) && productCDTO.getProductCategoryCode() != null){
			categoryCodeYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductId()) && productCDTO.getProductId() != null){
			productIdYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductName()) && productCDTO.getProductName() != null){
			productNameYn = "Y";
		}
		
		if("".equals(useYn) || useYn == null){
			useYn = "";
		}
		
		String sql = this.productJdbcDAO.pageListQuery(productCDTO.getPageCount(), productCDTO.getMaxPageCount()
														,categoryCodeYn, productIdYn, productNameYn, sellerYn
														, favoriteYn, sellerId, useYn, eventId, isSoldOutHidden, productSortCode, productBundleCfcd);
		ProductInfoListDTO productInfoListDTO = new ProductInfoListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		int paramCnt = 0;
		
		if(!("".equals(productBundleCfcd) || productBundleCfcd == null)) {
			_queryValue.add(paramCnt, productBundleCfcd);
			paramCnt++;
		}
		
		if(!"".equals(useYn)) {
			_queryValue.add(paramCnt, useYn);
			paramCnt++;
		}
		
		if("Y".equals(categoryCodeYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductCategoryCode());
			paramCnt++;
		}
		
		if("Y".equals(productIdYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductId());
			paramCnt++;
		}
		
		if("Y".equals(productNameYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductName());
			paramCnt++;
		}
		
		if("Y".equals(sellerYn)) {
			if("Y".equals(sellerIdYn)) {
				_queryValue.add(paramCnt, productCDTO.getSellerId());
				paramCnt++;
			}else {
				_queryValue.add(paramCnt, productCDTO.getUserId());
				paramCnt++;
			}
		} else {
			if(!("".equals(sellerId) || sellerId == null)) {
				_queryValue.add(paramCnt, productCDTO.getSellerId());
				paramCnt++;
			}
		}
		
		if("Y".equals(favoriteYn)) {
			_queryValue.add(paramCnt, productCDTO.getUserId());
			paramCnt++;
		}
		
		if(!("".equals(eventId) || eventId == null)) {
			_queryValue.add(paramCnt, eventId);
			paramCnt++;
		}
		
		List<ProductInfoListDTO> productList = this.utilService.getQueryString(sql,productInfoListDTO,_queryValue);
		
		int productListSize =  productList.size();
		
		ArrayList<ProductInfoListPDTO> productInfoListPDTO = new ArrayList<ProductInfoListPDTO>();
		ArrayList<ProductInfoListDTO> productInfoList = new ArrayList<ProductInfoListDTO>();
		ProductInfoListPDTO productInfoListInfo = new ProductInfoListPDTO();
		
		String productTagSql = this.produstTagJdbcDAO.productTgaList();
		String eventInfoSql = this.eventMstJdbcDAO.eventProductInfo();
		
		ArrayList<String> _productTagQueryValue = new ArrayList<>();
		ArrayList<String> _eventInfoQueryValue = new ArrayList<>();
		
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		EventProductInfoDTO eventProductInfoDTO = new EventProductInfoDTO();
		ParameterUtils ParameterUtils = new ParameterUtils();
		
		//영업일여부
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
		Date date = new Date();
		String _date = sf.format(date);
		String _dateArray[] =  _date.split(" ");
		
		String _toDate = _dateArray[0];
		String _toTime = _dateArray[1];
		String slsDateList[];
		String slsDateText = "";
		int _time = Integer.parseInt(_toTime);
		int _todayDeliveryTime = 0;
		String slsDayYn = "N";
		String _eventId = "";
		BigDecimal productStock = new BigDecimal(0);
		BigDecimal _zero = new BigDecimal(0);
		BigDecimal personBuyCount = new BigDecimal(0);
		BigDecimal eventCount = new BigDecimal(0);
		BigDecimal personPaymentCount = new BigDecimal(0);
		BigDecimal eventPaymentCount = new BigDecimal(0);
		
		for(int i = 0; i < productListSize; i++) {
			_eventId = "";
			productInfoListInfo = new ProductInfoListPDTO();
			_eventId = productList.get(i).getEventId();
			slsDateText = productList.get(i).getSlsDateText();
			
			if(slsDateText.indexOf(_toDate) > -1) {
				slsDayYn = "N";
			} else {
				slsDayYn = this.utilService.getSlsDayYn(_toDate);
			}
			
			slsDateList = slsDateText.split(";");
			
			productInfoListInfo.setProductId(productList.get(i).getProductId());
			productInfoListInfo.setProductName(productList.get(i).getProductName());
			productInfoListInfo.setProductPrice(productList.get(i).getProductPrice());
			productInfoListInfo.setSupplyPrice(productList.get(i).getSupplyPrice());
			productInfoListInfo.setProductStock(productList.get(i).getProductStock());
			productInfoListInfo.setProductDesc(productList.get(i).getProductDesc());
			productInfoListInfo.setThumbnailImagePath(productList.get(i).getThumbnailImagePath());
			productInfoListInfo.setCumulativeSalesCount(productList.get(i).getCumulativeSalesCount());
			productInfoListInfo.setShopName(productList.get(i).getShopName());
			productInfoListInfo.setUseYn(productList.get(i).getUseYn());
			productInfoListInfo.setCombinedDeliveryStandardAmount(productList.get(i).getCombinedDeliveryStandardAmount());
			productInfoListInfo.setDeliveryAmount(productList.get(i).getDeliveryAmount());
			productInfoListInfo.setCombinedDeliveryYn(productList.get(i).getCombinedDeliveryYn());
			
			if(productList.get(i).getTodayDeliveryStandardTime() == null) {
				productInfoListInfo.setTodayDeliveryStandardTime("1000");
			}else {
				productInfoListInfo.setTodayDeliveryStandardTime(productList.get(i).getTodayDeliveryStandardTime());
			}
			
			_todayDeliveryTime = Integer.parseInt(productList.get(i).getTodayDeliveryStandardTime());
			
			if(_todayDeliveryTime >= _time && "Y".equals(slsDayYn)) {
				productInfoListInfo.setTodayDeliveryYn("Y");
			}else {
				productInfoListInfo.setTodayDeliveryYn("N");
			}
			
			//if("N".equals(productInfoListInfo.getTodayDeliveryYn())) {
				productInfoListInfo.setSlsDate(this.utilService.getSlsDate(_toDate , productInfoListInfo.getTodayDeliveryYn(), slsDateList));
			//}else {
			//	productInfoListInfo.setSlsDate(this.utilService.getSlsDate(_toDate , "Y"));
			//}
			
			productInfoListPDTO.add(i, productInfoListInfo);
			
			_productTagQueryValue = new ArrayList<>();
			_productTagQueryValue.add(0, productList.get(i).getProductId());
			_productTagQueryValue.add(1, "Y");
			
			List<TagInfoDTO> tagInfoList = this.utilService.getQueryString(productTagSql,tagInfoDTO,_productTagQueryValue);
			
			if(!("".equals(_eventId) || _eventId == null)) {
				EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
				eventInfoCDTO.setSearchGb("000");
				eventInfoCDTO.setEventCfcd("000");
				eventInfoCDTO.setPageCnt("0");
				eventInfoCDTO.setMaxPage("5");
				eventInfoCDTO.setEventId(_eventId);
				//이벤트정보
				List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
				
				_eventInfoQueryValue = new ArrayList<>();
				_eventInfoQueryValue.add(0, productCDTO.getUserId());
				_eventInfoQueryValue.add(1, "003");
				_eventInfoQueryValue.add(2, productList.get(i).getProductId());
				_eventInfoQueryValue.add(3, productList.get(i).getProductId());
				_eventInfoQueryValue.add(4, "003");
				_eventInfoQueryValue.add(5, _eventId);
			
				List<EventProductInfoDTO> eventProductInfo = this.utilService.getQueryString(eventInfoSql,eventProductInfoDTO,_eventInfoQueryValue);
				
				productStock = productInfoListInfo.getProductStock();
				personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
				eventCount = eventProductInfo.get(0).getEventCount();
				personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
				eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
				
				if(personBuyCount.compareTo(_zero) == 0) {
					personBuyCount = productStock;
				} else {
					personBuyCount = personBuyCount.subtract(personPaymentCount);
					
					if(personBuyCount.compareTo(_zero) < 0) {
						personBuyCount = _zero;
					}
				}
				
				if(eventCount.compareTo(_zero) == 0) {
					eventCount = productStock;
				} else {
					eventCount = eventCount.subtract(eventPaymentCount);
					
					if(eventCount.compareTo(_zero) < 0) {
						eventCount = _zero;
					}
				}
			
				productInfoListInfo.setEventBeginTime(eventProductInfo.get(0).getEventBeginTime());
				productInfoListInfo.setEventEndTime(eventProductInfo.get(0).getEventEndTime());
				productInfoListInfo.setPersonBuyCount(personBuyCount);
				productInfoListInfo.setEventCount(eventCount);
				productInfoListInfo.setEventId(_eventId);
				productInfoListInfo.setEventBeginDate(eventProductInfo.get(0).getEventBeginDate());
				productInfoListInfo.setEventEndDate(eventProductInfo.get(0).getEventEndDate());
				
				if("001".equals(eventInfoList.get(0).getEventStateCode())) {
					productInfoListInfo.setProductStock(eventCount);
				} else {
					productInfoListInfo.setProductStock(_zero);
				}
				
			}
			productInfoListPDTO.get(i).setProductTagList((ArrayList<TagInfoDTO>) tagInfoList);
		}
		//List<TagInfo> tagInfo = this.tagDAO.findByTagIdAndUseYn(_productTagQueryValue, "Y");
		
		return productInfoListPDTO;
	}
	
	public List getPageInfo(ProductCDTO productCDTO) {
		String categoryCodeYn = "N";
		String productIdYn = "N";
		String productNameYn = "N";
		String favoriteYn = productCDTO.getFavoriteYn();
		String sellerId = productCDTO.getSellerId();
		String sellerYn = productCDTO.getSellerSearchYn();
		String sellerIdYn = productCDTO.getSellerIdYn();
		String useYn = productCDTO.getUseYn();
		String eventId = productCDTO.getEventId();
		String productBundleCfcd = productCDTO.getProductBundleCfcd();
		Boolean isSoldOutHidden = productCDTO.getIsSoldOutHidden();
		
		if(!"Y".equals(sellerYn)) {
			useYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductCategoryCode()) && productCDTO.getProductCategoryCode() != null){
			categoryCodeYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductId()) && productCDTO.getProductId() != null){
			productIdYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductName()) && productCDTO.getProductName() != null){
			productNameYn = "Y";
		}
		
		if("".equals(useYn) || useYn == null){
			useYn = "";
		}
		
		String sql = this.productJdbcDAO.pageQuery(productCDTO.getMaxPageCount()
				,categoryCodeYn, productIdYn, productNameYn, sellerYn, favoriteYn, sellerId, useYn, eventId, isSoldOutHidden, productBundleCfcd);
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		int paramCnt = 0;
		
		if(!("".equals(productBundleCfcd) || productBundleCfcd == null)) {
			_queryValue.add(paramCnt, productBundleCfcd);
			paramCnt++;
		}
		
		if(!"".equals(useYn)) {
			_queryValue.add(paramCnt, useYn);
			paramCnt++;
		}
		
		if("Y".equals(categoryCodeYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductCategoryCode());
			paramCnt++;
		}
		
		if("Y".equals(productIdYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductId());
			paramCnt++;
		}
		
		if("Y".equals(productNameYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductName());
			paramCnt++;
		}
		
		if("Y".equals(sellerYn)) {
			if("Y".equals(sellerIdYn)) {
				_queryValue.add(paramCnt, productCDTO.getSellerId());
				paramCnt++;
			} else {
				_queryValue.add(paramCnt, productCDTO.getUserId());
				paramCnt++;
			}
		} else {
			if(!("".equals(sellerId) || sellerId == null)) {
				_queryValue.add(paramCnt, productCDTO.getSellerId());
				paramCnt++;
			}
		}
		
		if("Y".equals(favoriteYn)) {
			_queryValue.add(paramCnt, productCDTO.getUserId());
			paramCnt++;
		}
		
		if(!("".equals(eventId) || eventId == null)) {
			_queryValue.add(paramCnt, eventId);
			paramCnt++;
		}
		
		return this.utilService.getQueryString(sql,pageInfoDTO,_queryValue);
	}
	
	public Page<ProductInfo> getList(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("productId"));
		Pageable pageable = PageRequest.of(page,  10, Sort.by(sorts));
		Specification<ProductInfo> spec = search("");
		//return this.questionRepository.findAll(spec, pageable);
		return this.productDAO.findAllByPage("22", pageable);
	}
	
	public Specification<ProductInfo> search(String grCd) {
		//return this.productDAO.findByProductId(productId);
		return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<ProductInfo> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거 
                return cb.and(cb.like(q.get("grade_cd"), "%" + grCd + "%"));
            }
        };
	}
	
	public List getProductPriceByProductId(String productId) {
		return this.productDAO.findByProductId(productId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Integer modifyProductStock(String orderNo) {
		String sql = this.productJdbcDAO.modifyProductStock("N");
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, orderNo);
		_queryValue.add(1, "001");
		
		return this.utilService.getQueryStringUpdate(sql,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void removeProductInfo(ProductDTO productDTO) {
		ProductInfo productInfo = new ProductInfo();
		productInfo = modelMapper.map(productDTO, ProductInfo.class);
		
		List<ProductInfo> _productInfo = this.productDAO.findByProductId(productInfo.getProductId());
		
		_productInfo.get(0).setUseYn(productInfo.getUseYn());
		
		this.productDAO.save(_productInfo.get(0));
	}
	
	public List getTagInfo(String productId) {
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		
		String productTagSql = this.produstTagJdbcDAO.productTgaList();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		_queryValue.add(1, "Y");
		
		return this.utilService.getQueryString(productTagSql,tagInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addTagInfo(TagInfoDTO tagInfoDTO) {
		Optional<TagInfo> _tagInfo = this.tagDAO.findByTagName(tagInfoDTO.getTagName());
		
		if(!_tagInfo.isPresent()) {
			TagInfo tagInfo = new TagInfo();
			tagInfo = modelMapper.map(tagInfoDTO, TagInfo.class);
			
			this.tagDAO.save(tagInfo);
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String addProductTagInfo(List<ProductTagInfoDTO> productTagInfoList) {
		String _rtnMsg = "저장 되었습니다.";
		String _productId = "";
		Long _tagId;
		
		Map productTagInfoMap = new HashMap<>();
		
		int productTagInfoListSize = productTagInfoList.size();
		
		ProductTagInfoDTO productTagInfoDTO = new ProductTagInfoDTO();
		
		for( int i = 0; i < productTagInfoListSize; i++ ) {
			productTagInfoMap = (Map) productTagInfoList.get(i);
			
			_productId = productTagInfoMap.get("productId").toString();
			_tagId = Long.valueOf(productTagInfoMap.get("tagId").toString());
			
			Optional<TagInfo> _tagInfo = this.tagDAO.findByTagId(_tagId);
			
			_productId = productTagInfoMap.get("productId").toString();
			
			if(_tagInfo.isPresent()) {
				productTagInfoDTO = new ProductTagInfoDTO();
				
				productTagInfoDTO.setProductId(_productId);
				productTagInfoDTO.setTagId(BigInteger.valueOf(_tagId));
				
				ProductTagInfo productTagInfo = new ProductTagInfo();
				ProductTagInfoId productTagInfoId = new ProductTagInfoId();
				
				productTagInfoId = modelMapper.map(productTagInfoDTO, ProductTagInfoId.class);
				
				productTagInfo.setProductTagInfoId(productTagInfoId);
				
				this.productTagDAO.save(productTagInfo);
			}
		}
		
		return _rtnMsg;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getTagList(TagInfoDTO tagInfoDTO) {
		return this.tagDAO.findByUseYn("Y");
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void removeProductTagInfo(String productId) {
		String _sql = this.produstTagJdbcDAO.removeProductTgaInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		
		this.utilService.getQueryStringUpdate(_sql, _queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addProductIntegratedInfo(ProductDTO productDTO, List productImageList, List noticeImageList
										, List deletedProductImageList, List deletedNoticeImageList, List tagInfoList) throws IllegalStateException, IOException {
		//상품등록
		String _productId = this.addProductInfo(productDTO);
		//상품 이미지 등록
		this.productImageService.modifyProductImageInfoList(productImageList, _productId, "01");
		//콘텐츠 이미지 등록
		this.productImageService.modifyProductImageInfoList(productImageList, _productId, "02");
		//이미지 삭제
		this.productImageService.removeProductImageInfoList(deletedProductImageList, _productId);
		//이미지 삭제
		this.productImageService.removeProductImageInfoList(deletedNoticeImageList, _productId);
		//대표이미지 설정
		String delegateFileNumber = this.productImageJdbcDAO.delegateFileNumber();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _productId);
		_queryValue.add(1, "01");
		String fileNumber = this.utilService.getQueryStringChk(delegateFileNumber, _queryValue);
		
		String setDelegateFileNumber = this.productImageJdbcDAO.setDelegateFileNumber();
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, fileNumber);
		_queryValue1.add(1, _productId);
		this.utilService.getQueryStringUpdate(setDelegateFileNumber, _queryValue1);
		//상품태그 삭제
		this.removeProductTagInfo(_productId);
		//상품태그 등록
		int productTagInfoListSize = tagInfoList.size();
		Map productTagInfoMap = new HashMap<>();
		for( int i = 0; i < productTagInfoListSize; i++ ) {
			productTagInfoMap = (Map) tagInfoList.get(i);
			
			productTagInfoMap.put("productId", _productId);
		}
		this.addProductTagInfo(tagInfoList);
		
//		if("".equals(productDTO.getProductId()) || productDTO.getProductId() == null) {
//			
//			String productId = utilService.getNextVal("PRODUCT_ID");
//			
//			productDTO.setProductId(productId);
//		}
//		
//		ProductInfo productInfo = new ProductInfo();
//		productInfo = modelMapper.map(productDTO, ProductInfo.class);
//		return this.productDAO.save(productInfo).getProductId();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addProductInfoList(ProductDTO productDTO, List productImageList, List tagInfoList) throws IllegalStateException, IOException {
		//상품등록
		String _productId = this.addProductInfo(productDTO);
		//상품 이미지 등록
		this.productImageService.mergeProductImageInfoList(productImageList, _productId, "");
		//콘텐츠 이미지 등록
		//this.productImageService.modifyProductImageInfoList(productImageList, _productId, "02");
		//이미지 삭제
		//this.productImageService.removeProductImageInfoList(deletedProductImageList, _productId);
		//이미지 삭제
		//this.productImageService.removeProductImageInfoList(deletedNoticeImageList, _productId);
		//대표이미지 설정
		String delegateFileNumber = this.productImageJdbcDAO.delegateFileNumber();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, _productId);
		_queryValue.add(1, "01");
		String fileNumber = this.utilService.getQueryStringChk(delegateFileNumber, _queryValue);
		
		if(!"".equals(fileNumber)){
			String setDelegateFileNumber = this.productImageJdbcDAO.setDelegateFileNumber();
			ArrayList<String> _queryValue1 = new ArrayList<>();
			_queryValue1.add(0, fileNumber);
			_queryValue1.add(1, _productId);
			this.utilService.getQueryStringUpdate(setDelegateFileNumber, _queryValue1);
		}
		//상품태그 삭제
		this.removeProductTagInfo(_productId);
		//상품태그 등록
		int productTagInfoListSize = tagInfoList.size();
		Map productTagInfoMap = new HashMap<>();
		for( int i = 0; i < productTagInfoListSize; i++ ) {
			productTagInfoMap = (Map) tagInfoList.get(i);
			
			productTagInfoMap.put("productId", _productId);
		}
		this.addProductTagInfo(tagInfoList);
		
//		if("".equals(productDTO.getProductId()) || productDTO.getProductId() == null) {
//			
//			String productId = utilService.getNextVal("PRODUCT_ID");
//			
//			productDTO.setProductId(productId);
//		}
//		
//		ProductInfo productInfo = new ProductInfo();
//		productInfo = modelMapper.map(productDTO, ProductInfo.class);
//		return this.productDAO.save(productInfo).getProductId();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String productCopyOrigin(ProductDTO productDTO, String userId) {
		String productId = "";
		
		List<ProductInfo> productInfo = this.productDAO.findByProductId(productDTO.getOriginalProductId());
		
		if(productInfo.size() > 0) {
			//상품정보
			productId = utilService.getNextVal("PRODUCT_ID");
			
			ArrayList<String> _queryValue = new ArrayList<>();
			
			_queryValue.add(0, productId);
			_queryValue.add(1, userId);
			_queryValue.add(2, userId);
			_queryValue.add(3, productDTO.getOriginalProductId());
			
			//상품복사 
			this.utilService.getQueryStringUpdate(this.productJdbcDAO.productInfoCopyOrigin(), _queryValue);
			//productInfo.get(0).setProductId(productId);
			//this.productDAO.save(productInfo.get(0));
			//상품이미지복사 
			this.utilService.getQueryStringUpdate(this.productImageJdbcDAO.productImeageCopyOrigin(), _queryValue);
			//상품태그복사 
			this.utilService.getQueryStringUpdate(this.produstTagJdbcDAO.productTagCopyOrigin(), _queryValue);
		}
		
		return productId;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyProductPrice(AdminProductDTO adminProductDTO) {
		
		List<ProductInfo> productInfo = this.productDAO.findByProductId(adminProductDTO.getProductId());
		
		productInfo.get(0).setSupplyPrice(new BigDecimal(adminProductDTO.getChangProductPrice()));
		productInfo.get(0).setProductPrice(adminProductDTO.getProductPrice());
	}
	
	public List getEventProductList(ProductCDTO productCDTO) throws IllegalAccessException, InvocationTargetException {
		String categoryCodeYn = "N";
		String productIdYn = "N";
		String productNameYn = "N";
		String favoriteYn = productCDTO.getFavoriteYn();
		String sellerId = productCDTO.getSellerId();
		String sellerYn = productCDTO.getSellerSearchYn();
		String sellerIdYn = productCDTO.getSellerIdYn();
		String useYn = productCDTO.getUseYn();
		String eventId = productCDTO.getEventId();
		Boolean isSoldOutHidden = productCDTO.getIsSoldOutHidden();
		String productSortCode = productCDTO.getProductSortCode();
		
		String sql = this.productJdbcDAO.eventProductInfo();
		EventProductInfoListDTO eventProductInfoListDTO = new EventProductInfoListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		int paramCnt = 0;
	
		if(!("".equals(eventId) || eventId == null)) {
			_queryValue.add(paramCnt, eventId);
			paramCnt++;
		}
		
		List<EventProductInfoListDTO> eventProductList = this.utilService.getQueryString(sql,eventProductInfoListDTO,_queryValue);
		
		int productListSize =  eventProductList.size();
		
		ArrayList<EventProductInfoListPDTO> productInfoListPDTO = new ArrayList<EventProductInfoListPDTO>();
		EventProductInfoListPDTO productInfoListInfo = new EventProductInfoListPDTO();
		
		String productTagSql = this.produstTagJdbcDAO.productTgaList();
		String eventInfoSql = this.eventMstJdbcDAO.eventProductInfo();
		
		ArrayList<String> _productTagQueryValue = new ArrayList<>();
		ArrayList<String> _eventInfoQueryValue = new ArrayList<>();
		
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		EventProductInfoDTO eventProductInfoDTO = new EventProductInfoDTO();
		ParameterUtils ParameterUtils = new ParameterUtils();
		
		//영업일여부
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
		Date date = new Date();
		String _date = sf.format(date);
		String _dateArray[] =  _date.split(" ");
		
		String _toDate = _dateArray[0];
		String _toTime = _dateArray[1];
		String slsDateList[];
		String slsDateText = "";
		int _time = Integer.parseInt(_toTime);
		int _todayDeliveryTime = 0;
		String slsDayYn = "N";
		String _eventId = "";
		String userId = productCDTO.getUserId();
		BigDecimal productStock = new BigDecimal(0);
		BigDecimal _zero = new BigDecimal(0);
		BigDecimal personBuyCount = new BigDecimal(0);
		BigDecimal eventCount = new BigDecimal(0);
		BigDecimal personPaymentCount = new BigDecimal(0);
		BigDecimal eventPaymentCount = new BigDecimal(0);
		
		for(int i = 0; i < productListSize; i++) {
			_eventId = "";
			productInfoListInfo = new EventProductInfoListPDTO();
			_eventId = eventProductList.get(i).getEventId();
			slsDateText = eventProductList.get(i).getSlsDateText();
			
			if(slsDateText.indexOf(_toDate) > -1) {
				slsDayYn = "N";
			} else {
				slsDayYn = this.utilService.getSlsDayYn(_toDate);
			}
			
			slsDateList = slsDateText.split(";");
			
			productInfoListInfo.setProductId(eventProductList.get(i).getProductId());
			productInfoListInfo.setProductName(eventProductList.get(i).getProductName());
			productInfoListInfo.setProductPrice(eventProductList.get(i).getProductPrice());
			productInfoListInfo.setOriginalProductPrice(eventProductList.get(i).getOriginalProductPrice());
			productInfoListInfo.setProductStock(eventProductList.get(i).getProductStock());
			productInfoListInfo.setProductDesc(eventProductList.get(i).getProductDesc());
			productInfoListInfo.setThumbnailImagePath(eventProductList.get(i).getThumbnailImagePath());
			productInfoListInfo.setCumulativeSalesCount(eventProductList.get(i).getCumulativeSalesCount());
			productInfoListInfo.setShopName(eventProductList.get(i).getShopName());
			productInfoListInfo.setUseYn(eventProductList.get(i).getUseYn());
			productInfoListInfo.setCombinedDeliveryStandardAmount(eventProductList.get(i).getCombinedDeliveryStandardAmount());
			productInfoListInfo.setDeliveryAmount(eventProductList.get(i).getDeliveryAmount());
			productInfoListInfo.setCombinedDeliveryYn(eventProductList.get(i).getCombinedDeliveryYn());
			productInfoListInfo.setDiscountRate(eventProductList.get(i).getDiscountRate());
			productInfoListInfo.setEventBeginTime(eventProductList.get(i).getEventBeginTime());
			productInfoListInfo.setEventEndTime(eventProductList.get(i).getEventEndTime());
			productInfoListInfo.setEventBeginDate(eventProductList.get(i).getEventBeginDate());
			productInfoListInfo.setEventEndDate(eventProductList.get(i).getEventEndDate());
			
			if(eventProductList.get(i).getTodayDeliveryStandardTime() == null) {
				productInfoListInfo.setTodayDeliveryStandardTime("1000");
			}else {
				productInfoListInfo.setTodayDeliveryStandardTime(eventProductList.get(i).getTodayDeliveryStandardTime());
			}
			
			_todayDeliveryTime = Integer.parseInt(eventProductList.get(i).getTodayDeliveryStandardTime());
			
			if(_todayDeliveryTime >= _time && "Y".equals(slsDayYn)) {
				productInfoListInfo.setTodayDeliveryYn("Y");
			}else {
				productInfoListInfo.setTodayDeliveryYn("N");
			}
			
			//if("N".equals(productInfoListInfo.getTodayDeliveryYn())) {
				productInfoListInfo.setSlsDate(this.utilService.getSlsDate(_toDate , productInfoListInfo.getTodayDeliveryYn(), slsDateList));
			//}else {
			//	productInfoListInfo.setSlsDate(this.utilService.getSlsDate(_toDate , "Y"));
			//}
			
			productInfoListPDTO.add(i, productInfoListInfo);
			
			_productTagQueryValue = new ArrayList<>();
			_productTagQueryValue.add(0, eventProductList.get(i).getProductId());
			_productTagQueryValue.add(1, "Y");
			
			List<TagInfoDTO> tagInfoList = this.utilService.getQueryString(productTagSql,tagInfoDTO,_productTagQueryValue);
			
			if(!("".equals(_eventId) || _eventId == null)) {
				EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
				eventInfoCDTO.setSearchGb("000");
				eventInfoCDTO.setEventCfcd("000");
				eventInfoCDTO.setPageCnt("0");
				eventInfoCDTO.setMaxPage("5");
				eventInfoCDTO.setEventId(_eventId);
				//이벤트정보
				List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
				
				if(!("".equals(userId) || userId == null)) {
					_eventInfoQueryValue = new ArrayList<>();
					_eventInfoQueryValue.add(0, userId);
					_eventInfoQueryValue.add(1, "003");
					_eventInfoQueryValue.add(2, eventProductList.get(i).getProductId());
					_eventInfoQueryValue.add(3, eventProductList.get(i).getProductId());
					_eventInfoQueryValue.add(4, "003");
					_eventInfoQueryValue.add(5, _eventId);
				
					List<EventProductInfoDTO> eventProductInfo = this.utilService.getQueryString(eventInfoSql,eventProductInfoDTO,_eventInfoQueryValue);
					
					personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
					eventCount = eventProductInfo.get(0).getEventCount();
					personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
					eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
				}
				
				productStock = productInfoListInfo.getProductStock();
				
				if(personBuyCount.compareTo(_zero) == 0) {
					personBuyCount = productStock;
				} else {
					personBuyCount = personBuyCount.subtract(personPaymentCount);
					
					if(personBuyCount.compareTo(_zero) < 0) {
						personBuyCount = _zero;
					}
				}
				
				if(eventCount.compareTo(_zero) == 0) {
					eventCount = productStock;
				} else {
					eventCount = eventCount.subtract(eventPaymentCount);
					
					if(eventCount.compareTo(_zero) < 0) {
						eventCount = _zero;
					}
				}
			
				//productInfoListInfo.setEventBeginTime(eventProductInfo.get(0).getEventBeginTime());
				//productInfoListInfo.setEventEndTime(eventProductInfo.get(0).getEventEndTime());
				productInfoListInfo.setPersonBuyCount(personBuyCount);
				productInfoListInfo.setEventCount(eventCount);
				productInfoListInfo.setEventId(_eventId);
				//productInfoListInfo.setEventBeginDate(eventProductInfo.get(0).getEventBeginDate());
				//productInfoListInfo.setEventEndDate(eventProductInfo.get(0).getEventEndDate());
				
				if("001".equals(eventInfoList.get(0).getEventStateCode())) {
					productInfoListInfo.setProductStock(eventCount);
				} else {
					productInfoListInfo.setProductStock(_zero);
				}
				
			}
			productInfoListPDTO.get(i).setProductTagList((ArrayList<TagInfoDTO>) tagInfoList);
		}
		//List<TagInfo> tagInfo = this.tagDAO.findByTagIdAndUseYn(_productTagQueryValue, "Y");
		
		return productInfoListPDTO;
	}
}
