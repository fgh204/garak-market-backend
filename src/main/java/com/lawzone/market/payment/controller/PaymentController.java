package com.lawzone.market.payment.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.event.service.EventInfoCDTO;
import com.lawzone.market.event.service.EventInfoDTO;
import com.lawzone.market.event.service.EventMstService;
import com.lawzone.market.event.service.EventProductInfoDTO;
import com.lawzone.market.externalLink.util.AppPush;
import com.lawzone.market.externalLink.util.AppPushDTO;
import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.CustOrderItemListPDTO;
import com.lawzone.market.order.service.OrderPaymentDTO;
import com.lawzone.market.order.service.OrderPaymentInfo;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.order.service.ProductOrderUserInfoDTO;
import com.lawzone.market.payment.service.PaymentCancleDTO;
import com.lawzone.market.payment.service.PaymentDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.payment.service.PushIdDTO;
import com.lawzone.market.payment.service.TempPaymentInfo;
import com.lawzone.market.payment.service.TempPaymentInfoDTO;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.send.service.SendFormInfoCDTO;
import com.lawzone.market.send.service.SendFormInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.SlackWebhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/payment")
public class PaymentController {
	private final BootpayUtils bootpayUtils;
	private final ProductOrderService productOrderService;
	private final ProductService productService;
	private final PaymentService paymentService;
	private final TelmsgLogService telmsgLogService;
	private final ParameterUtils parameterUtils;
	private final SlackWebhook slackWebhook;
	private final AppPush appPush;
	private final PointService pointService;
	private final ModelMapper modelMapper;
	private final EventMstService eventMstService;
	private final SendFormInfoService sendFormInfoService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/redirectConfirm")
	public String redirectConfirm(HttpServletRequest request,  HttpServletResponse response) throws IOException {
		Map map = parameterUtils.convertMap(request);
		Map paymentMap = new HashMap<>();
		Map rtnMap = new HashMap<>();
		
		paymentMap.put("dataset", map);
		
		paymentConfirm(paymentMap);
		//response.sendRedirect("http://localhost:4200/payment");
		return JsonUtils.returnValue("0000", "취소가능한 주문이 없습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/confirm")
	public String paymentConfirm(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		return paymentConfirm(map);
	}
	public String paymentConfirm(Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map,"");
		Map rtnMap = new HashMap<>();
		
		Map paymentMap = new HashMap<>();
		
		paymentMap = (Map) map.get("dataset");
		String userId = sessionBean.getUserId();
		String userNmae = sessionBean.getUserNm();
		String _event = (String) paymentMap.get("event");
		String _receiptId = (String) paymentMap.get("receipt_id");
		String _orderNo = (String) paymentMap.get("order_id");
		String orderDate = "";
		String phoneNumber = "";
		String recipient = "";
		BigDecimal paymentPrice = new BigDecimal("0");
		BigDecimal orderPrice = new BigDecimal("0");
		BigDecimal pointAmount = new BigDecimal("0");
		BigDecimal cancelledPointAmount = new BigDecimal("0");
		BigDecimal paymentOrderStock = new BigDecimal("0");
		
		
		Map receiptData = new HashMap<>();
		Map paymentData = new HashMap<>();
		Map receiptCancleData = new HashMap<>();
		
		this.telmsgLogService.addTelmsgLog("01", "01", "1", map,"");
		receiptData = this.bootpayUtils.getBootpayReceipt(_receiptId);
		this.telmsgLogService.addTelmsgLog("01", "01", "2", receiptData,"");
		
		if(receiptData.get("error_code") != null) {
			//log.info("receiptData================" + receiptData);
			return JsonUtils.returnValue("9999", receiptData.get("message").toString(), rtnMap).toString();
	    }
		
		paymentPrice = new BigDecimal(receiptData.get("price").toString());
		
		//주문정보
		List<CustOrderInfoDTO> custOrderInfo = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, "001", "", "Y");
		orderPrice = new BigDecimal(custOrderInfo.get(0).getTotalPrice().toString());
		pointAmount = new BigDecimal(custOrderInfo.get(0).getPointAmount().toString());
		phoneNumber = custOrderInfo.get(0).getPhoneNumber();
		recipient = custOrderInfo.get(0).getRecipient();
		//orderDate = custOrderInfo.get(0).getTotalPrice().toString();
		
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO.setUserId(userId);
		BigDecimal point = this.pointService.getPointAmount(pointInfoCDTO);
		
