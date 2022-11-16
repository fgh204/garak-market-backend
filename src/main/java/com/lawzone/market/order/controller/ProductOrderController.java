package com.lawzone.market.order.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderInfoListDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.CustOrderListDTO;
import com.lawzone.market.order.service.ProductOrderDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemDTO;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.order.service.UserOrderInfoDTO;
import com.lawzone.market.payment.service.PaymentInfoDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoDTO;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/order")
public class ProductOrderController {
	private final ProductOrderService productOrderService;
	private final ProductService productService;
	private final UserInfoService userInfoService;
	private final TelmsgLogService telmsgLogService;
	private final PaymentService paymentService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addProductOrderInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map mapData = new HashMap<>();
		Map mapOrderInfo = new HashMap<>();
		Map mapOrderItemInfo = new HashMap<>();
		List listOrderItemInfo = new ArrayList<>();
		
		mapData = (Map) map.get("dataset");
		mapOrderInfo = (Map) mapData.get("orderInfo");
		listOrderItemInfo = (List) mapData.get("orderItemList");
		BigDecimal _price = new BigDecimal("0");
		BigDecimal _productPrice = new BigDecimal("0");
		BigDecimal _productStock = new BigDecimal("0");
		BigDecimal _totalPrice = new BigDecimal(mapOrderInfo.get("productTotalPrice").toString());
		String _productId = "";
		String rtnProductId = "";
		String rtnProductName = "";
		String _orderNo = "";
		BigDecimal _orderCount = new BigDecimal("0");
		
		int orderCount = 0;
		int orderItemCount = listOrderItemInfo.size();
		
		String orderName = "";

	    //상품금액검증
		for(int i = 0; i < orderItemCount; i++) {
			mapOrderItemInfo = (Map) listOrderItemInfo.get(i);
			_productPrice = new BigDecimal("0");
			_productStock = new BigDecimal("0");
			_productId = mapOrderItemInfo.get("productId").toString();
			_orderCount = new BigDecimal(Integer.parseInt(mapOrderItemInfo.get("productCount").toString()));
			
			List<ProductInfo> productInfo = this.productService.getProductPriceByProductId(mapOrderItemInfo.get("productId").toString());
			_productPrice = productInfo.get(0).getProductPrice();
			_productStock = productInfo.get(0).getProductStock();
			if(i == 0) {
				orderName = productInfo.get(0).getProductName();
			}
			mapOrderItemInfo.put("productPrice",_productPrice);
			mapOrderItemInfo.put("orderItemStateCode","001");
			log.info("_productStock.compareTo(_orderCount)============" + _productStock.compareTo(_orderCount));
			if(_productStock.compareTo(_orderCount) == -1) {
				rtnProductId = _productId;
				rtnProductName = productInfo.get(0).getProductName();
				break;
			}
			
			_price = _price.add(_productPrice.multiply(_orderCount));
			orderCount++;
		}
		if(orderItemCount > 1) {
			orderName = orderName + " 외 " + Integer.toString(orderItemCount - 1);
		}
		
		Map rtnMap = new HashMap<>();
		
