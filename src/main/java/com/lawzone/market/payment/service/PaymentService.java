package com.lawzone.market.payment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lawzone.market.order.service.ProductOrderDTO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.payment.dao.PaymentDAO;
import com.lawzone.market.payment.dao.PaymentJdbcDAO;
import com.lawzone.market.product.dao.ProductJdbcDAO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
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
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional(rollbackFor = Exception.class)
	public String addPaymentInfo(PaymentDTO paymentDTO) {
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
		_queryValue2.add(1, orderNo);
		_queryValue2.add(2, "001");
		
		this.utilService.getQueryStringUpdate(sqlModifyOrderItemInfoStat,_queryValue2);
		
		//장바구니 삭제
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderItemInfoDAO.findByIdOrderNoAndOrderItemStateCodeAndCartNumberIsNotNull(orderNo, "003");
		
		int prdtItmSize = productOrderItemInfo.size();
		Long cartNumber;
		
		for (int i = 0; i < prdtItmSize; i++) {
			cartNumber = productOrderItemInfo.get(i).getCartNumber();
			cartInfoDAO.deleteById(cartNumber);
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
	public String paymentCancle(PaymentCancleDTO paymentCancleDTO, String _allCancleYn, Double _cancleAmt) {
		
		String _orderNo = paymentCancleDTO.getOrderNo();
		String _receiptId = paymentCancleDTO.getReceiptId();
		String _productId = paymentCancleDTO.getProductId();
		String _message = paymentCancleDTO.getMessage();
		String _productIdYn = "Y";
		String _rtnMsg = "";
		
		if("".equals(_productId) || _productId == null) {
			_productIdYn = "N";
		}
		
		Map receiptCancleData = new HashMap<>();
		
		receiptCancleData = this.bootpayUtils.getBootpayReceiptCancel(_receiptId, sessionBean.getUserNm(), _message, _cancleAmt);
		this.telmsgLogService.addTelmsgLog("01", "90", "2", receiptCancleData);
		if(receiptCancleData.get("error_code") == null) {
			
			List<PaymentInfo> _paymentInfo = this.paymentDAO.findByReceiptId(_receiptId);
			
			_paymentInfo.get(0).setCancelledPaymentDttm("now()");
			_paymentInfo.get(0).setCancelledPaymentAmount(new BigDecimal(receiptCancleData.get("cancelled_price").toString()));
			
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
			if("Y".equals(_allCancleYn)) {
				_queryValue1.add(0, "002");
			}else {
				_queryValue1.add(0, "004");
			}
			
			_queryValue1.add(1, _orderNo);
			_queryValue1.add(2, "003");
			
			this.utilService.getQueryStringUpdate(sqlModifyOrderInfoStat,_queryValue1);
			
			String sqlModifyOrderItemInfoStat = this.productOrderJdbcDAO.modifyOrderItemInfoStat(_productIdYn);
			ArrayList<String> _queryValue2 = new ArrayList<>();
			_queryValue2.add(0, "002");
			_queryValue2.add(1, _orderNo);
			_queryValue2.add(2, "003");
			if("Y".equals(_productIdYn)) {
				_queryValue2.add(3, _productId);
			}
			
			
			this.utilService.getQueryStringUpdate(sqlModifyOrderItemInfoStat,_queryValue2);
		}else {
			return _rtnMsg = receiptCancleData.get("message").toString();
		}
		return _rtnMsg;
	}
	
	public List getOrderPaymentInfo(String orderNo) {
		String _query = this.paymentJdbcDAO.orderPaymentInfo();
		
		ArrayList<String> _queryValue1 = new ArrayList<>();
		_queryValue1.add(0, orderNo);
		
		PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
		
		return this.utilService.getQueryString(_query,paymentInfo, _queryValue1);
	}
}
