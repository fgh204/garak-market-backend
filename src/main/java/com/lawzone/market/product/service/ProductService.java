package com.lawzone.market.product.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

import com.lawzone.market.order.service.ProductOrderItemInfoId;
import com.lawzone.market.product.controller.ProductController;
import com.lawzone.market.product.dao.ProductDAO;
import com.lawzone.market.product.dao.ProductJdbcDAO;
import com.lawzone.market.product.dao.ProdustTagDAO;
import com.lawzone.market.product.dao.ProdustTagJdbcDAO;
import com.lawzone.market.product.dao.TagDAO;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
	private final ProductDAO productDAO;
	private final TagDAO tagDAO;
	private final ProdustTagDAO productTagDAO;
	private final ProdustTagJdbcDAO produstTagJdbcDAO;
	private final ProductJdbcDAO productJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	
	@Transactional
	public String addProductInfo(ProductDTO productDTO) {
		
		if("".equals(productDTO.getProductId()) || productDTO.getProductId() == null) {
			
			String productId = utilService.getNextVal("PRODUCT_ID");
			
			productDTO.setProductId(productId);
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
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		
		ProductInfoDTO productInfoDTO = new ProductInfoDTO();
		
		return this.utilService.getQueryString(sql,productInfoDTO,_queryValue);
	}
	
	public List getList2(ProductCDTO productCDTO) throws IllegalAccessException, InvocationTargetException {
		String categoriesCodeYn = "N";
		String productIdYn = "N";
		String productNameYn = "N";
		if(!"".equals(productCDTO.getProductCategoriesCode()) && productCDTO.getProductCategoriesCode() != null){
			categoriesCodeYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductId()) && productCDTO.getProductId() != null){
			productIdYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductName()) && productCDTO.getProductName() != null){
			productNameYn = "Y";
		}
		String sql = this.productJdbcDAO.pageListQuery(productCDTO.getPageCount(), productCDTO.getMaxPageCount()
														,categoriesCodeYn, productIdYn, productNameYn);
		ProductInfoListDTO productInfoListDTO = new ProductInfoListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		int paramCnt = 0;
		
		if("Y".equals(categoriesCodeYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductCategoriesCode());
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
		
		List<ProductInfoListDTO> productList = this.utilService.getQueryString(sql,productInfoListDTO,_queryValue);
		
		int productListSize =  productList.size();
		
		ArrayList<ProductInfoListPDTO> productInfoListPDTO = new ArrayList<ProductInfoListPDTO>();
		ArrayList<ProductInfoListDTO> productInfoList = new ArrayList<ProductInfoListDTO>();
		ProductInfoListPDTO productInfoListInfo = new ProductInfoListPDTO();
		
		String productTagSql = this.produstTagJdbcDAO.productTgaList();
		
		ArrayList<String> _productTagQueryValue;
		
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		
		ParameterUtils ParameterUtils = new ParameterUtils();
		
		for(int i = 0; i < productListSize; i++) {
			productInfoListInfo = new ProductInfoListPDTO();
			
			productInfoListInfo.setProductId(productList.get(i).getProductId());
			productInfoListInfo.setProductName(productList.get(i).getProductName());
			productInfoListInfo.setProductPrice(productList.get(i).getProductPrice());
			productInfoListInfo.setProductStock(productList.get(i).getProductStock());
			productInfoListInfo.setProductDesc(productList.get(i).getProductDesc());
			productInfoListInfo.setThumbnailImagePath(productList.get(i).getThumbnailImagePath());
			productInfoListInfo.setCumulativeSalesCount(productList.get(i).getCumulativeSalesCount());
			productInfoListInfo.setShopName(productList.get(i).getShopName());
			
			productInfoListPDTO.add(i, productInfoListInfo);
			
			_productTagQueryValue = new ArrayList<>();
			_productTagQueryValue.add(0, productList.get(i).getProductId());
			_productTagQueryValue.add(1, "Y");
			
			List<TagInfoDTO> tagInfoList = this.utilService.getQueryString(productTagSql,tagInfoDTO,_productTagQueryValue);
			
			productInfoListPDTO.get(i).setProductTagList((ArrayList<TagInfoDTO>) tagInfoList);
		}
		
		
		return productInfoListPDTO;
	}
	
	public List getPageInfo(ProductCDTO productCDTO) {
		String categoriesCodeYn = "N";
		String productIdYn = "N";
		String productNameYn = "N";
		if(!"".equals(productCDTO.getProductCategoriesCode()) && productCDTO.getProductCategoriesCode() != null){
			categoriesCodeYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductId()) && productCDTO.getProductId() != null){
			productIdYn = "Y";
		}
		
		if(!"".equals(productCDTO.getProductName()) && productCDTO.getProductName() != null){
			productNameYn = "Y";
		}
		
		String sql = this.productJdbcDAO.pageQuery(productCDTO.getMaxPageCount()
				,categoriesCodeYn, productIdYn, productNameYn);
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		int paramCnt = 0;
		
		if("Y".equals(categoriesCodeYn)) {
			_queryValue.add(paramCnt, productCDTO.getProductCategoriesCode());
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
}