		if("".equals(rtnProductId) && _price.compareTo(_totalPrice) == 0) {
			//등록처리
			ProductOrderDTO productOrderDTO = new ProductOrderDTO();
			Map mapInsert = new HashMap<>();
			
			mapInsert.put("dataset", mapOrderInfo);
			
			productOrderDTO = (ProductOrderDTO) ParameterUtils.setDto(mapInsert, productOrderDTO, "insert", sessionBean);
			productOrderDTO.setOrderCount(new BigDecimal(orderCount));
			productOrderDTO.setOrderStateCode("001");
			productOrderDTO.setOrderDate("now()");
			productOrderDTO.setOrderName(orderName);
			_orderNo = this.productOrderService.addProductOrderInfo(productOrderDTO);
			
			if(!"".equals(_orderNo) && _orderNo != null) {
				
				for(int j = 0; j < listOrderItemInfo.size(); j++) {
					mapOrderItemInfo = (Map) listOrderItemInfo.get(j);
					
					mapInsert = new HashMap<>();
					
					ProductOrderItemDTO productOrderItemDTO = new ProductOrderItemDTO();
					
					mapInsert.put("dataset", mapOrderItemInfo);
					
					productOrderItemDTO = (ProductOrderItemDTO) ParameterUtils.setDto(mapInsert, productOrderItemDTO, "insert", sessionBean);
					
					productOrderItemDTO.setOrderNo(_orderNo);
					
					this.productOrderService.addProductOrderItemInfo(productOrderItemDTO);
				}
			}
		}else {
			if(!"".equals(rtnProductId)) {
				return JsonUtils.returnValue("9999", "재고 수량이 없습니다.", rtnMap).toString();
			}else {
				log.info("price ==== " + _price);
				log.info("totalPrice ==== " + _totalPrice);
				return JsonUtils.returnValue("9999", "금액이 잘못되었습니다.", rtnMap).toString();
			}
			
		}
		
		//주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, "001");
		
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(_orderNo, "001");
		
		//고객정보
		List<UserInfo> userInfo = this.userInfoService.getUserInfoByUserId(sessionBean.getUserId());
		
		Map mapUserInfo = new HashMap<>();
		
		mapUserInfo.put("userName", userInfo.get(0).getUserName());
		mapUserInfo.put("userEmail", userInfo.get(0).getEmail());
		mapUserInfo.put("userPhoneNumber", userInfo.get(0).getPhoneNumber());
		
		rtnMap.put("orderNo",_orderNo);
		rtnMap.put("orderInfo", custOrderInfoDTO.get(0));
		rtnMap.put("orderItemList", custOrderItemList);
		rtnMap.put("orderUserInfo", mapUserInfo);
		return JsonUtils.returnValue("0000", "저장되었습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/orderList")
	public String getProductOrderInfoByUser(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		//ProductOrderDTO productOrderDTO = new ProductOrderDTO();
		//productOrderDTO = (ProductOrderDTO) ParameterUtils.setDto(map, productOrderDTO, "insert", request);
		
		ProductCDTO productCDTO = new ProductCDTO();
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);
		
		//List<ProductOrderInfo> productOrderInfo = this.productOrderService.getProductOrderInfoByUser(productOrderDTO);
		
		Map orderMap = new HashMap<>();
		
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
		
		orderMap.put("userId", productCDTO.getUserId());
    	orderMap.put("pageCount", productCDTO.getPageCount());
    	orderMap.put("maxPageCount", productCDTO.getMaxPageCount());
		
    	List<PageInfoDTO> pageInfo = this.productOrderService.getCustOrderListPageInfo(orderMap);
		List<UserOrderInfoDTO> userOrderInfo = this.productOrderService.getCustOrderList(orderMap);
		
		List<CustOrderListDTO> _custOrderList = new ArrayList<>();
		List<CustOrderItemListDTO> _custOrderItemList = new ArrayList<>();
		
		int orderCnt = userOrderInfo.size();
		int orderItemCnt = 0;
		ProductOrderInfo _productOrderInfo;
		CustOrderListDTO _custOrderListDTO;
		
		CustOrderItemListDTO custOrderItemListDTO;
		CustOrderItemListDTO _custOrderItemListDTO;
		
		UserOrderInfoDTO _orderInfo;
		
		String orderNo = "";
		String itemStatCode = "";
		