		if(point.compareTo(pointAmount) < 0) {
			return JsonUtils.returnValue("0001", "포인트 금액이 일치하지 않습니다.", rtnMap).toString();
		}
		
		//주문항목정보
		List<CustOrderItemListPDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(_orderNo, "001", "", "N");
		
		int cnt = custOrderItemList.size();
		String _productId = "";
		BigDecimal orderCnt = new BigDecimal("0");
		BigDecimal productStock = new BigDecimal("0");
		String eventId = "";
		EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
		String paymentConfirm = "Y";
		
		BigDecimal personBuyCount = new BigDecimal("0");
		BigDecimal eventCount = new BigDecimal("0");
		BigDecimal personPaymentCount = new BigDecimal("0");
		BigDecimal eventPaymentCount = new BigDecimal("0");
		BigDecimal _zero = new BigDecimal("0");
		
		for(int i = 0; i < cnt; i++) {
			_productId = custOrderItemList.get(i).getProductId();
			orderCnt = custOrderItemList.get(i).getProductCount();
			List<ProductInfo> productInfo = this.productService.getProductPriceByProductId(_productId);
			productStock = productInfo.get(0).getProductStock();
			eventId = productInfo.get(0).getEventId();
			
			if("".equals(eventId) || eventId == null) {
				if(productStock.compareTo(orderCnt) < 0) {
					paymentConfirm = "N";
					break;
				}
			} else {
				eventInfoCDTO = new EventInfoCDTO();
				eventInfoCDTO.setSearchGb("000");
				eventInfoCDTO.setEventCfcd("000");
				eventInfoCDTO.setPageCnt("0");
				eventInfoCDTO.setMaxPage("5");
				eventInfoCDTO.setEventId(eventId);
				List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
				
				if("001".equals(eventInfoList.get(0).getEventStateCode())) {
					//이벤트상품정보
					List<EventProductInfoDTO> eventProductInfo = this.eventMstService.getEventProductInfo(_productId, eventId);
					
					personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
					eventCount = eventProductInfo.get(0).getEventCount();
					personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
					eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
					
					if(personBuyCount.compareTo(_zero) == 0) {
						personBuyCount = productStock;
					} else {
						personBuyCount = personBuyCount.subtract(personPaymentCount);
						
						if(personBuyCount.compareTo(_zero) < 0) {
							personBuyCount = _zero;
						}
					}
					
					if(eventCount.compareTo(_zero) == 0) {
						eventCount = productStock;
					} else {
						eventCount = eventCount.subtract(eventPaymentCount);
						
						if(eventCount.compareTo(_zero) < 0) {
							eventCount = _zero;
						}
					}
					
					if(personBuyCount.compareTo(_zero) == 0 || eventCount.compareTo(_zero) == 0) {
						paymentConfirm = "N";
						break;
					}
				} else {
					paymentConfirm = "N";
					break;
				}
			}
		}
		
