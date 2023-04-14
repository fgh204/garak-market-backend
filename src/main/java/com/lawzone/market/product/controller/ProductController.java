package com.lawzone.market.product.controller;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.imgscalr.Scalr;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.aws.service.S3Controller;
import com.lawzone.market.category.service.ProductCategoryCDTO;
import com.lawzone.market.category.service.ProductCategoryDTO;
import com.lawzone.market.category.service.ProductCategoryInfoService;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.image.service.ProductImageListDTO;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfoDTO;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.product.service.ProductTagInfoDTO;
import com.lawzone.market.product.service.TagInfoDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
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
	private final ProductCategoryInfoService productCategoryInfoService;
	private final S3Controller s3Upload;
	private final JwtTokenUtil jwtTokenUtil;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addProductInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(map, productDTO, "insert", sessionBean);
		this.productService.addProductInfo(productDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/createIntegrated2")
	public String addProductIntegratedInfo2(HttpServletRequest request
//			, @RequestPart(value="productImage1",required = false) MultipartFile[] productImage1
//			, @RequestPart(value="productImage2",required = false) MultipartFile[] productImage2
//			, @RequestPart(value="productImage3",required = false) MultipartFile[] productImage3
//			, @RequestPart(value="productImage4",required = false) MultipartFile[] productImage4
//			, @RequestPart(value="productImage5",required = false) MultipartFile[] productImage5
//			, @RequestPart(value="productImage6",required = false) MultipartFile[] productImage6
//			, @RequestPart(value="productImage7",required = false) MultipartFile[] productImage7
//			, @RequestPart(value="productImage8",required = false) MultipartFile[] productImage8
//			, @RequestPart(value="productImage9",required = false) MultipartFile[] productImage9
//			, @RequestPart(value="productImage10",required = false) MultipartFile[] productImage10
//			, @RequestPart(value="noticeImage1",required = false) MultipartFile[] noticeImage1
//			, @RequestPart(value="noticeImage2",required = false) MultipartFile[] noticeImage2
//			, @RequestPart(value="noticeImage3",required = false) MultipartFile[] noticeImage3
//			, @RequestPart(value="noticeImage4",required = false) MultipartFile[] noticeImage4
//			, @RequestPart(value="noticeImage5",required = false) MultipartFile[] noticeImage5
			, @RequestPart(value="productInfo",required = false) Map productInfoMap
			, @RequestPart(value="productTagList",required = false) Map productTagListMap
			, @RequestPart(value="productImageList",required = false) Map productImageListMap
			, @RequestPart(value="noticeImageList",required = false) Map noticeImageListMap
			, @RequestPart(value="deletedProductImageList",required = false) Map deletedProductImageListMap
			, @RequestPart(value="deletedNoticeImageList",required = false) Map deletedNoticeImageListMap
			) throws IllegalStateException, IOException{		
		Map logMap = new HashMap<>();
		Map rtnMap = new HashMap<>();
		logMap.put("productInfoMap", productInfoMap);
		logMap.put("productTagListMap", productTagListMap);
		logMap.put("productImageListMap", productImageListMap);
		logMap.put("noticeImageListMap", noticeImageListMap);
		logMap.put("deletedProductImageListMap", deletedProductImageListMap);
		logMap.put("deletedNoticeImageListMap", deletedNoticeImageListMap);
		
		this.telmsgLogService.addTelmsgLog("00", "00", "1", logMap,"");
		Map productMap = new HashMap<>();
		Map productTagInfoMap = new HashMap<>();
		
		productMap.put("dataset", productInfoMap);
		
		//상품정보
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(productMap, productDTO, "insert", sessionBean);
		
		String _originalProductId = productDTO.getOriginalProductId();
		String _poductId = "";
		if(!(_originalProductId == null || "".equals(_originalProductId))) {
			//상품복사 상품, 이미지, 태그
			_poductId = this.productService.productCopyOrigin(productDTO, sessionBean.getUserId());
			
			if("".equals(_poductId)) {
				return JsonUtils.returnValue("0000", "복사할 상품정보가 없습니다", rtnMap).toString();
			}else {
				productDTO.setProductId(_poductId);
			}
		}
		
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
		List<ProductImageDTO> deletedProductImageList = (List) deletedProductImageListMap.get("deletedProductImageList");
		
		List<ProductImageDTO> deletedNoticeImageList = (List) deletedNoticeImageListMap.get("deletedNoticeImageList");
		
		//이미지 삭제 리스트
		List<ProductImageDTO> productImageList = (List) productTagListMap.get("productImageListMap");
		
		List<ProductImageDTO> noticeImageList = (List) noticeImageListMap.get("noticeImageListMap");
		
		//이미지 리스트
//		List productImageList = new ArrayList<>();
//		if(productImage1 != null) productImageList.add(productImage1);
//		if(productImage2 != null) productImageList.add(productImage2);
//		if(productImage3 != null) productImageList.add(productImage3);
//		if(productImage4 != null) productImageList.add(productImage4);
//		if(productImage5 != null) productImageList.add(productImage5);
//		if(productImage6 != null) productImageList.add(productImage6);
//		if(productImage7 != null) productImageList.add(productImage7);
//		if(productImage8 != null) productImageList.add(productImage8);
//		if(productImage9 != null) productImageList.add(productImage9);
//		if(productImage10 != null) productImageList.add(productImage10);
		
//		List noticeImageList = new ArrayList<>();
//		if(noticeImage1 != null) noticeImageList.add(noticeImage1);
//		if(noticeImage2 != null) noticeImageList.add(noticeImage2);
//		if(noticeImage3 != null) noticeImageList.add(noticeImage3);
//		if(noticeImage4 != null) noticeImageList.add(noticeImage4);
//		if(noticeImage5 != null) noticeImageList.add(noticeImage5);
		
		this.productService.addProductIntegratedInfo(productDTO, productImageList, noticeImageList, deletedProductImageList,deletedNoticeImageList, productTagInfoList);
		
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
		rtnMap.put("originalProductId", _originalProductId);
		rtnMap.put("copyProductId", _poductId);
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/createIntegrated")
	public String addProductIntegratedInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {		
		Map logMap = new HashMap<>();
		Map rtnMap = new HashMap<>();
		
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map productMap = new HashMap<>();
		Map addProductMap = new HashMap<>();
		Map productTagInfoMap = new HashMap<>();
		
		addProductMap = (Map) map.get("dataset");
		
		productMap.put("dataset", addProductMap.get("productInfo"));
		
		//상품정보
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(productMap, productDTO, "insert", sessionBean);
		
		String _originalProductId = productDTO.getOriginalProductId();
		String _poductId = "";
		if(!(_originalProductId == null || "".equals(_originalProductId))) {
			//상품복사 상품, 이미지, 태그
			_poductId = this.productService.productCopyOrigin(productDTO, sessionBean.getUserId());
			
			if("".equals(_poductId)) {
				return JsonUtils.returnValue("0000", "복사할 상품정보가 없습니다", rtnMap).toString();
			}else {
				productDTO.setProductId(_poductId);
				productDTO.setUseYn("Y");
			}
		}
		
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
		List<ProductTagInfoDTO> productTagInfoList = (List) addProductMap.get("productTagList");
		
		//이미지리스트
		List<ProductImageListDTO> productImageList = (List) addProductMap.get("productImageList");
//		
//		//이미지 삭제 리스트
//		List<ProductImageDTO> deletedProductImageList = (List) deletedProductImageListMap.get("deletedProductImageList");
//		
//		List<ProductImageDTO> deletedNoticeImageList = (List) deletedNoticeImageListMap.get("deletedNoticeImageList");
//		
//		//이미지 삭제 리스트
//		List<ProductImageDTO> productImageList = (List) productTagListMap.get("productImageListMap");
//		
//		List<ProductImageDTO> noticeImageList = (List) noticeImageListMap.get("noticeImageListMap");
		
		this.productService.addProductInfoList(productDTO, productImageList, productTagInfoList);
		rtnMap.put("originalProductId", _originalProductId);
		rtnMap.put("copyProductId", _poductId);
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/list")
	public String getproductList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		List productList = null;
		productList = this.productService.getProductList();
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("productList", productList);
		
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		
		//log.info(rtnValue);
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/uploadImage")
	public JSONObject addProductImage(
			HttpServletRequest request
			, @RequestPart(value="imageFile",required = false) MultipartFile[] productImage
			, @RequestParam(name="imageCfcd", defaultValue="00") String imageCfcd
			) throws IllegalStateException, IOException, ImageProcessingException, MetadataException {
		String imagePath = "";
		Map _map = new HashMap<>();
		_map.put("imageCfcd", imageCfcd);
		this.telmsgLogService.addTelmsgLog("00", "00", "1", _map,"");
		
		Enumeration headerNames = request.getHeaderNames();

//    	while(headerNames.hasMoreElements()) {
//    		   String name = (String)headerNames.nextElement();
//    		   String value = request.getHeader(name);
//    		   
//    		   log.error("name ======================= " + name);
//    		   log.error("value ======================= " + value);
//    		}
		Map imageMap = new HashMap<>();
		imageMap = this.productImageService.productFileUpload("000000000000", productImage, "N", imageCfcd);

		//this.telmsgLogService.addTelmsgLog("00", "00", "2", imageMap);
		JSONObject json =  new JSONObject(imageMap);
		return json;
		
	}
	
	@ResponseBody
	@PostMapping("/uploadImageInfo")
	public JSONObject addProductImageInfo(
			HttpServletRequest request
			, @RequestPart(value="imageFile",required = false) MultipartFile[] productImage
			, @RequestParam(name="imageCfcd", defaultValue="00") String imageCfcd
			) throws IllegalStateException, IOException, ImageProcessingException, MetadataException {
		String imagePath = "";
		Map _map = new HashMap<>();
		_map.put("imageCfcd", imageCfcd);
		this.telmsgLogService.addTelmsgLog("00", "00", "1", _map,"");

		long maxSize = 1204 * 1204 * 1;
		
		byte[] encodeBase64;
		
		File outputfile = null;
		File imageFile = null;
		
		if(productImage[0].getSize() > maxSize) {
			imageFile = new File(productImage[0].getOriginalFilename());
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			fos.write(productImage[0].getBytes());
			fos.close();
			Metadata metadata; // 이미지 메타 데이터 객체
			 Directory directory; // 이미지의 Exif 데이터를 읽기 위한 객체
			JpegDirectory jpegDirectory; // JPG 이미지 정보를 읽기 위한 객체
			
			int orientation = 1;
			
			metadata = ImageMetadataReader.readMetadata(imageFile);
			directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
			if(directory != null){
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
			}
			
			BufferedImage inputImage = ImageIO.read(productImage[0].getInputStream());
			
			switch (orientation) {
			case 6:
				inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_90); 
				break;
			case 1:
		 
				break;
			case 3:
				inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_180);
				break;
			case 8:
				inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_270);
				break;
		 
			default:
				orientation=1;
				break;
			}

			//int width = inputImage.getWidth();
			//int height = inputImage.getHeight();
			
			int _width = inputImage.getWidth() / 2 ;
			int _height = inputImage.getHeight() / 2;
			
//			BigDecimal width = new BigDecimal(1500);
//			BigDecimal oWidth = new BigDecimal(_width);
//			BigDecimal oheight = new BigDecimal(_height);
//			BigDecimal widthRate = width.divide(oWidth, 2, BigDecimal.ROUND_CEILING);
//			
//			oheight = oheight.multiply(widthRate);
//			
//			oheight.setScale(0, RoundingMode.FLOOR);
			Image reImg = inputImage.getScaledInstance(_width, _height,Image.SCALE_SMOOTH );
			BufferedImage outputImage = new BufferedImage(_width, _height, inputImage.getType());
			
			Graphics2D graphics2D = outputImage.createGraphics();
			graphics2D.drawImage(reImg, 0, 0, _width, _height, null);
			graphics2D.dispose();
			
			outputfile = new File( "111" + productImage[0].getOriginalFilename());
			
			 try {
		        if (outputfile.createNewFile()) {
		        	
		    	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    	    
		            String type = productImage[0].getContentType().substring(productImage[0].getContentType().indexOf("/")+1);
		    	    ImageIO.write(outputImage, type, bos);
		    	    
		            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
		            
		            Files.copy(inputStream, outputfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		            
		            
		        }
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			 
//			 DiskFileItem fileItem = new DiskFileItem("file", Files.probeContentType(outputfile.toPath()), false, outputfile.getName(), (int) outputfile.length() , outputfile.getParentFile());
//			 
//			 fileItem.getOutputStream();
//			 
			 File file = new File(outputfile.toPath().toString());
			
			 InputStream input = new FileInputStream(file);
//		     OutputStream os = fileItem.getOutputStream();
//		     IOUtils.copy(input, os);
			 
			 long bytes = Files.size(outputfile.toPath());
			 String aa = Files.probeContentType(outputfile.toPath());
			 String imeageUrl = s3Upload.uploadInputStream(input, productImage[0].getOriginalFilename(), imageCfcd);
			
			 //DiskFileItem lfi = new LocalFileItem(outputfile.toPath());
			 //CommonsMultipartFile cmf = new CommonsMultipartFile(lfi);
			 
			 byte[] fileContent = Files.readAllBytes(outputfile.toPath());
			 
			 encodeBase64 = Base64.encodeBase64(fileContent);
			 
			 if(imageFile.exists()) {
				 imageFile.delete();
			 }
			 if(outputfile.exists()) {
				 outputfile.delete();
			 }
		}else {
			encodeBase64 = Base64.encodeBase64(productImage[0].getBytes());
			String imeageUrl = s3Upload.upload(productImage[0], productImage[0].getOriginalFilename(), imageCfcd);
		}
		
		//File file = new File(productImage.getOriginalFilename());
		
//		Enumeration headerNames = request.getHeaderNames();
//		
//		Map rtnMap = new HashMap<>();
//		Map imageMap = new HashMap<>();
//		imageMap = this.productImageService.productFileUpload("000000000000", productImage, "N", imageCfcd);
//
//		//this.telmsgLogService.addTelmsgLog("00", "00", "2", imageMap);
//		
//		
		//log.info("DatatypeConverter.printBase64Binary(encodeBase64)==" + new String(productImage.getBytes()));
		String _ext = productImage[0].getOriginalFilename().substring(productImage[0].getOriginalFilename().lastIndexOf(".") + 1);

		_ext = _ext.toLowerCase();
		
		if("jpg".equals(_ext)) {
			_ext = "jpeg";
		}
		
		Map rtnMap = new HashMap<>();
		Map imageMap = new HashMap<>();
		
		imageMap.put("name", productImage[0].getOriginalFilename());
		imageMap.put("type", "image/" + _ext);
		
		if(outputfile == null) {
			imageMap.put("size", productImage[0].getSize());
			imageMap.put("lastModified", "");
		}else {
			imageMap.put("size", outputfile.length());
			imageMap.put("lastModified", outputfile.lastModified());
		}
		imageMap.put("base64image", "data:image/" + _ext + ";base64," + new String(encodeBase64));
		
		rtnMap.put("imageInfo", imageMap);
		this.telmsgLogService.addTelmsgLog("00", "00", "1", rtnMap,"");
//		
		 
		JSONObject json =  new JSONObject(rtnMap);
		return json;
	}
	
	@ResponseBody
	@PostMapping("/detail")
	public String getPrdDetailInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductDTO produstDTO = new ProductDTO();
		produstDTO = (ProductDTO) ParameterUtils.setDto(map, produstDTO, "insert", sessionBean);
		
		//List productList = null;
		List productImageList = null;
		List productReviewList = null;
		List tagInfoList = null;
		
		String productId = produstDTO.getProductId();
		//상품정보
		List<ProductInfoDTO> productList = this.productService.getProductDetailInfo(productId);
		
		Map orderMap = new HashMap<>();
		orderMap.put("productId", "");
		orderMap.put("sellerId", productList.get(0).getSellerId());
    	orderMap.put("pageCount", "0");
    	orderMap.put("maxPageCount", "5");
    	orderMap.put("orderCd", "01");
		
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		ProductCDTO productCDTO = new ProductCDTO();
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
    	
		if("Y".equals(productCDTO.getFavoriteYn())) {
			String token = this.jwtTokenUtil.resolveToken((HttpServletRequest) request);
			if(!(token != null && jwtTokenUtil.validateToken(token))) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return null;
			}
		}
		
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
    		//productCDTO.setSellerSearchYn(this.sellerInfoService.getSellerYn(productCDTO.getUserId()));
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
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
	
	@ResponseBody
	@PostMapping("/copy")
	public String productCopyOrigin(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		ProductDTO produstDTO = new ProductDTO();
		produstDTO = (ProductDTO) ParameterUtils.setDto(map, produstDTO, "insert", sessionBean);
		
		this.productService.productCopyOrigin(produstDTO, sessionBean.getUserId());
		
		Map rtnMap = new HashMap<>();
		
		String rtnValue = JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
		
		return rtnValue;
	}
	
	@ResponseBody
	@PostMapping("/category/list")
	public String getLargeList(HttpServletRequest request, @RequestBody(required = true) Map map)  {		
		ProductCategoryCDTO productCategoryCDTO = new ProductCategoryCDTO();
		productCategoryCDTO = (ProductCategoryCDTO) ParameterUtils.setDto(map, productCategoryCDTO, "insert", sessionBean);
		
		List<ProductCategoryDTO> productCategoryList = this.productCategoryInfoService.getLargeList();
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("productCategoryList", productCategoryList);
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
		
		return rtnValue;
	}
}
