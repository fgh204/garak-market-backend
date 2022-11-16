package com.lawzone.market.product.controller;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.externalLink.util.DoobalHeroUtils;
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
			, @RequestPart(value="file1",required = false) MultipartFile[] uploadFile1
			, @RequestPart(value="file2",required = false) MultipartFile[] uploadFile2
			, @RequestPart(value="file3",required = false) MultipartFile[] uploadFile3
			, @RequestPart(value="file4",required = false) MultipartFile[] uploadFile4
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile5
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile6
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile7
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile8
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile9
			, @RequestPart(value="file5",required = false) MultipartFile[] uploadFile10
			, @RequestPart(value="productInfo",required = false) Map productInfoMap
			, @RequestPart(value="productTagList",required = false) Map productTagListMap
			) throws IllegalStateException, IOException{
		this.telmsgLogService.addTelmsgLog("00", "00", "1", productInfoMap);
		this.telmsgLogService.addTelmsgLog("00", "00", "1", productTagListMap);
		Map productMap = new HashMap<>();
		Map productTagInfoMap = new HashMap<>();
		//Map reviewMap = new HashMap<>();
		productMap.put("dataset", productInfoMap);
		//reviewMap.put("dataset", reviewInfoMap);
		
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(productMap, productDTO, "insert", sessionBean);
			
		productDTO.setUseYn("Y");
		String _productId = this.productService.addProductInfo(productDTO);
		
		List rtnList = new ArrayList<>();
		
		if(uploadFile1 != null) rtnList.add(uploadFile1);
		if(uploadFile2 != null) rtnList.add(uploadFile2);
		if(uploadFile3 != null) rtnList.add(uploadFile3);
		if(uploadFile4 != null) rtnList.add(uploadFile4);
		if(uploadFile5 != null) rtnList.add(uploadFile5);
		if(uploadFile6 != null) rtnList.add(uploadFile6);
		if(uploadFile7 != null) rtnList.add(uploadFile7);
		if(uploadFile8 != null) rtnList.add(uploadFile8);
		if(uploadFile9 != null) rtnList.add(uploadFile9);
		if(uploadFile10 != null) rtnList.add(uploadFile10);
		
		this.productImageService.productFileUploadList(_productId, rtnList);
		
		List<ProductTagInfoDTO> productTagInfoList = (List) productTagListMap.get("productTagList");
		
		int productTagInfoListSize = productTagInfoList.size();
		
		for( int i = 0; i < productTagInfoListSize; i++ ) {
			productTagInfoMap = (Map) productTagInfoList.get(i);
			
			productTagInfoMap.put("productId", _productId);
		}
		
		String _rtnMsg = this.productService.addProductTagInfo(productTagInfoList);
		
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
		
		log.info(rtnValue);
		
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
		
		log.info(rtnValue);
		
		return rtnValue;
	}
	
	@ResponseBody
	@RequestMapping("/productList")
    public String getList(HttpServletRequest request, @RequestBody() Map map) throws JsonMappingException, JsonProcessingException, IllegalAccessException, InvocationTargetException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();
		
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
		
		//List<Question> questionList = this.questionService.getList();
        //model.addAttribute("questionList", questionList);
    	log.info("page:{}", productCDTO.getPageCount());
    	
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
