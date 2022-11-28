package com.lawzone.market.admin.controller;

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
import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.login.LoginDTO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.order.AdminOrderItemListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderListDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.dto.user.AdminUserDTO;
import com.lawzone.market.admin.service.GarakAdminService;
import com.lawzone.market.admin.service.ProductOrderItemBookIdInfo;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.common.service.MenuDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.CustOrderItemListDTO;
import com.lawzone.market.order.service.ProductOrderDTO;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.payment.service.PaymentCancleDTO;
import com.lawzone.market.payment.service.PaymentInfoDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.user.service.SellerInfoService;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/garak/admin")
@RequiredArgsConstructor
@Controller
public class GarakAdminController {
	@Resource
	private SessionBean sessionBean;
	
	private final SellerInfoService sellerInfoService;
	private final CommonService commonService;
	private final TelmsgLogService telmsgLogService;
	private final JwtTokenUtil jwtTokenUtil;
	private final ProductOrderService productOrderService;
	private final PaymentService paymentService;
	private final GarakAdminService garakAdminService;
	
	@ResponseBody
	@PostMapping("/login")
	public String getLogin(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		LoginDTO loginDTO = new LoginDTO();
		loginDTO = (LoginDTO) ParameterUtils.setDto(map, loginDTO, "insert", sessionBean);
		
		Map rtnMap = new HashMap<>();
		
		List<UserInfo> userInfo = this.sellerInfoService.getSellerInfo(loginDTO.getPortalId(), loginDTO.getPortalPw());
		
		List<MenuDTO> menuList = new ArrayList<>();
		
		if(!userInfo.isEmpty()) {
			menuList = this.commonService.getMenuInfo(userInfo.get(0).getUserId());
			
			rtnMap.put("adminToken", jwtTokenUtil.generateToken(userInfo.get(0)));
			rtnMap.put("menuTreeInfoList", menuList);
		}else {
			return JsonUtils.returnValue("9999", "로그인정보를 확인하세요", rtnMap).toString();
		}
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/orderList")
	public String getOrderList(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);
		
		if(Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}
		List<AdminPageInfoDTO> paging = this.productOrderService.getAdminOrderListPageInfo(adminOrderCDTO);
    	List<AdminOrderListDTO> adminOrder = this.productOrderService.getAdminOrderList(adminOrderCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("orderList", adminOrder);
		rtnMap.put("paging", paging.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/orderInfo")
	public String getOrderInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);
		
		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();
		
		//주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, "");
		
		//주문항목정보
		List<CustOrderItemListDTO> custOrderItemList = this.productOrderService.getAdminOrderItemList(_orderNo, _productId, "");
		
		//결제정보
		List<PaymentInfoDTO> paymentInfo = this.paymentService.getOrderPaymentInfo(_orderNo);
		
		//운송장내역
		List<ProductOrderItemBookIdInfo> productOrderItemBookIdInfo = this.garakAdminService.getBookIdList(adminOrderCDTO);
		Map rtnMap = new HashMap<>();
		rtnMap.put("orderInfo", custOrderInfoDTO.get(0));
		rtnMap.put("orderItemList", custOrderItemList);
		rtnMap.put("paymentInfo", paymentInfo.get(0));
		rtnMap.put("bookIdList", productOrderItemBookIdInfo);
		
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/paymentcancel")
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
		
		//주문상태확인
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		
		if("002".equals(_orderItemStateCode)) {
			return JsonUtils.returnValue("0000", "이미 취소된 주문입니다.", rtnMap).toString();
		}else {
			//if("100".equals(_orderItemDlngStateCode)){
				//승인번호 채번 금액
				List<PaymentCancleDTO> _paymentCancleList = this.paymentService.getPaymentCancleInfo(paymentCancleDTO);
				
				if(_paymentCancleList.size() > 0) {
					_productTotalAmt = _paymentCancleList.get(0).getProductTotalAmt();
					_orderTotalAmt = _paymentCancleList.get(0).getOrderTotalAmt();
					_receiptId = _paymentCancleList.get(0).getReceiptId();
					_paymentAmount = _paymentCancleList.get(0).getPaymentAmount();
					_cancelledPaymentAmount = _paymentCancleList.get(0).getCancelledPaymentAmount();
					_paymentAmt = _paymentAmount.subtract(_cancelledPaymentAmount);
					paymentCancleDTO.setReceiptId(_receiptId);
					
					//취소처리
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
			//}else if("200".equals(_orderItemDlngStateCode) || "300".equals(_orderItemDlngStateCode)) {
			//	//취소요청만 가능
			//	this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, _orderItemStateCode);
			//	return JsonUtils.returnValue("0000", "취소요청 하였습니다.", rtnMap).toString();
			//}else {
			//	//그 외 취소불가
			//	return JsonUtils.returnValue("0000", "취소가능한 주문이 없습니다.", rtnMap).toString();
			//}
		}
	}
	
	@ResponseBody
	@PostMapping("/orderInfoConfirm")
	public String getOrderConfirm(HttpServletRequest request, @RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		Map rtnMap = new HashMap<>();
		
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);
		
		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();
		
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		if("100".equals(_orderItemDlngStateCode)) {
			this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "200");
			return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
		}else {
			return JsonUtils.returnValue("0000", "주문 접수건만 확인이 가능합니다", rtnMap).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/addDeliveries")
	public String addDeliveries(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		Map rtnMap = new HashMap<>();
		
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);
		adminOrderCDTO.setSellerId(sessionBean.getUserId());
		String _soptId = "";
		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();
		
		
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		if("200".equals(_orderItemDlngStateCode)) {
			Optional<SellerInfo> sellerInfo = this.sellerInfoService.getSellerInfo(adminOrderCDTO.getSellerId());
			if(sellerInfo.isPresent()) {
				_soptId = sellerInfo.get().getSpotId();
				
				if("".equals(_soptId) || _soptId == null) {
					return JsonUtils.returnValue("0000", "수거지 코드를 확인하세요", rtnMap).toString();
				}else {
					adminOrderCDTO.setSpotId(_soptId);
					
					String _msg = this.garakAdminService.addBookId(adminOrderCDTO);
					
					if("".equals(_msg)) {
						this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "300");
					}
					
					return JsonUtils.returnValue("0000", "등록되었습니다", rtnMap).toString();
				}
			}else {
				return JsonUtils.returnValue("0000", "수거지 코드를 확인하세요", rtnMap).toString();
			}
		}else {
			return JsonUtils.returnValue("0000", "주문 확인건만 배송요청이 가능합니다", rtnMap).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/userList")
	public String getAdminUserList(HttpServletRequest request, @RequestBody(required = true) Map map) {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		if(Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			return JsonUtils.returnValue("9999", "조회권한이없습니다", rtnMap).toString();
		}
		
		AdminUserCDTO adminUserCDTO = new AdminUserCDTO();
		adminUserCDTO = (AdminUserCDTO) ParameterUtils.setDto(map, adminUserCDTO, "insert", sessionBean);
		
		List<AdminPageInfoDTO> pagingInfo = this.garakAdminService.getAdminUserListPageInfo(adminUserCDTO);
    	List<AdminUserDTO> adminUserList = this.garakAdminService.getAdminUserList(adminUserCDTO);
		
		rtnMap.put("adminUserList", adminUserList);
		rtnMap.put("paging", pagingInfo.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/modifyUserInfo")
	public String modifyUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		
		Map rtnMap = new HashMap<>();
		if(Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			return JsonUtils.returnValue("9999", "수정권한이없습니다", rtnMap).toString();
		}
		
		AdminUserDTO adminUserDTO = new AdminUserDTO();
		adminUserDTO = (AdminUserDTO) ParameterUtils.setDto(map, adminUserDTO, "insert", sessionBean);
		
		this.garakAdminService.modifyUserInfo(adminUserDTO);
		
		//rtnMap.put("adminUserList", adminUserList);
		//rtnMap.put("paging", pagingInfo.get(0));
		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
}
