package com.lawzone.market.payment.controller;

import java.math.BigDecimal;
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
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.payment.service.PaymentCancleDTO;
import com.lawzone.market.payment.service.PaymentDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

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
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/confirm")
	public String paymentConfirm(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		Map rtnMap = new HashMap<>();
		
		Map paymentMap = new HashMap<>();
		
		paymentMap = (Map) map.get("dataset");
		
		String _event = (String) paymentMap.get("event");
		String _receiptId = (String) paymentMap.get("receipt_id");
		String _orderNo = (String) paymentMap.get("order_id");
		String orderDate = "";
		BigDecimal paymentPrice = new BigDecimal("0");
		BigDecimal orderPrice = new BigDecimal("0");
		BigDecimal paymentOrderStock = new BigDecimal("0");
		
		
		Map receiptData = new HashMap<>();
		Map paymentData = new HashMap<>();
		Map receiptCancleData = new HashMap<>();
		
		this.telmsgLogService.addTelmsgLog("01", "01", "1", map);
		receiptData = this.bootpayUtils.getBootpayReceipt(_receiptId);
		this.telmsgLogService.addTelmsgLog("01", "01", "2", receiptData);
		
		if(receiptData.get("error_code") != null) {
			//log.info("receiptData================" + receiptData);
			return JsonUtils.returnValue("9999", receiptData.get("message").toString(), rtnMap).toString();
	    }
		
		paymentPrice = new BigDecimal(receiptData.get("price").toString());
		
		//????????????
		List<CustOrderInfoDTO> custOrderInfo = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, "001");
		orderPrice = new BigDecimal(custOrderInfo.get(0).getTotalPrice().toString());
		//orderDate = custOrderInfo.get(0).getTotalPrice().toString();
		
		//??????????????????
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getCustOrderItemList(_orderNo, "001");
		
		int cnt = custOrderItemList.size();
		String _productId = "";
		BigDecimal orderCnt = new BigDecimal("0");
		BigDecimal productStock = new BigDecimal("0");
		
		String paymentConfirm = "Y";
		for(int i = 0; i < cnt; i++) {
			_productId = custOrderItemList.get(i).getProductId();
			orderCnt = custOrderItemList.get(i).getProductCount();
			List<ProductInfo> productInfo = this.productService.getProductPriceByProductId(_productId);
			productStock = productInfo.get(0).getProductStock();
			
			if(productStock.compareTo(orderCnt) == -1) {
				paymentConfirm = "N";
				break;
			}
		}
		
		if(paymentPrice.compareTo(orderPrice) == 0 && "Y".equals(paymentConfirm)) {
			this.telmsgLogService.addTelmsgLog("01", "04", "1", map);
			paymentData = this.bootpayUtils.getBootpayConfirm(_receiptId);
			this.telmsgLogService.addTelmsgLog("01", "04", "2", paymentData);
			if(paymentData.get("error_code") == null) {
				try {
					PaymentDTO paymentDTO = new PaymentDTO();
					
					String _receipt_id = (String) paymentData.get("receipt_id");
					String _order_no = (String) paymentData.get("order_id");
					String _order_name = (String) paymentData.get("order_name");
					String _payment_gb = (String) paymentData.get("method_origin");
					String _method_symbol = (String) paymentData.get("method_symbol");
					String _payment_dttm = (String) paymentData.get("purchased_at");
					String _payment_req_dttm = (String) paymentData.get("requested_at");
					BigDecimal _cancelled_payment_amount = new BigDecimal((Double) paymentData.get("cancelled_price"));
					String _cancelled_payment_dttm = (String) paymentData.get("cancelled_at");
					String _order_date = (String) paymentData.get("requested_at");
					BigDecimal _payment_amount = new BigDecimal((Double) paymentData.get("price"));
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
					if("card".equals(_payment_gb)) {
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
					paymentDTO.setPgName(_pg_name);
					paymentDTO.setReceiptUrl(_receipt_url);
					paymentDTO.setApproveNo(_approve_no);
					paymentDTO.setCustPaymentNo(_cust_payment_no);
					paymentDTO.setPgId(_pg_id);
					paymentDTO.setPgCustNm(_pg_cust_nm);
					paymentDTO.setPgPayCoCd(_pg_pay_co_cd);
					paymentDTO.setPgPayCoNm(_pg_pay_co_nm);
					paymentDTO.setCardQuota(_card_quota);
					
					//????????????
					this.paymentService.addPaymentInfo(paymentDTO);
					//this.productOrderService.modifyProductOrderInfoStat("003",_orderNo);
					//this.productOrderService.modifyProductOrderItemInfoStat("003",_orderNo);
					//this.productService.modifyProductStock(_orderNo);
				}catch (Exception e) {
					receiptCancleData = this.bootpayUtils.getBootpayReceiptCancel(_receiptId, "?????????", "????????????", null);
					//log.info("receiptCancleData========= " + receiptCancleData);
					return JsonUtils.returnValue("9999", "????????? ????????? ?????????????????????.", rtnMap).toString();
				}
		    }else {
		    	return JsonUtils.returnValue("9999", paymentData.get("message").toString(), rtnMap).toString();
		    }
		}
		rtnMap.put("orderNo", _orderNo);
		
		return JsonUtils.returnValue("0000", "????????? ?????????????????????", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/cancel")
	public String paymentCancle(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map);
		Map rtnMap = new HashMap<>();
		String rtnMsg = "";
		PaymentCancleDTO paymentCancleDTO = new PaymentCancleDTO();
		paymentCancleDTO = (PaymentCancleDTO) ParameterUtils.setDto(map, paymentCancleDTO, "insert", sessionBean);
		
		String _orderNo = paymentCancleDTO.getOrderNo();
		String _productId = paymentCancleDTO.getProductId();
		String _receiptId = "";
		String _allCancleYn = "N";
		String _rtnMsg = "";
		BigDecimal _productTotalAmt = new BigDecimal("0");
		BigDecimal _orderTotalAmt = new BigDecimal("0");
		BigDecimal _paymentAmount = new BigDecimal("0");
		BigDecimal _cancelledPaymentAmount = new BigDecimal("0");
		BigDecimal _paymentAmt = new BigDecimal("0");
		BigDecimal _amt = new BigDecimal("0");
		Double _cancleAmt = 0.0;
		
		//??????????????????
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		
		if("002".equals(_orderItemStateCode)) {
			return JsonUtils.returnValue("0000", "?????? ????????? ???????????????.", rtnMap).toString();
		}else {
			if("100".equals(_orderItemDlngStateCode)){
				//???????????? ?????? ??????
				List<PaymentCancleDTO> _paymentCancleList = this.paymentService.getPaymentCancleInfo(paymentCancleDTO);
				
				if(_paymentCancleList.size() > 0) {
					_productTotalAmt = _paymentCancleList.get(0).getProductTotalAmt();
					_orderTotalAmt = _paymentCancleList.get(0).getOrderTotalAmt();
					_receiptId = _paymentCancleList.get(0).getReceiptId();
					_paymentAmount = _paymentCancleList.get(0).getPaymentAmount();
					_cancelledPaymentAmount = _paymentCancleList.get(0).getCancelledPaymentAmount();
					_paymentAmt = _paymentAmount.subtract(_cancelledPaymentAmount);
					paymentCancleDTO.setReceiptId(_receiptId);
					
					//????????????
					_amt = _productTotalAmt;
					if(_productId == null || "".equals(_productId)) {
						_amt = _orderTotalAmt;
					}
					
					if(_paymentAmt.compareTo(_amt) >= 0) {
						_cancleAmt = _amt.doubleValue();
						if(_paymentAmt.compareTo(_amt) == 0) {
							_allCancleYn = "Y";
						}
						this.telmsgLogService.addTelmsgLog("01", "90", "1", map);
						_rtnMsg = this.paymentService.paymentCancle(paymentCancleDTO, _allCancleYn, _cancleAmt, _orderItemDlngStateCode);
					}else {
						return JsonUtils.returnValue("9999", "?????????????????????", rtnMap).toString();
					}
					
					if("".equals(_rtnMsg)) {
						return JsonUtils.returnValue("0000", "?????????????????????.", rtnMap).toString();
					}else {
						return JsonUtils.returnValue("9999", _rtnMsg, rtnMap).toString();
					}
				}else {
					return JsonUtils.returnValue("9999", "?????????????????? ????????????.", rtnMap).toString();
				}
			}else if("200".equals(_orderItemDlngStateCode) || "300".equals(_orderItemDlngStateCode)) {
				//??????????????? ??????
				this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "800");
				return JsonUtils.returnValue("0000", "???????????? ???????????????.", rtnMap).toString();
			}else {
				//??? ??? ????????????
				return JsonUtils.returnValue("0000", "??????????????? ????????? ????????????.", rtnMap).toString();
			}
		}
	}
}
