package com.lawzone.market.order.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.AppPush;
import com.lawzone.market.externalLink.util.AppPushDTO;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderInfoListDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.CustOrderListDTO;
import com.lawzone.market.order.service.OrderPaymentInfo;
import com.lawzone.market.order.service.ProductOrderDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemDTO;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.order.service.UserOrderInfoDTO;
import com.lawzone.market.payment.service.PaymentDTO;
import com.lawzone.market.payment.service.PaymentInfoDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.payment.service.PushIdDTO;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.SellerInfoService;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoDTO;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.SlackWebhook;

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
	private final PointService pointService;
	private final SlackWebhook slackWebhook;
	private final AppPush appPush;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addProductOrderInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map mapData = new HashMap<>();
		Map mapOrderInfo = new HashMap<>();
		Map mapOrderItemInfo = new HashMap<>();
		Map rtnMap = new HashMap<>();
		List listOrderItemInfo = new ArrayList<>();
		String userId = sessionBean.getUserId();
		String userNm = sessionBean.getUserNm();
		mapData = (Map) map.get("dataset");
		mapOrderInfo = (Map) mapData.get("orderInfo");
		listOrderItemInfo = (List) mapData.get("orderItemList");
		BigDecimal _price = new BigDecimal("0");
		BigDecimal _productPrice = new BigDecimal("0");
		BigDecimal _totalProductPrice = new BigDecimal("0");
		BigDecimal _productStock = new BigDecimal("0");
		BigDecimal _totalPrice = new BigDecimal(mapOrderInfo.get("productTotalPrice").toString());
		BigDecimal _pointAmount = new BigDecimal("0");
		BigDecimal _deliveryAmount = new BigDecimal("0");
		BigDecimal _paymentPointAmount = new BigDecimal("0");
		BigDecimal _paymentAmount = new BigDecimal("0");
		BigDecimal _amountZero = new BigDecimal("0");
		
		if(mapOrderInfo.get("pointAmount") == null || "".equals(mapOrderInfo.get("pointAmount"))) {
			_pointAmount = new BigDecimal("0");
		}else {
			_pointAmount = new BigDecimal(mapOrderInfo.get("pointAmount").toString());
		}
		
		if(mapOrderInfo.get("deliveryAmount") == null || "".equals(mapOrderInfo.get("deliveryAmount"))) {
			_deliveryAmount = new BigDecimal("0");
		}else {
			_deliveryAmount = new BigDecimal(mapOrderInfo.get("deliveryAmount").toString());
		}
		
		_paymentPointAmount = _pointAmount;
		_paymentAmount = _totalPrice.add(_deliveryAmount).subtract(_pointAmount);
		//_totalPrice = _totalPrice.subtract(_deliveryAmount);
		mapOrderInfo.put("pointAmount", _pointAmount);
		mapOrderInfo.put("deliveryAmount", _deliveryAmount);
		mapOrderInfo.put("paymentAmount", _paymentAmount);
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO.setUserId(userId);
		BigDecimal point = this.pointService.getPointAmount(pointInfoCDTO);
		
		if(point.compareTo(_pointAmount) < 0) {
			return JsonUtils.returnValue("0001", "포인트 금액이 일치하지 않습니다.", rtnMap).toString();
		}
		
		if(_pointAmount.compareTo(_amountZero) == 0 && _paymentAmount.compareTo(_amountZero) == 0) {
			return JsonUtils.returnValue("9999", "결제 금액을 확인하세요", rtnMap).toString();
		}
		
		String _productId = "";
		String _sellerId = "";
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
			_sellerId = productInfo.get(0).getSellerId();
			if(i == 0) {
				orderName = productInfo.get(0).getProductName();
			}
			
			_totalProductPrice = _productPrice.multiply(_orderCount);

			if(_totalProductPrice.compareTo(_pointAmount) >= 0) {
				_totalProductPrice = _totalProductPrice.subtract(_pointAmount);
				mapOrderItemInfo.put("pointAmount",_pointAmount);
				_pointAmount = new BigDecimal("0");
			}else {
				mapOrderItemInfo.put("pointAmount",_totalProductPrice);
				_pointAmount = _pointAmount.subtract(_totalProductPrice);
				_totalProductPrice = new BigDecimal("0");
			}
			
			mapOrderItemInfo.put("productPrice",_productPrice);
			mapOrderItemInfo.put("orderItemStateCode","001");
			mapOrderItemInfo.put("orderItemDlngStateCode","000");
			mapOrderItemInfo.put("totalProductPrice",_totalProductPrice);
			mapOrderItemInfo.put("deliveryStateCode", "000");
			mapOrderItemInfo.put("sellerId", _sellerId);
			//log.info("_productStock.compareTo(_orderCount)============" + _productStock.compareTo(_orderCount));
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
		
		if("".equals(rtnProductId) && _price.compareTo(_totalPrice) == 0) {
			//등록처리
			ProductOrderDTO productOrderDTO = new ProductOrderDTO();
			Map mapInsert = new HashMap<>();
			
			mapInsert.put("dataset", mapOrderInfo);
			
			productOrderDTO = (ProductOrderDTO) ParameterUtils.setDto(mapInsert, productOrderDTO, "insert", sessionBean);
			productOrderDTO.setOrderCount(new BigDecimal(orderCount));
			productOrderDTO.setOrderStateCode("001");
			//productOrderDTO.setOrderDlngStateCode("000");
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
				//log.info("price ==== " + _price);
				//log.info("totalPrice ==== " + _totalPrice);
				return JsonUtils.returnValue("9999", "금액이 잘못되었습니다.", rtnMap).toString();
			}
		}
		
		String orderStateCode = "001";
		
		if(_paymentAmount.compareTo(new BigDecimal("0")) == 0) {
			PaymentDTO paymentDTO = new PaymentDTO();
			paymentDTO.setReceiptId(_orderNo);
			paymentDTO.setOrderNo(_orderNo);
			paymentDTO.setOrderName(orderName);
			paymentDTO.setPaymentGb("도매도 포인트");
			paymentDTO.setPaymentDttm("now()");
			paymentDTO.setPaymentReqDttm("now()");
			paymentDTO.setCancelledPaymentAmount(new BigDecimal("0"));
			paymentDTO.setCancelledPaymentDttm(null);
			paymentDTO.setOrderDate("now()");
			paymentDTO.setPaymentAmount(new BigDecimal("0"));
			paymentDTO.setPointAmount(_paymentPointAmount);
			paymentDTO.setCancelledPointAmount(new BigDecimal("0"));
			paymentDTO.setPgName("도매도 포인트");
			paymentDTO.setReceiptUrl("");
			paymentDTO.setApproveNo(_orderNo);
			paymentDTO.setCustPaymentNo(_orderNo);
			paymentDTO.setPgId(_orderNo);
			paymentDTO.setPgCustNm(userNm);
			paymentDTO.setPgPayCoCd(null);
			paymentDTO.setPgPayCoNm("도매도");
			paymentDTO.setCardQuota(null);
			paymentDTO.setPaymentName("포인트");
			
			//결제처리
			this.paymentService.addPaymentInfo(paymentDTO, userId);
			//this.productOrderService.modifyProductOrderInfoStat("003",_orderNo);
			//this.productOrderService.modifyProductOrderItemInfoStat("003",_orderNo);
			//this.productService.modifyProductStock(_orderNo);
			
			orderStateCode = "003";
			
			StringBuilder slackMsg = new StringBuilder();
			slackMsg.append("주문번호 : ")
			.append(_orderNo)
			.append("\n상품명 : ")
			.append(orderName)
			.append("\n결제금액 : ")
			.append(_paymentPointAmount)
			.append("\n구매자 : ")
			.append(userNm)
			.append("\n결제방법 : ")
			.append("도매도 포인트")
			.append("\n결제수단 : ")
			.append("포인트");
			
			//slackWebhook.
			this.slackWebhook.postSlackMessage(slackMsg.toString(), "01");
			
			List<PushIdDTO> pushIdList = this.paymentService.getSellerPushId(_orderNo);
			
			AppPushDTO appPushDTO = new AppPushDTO();
			
			appPushDTO.setAllYn("N");
			appPushDTO.setTitle("[도매도] 신규주문 알림!");
			appPushDTO.setUrl("");
			
			ArrayList<String> push = new ArrayList<>();
			for(int i = 0; i < pushIdList.size(); i++) {
				push = new ArrayList<>();
				push.add(0,pushIdList.get(i).getPushId());
				appPushDTO.setContent("상품 : " + pushIdList.get(i).getProductName());
				appPushDTO.setPushList(push);
				
				this.appPush.getAppPush(appPushDTO);
			}
			//Map mapUserInfo = new HashMap<>();
			//return JsonUtils.returnValue("0000", "저장되었습니다.", rtnMap).toString();
		}
		//else {
		//주문금액정보 등록
		this.productOrderService.addOrderPaymentInfo(_orderNo);
		//주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, orderStateCode, "", "Y");
		
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(_orderNo, orderStateCode, "", "Y");
		
		int itemCnt = custOrderItemList.size();
		
		_deliveryAmount = _deliveryAmount.subtract(_pointAmount);
		
		if(_deliveryAmount.compareTo(_amountZero)> 0) {
			if(itemCnt > 0) {
				BigDecimal _itemProductPrice = custOrderItemList.get(0).getProductPrice();
				
				_itemProductPrice = _itemProductPrice.add(_deliveryAmount);
				
				custOrderItemList.get(0).setProductPrice(_itemProductPrice);
			}
		}
		//고객정보
		List<UserInfo> userInfo = this.userInfoService.getUserInfoByUserId(userId);
		
		Map mapUserInfo = new HashMap<>();
		
		mapUserInfo.put("userName", userInfo.get(0).getUserName());
		mapUserInfo.put("userEmail", userInfo.get(0).getEmail());
		mapUserInfo.put("userPhoneNumber", userInfo.get(0).getPhoneNumber());
		
		rtnMap.put("orderNo",_orderNo);
		rtnMap.put("paymentAmount",_paymentAmount);
		rtnMap.put("orderInfo", custOrderInfoDTO.get(0));
		rtnMap.put("orderItemList", custOrderItemList);
		rtnMap.put("orderUserInfo", mapUserInfo);
		return JsonUtils.returnValue("0000", "저장되었습니다.", rtnMap).toString();
		//}
	}
	
	@ResponseBody
	@PostMapping("/orderList")
	public String getProductOrderInfoByUser(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
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
			//_orderInfo.setOrderDlngStateCode(userOrderInfo.get(i).getOrderDlngStateCode());
			//_orderInfo.setOrderDlngStateName(userOrderInfo.get(i).getOrderDlngStateName());
			
			_custOrderListDTO.setOrderInfo(_orderInfo);
			
			//List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrder(orderNo);
			
			itemStatCode = userOrderInfo.get(i).getOrderStateCode();
			
			//if("004".equals(itemStatCode)) {
			//	itemStatCode = "002";
			//}
			
			List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(orderNo,"", "", "N");
			
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductOrderDTO productOrderDTO = new ProductOrderDTO();
		productOrderDTO = (ProductOrderDTO) ParameterUtils.setDto(map, productOrderDTO, "insert", sessionBean);
		
		String orderNo = productOrderDTO.getOrderNo();
		//주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(orderNo, "", productOrderDTO.getUserId(),"N");
		
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(orderNo, "", productOrderDTO.getUserId(), "N");
		
		//결제정보
		List<PaymentInfoDTO> paymentInfo = this.paymentService.getOrderPaymentInfo(orderNo, productOrderDTO.getUserId());
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
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductOrderItemDTO productOrderItemDTO = new ProductOrderItemDTO();
		productOrderItemDTO = (ProductOrderItemDTO) ParameterUtils.setDto(map, productOrderItemDTO, "insert", sessionBean);
		this.productOrderService.addProductOrderItemInfo(productOrderItemDTO);
		
		Map rtnMap = new HashMap<>();
		
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}
}
