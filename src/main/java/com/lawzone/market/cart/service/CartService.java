package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.cart.dao.CartInfoDAO;
import com.lawzone.market.cart.dao.CartInfoJdbcDAO;
import com.lawzone.market.product.dao.ProductDAO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartService {
	private final CartInfoDAO cartInfoDAO;
	private final CartInfoJdbcDAO cartInfoJdbcDAO;
	private final ModelMapper modelMapper;
	private final ProductDAO productDAO;
	private final UtilService utilService;
	
	@Transactional
	public Long addCartInfo(CartInfoDTO cartInfoDTO) {
		Long cartNumber = (long) 0;
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		
		List<CartInfo> _cartInfo = this.cartInfoDAO.findByUserIdAndProductId(cartInfoDTO.getUserId(), cartInfoDTO.getProductId());
		if(_cartInfo.size() > 0) {
			cartStock = _cartInfo.get(0).getProductCount().add(cartInfoDTO.getProductCount());
			cartInfoDTO.setCartNumber(_cartInfo.get(0).getCartNumber());
		}else {
			cartStock = cartInfoDTO.getProductCount();
		}
		
		List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
		
		productStock = _productInfo.get(0).getProductStock();

		if(productStock.compareTo(cartStock) >= 0) {
			cartInfoDTO.setProductCount(cartStock);
			CartInfo cartInfo = new CartInfo();
			cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
			cartNumber =  this.cartInfoDAO.save(cartInfo).getCartNumber();
		}
		
		return cartNumber;
	}
	
	@Transactional
	public void modifyCartInfo(CartInfoDTO cartInfoDTO) {

		Map cartMap = new HashMap<>();
		Map cartDataMap = new HashMap<>();
		
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		
		productStock = new BigDecimal("0");
		cartStock = new BigDecimal("0");
		
		List<CartInfo> _cartInfo = this.cartInfoDAO.findByCartNumber(cartInfoDTO.getCartNumber());
		
		if(_cartInfo.size() > 0) {
			cartInfoDTO.setProductId(_cartInfo.get(0).getProductId());				
			cartInfoDTO.setUserId(_cartInfo.get(0).getUserId());
			
			cartStock = cartInfoDTO.getProductCount();
			
			List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
			
			productStock = _productInfo.get(0).getProductStock();
			
			if(productStock.compareTo(cartStock) >= 0) {
				CartInfo cartInfo = new CartInfo();
				cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
				this.cartInfoDAO.save(cartInfo).getCartNumber();
			}
			
		}
	}
	
	@Transactional
	public void modifyCartInfos(List cartItemList) {
		int _cnt = cartItemList.size();
		CartInfoDTO cartInfoDTO;
		
		Map cartMap = new HashMap<>();
		Map cartDataMap = new HashMap<>();
		
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		
		for( int i = 0; i < _cnt; i++){
			cartInfoDTO = new CartInfoDTO();
			
			cartMap = (Map) cartItemList.get(i);
			cartDataMap.put("dataset", cartMap);
			
			cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(cartDataMap, cartInfoDTO, "insert", null);
			
			productStock = new BigDecimal("0");
			cartStock = new BigDecimal("0");
			
			List<CartInfo> _cartInfo = this.cartInfoDAO.findByCartNumber(cartInfoDTO.getCartNumber());
			
			if(_cartInfo.size() > 0) {
				cartInfoDTO.setProductId(_cartInfo.get(0).getProductId());				
				cartInfoDTO.setUserId(_cartInfo.get(0).getUserId());
				
				cartStock = cartInfoDTO.getProductCount();
				
				List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
				
				productStock = _productInfo.get(0).getProductStock();
				
				if(productStock.compareTo(cartStock) >= 0) {
					CartInfo cartInfo = new CartInfo();
					cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
					this.cartInfoDAO.save(cartInfo).getCartNumber();
				}
			}
		}
	}
	
	@Transactional
	public void removeCartInfo(CartInfoDTO cartInfoDTO) {
		CartInfo cartInfo = new CartInfo();
		cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
		
		this.cartInfoDAO.delete(cartInfo);
	}
	
	@Transactional
	public List getCartInfoList() {
		return this.cartInfoDAO.findAll();
	}
	
	public List getCartInfoList(CartInfoDTO cartInfoDTO) {
		
		String sql = this.cartInfoJdbcDAO.getCartInfoList();
		CartInfoListDTO cartInfoListDTO = new CartInfoListDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, cartInfoDTO.getUserId());
		
		return this.utilService.getQueryString(sql,cartInfoListDTO,_queryValue);
	}
}
