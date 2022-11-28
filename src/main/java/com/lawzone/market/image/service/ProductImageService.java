package com.lawzone.market.image.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lawzone.market.aws.service.S3Upload;
import com.lawzone.market.image.dao.ProductImageDAO;
import com.lawzone.market.util.UtilService;
import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductImageService {
	private final ProductImageDAO productImageDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final S3Upload s3Upload;
	
	@Transactional
	public String addProductImageInfo(ProductImageDTO productImageDTO) {
		ProductImageInfo productImageInfo = new ProductImageInfo();
		productImageInfo = modelMapper.map(productImageDTO, ProductImageInfo.class);
		
		return this.productImageDAO.save(productImageInfo).getFileName();
	}
	
	@Transactional
	public List getProductImageInfoList() {
		return this.productImageDAO.findAll();
	}
	
	@Transactional
	public List getProductImageInfoList(String productId) {
		return this.productImageDAO.findByproductId(productId);
	}
	
	public void productFileUploadList(String productId, List uploadList, String imgCfcd) throws IllegalStateException, IOException {
		String delegateYn = "N";
		
		for(int i = 0; i < uploadList.size(); i++) {
			delegateYn = "N";
			if(i == 0) {
				delegateYn = "Y";
			}
			productFileUpload(productId, (MultipartFile[]) uploadList.get(i), delegateYn, imgCfcd);
		}
	}
	
	public void productFileUpload(String productId, MultipartFile[] uploadFile, String delegateYn, String imgCfcd) throws IllegalStateException, IOException {
		List<ProductImageDTO> list = new ArrayList<>();
		int imgCnt = 0;
		for(MultipartFile file : uploadFile) {
			if("".equals(delegateYn) || delegateYn == null) {
				delegateYn = "N";
			}
			
			//_delegateThumbNailYn = "N";
			if(!file.isEmpty()) {
				
				if(file.getOriginalFilename().indexOf("HEIC") > -1 ) {
					try {
						File _file = new File(file.getOriginalFilename());
						
						file.transferTo(_file);
						
						//JDeli.write(_file, "png", outputStream);
						
						//BufferedImage image = JDeli.read(_file2);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
				String _fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				ProductImageDTO productImageDTO = new ProductImageDTO();
				
				productImageDTO.setProductId(productId);
				productImageDTO.setFileName(_fileName);
				productImageDTO.setOriginFileName(file.getOriginalFilename());
				productImageDTO.setThumbnailImagePath("");
				productImageDTO.setDelegateThumbnailYn(delegateYn);
				productImageDTO.setImageCfcd(imgCfcd);
				productImageDTO.setFileSize(file.getSize());
				
				list.add(productImageDTO);
				
				String imeageUrl = s3Upload.upload(file, _fileName);
				
				//log.info("imeageUrl===" + imeageUrl);
				
				if(imeageUrl != null) {
					productImageDTO.setThumbnailImagePath(imeageUrl);
					
					this.addProductImageInfo(productImageDTO);
					//productImageService.addProductImageInfo(productImageDTO);
				}
				imgCnt++;
			}
		}
	}
	@Transactional
	public void removeProductImageInfoList(List<ProductImageDTO> productImageList) {
		
		int cnt = productImageList.size();
		ProductImageDTO productImageDTO = new ProductImageDTO();
		ProductImageInfo productImageInfo = new ProductImageInfo();
		
		Map productImageMap = new HashMap<>();
		for(int i = 0; i < cnt; i++) {
			productImageMap = (Map) productImageList.get(i);
			productImageInfo = new ProductImageInfo();
			
			productImageInfo.setImageFileNumber(Long.parseLong(productImageMap.get("imageFileNumber").toString()));
			
			this.productImageDAO.delete(productImageInfo);
		}
	}
}
