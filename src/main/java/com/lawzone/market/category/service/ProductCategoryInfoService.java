package com.lawzone.market.category.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.category.dao.ProductCategoryInfoDAO;
import com.lawzone.market.category.dao.ProductCategoryInfoJdbcDAO;
import com.lawzone.market.image.service.ProductImageInfo;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductCategoryInfoService {
	private final ProductCategoryInfoDAO productCategoryInfoDAO;
	private final ProductCategoryInfoJdbcDAO productCategoryInfoJdbcDAO;
	private final UtilService utilService;
	private final ModelMapper modelMapper;
	
	@Transactional
	public String addProductCategoryInfo(ProductCategoryInfoDTO productCategoryInfoDTO) {
		ProductCategoryInfo productCategoryInfo = new ProductCategoryInfo();
		productCategoryInfo = modelMapper.map(productCategoryInfoDTO, ProductCategoryInfo.class);
		
		return this.productCategoryInfoDAO.save(productCategoryInfo).getProductCategoryLargeCode();
	}
	
	@Transactional
	public List getProductCategoryInfo() {
		return this.productCategoryInfoDAO.findAll();
	}
	
	@Transactional
	public List getLargeList() {
		String _sql = this.productCategoryInfoJdbcDAO.largeList();
		
		ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "000");
		_queryValue.add(1, "000");
		_queryValue.add(2, "000");
		
		return this.utilService.getQueryString(_sql,productCategoryDTO,_queryValue);
	}
}
