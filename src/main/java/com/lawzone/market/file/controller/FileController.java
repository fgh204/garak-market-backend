package com.lawzone.market.file.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.criteria.CommonAbstractCriteria;

import org.apache.commons.collections.map.MultiValueMap;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lawzone.market.aws.service.S3Upload;
import com.lawzone.market.file.FileDto;
import com.lawzone.market.image.dao.ProductImageDAO;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.image.service.ProductImageInfo;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.product.controller.ProductController;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/file")
public class FileController {
	private final TelmsgLogService telmsgLogService;
	private final ProductImageService productImageService;
	private final ModelMapper modelMapper;
	
	private final S3Upload s3Upload;
	
	@ResponseBody
	@PostMapping("/upload2")
	public String upload(@RequestPart(value="file",required = false) MultipartFile[] uploadFile) throws IllegalStateException, IOException{
		List<FileDto> list = new ArrayList<>();
		
		for(MultipartFile file : uploadFile) {
			if(!file.isEmpty()) {
				FileDto fileDto = new FileDto(UUID.randomUUID().toString(),
											file.getOriginalFilename(),
											file.getContentType());
				list.add(fileDto);
				
				File newFileName = new File(fileDto.getUuid() + "_" + fileDto.getFileName());
				
				file.transferTo(newFileName);
			}
		}
		
        return JsonUtils.returnValue("0000","등록되었습니다.",list);
	}
	
	@ResponseBody
	@PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public String upload2(@RequestPart(value="file",required = false) MultipartFile[] uploadFile,
			@RequestPart(value="productInfo",required = false) Map map) throws IllegalStateException, IOException{
		log.info("uploadFile=" + uploadFile);
		log.info("productId=" + map.get("productId"));
		
		this.fileUpload(map.get("productId").toString(),uploadFile);
		
        return JsonUtils.returnValue("0000","등록되었습니다.","");
	}
	
	public void fileUpload(String productId, MultipartFile[] uploadFile) throws IllegalStateException, IOException {
		List<ProductImageDTO> list = new ArrayList<>();
		
		for(MultipartFile file : uploadFile) {
			if(!file.isEmpty()) {
				ProductImageDTO productImageDTO = new ProductImageDTO(productId,
						UUID.randomUUID().toString() + "_" + file.getName(),
						file.getOriginalFilename(),
						file.getOriginalFilename(),
						"","",file.getSize());
											
				list.add(productImageDTO);
				
				File newFileName = new File(productImageDTO.getFileName());
	
				String _productId = productImageService.addProductImageInfo(productImageDTO);
				
				if(_productId != null) {
					file.transferTo(newFileName);
				}
			}
		}
	}
	
	
	@ResponseBody
	@PostMapping("/awsUpload")
	public String awsUpload(@RequestPart(value="file",required = false) MultipartFile[] uploadFile,
			@RequestPart(value="productInfo",required = false) Map map) throws IllegalStateException, IOException{
		List<ProductImageDTO> list = new ArrayList<>();
		int imgCnt = 0;
		String _delegateThumbNailYn = "N";
		for(MultipartFile file : uploadFile) {
			_delegateThumbNailYn = "N";
			if(!file.isEmpty()) {
				if(imgCnt == 0) {
					_delegateThumbNailYn = "Y";
				}
				String _fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				ProductImageDTO productImageDTO = new ProductImageDTO(map.get("productId").toString(),
						_fileName,
						file.getOriginalFilename(),
						"",
						_delegateThumbNailYn,
						"",
						file.getSize());
											
				list.add(productImageDTO);
				
				String imeageUrl = s3Upload.upload(file, _fileName);
				
				log.info("imeageUrl===" + imeageUrl);
				
				if(imeageUrl != null) {
					productImageDTO.setThumbnailImagePath(imeageUrl);
					
					productImageService.addProductImageInfo(productImageDTO);
				}
				imgCnt++;
			}
		}
		
        return JsonUtils.returnValue("0000","등록되었습니다.",list);
	}
	
	public void productFileUpload(String productId, MultipartFile[] uploadFile) throws IllegalStateException, IOException {
		List<ProductImageDTO> list = new ArrayList<>();
		int imgCnt = 0;
		String _delegateThumbNailYn = "N";
		for(MultipartFile file : uploadFile) {
			_delegateThumbNailYn = "N";
			if(!file.isEmpty()) {
				if(imgCnt == 0) {
					_delegateThumbNailYn = "Y";
				}
				String _fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				ProductImageDTO productImageDTO = new ProductImageDTO(productId,
						_fileName,
						file.getOriginalFilename(),
						"",
						_delegateThumbNailYn,
						"",
						file.getSize());
											
				list.add(productImageDTO);
				
				String imeageUrl = s3Upload.upload(file, _fileName);
				
				log.info("imeageUrl===" + imeageUrl);
				
				if(imeageUrl != null) {
					productImageDTO.setThumbnailImagePath(imeageUrl);
					
					productImageService.addProductImageInfo(productImageDTO);
				}
				imgCnt++;
			}
		}
	}
}

