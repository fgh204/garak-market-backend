package com.lawzone.market.image.service;

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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.lawzone.market.aws.service.S3Controller;
import com.lawzone.market.image.dao.ProductImageDAO;
import com.lawzone.market.image.dao.ProductImageJdbcDAO;
import com.lawzone.market.util.UtilService;

import org.imgscalr.Scalr;
import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductImageService {
	private final ProductImageDAO productImageDAO;
	private final ProductImageJdbcDAO productImageJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final S3Controller s3Upload;
	
	@Transactional
	public Long addProductImageInfo(ProductImageDTO productImageDTO) {
		ProductImageInfo productImageInfo = new ProductImageInfo();
		productImageInfo = modelMapper.map(productImageDTO, ProductImageInfo.class);
		
		return this.productImageDAO.save(productImageInfo).getImageFileNumber();
	}
	
	@Transactional
	public List getProductImageInfoList() {
		return this.productImageDAO.findAll();
	}
	
	@Transactional
	public List getProductImageInfoList(String productId) {
		String _sql = this.productImageJdbcDAO.productImeageList();
		
		ProductImageListDTO productImageListDTO = new ProductImageListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		
		return this.utilService.getQueryString(_sql,productImageListDTO,_queryValue);
	}
	
	@Transactional
	public List getProductReviewImageInfoList(String productId, String orderNo) {
		String _sql = this.productImageJdbcDAO.productReviewImeageList();
		
		ProductImageListDTO productImageListDTO = new ProductImageListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, productId);
		_queryValue.add(1, orderNo);
		
		return this.utilService.getQueryString(_sql,productImageListDTO,_queryValue);
	}
	
	public void productFileUploadList(String productId, List uploadList, String imgCfcd) throws IllegalStateException, IOException, ImageProcessingException, MetadataException {
		String delegateYn = "N";
		
		for(int i = 0; i < uploadList.size(); i++) {
			delegateYn = "N";
			if(i == 0) {
				delegateYn = "Y";
			}
			productFileUpload(productId, (MultipartFile[]) uploadList.get(i), delegateYn, imgCfcd);
		}
	}
	
	public Map productFileUpload(String productId, MultipartFile[] uploadFile, String delegateYn, String imgCfcd) throws IllegalStateException, IOException, ImageProcessingException, MetadataException {
		List<ProductImageDTO> list = new ArrayList<>();
		int imgCnt = 0;
		String imeageUrl = "";
		String[] _fileExtFormat = {"BMP", "JPG", "JPEG", "GIF", "TIF", "TIFF", "PNG", "HEIC"};
		long maxSize = 1204 * 1204 * 10;
		Long imageFileNumber;
		Map productImageMap = new HashMap<>();
		for(MultipartFile file : uploadFile) {
			if("".equals(delegateYn) || delegateYn == null) {
				delegateYn = "N";
			}
			
			if(!file.isEmpty()) {
				String orgFileName = file.getOriginalFilename();
				String _ext = orgFileName.substring(orgFileName.lastIndexOf(".") + 1);
				log.error("file.getContentType() === " + file.getContentType());
				log.error("orgFileName === " + orgFileName);
				if(file.getContentType().indexOf("image") == -1) {
					if(!"application/octet-stream".equals(file.getContentType())) {
						productImageMap.put("msgCd", "9999");
						productImageMap.put("msgNm", "이미지 파일만 업로드 가능합니다.");
						return productImageMap;
					}
				}
				if (!Arrays.asList(_fileExtFormat).contains(_ext.toUpperCase())) {
					productImageMap.put("msgCd", "9999");
					productImageMap.put("msgNm", "파일 확장자를 확인하세요.");
					return productImageMap;
				}
				
				if( maxSize < file.getSize()) {
					productImageMap.put("msgCd", "9999");
					productImageMap.put("msgNm", "최대 10MB까지만 첨부 가능합니다.");
					return productImageMap;
				}
				
				long _size = 1204 * 1204 * 1;
				
				String _fileName = UUID.randomUUID().toString() + "_" + orgFileName;
				ProductImageDTO productImageDTO = new ProductImageDTO();
				productImageDTO.setProductId(productId);
				productImageDTO.setOrderNo("000000000000");
				productImageDTO.setDelegateThumbnailYn(delegateYn);
				productImageDTO.setImageCfcd(imgCfcd);
				productImageDTO.setFileName(_fileName);
				productImageDTO.setOriginFileName(orgFileName);
				productImageDTO.setThumbnailImagePath("");
				
				if(file.getSize() > _size) {
					log.error("이미지 작업시작");
					File outputfile = null;
					File imageFile = null;
					Metadata metadata; // 이미지 메타 데이터 객체
					Directory directory; // 이미지의 Exif 데이터를 읽기 위한 객체
					JpegDirectory jpegDirectory; // JPG 이미지 정보를 읽기 위한 객체
					int orientation = 1;
					imageFile = new File(file.getOriginalFilename());
					imageFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(imageFile);
					fos.write(file.getBytes());
					fos.close();
					
					metadata = ImageMetadataReader.readMetadata(imageFile);
					directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
					jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
					if(directory == null){
						try {
							orientation = jpegDirectory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
						}catch (Exception e) {
							// TODO: handle exception
							//orientation = 1;
						}finally{
						}
					} else {
						try {
							orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
						}catch (Exception e) {
							// TODO: handle exception
							//orientation = 1;
						}finally{
							
						}
					}
					
					BufferedImage inputImage = ImageIO.read(file.getInputStream());
					
					switch (orientation) {
					case 6:
						inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_90, null); 
						break;
					case 1:
				 
						break;
					case 3:
						inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_180, null);
						break;
					case 8:
						inputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_270, null);
						break;
				 
					default:
						orientation=1;
						break;
					}
					
					int _width = inputImage.getWidth() / 2 ;
					int _height = inputImage.getHeight() / 2;
					
					Image reImg = inputImage.getScaledInstance(_width, _height,Image.SCALE_SMOOTH );
					BufferedImage outputImage = new BufferedImage(_width, _height, inputImage.getType());
					
					Graphics2D graphics2D = outputImage.createGraphics();
					graphics2D.drawImage(reImg, 0, 0, _width, _height, null);
					graphics2D.dispose();
					
					outputfile = new File(_fileName);
					
					 try {
				        if (outputfile.createNewFile()) {
				        	
				    	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
				    	    
				            String type = file.getContentType().substring(file.getContentType().indexOf("/")+1);
				    	    ImageIO.write(outputImage, type, bos);
				    	    
				            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
				            
				            Files.copy(inputStream, outputfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				        }
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					 File imgFile = new File(outputfile.toPath().toString());
						
					 InputStream input = new FileInputStream(imgFile);
					 
					 productImageDTO.setFileSize(Files.size(outputfile.toPath()));
					 log.error("이미지 업로드 시작");
					 imeageUrl = s3Upload.uploadInputStream(input, _fileName, imgCfcd);
					 log.error("이미지 업로드 종료");
					 if(imageFile.exists()) {
						 imageFile.delete();
					 }
					 if(outputfile.exists()) {
						 outputfile.delete();
					 }
					 log.error("이미지 작업종료");
				}else {
					productImageDTO.setFileSize(file.getSize());
					list.add(productImageDTO);
					imeageUrl = "";
					imeageUrl = s3Upload.upload(file, _fileName, imgCfcd);
				}
				

				if(imeageUrl != null) {
					productImageDTO.setThumbnailImagePath(imeageUrl);
					
					imageFileNumber = this.addProductImageInfo(productImageDTO);
					productImageMap.put("imageFileNumber", imageFileNumber);
					productImageMap.put("imageCfcd", productImageDTO.getImageCfcd());
					productImageMap.put("originFileName", productImageDTO.getOriginFileName());
					productImageMap.put("fileName", productImageDTO.getFileName());
					productImageMap.put("fileSize", productImageDTO.getFileSize());
					productImageMap.put("thumbnailImagePath", productImageDTO.getThumbnailImagePath());
					productImageMap.put("delegateThumbnailYn", productImageDTO.getDelegateThumbnailYn());
					productImageMap.put("msgCd", "0000");
					productImageMap.put("msgNm", "등록되었습니다.");

				}
				imgCnt++;
			}
		}
		
		return productImageMap;
	}
	
	@Transactional
	public void removeProductImageInfoList(List<ProductImageDTO> productImageList, String productId) {
		
		int cnt = productImageList.size();
		//ProductImageDTO productImageDTO = new ProductImageDTO();
		ProductImageInfo productImageInfo = new ProductImageInfo();
		
		Map productImageMap = new HashMap<>();
		for(int i = 0; i < cnt; i++) {
			productImageMap = (Map) productImageList.get(i);
			productImageInfo = new ProductImageInfo();
			
			List<ProductImageInfo> _productImageInfo = this.productImageDAO.findByproductIdAndFileName(productId, productImageMap.get("fileName").toString());
			
			if(_productImageInfo.size() > 0) {
				productImageInfo.setImageFileNumber(_productImageInfo.get(0).getImageFileNumber());
				this.productImageDAO.delete(productImageInfo);
			}
		}
	}
	
	@Transactional
	public void modifyProductImageInfoList(List<ProductImageDTO> productImageList, String productId, String imageCfcd) {
		
		int cnt = productImageList.size();
		//ProductImageDTO productImageDTO = new ProductImageDTO();
		ProductImageInfo productImageInfo = new ProductImageInfo();
		
		Map productImageMap = new HashMap<>();
		for(int i = 0; i < cnt; i++) {
			productImageMap = (Map) productImageList.get(i);
			productImageInfo = new ProductImageInfo();
			
			List<ProductImageInfo> _productImageInfo = this.productImageDAO.findByproductIdAndFileName("000000000000", productImageMap.get("fileName").toString());
			
			if(_productImageInfo.size() > 0) {
				productImageInfo.setProductId(productId);
				productImageInfo.setImageCfcd(imageCfcd);
			}
		}
	}
	
	@Transactional
	public void mergeProductImageInfoList(List<ProductImageDTO> productImageList, String productId, String orderNo) {
		
		int cnt = productImageList.size();
		//ProductImageDTO productImageDTO = new ProductImageDTO();
		ProductImageInfo productImageInfo = new ProductImageInfo();
		String _delYn = "N";
		String _newYn = "N";
		String _imageCfcd = "00";
		Long _imageFileNumber;
		
		Map productImageMap = new HashMap<>();
		for(int i = 0; i < cnt; i++) {
			productImageMap = (Map) productImageList.get(i);
			productImageInfo = new ProductImageInfo();
			
			_delYn = (String) productImageMap.get("delYn");
			_newYn = (String) productImageMap.get("newYn");
			_imageCfcd = (String) productImageMap.get("imageCfcd");
			_imageFileNumber = Long.parseLong(productImageMap.get("imageFileNumber").toString());
			
			//if("Y".equals(_delYn) && "N".equals(_newYn)) {
			if("Y".equals(_delYn)) {
				//삭제
				List<ProductImageInfo> _productImageInfo = this.productImageDAO.findByproductIdAndFileName(productId, productImageMap.get("fileName").toString());
				
				if(_productImageInfo.size() > 0) {
					productImageInfo.setImageFileNumber(_productImageInfo.get(0).getImageFileNumber());
					this.productImageDAO.delete(productImageInfo);
				}
			//}else if("N".equals(_delYn) && "Y".equals(_newYn)) {
			}else if("Y".equals(_newYn)) {
				//수정
				List<ProductImageInfo> _productImageInfo = this.productImageDAO.findByImageFileNumberAndProductId(_imageFileNumber, "000000000000");
				if(_productImageInfo.size() > 0) {
					_productImageInfo.get(0).setProductId(productId);
					_productImageInfo.get(0).setImageCfcd(_imageCfcd);
					
					if("04".equals(_imageCfcd)) {
						_productImageInfo.get(0).setOrderNo(orderNo);
					}
				}
			} 
		}
	}
}