		for(int i = 0; i < orderCnt; i++) {
			_productOrderInfo = new ProductOrderInfo();
			_custOrderListDTO = new CustOrderListDTO();
			_orderInfo = new UserOrderInfoDTO();
			
			
			orderNo = userOrderInfo.get(i).getOrderNo();
			orderItemCnt = 0;
			
			_orderInfo.setOrderNo(orderNo);
			_orderInfo.setOrderDate(userOrderInfo.get(i).getOrderDate());
			_orderInfo.setOrderStateCode(userOrderInfo.get(i).getOrderStateCode());
			_orderInfo.setOrderStateName(userOrderInfo.get(i).getOrderStateName());
			
			_custOrderListDTO.setOrderInfo(_orderInfo);
			
			//List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrder(orderNo);
			
			itemStatCode = userOrderInfo.get(i).getOrderStateCode();
			
			if("004".equals(itemStatCode)) {
				itemStatCode = "002";
			}
			
			List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(orderNo,itemStatCode);
			
			orderItemCnt = custOrderItemList.size();
			
			_custOrderItemList = new ArrayList<>();
			
			for(int j = 0; j < orderItemCnt; j++) {
				custOrderItemListDTO = new CustOrderItemListDTO();
				_custOrderItemListDTO = new CustOrderItemListDTO();
				
				custOrderItemListDTO = custOrderItemList.get(j);
				//_custOrderItemListDTO.setOrderNo(custOrderItemListDTO.getOrderNo());
				_custOrderItemListDTO = custOrderItemListDTO;
				
				_custOrderItemList.add(j, _custOrderItemListDTO);
			}
			_custOrderListDTO.setOrderItemList((ArrayList<CustOrderItemListDTO>) _custOrderItemList);
			
			_custOrderList.add(i, _custOrderListDTO);
		}
		
		Map rtnMap = new HashMap<>();
		
		rtnMap.put("productOrderList", _custOrderList);
		rtnMap.put("pageInfo", pageInfo.get(0));
		
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/orderInfo")
	public String getProductOrderInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductOrderDTO productOrderDTO = new ProductOrderDTO();
		productOrderDTO = (ProductOrderDTO) ParameterUtils.setDto(map, productOrderDTO, "insert", sessionBean);
		
		String orderNo = productOrderDTO.getOrderNo();
		
		//주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(orderNo, "");
		
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(orderNo, "");
		
		//결제정보
		List<PaymentInfoDTO> paymentInfo = this.paymentService.getOrderPaymentInfo(orderNo);
//		CustOrderInfoListDTO  custOrderInfoListDTO = new CustOrderInfoListDTO();
//		CustOrderItemListDTO _custOrderItemListDTO = new CustOrderItemListDTO();
//		List<CustOrderItemListDTO> _custOrderItemList = new ArrayList<>();
//		
//		custOrderInfoListDTO.setOrderNo(custOrderInfoDTO.get(0).getOrderNo());
//		custOrderInfoListDTO.setUsername(custOrderInfoDTO.get(0).getUsername());
//		custOrderInfoListDTO.setZipNumber(custOrderInfoDTO.get(0).getZipNumber());
//		custOrderInfoListDTO.setTotalPrice(custOrderInfoDTO.get(0).getTotalPrice());
//		custOrderInfoListDTO.setBaseAddress(custOrderInfoDTO.get(0).getBaseAddress());
//		custOrderInfoListDTO.setDetailAddress(custOrderInfoDTO.get(0).getDetailAddress());
//		
//		for(int i = 0; i < custOrderItemList.size(); i++) {
//			_custOrderItemListDTO = custOrderItemList.get(i);
//			
//			_custOrderItemList.add(i, _custOrderItemListDTO);
//		}
//		custOrderInfoListDTO.setOrderItemList((ArrayList<CustOrderItemListDTO>) _custOrderItemList);
		Map rtnMap = new HashMap<>();
		
		rtnMap.put("orderInfo", custOrderInfoDTO.get(0));
		rtnMap.put("orderItemList", custOrderItemList);
		rtnMap.put("paymentInfo", paymentInfo.get(0));
		
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/createItem")
	public String addProductOrderItemInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductOrderItemDTO productOrderItemDTO = new ProductOrderItemDTO();
		productOrderItemDTO = (ProductOrderItemDTO) ParameterUtils.setDto(map, productOrderItemDTO, "insert", sessionBean);
		this.productOrderService.addProductOrderItemInfo(productOrderItemDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
}
