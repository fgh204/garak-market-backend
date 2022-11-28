package com.lawzone.market.product.controller;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.externalLink.util.DoobalHeroUtils;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.product.service.ProductTagInfoDTO;
import com.lawzone.market.product.service.TagInfoDTO;
import com.lawzone.market.question.Question;
import com.lawzone.market.review.service.ProductReviewInfoDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.SellerInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	private final ProductReviewInfoService productReviewInfoService;
	private final ProductImageService productImageService;
	private final TelmsgLogService telmsgLogService;
	private final SellerInfoService sellerInfoService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addProductInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(map, productDTO, "insert", sessionBean);
		String productId = this.productService.addProductInfo(productDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/createIntegrated")
	public String addProductIntegratedInfo(HttpServletRequest request
			, @RequestPart(value="productImage1",required = false) MultipartFile[] productImage1
			, @RequestPart(value="productImage2",required = false) MultipartFile[] productImage2
			, @RequestPart(value="productImage3",required = false) MultipartFile[] productImage3
			, @RequestPart(value="productImage4",required = false) MultipartFile[] productImage4
			, @RequestPart(value="productImage5",required = false) MultipartFile[] productImage5
			, @RequestPart(value="productImage6",required = false) MultipartFile[] productImage6
			, @RequestPart(value="productImage7",required = false) MultipartFile[] productImage7
			, @RequestPart(value="productImage8",required = false) MultipartFile[] productImage8
			, @RequestPart(value="productImage9",required = false) MultipartFile[] productImage9
			, @RequestPart(value="productImage10",required = false) MultipartFile[] productImage10
			, @RequestPart(value="noticeImage1",required = false) MultipartFile[] noticeImage1
			, @RequestPart(value="noticeImage2",required = false) MultipartFile[] noticeImage2
			, @RequestPart(value="noticeImage3",required = false) MultipartFile[] noticeImage3
			, @RequestPart(value="noticeImage4",required = false) MultipartFile[] noticeImage4
			, @RequestPart(value="noticeImage5",required = false) MultipartFile[] noticeImage5
			, @RequestPart(value="productInfo",required = false) Map productInfoMap
			, @RequestPart(value="productTagList",required = false) Map productTagListMap
			, @RequestPart(value="deletedImageList",required = false) Map deletedImageListMap
			) throws IllegalStateException, IOException{		
		Map logMap = new HashMap<>();
		
		logMap.put(productInfoMap, productInfoMap);
		logMap.put(productTagListMap, productTagListMap);
		logMap.put(deletedImageListMap, deletedImageListMap);
		
		this.telmsgLogService.addTelmsgLog("00", "00", "1", logMap);
		Map productMap = new HashMap<>();
		Map productTagInfoMap = new HashMap<>();
		
		productMap.put("dataset", productInfoMap);
		
		//상품정보
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(productMap, productDTO, "insert", sessionBean);
		
		if("".equals(productDTO.getUseYn()) ||  productDTO.getUseYn() == null) {
			productDTO.setUseYn("Y");
		}
		
		if("".equals(productDTO.getSellerId()) ||  productDTO.getSellerId() == null) {
			productDTO.setSellerId(sessionBean.getUserId());
		}
		
		if("".equals(productDTO.getBeginDate()) ||  productDTO.getBeginDate() == null) {
			productDTO.setBeginDate("now()");
		}
		
		//상품태그정보 리스트
		List<ProductTagInfoDTO> productTagInfoList = (List) productTagListMap.get("productTagList");
		
		//이미지 삭제 리스트
		List<ProductImageDTO> productDeleteImageList = (List) deletedImageListMap.get("deletedImageList");
		
		//이미지 리스트
		List productImageList = new ArrayList<>();
		if(productImage1 != null) productImageList.add(productImage1);
		if(productImage2 != null) productImageList.add(productImage2);
		if(productImage3 != null) productImageList.add(productImage3);
		if(productImage4 != null) productImageList.add(productImage4);
		if(productImage5 != null) productImageList.add(productImage5);
		if(productImage6 != null) productImageList.add(productImage6);
		if(productImage7 != null) productImageList.add(productImage7);
		if(productImage8 != null) productImageList.add(productImage8);
		if(productImage9 != null) productImageList.add(productImage9);
		if(productImage10 != null) productImageList.add(productImage10);
		
		List noticeImageList = new ArrayList<>();
		if(noticeImage1 != null) noticeImageList.add(noticeImage1);
		if(noticeImage2 != null) noticeImageList.add(noticeImage2);
		if(noticeImage3 != null) noticeImageList.add(noticeImage3);
		if(noticeImage4 != null) noticeImageList.add(noticeImage4);
		if(noticeImage5 != null) noticeImageList.add(noticeImage5);
		
		this.productService.addProductIntegratedInfo(productDTO, productImageList, noticeImageList, productDeleteImageList, productTagInfoList);
		
//		//상품등록 addProductIntegratedInfo
//		String _productId = this.productService.addProductInfo(productDTO);
//		//이미지 삭제
//		this.productImageService.removeProductImageInfoList(productDeleteImageList);
//		//이미지등록
//		this.productImageService.productFileUploadList(_productId, rtnList);
//		//태그정보 설정
//		int productTagInfoListSize = productTagInfoList.size();
//		
//		for( int i = 0; i < productTagInfoListSize; i++ ) {
//			productTagInfoMap = (Map) productTagInfoList.get(i);
//			
//			productTagInfoMap.put("productId", _productId);
//		}
//		//태그정보삭제
//		this.productService.removeProductTagInfo(_productId);
//		//태그정보등록
//		this.productService.addProductTagInfo(productTagInfoList);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/list")
	public String getproductList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		List productList = null;
		productList = this.productService.getProductList();
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("productList", productList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
		//log.info(rtnValue);
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/detail")
	public String getPrdDetailInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductDTO produstDTO = new ProductDTO();
		produstDTO = (ProductDTO) ParameterUtils.setDto(map, produstDTO, "insert", sessionBean);
		
		List productList = null;
		List productImageList = null;
		List productReviewList = null;
		List tagInfoList = null;
		
		String productId = produstDTO.getProductId();
		//상품정보
		productList = this.productService.getProductDetailInfo(productId);
		
		Map orderMap = new HashMap<>();
		orderMap.put("productId", productId);
    	orderMap.put("pageCount", "0");
    	orderMap.put("maxPageCount", "5");
    	orderMap.put("orderCd", "02");
		
    	//상품리뷰정보
    	productReviewList = this.productReviewInfoService.getProductReviewList(orderMap);
    	
    	//상품이미지정보
    	productImageList = this.productImageService.getProductImageInfoList(productId);
		
    	//상품태그정보
    	tagInfoList = this.productService.getTagInfo(productId);
    	
		Map rtnMap = new HashMap<>();
		rtnMap.put("productInfo", productList.get(0));
		rtnMap.put("productReviewList", productReviewList);
		rtnMap.put("productImageList", productImageList);
		rtnMap.put("productTagList", tagInfoList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
		//log.info(rtnValue);
		
		return rtnValue;
	}
	
	@ResponseBody
	@RequestMapping("/productList")
    public String getList(HttpServletRequest request
    		, HttpServletResponse response
    		, @RequestBody() Map map) throws IllegalAccessException, InvocationTargetException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();
		
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
		
		//List<Question> questionList = this.questionService.getList();
        //model.addAttribute("questionList", questionList);
    	//log.info("page:{}", productCDTO.getPageCount());
    	
    	if("".equals(productCDTO.getMaxPageCount()) || productCDTO.getMaxPageCount() == null) {
    		productCDTO.setMaxPageCount("10");
    	}
    	
    	if("".equals(productCDTO.getPageCount()) || productCDTO.getPageCount() == null) {
    		productCDTO.setPageCount("0");
    	}else {
    		int _currentCnt = Integer.parseInt(productCDTO.getPageCount());
    		int _limitCnt = Integer.parseInt(productCDTO.getMaxPageCount());
    		
    		productCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
    	}
    	
    	if("Y".equals(productCDTO.getSellerSearchYn())) {
    		productCDTO.setSellerSearchYn(this.sellerInfoService.getSellerYn(productCDTO.getUserId()));
    	}
    	
    	List<PageInfoDTO> pageInfo = this.productService.getPageInfo(productCDTO);
    	
    	List<ProductInfoListDTO> productList = this.productService.getList2(productCDTO);
    	
    	Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("productList", productList);
    	
    	String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
        return rtnValue;
    }
	
	@ResponseBody
	@PostMapping("/remove")
	public String removeProductInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductDTO produstDTO = new ProductDTO();
		produstDTO = (ProductDTO) ParameterUtils.setDto(map, produstDTO, "insert", sessionBean);
		
		produstDTO.setUseYn("N");
		
		this.productService.removeProductInfo(produstDTO);
		
		Map rtnMap = new HashMap<>();
		
		String rtnValue = JsonUtils.returnValue("0000", "삭제되었습니다.", rtnMap).toString();
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/tag/list")
	public String getTagList(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		tagInfoDTO = (TagInfoDTO) ParameterUtils.setDto(map, tagInfoDTO, "insert", sessionBean);
		
		List productTagList = this.productService.getTagList(tagInfoDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("productTagList", productTagList);
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/tag/create")
	public String addTagInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		TagInfoDTO tagInfoDTO = new TagInfoDTO();
		tagInfoDTO = (TagInfoDTO) ParameterUtils.setDto(map, tagInfoDTO, "insert", sessionBean);
		
		this.productService.addTagInfo(tagInfoDTO);
		
		Map rtnMap = new HashMap<>();
		
		String rtnValue = JsonUtils.returnValue("0000", "저장되었습니다.", rtnMap).toString();
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/productTag/create")
	public String addProductTagInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		Map dataMap = (Map) map.get("dataset");
		Map productInfoMap = (Map) dataMap.get("productInfo");
		Map productTagInfoMap = new HashMap<>();
		
		String _productId = (String) productInfoMap.get("productId");
		
		List<ProductTagInfoDTO> productTagInfoList = (List) dataMap.get("productTagList");
		
		int productTagInfoListSize = productTagInfoList.size();
		
		for( int i = 0; i < productTagInfoListSize; i++ ) {
			productTagInfoMap = (Map) productTagInfoList.get(i);
			
			productTagInfoMap.put("productId", _productId);
		}
		
		String _rtnMsg = this.productService.addProductTagInfo(productTagInfoList);
		
		Map rtnMap = new HashMap<>();
		
		String rtnValue = JsonUtils.returnValue("0000", _rtnMsg, rtnMap).toString();
		
		return rtnValue;
	}
}