		if(paymentPrice.compareTo(orderPrice) == 0 && "Y".equals(paymentConfirm)) {
			this.telmsgLogService.addTelmsgLog("01", "04", "1", map,"");
			paymentData = this.bootpayUtils.getBootpayConfirm(_receiptId);
			this.telmsgLogService.addTelmsgLog("01", "04", "2", paymentData,"");
			if(paymentData.get("error_code") == null) {
				try {
					PaymentDTO paymentDTO = new PaymentDTO();
					
					BigDecimal _cancelled_payment_amount = new BigDecimal(0);
					BigDecimal _payment_amount = new BigDecimal(0);
					
					try {
						_payment_amount = new BigDecimal((Integer) paymentData.get("price"));
					}catch (Exception e) {
						_payment_amount = new BigDecimal((Double) paymentData.get("price"));
					}

					try {
						_cancelled_payment_amount = new BigDecimal((Integer) paymentData.get("cancelled_price"));
					}catch (Exception e) {
						_cancelled_payment_amount = new BigDecimal((Double) paymentData.get("cancelled_price"));
					}
					
					
					String _receipt_id = (String) paymentData.get("receipt_id");
					String _order_no = (String) paymentData.get("order_id");
					String _order_name = (String) paymentData.get("order_name");
					String _payment_gb = (String) paymentData.get("method_origin");
					String _payment_name = (String) paymentData.get("method");
					String _method_symbol = (String) paymentData.get("method_symbol");
					String _payment_dttm = (String) paymentData.get("purchased_at");
					String _payment_req_dttm = (String) paymentData.get("requested_at");
					//_cancelled_payment_amount = new BigDecimal((Double) paymentData.get("cancelled_price"));
					String _cancelled_payment_dttm = (String) paymentData.get("cancelled_at");
					String _order_date = (String) paymentData.get("requested_at");
					//_payment_amount = new BigDecimal((Double) paymentData.get("price"));
					String _pg_name = (String) paymentData.get("pg");
					String _receipt_url = (String) paymentData.get("receipt_url");


					String _approve_no = "";
					String _cust_payment_no = "";
					String _pg_id = "";
					String _pg_cust_nm = "";
					String _pg_pay_co_cd = "";
					String _pg_pay_co_nm = "";
					String _card_quota = "";
					
					Map tidMap = new HashMap<>();
					tidMap = (Map) paymentData.get(_method_symbol + "_data");
					_pg_id = (String) tidMap.get("tid");
					if("card".equals(_method_symbol)) {
						Map cardData = new HashMap<>();
						
						cardData = (Map) paymentData.get("card_data");
						_approve_no = (String) cardData.get("card_approve_no");
						_cust_payment_no = (String) cardData.get("card_no");
						//_pg_id = (String) cardData.get("tid");
						//_pg_cust_nm = (String) cardData.get("order_id");
						_pg_pay_co_cd = (String) cardData.get("card_company_code");
						_pg_pay_co_nm = (String) cardData.get("card_company");
						_card_quota = (String) cardData.get("card_quota");
					}
					
					if(_cancelled_payment_dttm != null) {
						_cancelled_payment_dttm = _cancelled_payment_dttm.replace("T", " ");
					}
					
					paymentDTO.setReceiptId(_receipt_id);
					paymentDTO.setOrderNo(_order_no);
					paymentDTO.setOrderName(_order_name);
					paymentDTO.setPaymentGb(_payment_gb);
					paymentDTO.setPaymentDttm(_payment_dttm.replace("T", " "));
					paymentDTO.setPaymentReqDttm(_payment_req_dttm.replace("T", " "));
					paymentDTO.setCancelledPaymentAmount(_cancelled_payment_amount);
					paymentDTO.setCancelledPaymentDttm(_cancelled_payment_dttm);
					paymentDTO.setOrderDate("now()");
					paymentDTO.setPaymentAmount(_payment_amount);
					paymentDTO.setPointAmount(pointAmount);
					paymentDTO.setCancelledPointAmount(cancelledPointAmount);
					paymentDTO.setPgName(_pg_name);
					paymentDTO.setReceiptUrl(_receipt_url);
					paymentDTO.setApproveNo(_approve_no);
					paymentDTO.setCustPaymentNo(_cust_payment_no);
					paymentDTO.setPgId(_pg_id);
					paymentDTO.setPgCustNm(_pg_cust_nm);
					paymentDTO.setPgPayCoCd(_pg_pay_co_cd);
					paymentDTO.setPgPayCoNm(_pg_pay_co_nm);
					paymentDTO.setCardQuota(_card_quota);
					paymentDTO.setPaymentName(_payment_name);
					
					//결제처리
					this.paymentService.addPaymentInfo(paymentDTO, userId);
					//this.productOrderService.modifyProductOrderInfoStat("003",_orderNo);
					//this.productOrderService.modifyProductOrderItemInfoStat("003",_orderNo);
					//this.productService.modifyProductStock(_orderNo);
					
					DecimalFormat df = new DecimalFormat("###,###");
					String formatMoney = df.format(_payment_amount.add(pointAmount));
					
					if(!"".equals(recipient)) {
						SendFormInfoCDTO sendFormInfoCDTO = new SendFormInfoCDTO();
						
						sendFormInfoCDTO.setSendFormCode("00000007");
						sendFormInfoCDTO.setRecipient(recipient);
						sendFormInfoCDTO.setProductName(_order_name);
						sendFormInfoCDTO.setOrderNo(_orderNo);
						sendFormInfoCDTO.setTotalAmount(formatMoney);
						sendFormInfoCDTO.setReceiveUserId(userId);
						
						this.sendFormInfoService.sendBiztalkInfo(sendFormInfoCDTO);
					}
					
					StringBuilder slackMsg = new StringBuilder();
					slackMsg.append("주문번호 : ")
					.append(_order_no)
					.append("\n상품명 : ")
					.append(_order_name)
					.append("\n결제금액 : ")
					.append(_payment_amount)
					.append("\n사용포인트 : ")
					.append(pointAmount)
					.append("\n구매자 : ")
					.append(userNmae)
					.append("\n결제방법 : ")
					.append(_payment_gb)
					.append("\n결제수단 : ")
					.append(_payment_name);
					
					//slackWebhook.
					this.slackWebhook.postSlackMessage(slackMsg.toString(), "01");
					
					List<PushIdDTO> pushIdList = this.paymentService.getSellerPushId(_order_no);
					
					AppPushDTO appPushDTO = new AppPushDTO();
					
					appPushDTO.setAllYn("N");
					appPushDTO.setTitle("[도매도] 신규주문 알림!");
					appPushDTO.setUrl("");
					
					String sellerId = "";
					
					ArrayList<String> push = new ArrayList<>();
					for(int i = 0; i < pushIdList.size(); i++) {
						push = new ArrayList<>();
						sellerId = pushIdList.get(i).getUserId();
						push.add(0, pushIdList.get(i).getPushId());
//						if ("00000069".equals(sellerId)) {
//							push.add(0, "e01fc4e3-92bb-486c-aa8c-f7c88d4514b9");
//						} else if ("00000070".equals(sellerId)) {
//							push.add(0, "ae63fb01-b52c-459a-b51b-509ecd93f22a");
//						} else if ("00000072".equals(sellerId)) {
//							push.add(0, "ace1539a-3b58-4fe4-814a-3d1c76eeecb3");
//						} else if ("00000606".equals(sellerId)) {
//							push.add(0, "45d05a1f-7945-4d27-ad6e-a3c5987f9389");
//						} else if ("00000601".equals(sellerId)) {
//							push.add(0, "fef10bec-e5af-4c62-a9e8-d09e61a71cd3");
//						} 
						appPushDTO.setContent("상품 : " + pushIdList.get(i).getProductName());
						appPushDTO.setPushList(push);
						
						this.appPush.getAppPush(appPushDTO);
					}
				}catch (Exception e) {
					receiptCancleData = this.bootpayUtils.getBootpayReceiptCancel(_receiptId, "관리자", "승인오류", null);
					
					Map exceptionMap = new HashMap<>();
					exceptionMap.put("exception", ExceptionUtils.getStackTrace(e));
					this.telmsgLogService.addTelmsgLog("99", "00", "1", exceptionMap, e.toString());
					//log.info("receiptCancleData========= " + receiptCancleData);
					return JsonUtils.returnValue("9999", "결제시 오류가 발생하였습니다.", rtnMap).toString();
				}
		    }else {
		    	return JsonUtils.returnValue("9999", paymentData.get("message").toString(), rtnMap).toString();
		    }
			rtnMap.put("orderNo", _orderNo);
			
			return JsonUtils.returnValue("0000", "결제가 완료되었습니다", rtnMap).toString();		
		} else {
			return JsonUtils.returnValue("9999", "결제시 오류가 발생하였습니다.", rtnMap).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/cancel")
	public String paymentCancle(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map,"");
		Map rtnMap = new HashMap<>();
		String rtnMsg = "";
		PaymentCancleDTO paymentCancleDTO = new PaymentCancleDTO();
		paymentCancleDTO = (PaymentCancleDTO) ParameterUtils.setDto(map, paymentCancleDTO, "insert", sessionBean);
		
		String _orderNo = paymentCancleDTO.getOrderNo();
		String userId = sessionBean.getUserId();
		String userNm = sessionBean.getUserNm();
		String _productId = paymentCancleDTO.getProductId();
		String _receiptId = "";
		String _allCancleYn = "N";
		String _rtnMsg = "";
		BigDecimal _productTotalAmt = new BigDecimal("0");
		BigDecimal _orderTotalAmt = new BigDecimal("0");
		BigDecimal _paymentAmount = new BigDecimal("0");
		BigDecimal _cancelledPaymentAmount = new BigDecimal("0");
		BigDecimal _pointAmount = new BigDecimal("0");
		BigDecimal _pointAmt = new BigDecimal("0");
		BigDecimal _cancelledPointAmount = new BigDecimal("0");
		BigDecimal _deliveryAmount = new BigDecimal("0");
		BigDecimal _cancelledPointAmt = new BigDecimal("0");
		BigDecimal _addPointAmt = new BigDecimal("0");
		BigDecimal _paymentAmt = new BigDecimal("0");
		BigDecimal _totalPaymentAmt = new BigDecimal("0");
		BigDecimal _amt = new BigDecimal("0");
		Double _cancleAmt = 0.0;
		
		BigDecimal _orderAmount = new BigDecimal("0");
		BigDecimal _cancelledOrderAmount = new BigDecimal("0");
		BigDecimal _orderDeliveryAmount = new BigDecimal("0");
		
		//주문상태확인
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		String _sellerId = productOrderItemInfo.get(0).getSellerId();
		
		if("002".equals(_orderItemStateCode)) {
			return JsonUtils.returnValue("0000", "이미 취소된 주문입니다.", rtnMap).toString();
		}else {
			if("100".equals(_orderItemDlngStateCode)){
				//승인번호 채번 금액
				List<PaymentCancleDTO> _paymentCancleList = this.paymentService.getPaymentCancleInfo(paymentCancleDTO);
				
				if(_paymentCancleList.size() > 0) {
					Optional<OrderPaymentInfo> orderPaymentInfo = this.productOrderService.getOrderPaymentInfo(_orderNo, _sellerId);
					
					_productTotalAmt = _paymentCancleList.get(0).getProductTotalAmt();
					_orderTotalAmt = _paymentCancleList.get(0).getOrderTotalAmt();
					_receiptId = _paymentCancleList.get(0).getReceiptId();
					_paymentAmount = _paymentCancleList.get(0).getPaymentAmount();
					_deliveryAmount = _paymentCancleList.get(0).getDeliveryAmount();
					_cancelledPaymentAmount = _paymentCancleList.get(0).getCancelledPaymentAmount();
					_pointAmount = _paymentCancleList.get(0).getPointAmount();
					_cancelledPointAmt = _paymentCancleList.get(0).getCancelledPointAmount();
					_cancelledPointAmount = _paymentCancleList.get(0).getCancelledPointAmount();
					_paymentAmt = _paymentAmount.subtract(_cancelledPaymentAmount);
					_pointAmt = _pointAmount.subtract(_cancelledPointAmount);
					paymentCancleDTO.setReceiptId(_receiptId);
					paymentCancleDTO.setProductName(_paymentCancleList.get(0).getProductName());
					paymentCancleDTO.setPhoneNumber(_paymentCancleList.get(0).getPhoneNumber());
					
					_orderAmount = new BigDecimal("0");
					_cancelledOrderAmount = new BigDecimal("0");
					_orderDeliveryAmount = new BigDecimal("0");
					
					_totalPaymentAmt = _paymentAmt.add(_pointAmt);
					//취소처리
					_amt = _productTotalAmt;
					if(_productId == null || "".equals(_productId)) {
						_amt = _orderTotalAmt;
					}
					
					_orderAmount = orderPaymentInfo.get().getOrderAmount();
					//_orderAmount = _orderAmount.add(_pointAmt);
					_cancelledOrderAmount = orderPaymentInfo.get().getCancelledOrderAmount();
					_orderDeliveryAmount = orderPaymentInfo.get().getDeliveryAmount();
					
					_orderAmount = _orderAmount.subtract(_cancelledOrderAmount);
					
					_cancelledOrderAmount = _cancelledOrderAmount.add(_amt);
					
					if(_amt.compareTo(_orderAmount) == 0) {
						_amt = _amt.add(_orderDeliveryAmount);
					} 
					
					if(_totalPaymentAmt.compareTo(_amt) >= 0) {
						if(_totalPaymentAmt.compareTo(_amt) == 0) {
							_cancleAmt = _paymentAmt.doubleValue();
							_cancelledPointAmount = _cancelledPointAmount.add(_pointAmt);
							
							_allCancleYn = "Y";
						} else {
							if(_paymentAmt.compareTo(_amt) >= 0) {
								if(_pointAmt.compareTo(_amt) >= 0 ) {
									_cancelledPointAmount = _cancelledPointAmount.add(_amt);
									_cancleAmt = 0.0;
								} else {
									_cancleAmt = _amt.subtract(_pointAmt).doubleValue();
									_cancelledPointAmount = _cancelledPointAmount.add(_pointAmt);
								}
							} else {
								//_cancleAmt = _paymentAmt.doubleValue();
								_cancleAmt = 0.0;
								//_cancelledPointAmount = _cancelledPointAmount.add(_amt.subtract(_paymentAmt));
								_cancelledPointAmount = _cancelledPointAmount.add(_amt);
							}
						}
						_addPointAmt = _cancelledPointAmount.subtract(_cancelledPointAmt);
						//if(_paymentAmt.compareTo(_amt) == 0) {
						//	_allCancleYn = "Y";
						//}
						this.telmsgLogService.addTelmsgLog("01", "90", "1", map,"");
						
						OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
						
						orderPaymentDTO.setOrderNo(_orderNo);
						orderPaymentDTO.setSellerId(_sellerId);
						orderPaymentDTO.setCancelledOrderAmount(_cancelledOrderAmount);
						
						_rtnMsg = this.paymentService.paymentCancle(paymentCancleDTO, orderPaymentDTO,_allCancleYn, _cancleAmt,_cancelledPointAmount
																	, _addPointAmt, _orderItemDlngStateCode, userId, userNm);
						this.telmsgLogService.addTelmsgLog("01", "90", "2", map,"");
					}else {
						return JsonUtils.returnValue("9999", "취소금액불일치", rtnMap).toString();
					}
					
					if("".equals(_rtnMsg)) {
						return JsonUtils.returnValue("0000", "취소되었습니다.", rtnMap).toString();
					}else {
						return JsonUtils.returnValue("9999", _rtnMsg, rtnMap).toString();
					}
				}else {
					return JsonUtils.returnValue("9999", "결제취소건이 없습니다.", rtnMap).toString();
				}
			}else if("200".equals(_orderItemDlngStateCode) 
					|| "300".equals(_orderItemDlngStateCode)) {
				//취소요청만 가능
				this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "800");
				
				StringBuilder slackMsg = new StringBuilder();
				slackMsg
				.append("고객 취소요청 알림")
				.append("주문번호 : ")
				.append(_orderNo)
				.append("\n취소요청 고객명 : ")
				.append(userNm);
				
				//slackWebhook.
				this.slackWebhook.postSlackMessage(slackMsg.toString(), "03");
				
				return JsonUtils.returnValue("0000", "취소요청 하였습니다.", rtnMap).toString();
			}else {
				//그 외 취소불가
				return JsonUtils.returnValue("0000", "취소가능한 주문이 없습니다.", rtnMap).toString();
			}
		}
	}
	
	@ResponseBody
	@PostMapping("/addTempPaymentInfo")
	public String addTempPaymentInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		
		TempPaymentInfoDTO tempPaymentInfoDTO = new TempPaymentInfoDTO();
		tempPaymentInfoDTO = (TempPaymentInfoDTO) ParameterUtils.setDto(map, tempPaymentInfoDTO, "insert", sessionBean);
		
		this.paymentService.addTempPaymentInfo(tempPaymentInfoDTO);
		
		return JsonUtils.returnValue("0000", "저장 되었습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/tempPaymentInfo")
	public String getTempPaymentInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		
		TempPaymentInfoDTO tempPaymentInfoDTO = new TempPaymentInfoDTO();
		tempPaymentInfoDTO = (TempPaymentInfoDTO) ParameterUtils.setDto(map, tempPaymentInfoDTO, "insert", sessionBean);
		
		Optional<TempPaymentInfo> tempPaymentInfo = this.paymentService.getTempPaymentInfo(tempPaymentInfoDTO);
		
		if(tempPaymentInfo.isPresent()) {
			rtnMap.put("tempPaymentInfo", tempPaymentInfo.get());
		}else {
			rtnMap.put("tempPaymentInfo", "");
		}
		
		return JsonUtils.returnValue("0000", "조회 되었습니다.", rtnMap).toString();
	}
}
