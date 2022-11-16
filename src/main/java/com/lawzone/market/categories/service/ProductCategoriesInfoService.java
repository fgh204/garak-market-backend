package com.lawzone.market.categories.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.categories.dao.ProductCategoriesInfoDAO;
import com.lawzone.market.image.service.ProductImageInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductCategoriesInfoService {
	private final ProductCategoriesInfoDAO productCategoriesInfoDAO;
	private final ModelMapper modelMapper;
	
	@Transactional
	public String addProductCategoriesInfo(ProductCategoriesInfoDTO productCategoriesInfoDTO) {
		ProductCategoriesInfo productCategoriesInfo = new ProductCategoriesInfo();
		productCategoriesInfo = modelMapper.map(productCategoriesInfoDTO, ProductCategoriesInfo.class);
		
		return this.productCategoriesInfoDAO.save(productCategoriesInfo).getProductCategoryLargeCode();
	}
	
	@Transactional
	public List getProductCategoriesInfo() {
		return this.productCategoriesInfoDAO.findAll();
	}
	
//	@Transactional
//	public List getProductCategoriesInfoByCategoies(ProductCategoriesInfoDTO productCategoriesInfoDTO) {
//		return this.productCategoriesInfoDAO.findByLccd(
//				productCategoriesInfoDTO.getProductCategoryLargeCode()
//				,productCategoriesInfoDTO.getProductCategoryMediumCode()
//				,productCategoriesInfoDTO.getProductCategorySmallCode()
//				);
//	}
}
