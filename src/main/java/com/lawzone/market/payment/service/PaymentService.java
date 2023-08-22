package com.lawzone.market.payment.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.cart.dao.CartInfoDAO;
import com.lawzone.market.cart.service.CartInfo;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.order.dao.ProductOrderDAO;
import com.lawzone.market.order.dao.ProductOrderItemInfoDAO;
import com.lawzone.market.order.dao.ProductOrderJdbcDAO;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.OrderPaymentDTO;
import com.lawzone.market.order.service.ProductOrderDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.order.service.ProductOrderUserInfoDTO;
import com.lawzone.market.payment.dao.PaymentDAO;
import com.lawzone.market.payment.dao.PaymentJdbcDAO;
import com.lawzone.market.payment.dao.TempPaymentInfoDAO;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.dao.ProductJdbcDAO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.send.service.SendFormInfoCDTO;
import com.lawzone.market.send.service.SendFormInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.util.SlackWebhook;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {
	private final PaymentDAO paymentDAO;
	private final ModelMapper modelMapper;
	private final ProductOrderJdbcDAO productOrderJdbcDAO;
	private final PaymentJdbcDAO paymentJdbcDAO;
	private final CartInfoDAO cartInfoDAO;
	private final UtilService utilService;
	private final ProductJdbcDAO productJdbcDAO;
	private final ProductOrderItemInfoDAO productOrderItemInfoDAO;
	private final BootpayUtils bootpayUtils;
	private final TelmsgLogService telmsgLogService;
	private final ProductOrderDAO productOrderDAO;
	private final TempPaymentInfoDAO tempPaymentInfoDAO;
	private final SlackWebhook slackWebhook;
	private final PointService pointService;
	private final ProductOrderService productOrdeService;
	private final SendFormInfoService sendFormInfoService;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional(rollbackFor = Exception.class)
	public String addPaymentInfo(PaymentDTO paymentDTO, String userId) {
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo = modelMapper.map(paymentDTO, PaymentInfo.class);
		
		String orderNo = paymentDTO.getOrderNo();
		
		String receiptId = this.paymentDAO.save(paymentInfo).getReceiptId();
		
		String sqlModifyProductStock = this.productJdbcDAO.modifyProductStock("N");
		ArrayList<String> _queryValue3 = new ArrayList<>();
		_queryValue3.add(0, orderNo);
		_queryValue3.add(1, "001");
		
		this.utilService.getQueryStringUpdate(sqlModifyProductStock,_queryValue3);
		
		String sqlModifyOrderInfoStat = this.productOrderJdbcDAO.modifyOrderInfoStat();
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, "003");
		_queryValue1.add(1, orderNo);
		_queryValue1.add(2, "001");
		
		CustOrderInfoDTO custOrderInfoDTO = new CustOrderInfoDTO();
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
		
		String sqlModifyOrderItemInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoStat("N");
		ArrayList<String> _queryValue2 = new ArrayList<>();
		_queryValue2.add(0, "003");
		_queryValue2.add(1, "100");
		_queryValue2.add(2, orderNo);
		_queryValue2.add(3, "001");
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderItemInfoStat,_queryValue2);
		
		//장바구니 삭제
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderItemInfoDAO.findByIdOrderNoAndOrderItemStateCodeAndCartNumberIsNotNull(orderNo, "003");
		
		int prdtItmSize = productOrderItemInfo.size();
		Long cartNumber;
		
		for (int i = 0; i < prdtItmSize; i++) {
			cartNumber = productOrderItemInfo.get(i).getCartNumber();
			cartInfoDAO.deleteById(cartNumber);
		}
		
		//포인트 차감
		if(paymentDTO.getPointAmount().compareTo(new BigDecimal("0")) > 0) {
			PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
			pointInfoCDTO.setUserId(userId);
			pointInfoCDTO.setPointValue(paymentDTO.getPointAmount());
			pointInfoCDTO.setEventCode("003");
			pointInfoCDTO.setEventId(orderNo);
			this.pointService.setPointDifference(pointInfoCDTO);
		}
		return receiptId;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List<PaymentCancleDTO> getPaymentCancleInfo(PaymentCancleDTO paymentCancleDTO) {
		String _orderNo = paymentCancleDTO.getOrderNo();
		String _productId = paymentCancleDTO.getProductId();
		String _productIdYn = "Y";
		
		if("".equals(_productId) || _productId == null) {
			_productIdYn = "N";
		}
		
		String _query = this.paymentJdbcDAO.getpaymentCancleAmt(_productIdYn);
		
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, _orderNo);
		if(!("".equals(_productId) || _productId == null)) {
			_queryValue1.add(1, _productId);
		}
		
		PaymentCancleDTO paymentCancleList = new PaymentCancleDTO();
		
		List<PaymentCancleDTO> rtnList =  this.utilService.getQueryString(_query,paymentCancleList, _queryValue1);
		
		return rtnList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String paymentCancle(PaymentCancleDTO paymentCancleDTO , OrderPaymentDTO orderPaymentDTO
				, String _allCancleYn, Double _cancleAmt, BigDecimal _cancelledPointAmount
				, BigDecimal _addPointAmt, String _orderDlngStateCode, String userId, String userNm) {
		
		String _orderNo = paymentCancleDTO.getOrderNo();
		String _receiptId = paymentCancleDTO.getReceiptId();
		String _productId = paymentCancleDTO.getProductId();
		String _productName = paymentCancleDTO.getProductName();
		String _phoneNumber = paymentCancleDTO.getPhoneNumber();
		String _message = paymentCancleDTO.getMessage();
		String _productIdYn = "Y";
		String _rtnMsg = "";
		
		if("".equals(_productId) || _productId == null) {
			_productIdYn = "N";
		}
		Map receiptCancleData = new HashMap<>();
		
		if(_cancleAmt > 0) {
			
			this.telmsgLogService.addTelmsgLog("01", "90", "1", receiptCancleData,"");
			receiptCancleData = this.bootpayUtils.getBootpayReceiptCancel(_receiptId, sessionBean.getUserNm(), _message, _cancleAmt);
			this.telmsgLogService.addTelmsgLog("01", "90", "2", receiptCancleData,"");
		}
		
		if(receiptCancleData.get("error_code") == null) {
			
			List<PaymentInfo> _paymentInfo = this.paymentDAO.findByReceiptId(_receiptId);
			
			_paymentInfo.get(0).setCancelledPaymentDttm("now()");
			if(_cancleAmt > 0) {
				_paymentInfo.get(0).setCancelledPaymentAmount(new BigDecimal(receiptCancleData.get("cancelled_price").toString()));
			}
			_paymentInfo.get(0).setCancelledPointAmount(_cancelledPointAmount);
			
			String sqlModifyProductStock = this.productJdbcDAO.modifyProductStock(_productIdYn);
			ArrayList<String> _queryValue3 = new ArrayList<>();
			_queryValue3.add(0, _orderNo);
			_queryValue3.add(1, "003");
			if("Y".equals(_productIdYn)) {
				_queryValue3.add(2, _productId);
			}
			
			this.utilService.getQueryStringUpdate(sqlModifyProductStock,_queryValue3);
			
			String sqlModifyOrderInfoStat = this.productOrderJdbcDAO.modifyOrderInfoStat();
			ArrayList<String> _queryValue1 = new ArrayList<>();
			String productOrderStatCode = "003";
			
			if("Y".equals(_allCancleYn)) {
				List<ProductOrderInfo> productOrderInfo = this.productOrderDAO.findByOrderNo(_orderNo);
				productOrderStatCode = productOrderInfo.get(0).getOrderStateCode();
				_queryValue1.add(0, "002");
				//_queryValue1.add(1, "900");
			}else {
				_queryValue1.add(0, "004");
				//_queryValue1.add(1, _orderDlngStateCode);
			}
			
			_queryValue1.add(1, _orderNo);
			_queryValue1.add(2, productOrderStatCode);
			
			this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
			
			String sqlModifyOrderItemInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoStat(_productIdYn);
			ArrayList<String> _queryValue2 = new ArrayList<>();
			_queryValue2.add(0, "002");
			_queryValue2.add(1, "900");
			_queryValue2.add(2, _orderNo);
			_queryValue2.add(3, "003");
			if("Y".equals(_productIdYn)) {
				_queryValue2.add(4, _productId);
			}
			
			this.utilService.getQueryStringUpdate(sqlModifyOrderItemInfoStat,_queryValue2);
			
			//포인트 원복
			if(_addPointAmt.compareTo(new BigDecimal("0")) > 0) {
				PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
				pointInfoCDTO.setUserId(userId);
				pointInfoCDTO.setPointValue(_addPointAmt);
				pointInfoCDTO.setEventCode("003");
				pointInfoCDTO.setEventId(_orderNo);
				this.pointService.addPoint(pointInfoCDTO);
			}
			
			//주문 취소금액 수정
			this.productOrdeService.modifyOrderPaymentInfo(orderPaymentDTO);
			
			try {
				String _order_no = _paymentInfo.get(0).getOrderNo();
				String _order_name = _paymentInfo.get(0).getOrderName();
				String _payment_name = _paymentInfo.get(0).getPaymentName();
				String _payment_gb = _paymentInfo.get(0).getPaymentGb();
				//BigDecimal _cancelled_payment_amount = new BigDecimal((Double) receiptCancleData.get("cancelled_price"));
				
				StringBuilder slackMsg = new StringBuilder();
				
				List<ProductOrderUserInfoDTO> productOrderUserInfo = this.productOrdeService.getProductOrderUserInfo(_orderNo);
				
				DecimalFormat df = new DecimalFormat("###,###");
				String formatMoney = df.format(_cancleAmt + _addPointAmt.doubleValue());
				
				SendFormInfoCDTO sendFormInfoCDTO = new SendFormInfoCDTO();
				
				sendFormInfoCDTO.setSendFormCode("00000008"); 
				sendFormInfoCDTO.setRecipient(_phoneNumber);
				sendFormInfoCDTO.setProductName(_productName);
				sendFormInfoCDTO.setOrderNo(_orderNo);
				sendFormInfoCDTO.setCancelledPaymentAmount(formatMoney);
				sendFormInfoCDTO.setReceiveUserId(userId);
				this.sendFormInfoService.sendBiztalkInfo(sendFormInfoCDTO);
				
				slackMsg.append("주문번호 : ")
				.append(_order_no)
				.append("\n상품명 : ")
				.append(_order_name)
				.append("\n취소금액 : ")
				.append(_cancleAmt)
				.append("\n취소 포인트 : ")
				.append(_cancelledPointAmount.doubleValue())
				.append("\n구매자 : ")
				.append(productOrderUserInfo.get(0).getUserName())
				.append("\n결제방법 : ")
				.append(_payment_gb)
				.append("\n결제수단 : ")
				.append(_payment_name)
				.append("\n취소자 : ")
				.append(userNm);
				
				//slackWebhook.
				this.slackWebhook.postSlackMessage(slackMsg.toString(), "02");
			}catch (Exception e) {
				
			}
			
		}else {
			return _rtnMsg = receiptCancleData.get("message").toString();
		}
		return _rtnMsg;
	}
	
	public List getOrderPaymentInfo(String orderNo, String userId) {
		String _query = this.paymentJdbcDAO.orderPaymentInfo(userId);
		
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, orderNo);
		_queryValue1.add(1, orderNo);
		_queryValue1.add(2, orderNo);
		
		if(!"".equals(userId)) {
			_queryValue1.add(3, userId);
		}
		
		PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
		
		return this.utilService.getQueryString(_query,paymentInfo, _queryValue1);
	}
	
	public void addTempPaymentInfo(TempPaymentInfoDTO tempPaymentInfoDTO) {
		TempPaymentInfo tempPaymentInfo = new TempPaymentInfo();
		tempPaymentInfo = modelMapper.map(tempPaymentInfoDTO, TempPaymentInfo.class);
		
		this.tempPaymentInfoDAO.save(tempPaymentInfo);
	}
	
	public Optional getTempPaymentInfo(TempPaymentInfoDTO tempPaymentInfoDTO) {
		Optional<TempPaymentInfo> rtnValue;
		
		List<TempPaymentInfo> tempPaymentInfo = this.tempPaymentInfoDAO.findByUserId(tempPaymentInfoDTO.getUserId());
		if(tempPaymentInfo.size() > 0) {
			rtnValue = this.tempPaymentInfoDAO.findTopByUserIdOrderByTempPaymentNumberDesc(tempPaymentInfoDTO.getUserId());
		}else {
			rtnValue = Optional.empty();
		}
		return rtnValue;
	}
	
	public List getSellerPushId(String orderNo) {
		String _query = this.paymentJdbcDAO.getSellerPushId();
		
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, orderNo);
		
		PushIdDTO pushIdDTO = new PushIdDTO();
		
		return this.utilService.getQueryString(_query,pushIdDTO, _queryValue1);
	}
}
