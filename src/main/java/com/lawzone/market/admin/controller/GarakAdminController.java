package com.lawzone.market.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.login.LoginDTO;
import com.lawzone.market.admin.dto.order.AdminCustOrderItemListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.order.AdminOrderListDTO;
import com.lawzone.market.admin.dto.order.AdminOrderStatCountInfoDTO;
import com.lawzone.market.admin.dto.order.BookIdListInfoDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.dto.user.AdminUserDTO;
import com.lawzone.market.admin.dto.user.BoilerplateDTO;
import com.lawzone.market.admin.service.AdminAddProductDTO;
import com.lawzone.market.admin.service.AdminOrderDTO;
import com.lawzone.market.admin.service.AdminProductCDTO;
import com.lawzone.market.admin.service.AdminProductDTO;
import com.lawzone.market.admin.service.AdminUserInfoDTO;
import com.lawzone.market.admin.service.CustReviewInfoDTO;
import com.lawzone.market.admin.service.GarakAdminService;
import com.lawzone.market.admin.service.ProductOrderItemBookIdInfo;
import com.lawzone.market.admin.service.SettlementListDTO;
import com.lawzone.market.admin.service.SlsDateInfoDTO;
import com.lawzone.market.common.service.BoilerplateInfoDTO;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.common.service.MenuDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.externalLink.util.AppPush;
import com.lawzone.market.externalLink.util.DoobalHeroUtils;
import com.lawzone.market.externalLink.util.TodayUtils;
import com.lawzone.market.image.service.ProductImageListDTO;
import com.lawzone.market.image.service.ProductImageService;
import com.lawzone.market.order.service.CustOrderInfoDTO;
import com.lawzone.market.order.service.OrderPaymentDTO;
import com.lawzone.market.order.service.OrderPaymentInfo;
import com.lawzone.market.order.service.ProductOrderItemInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.payment.service.PaymentCancleDTO;
import com.lawzone.market.payment.service.PaymentInfoDTO;
import com.lawzone.market.payment.service.PaymentService;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointInfoHistInfoDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.ProductDTO;
import com.lawzone.market.product.service.ProductInfoDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.product.service.ProductTagInfoDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.user.service.SellerInfoService;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

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
	private final UtilService utilService;
	private final AppPush appPush;
	private final DoobalHeroUtils doobalHeroUtils;
	private final TodayUtils todayUtils;
	private final ProductService productService;
	private final ProductReviewInfoService productReviewInfoService;
	private final ProductImageService productImageService;
	private final PointService pointService;

	@ResponseBody
	@PostMapping("/login")
	public String getLogin(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		LoginDTO loginDTO = new LoginDTO();
		loginDTO = (LoginDTO) ParameterUtils.setDto(map, loginDTO, "insert", sessionBean);

		Map rtnMap = new HashMap<>();

		List<UserInfo> userInfo = this.sellerInfoService.getSellerInfo(loginDTO.getPortalId(), loginDTO.getPortalPw());

		List<MenuDTO> menuList = new ArrayList<>();

		if (!userInfo.isEmpty()) {
			menuList = this.commonService.getMenuInfo(userInfo.get(0).getUserId());
			List<AdminUserInfoDTO> adminUserInfo = this.garakAdminService.getAdminUserInfo(loginDTO.getPortalId());

			// this.garakAdminService.getDeliveryStaus();
			rtnMap.put("adminToken", jwtTokenUtil.generateToken(userInfo.get(0), null));
			rtnMap.put("menuTreeInfoList", menuList);
			rtnMap.put("adminUserInfo", adminUserInfo.get(0));
		} else {
			return JsonUtils.returnValue("9999", "로그인정보를 확인하세요", rtnMap).toString();
		}
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/orderList")
	public String getOrderList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}
		Map rtnMap = new HashMap<>();
		
		if("10".equals(adminOrderCDTO.getMaxPage())) {
			List<AdminPageInfoDTO> paging = this.productOrderService.getAdminOrderListPageInfo(adminOrderCDTO);
			List<AdminOrderStatCountInfoDTO> adminOrderStatCountInfoDTO = this.productOrderService.getAdminOrderStatCountInfo(adminOrderCDTO);
			
			rtnMap.put("paging", paging.get(0));
			rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		}
		List<AdminOrderListDTO> adminOrder = this.productOrderService.getAdminOrderList(adminOrderCDTO);
		
		rtnMap.put("orderList", adminOrder);
		
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/orderInfo")
	public String getOrderInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();

		// 주문정보
		List<CustOrderInfoDTO> custOrderInfoDTO = this.productOrderService.getCustOrderInfoByOrderNo(_orderNo, "", "","N");

		// 주문항목정보
		List<AdminCustOrderItemListDTO> adminCustOrderItemListDTO = this.productOrderService.getAdminOrderItemList(_orderNo, _productId, "");

		// 결제정보
		List<PaymentInfoDTO> paymentInfo = this.paymentService.getOrderPaymentInfo(_orderNo, "");

		// 운송장내역
		List<ProductOrderItemBookIdInfo> productOrderItemBookIdInfo = this.garakAdminService.getBookIdList(adminOrderCDTO);
		Map rtnMap = new HashMap<>();
		rtnMap.put("orderInfo", custOrderInfoDTO.get(0));
		rtnMap.put("orderItemList", adminCustOrderItemListDTO);
		rtnMap.put("paymentInfo", paymentInfo.get(0));
		rtnMap.put("bookIdList", productOrderItemBookIdInfo);

		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/paymentcancel")
	public String paymentCancle(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		this.telmsgLogService.addTelmsgLog("01", "00", "1", map, "");
		Map rtnMap = new HashMap<>();
		String rtnMsg = "";
		PaymentCancleDTO paymentCancleDTO = new PaymentCancleDTO();
		paymentCancleDTO = (PaymentCancleDTO) ParameterUtils.setDto(map, paymentCancleDTO, "insert", sessionBean);

		String _orderNo = paymentCancleDTO.getOrderNo();
		String _productId = paymentCancleDTO.getProductId();
		String userId = sessionBean.getUserId();
		String userNm = sessionBean.getUserNm();
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

		// 주문상태확인
		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService
				.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		String _sellerId = productOrderItemInfo.get(0).getSellerId();

		if ("002".equals(_orderItemStateCode)) {
			return JsonUtils.returnValue("0000", "이미 취소된 주문입니다.", rtnMap).toString();
		} else {
			// if("100".equals(_orderItemDlngStateCode)){
			// 승인번호 채번 금액
			List<PaymentCancleDTO> _paymentCancleList = this.paymentService.getPaymentCancleInfo(paymentCancleDTO);

			if (_paymentCancleList.size() > 0) {
				Optional<OrderPaymentInfo> orderPaymentInfo = this.productOrderService.getOrderPaymentInfo(_orderNo,_sellerId);
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
				userId = _paymentCancleList.get(0).getUserId();
				_orderAmount = new BigDecimal("0");
				_cancelledOrderAmount = new BigDecimal("0");
				_orderDeliveryAmount = new BigDecimal("0");
				
				_totalPaymentAmt = _paymentAmt.add(_pointAmt);
				// 취소처리
				_amt = _productTotalAmt;
				if (_productId == null || "".equals(_productId)) {
					_amt = _orderTotalAmt;
				}

				_orderAmount = orderPaymentInfo.get().getOrderAmount();
				_cancelledOrderAmount = orderPaymentInfo.get().getCancelledOrderAmount();
				_orderDeliveryAmount = orderPaymentInfo.get().getDeliveryAmount();

				_orderAmount = _orderAmount.subtract(_cancelledOrderAmount);

				_cancelledOrderAmount = _cancelledOrderAmount.add(_amt);

				if (_amt.compareTo(_orderAmount) == 0) {
					_amt = _amt.add(_orderDeliveryAmount);
				}

				if (_totalPaymentAmt.compareTo(_amt) >= 0) {
					if (_totalPaymentAmt.compareTo(_amt) == 0) {
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
					this.telmsgLogService.addTelmsgLog("01", "90", "1", map, "");

					OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();

					orderPaymentDTO.setOrderNo(_orderNo);
					orderPaymentDTO.setSellerId(_sellerId);
					orderPaymentDTO.setCancelledOrderAmount(_cancelledOrderAmount);

					_rtnMsg = this.paymentService.paymentCancle(paymentCancleDTO, orderPaymentDTO, _allCancleYn,
							_cancleAmt, _cancelledPointAmount, _addPointAmt, _orderItemDlngStateCode, userId, userNm);
					this.telmsgLogService.addTelmsgLog("01", "90", "2", map, "");
				} else {
					return JsonUtils.returnValue("9999", "취소금액불일치", rtnMap).toString();
				}

				if ("".equals(_rtnMsg)) {
					return JsonUtils.returnValue("0000", "취소되었습니다.", rtnMap).toString();
				} else {
					return JsonUtils.returnValue("9999", _rtnMsg, rtnMap).toString();
				}
			} else {
				return JsonUtils.returnValue("9999", "결제취소건이 없습니다.", rtnMap).toString();
			}
			// }else if("200".equals(_orderItemDlngStateCode) ||
			// "300".equals(_orderItemDlngStateCode)) {
			// //취소요청만 가능
			// this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId,
			// _orderItemStateCode);
			// return JsonUtils.returnValue("0000", "취소요청 하였습니다.", rtnMap).toString();
			// }else {
			// //그 외 취소불가
			// return JsonUtils.returnValue("0000", "취소가능한 주문이 없습니다.", rtnMap).toString();
			// }
		}
	}

	@ResponseBody
	@PostMapping("/orderInfoConfirm")
	public String getOrderConfirm(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();

		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService
				.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		if ("100".equals(_orderItemDlngStateCode)) {
			this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "200");
			return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
		} else {
			return JsonUtils.returnValue("0000", "주문 접수건만 확인이 가능합니다", rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/addDeliveries")
	public String addDeliveries(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);
		adminOrderCDTO.setSellerId(sessionBean.getUserId());
		String _soptId = "";
		String _orderNo = adminOrderCDTO.getOrderNo();
		String _productId = adminOrderCDTO.getProductId();

		List<ProductOrderItemInfo> productOrderItemInfo = this.productOrderService
				.getProductOrderItemInfoByOrderNoAndProductId(_orderNo, _productId);
		String _orderItemStateCode = productOrderItemInfo.get(0).getOrderItemStateCode();
		String _orderItemDlngStateCode = productOrderItemInfo.get(0).getOrderItemDlngStateCode();
		if ("200".equals(_orderItemDlngStateCode)) {
			Optional<SellerInfo> sellerInfo = this.sellerInfoService.getSellerInfo(adminOrderCDTO.getSellerId());
			if (sellerInfo.isPresent()) {
				_soptId = sellerInfo.get().getSpotId();

				if ("".equals(_soptId) || _soptId == null) {
					return JsonUtils.returnValue("0000", "수거지 코드를 확인하세요", rtnMap).toString();
				} else {
					adminOrderCDTO.setSpotId(_soptId);

					String _msg = this.garakAdminService.addBookId(adminOrderCDTO);

					if ("".equals(_msg)) {
						this.productOrderService.modifyOrderItemStatInfo(_orderNo, _productId, "300");
					}

					return JsonUtils.returnValue("0000", "등록되었습니다", rtnMap).toString();
				}
			} else {
				return JsonUtils.returnValue("0000", "수거지 코드를 확인하세요", rtnMap).toString();
			}
		} else {
			return JsonUtils.returnValue("0000", "주문 확인건만 배송요청이 가능합니다", rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/userList")
	public String getAdminUserList(HttpServletRequest request, @RequestBody(required = true) Map map) {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
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
	public String modifyUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		Map rtnMap = new HashMap<>();
		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			return JsonUtils.returnValue("9999", "수정권한이없습니다", rtnMap).toString();
		}

		AdminUserDTO adminUserDTO = new AdminUserDTO();
		adminUserDTO = (AdminUserDTO) ParameterUtils.setDto(map, adminUserDTO, "insert", sessionBean);

		String _msg = this.garakAdminService.modifyUserInfo(adminUserDTO, sessionBean.getUserId());

		// rtnMap.put("adminUserList", adminUserList);
		// rtnMap.put("paging", pagingInfo.get(0));

		String msgCd = "0000";

		if (!"저장되었습니다.".equals(_msg)) {
			msgCd = "9999";
		}

		return JsonUtils.returnValue("0000", _msg, rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/orderInfoConfirmAll")
	public String getorderInfoConfirmAll(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}

		// 송장등록
		String rtnMsg = this.garakAdminService.getorderInfoConfirmAll(adminOrderCDTO);

		return JsonUtils.returnValue("0000", rtnMsg, rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/bookIdInfoList")
	public String getBookIdInfoList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		// this.garakAdminService.getDeliveryStaus();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}
		List<AdminPageInfoDTO> paging = this.garakAdminService.getAdminBookIdListPageInfo(adminOrderCDTO);
		List<BookIdListInfoDTO> bookIdListInfo = this.garakAdminService.getAdminBookIdList(adminOrderCDTO);
		// List<AdminOrderStatCountInfoDTO> adminOrderStatCountInfoDTO =
		// this.productOrderService.getAdminOrderStatCountInfo(adminOrderCDTO);

		rtnMap.put("orderList", bookIdListInfo);
		rtnMap.put("paging", paging.get(0));
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addBookIdList")
	public String addBookIdList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		String _soptId = "";

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
//		adminOrderCDTO.setSellerId(sessionBean.getUserId());

		adminOrderCDTO.setUserLvl(Integer.parseInt(sessionBean.getUserLvl()));

		// 영업일여부
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
		Date date = new Date();
		String _date = sf.format(date);
		String _dateArray[] = _date.split(" ");
		String slsDayYn = this.utilService.getSlsDayYn(_dateArray[0]);

		if ("Y".equals(slsDayYn) || adminOrderCDTO.getUserLvl() >= 6) {
			// Optional<SellerInfo> sellerInfo =
			// this.sellerInfoService.getSellerInfo(adminOrderCDTO.getSellerId());
			// if(sellerInfo.isPresent()) {
			// _soptId = sellerInfo.get().getSpotId();
			//
			// if("".equals(_soptId) || _soptId == null) {
			// return JsonUtils.returnValue("0000", "수거지 코드를 확인하세요", rtnMap).toString();
			// }else {
			// adminOrderCDTO.setSpotId(_soptId);
			// adminOrderCDTO.setShopName(sellerInfo.get().getShopName());
			// adminOrderCDTO.setSellerPhoneNumber(sellerInfo.get().getSellerPhoneNumber());
			// adminOrderCDTO.setBusinessAddress(sellerInfo.get().getBusinessAddress());
			// adminOrderCDTO.setSpotName(sellerInfo.get().getSpotName());
			List<BookIdListInfoDTO> bookIdList = (List<BookIdListInfoDTO>) map.get("dataset");
			this.garakAdminService.addBookIdList(bookIdList, adminOrderCDTO);
			// }
			// }
			return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
		} else {
			String slsDateList[];
			String slsDateText = "";
			slsDateList = slsDateText.split(";");
			String nextDay = this.utilService.getSlsDate(_dateArray[0], "Y", slsDateList);
			return JsonUtils.returnValue("9999", "영업일이 아닙니다 다음영업일은 " + nextDay + "일 입니다.", rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/boilerplateList")
	public String getBoilerplateList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}

		List<AdminPageInfoDTO> paging = this.garakAdminService.getBoilerplateListPageInfo(adminOrderCDTO);
		List<BoilerplateDTO> boilerplateinfo = this.garakAdminService.getBoilerplateList(adminOrderCDTO);
		// List<AdminOrderStatCountInfoDTO> adminOrderStatCountInfoDTO =
		// this.productOrderService.getAdminOrderStatCountInfo(adminOrderCDTO);

		rtnMap.put("boilerplateList", boilerplateinfo);
		rtnMap.put("paging", paging.get(0));
		rtnMap.put("userLvl", sessionBean.getUserLvl());
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addBoilerplateInfo")
	public String addBoilerplateInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		BoilerplateInfoDTO boilerplateInfoDTO = new BoilerplateInfoDTO();
		boilerplateInfoDTO = (BoilerplateInfoDTO) ParameterUtils.setDto(map, boilerplateInfoDTO, "insert", sessionBean);

		String msgcd = this.commonService.addBoilerplateInfo(boilerplateInfoDTO);
		String msg = "저장되었습니다.";
		if ("9999".equals(msgcd)) {
			msg = "사용문구는 5까지 가능합니다.";
		}

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue(msgcd, msg, rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addDateInfoList")
	public String addDateInfoList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		this.commonService.addDateInfoList(adminOrderCDTO);

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getDateInfoList")
	public String getDateInfoList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		List dateInfoList = this.commonService.getDateInfoList(adminOrderCDTO);

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		rtnMap.put("dateInfoList", dateInfoList);
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/modifyDateInfoList")
	public String modifyDateInfoList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		SlsDateInfoDTO slsDateInfoDTO = new SlsDateInfoDTO();
		slsDateInfoDTO = (SlsDateInfoDTO) ParameterUtils.setDto(map, slsDateInfoDTO, "insert", sessionBean);

		this.commonService.modifyDateInfoList(slsDateInfoDTO);

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		// rtnMap.put("dateInfoList", dateInfoList);
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/modifyBookIdPrintYn")
	public String modifyBookIdPrintYn(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		List bookIdPrintList = (List) map.get("dataset");
		// SlsDateInfoDTO slsDateInfoDTO = new SlsDateInfoDTO();
		// slsDateInfoDTO = (SlsDateInfoDTO) ParameterUtils.setDto(map, slsDateInfoDTO,
		// "insert", sessionBean);

		this.garakAdminService.modifyBookIdPrintYn(bookIdPrintList);

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		// rtnMap.put("dateInfoList", dateInfoList);
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getBookIdPrintImg")
	public String getBookIdPrintImg(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		List bookIdPrintList = (List) map.get("dataset");
		// SlsDateInfoDTO slsDateInfoDTO = new SlsDateInfoDTO();
		// slsDateInfoDTO = (SlsDateInfoDTO) ParameterUtils.setDto(map, slsDateInfoDTO,
		// "insert", sessionBean);

		List bookIdPrintImgList = this.garakAdminService.getBookIdPrintImg(bookIdPrintList);

		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		// rtnMap.put("dateInfoList", dateInfoList);
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		rtnMap.put("bookIdPrintImgList", bookIdPrintImgList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getDeliveryStaus")
	public String getDeliveryStaus(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		this.garakAdminService.getDeliveryStaus();

		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/custReviewInfoList")
	public String getAdminCustReviewInfoList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		List<AdminPageInfoDTO> paging = this.garakAdminService.getAdminCustReviewPageInfo(adminOrderCDTO);
		List<CustReviewInfoDTO> custReviewInfo = this.garakAdminService.getAdminCustReviewInfoList(adminOrderCDTO);
		// List<AdminOrderStatCountInfoDTO> adminOrderStatCountInfoDTO =
		// this.productOrderService.getAdminOrderStatCountInfo(adminOrderCDTO);

		rtnMap.put("custReviewInfo", custReviewInfo);
		rtnMap.put("paging", paging.get(0));
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/settlementList")
	public String getSettlementList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(map, adminOrderCDTO, "insert", sessionBean);

		List<SettlementListDTO> settlementList = this.garakAdminService.getSettlementList(adminOrderCDTO);
		// List<AdminOrderStatCountInfoDTO> adminOrderStatCountInfoDTO =
		// this.productOrderService.getAdminOrderStatCountInfo(adminOrderCDTO);

		rtnMap.put("settlementList", settlementList);
		// rtnMap.put("orderStatCount", adminOrderStatCountInfoDTO.get(0));
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addProductList")
	public String addProductList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		Map rtnMap = new HashMap<>();
		Map addProductListMap = new HashMap<>();
		Map adminInfoMap = new HashMap<>();

		addProductListMap = (Map) map.get("dataset");
		adminInfoMap = (Map) addProductListMap.get("adminInfo");

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();
		adminOrderCDTO = (AdminOrderCDTO) ParameterUtils.setDto(adminInfoMap, adminOrderCDTO, "insert", sessionBean);

		adminOrderCDTO.setUserLvl(Integer.parseInt(sessionBean.getUserLvl()));

		if (adminOrderCDTO.getUserLvl() < 5) {
			adminOrderCDTO.setSellerId(sessionBean.getUserId());
		}

		List<AdminAddProductDTO> addProductList = (List) addProductListMap.get("productist");

		String rtnMsg = this.garakAdminService.addProductList(adminOrderCDTO, addProductList);

		return JsonUtils.returnValue("0000", rtnMsg, rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getProductList")
	public String getProductList(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		Map rtnMap = new HashMap<>();
		String _soptId = "";

		AdminProductCDTO adminProductCDTO = new AdminProductCDTO();
		adminProductCDTO = (AdminProductCDTO) ParameterUtils.setDto(map, adminProductCDTO, "insert", sessionBean);
		adminProductCDTO.setUserLvl(Integer.parseInt(sessionBean.getUserLvl()));
		adminProductCDTO.setSellerId(sessionBean.getUserId());

		List adminProductList = this.garakAdminService.getProductList(adminProductCDTO);
		rtnMap.put("adminProductList", adminProductList);
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getProductDetail")
	public String getProductDetail(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		ProductDTO produstDTO = new ProductDTO();
		produstDTO = (ProductDTO) ParameterUtils.setDto(map, produstDTO, "insert", sessionBean);

		// List productList = null;
		List productImageList = null;
		List productReviewList = null;
		List tagInfoList = null;

		String productId = produstDTO.getProductId();
		// 상품정보
		List<ProductInfoDTO> productList = this.productService.getProductDetailInfo(productId);

		// 상품이미지정보
		productImageList = this.productImageService.getProductImageInfoList(productId);

		// 상품태그정보
		tagInfoList = this.productService.getTagInfo(productId);

		Map rtnMap = new HashMap<>();
		rtnMap.put("productInfo", productList.get(0));
		rtnMap.put("productImageList", productImageList);
		rtnMap.put("productTagList", tagInfoList);

		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();

		// log.info(rtnValue);

		return rtnValue;
	}

	@ResponseBody
	@PostMapping("/uploadImage")
	public JSONObject addProductImage(HttpServletRequest request,
			@RequestPart(value = "imageFile", required = false) MultipartFile[] productImage,
			@RequestParam(name = "imageCfcd", defaultValue = "00") String imageCfcd)
			throws IllegalStateException, IOException, ImageProcessingException, MetadataException {
		String imagePath = "";
		Map _map = new HashMap<>();
		_map.put("imageCfcd", imageCfcd);
		this.telmsgLogService.addTelmsgLog("00", "00", "1", _map, "");

		Enumeration headerNames = request.getHeaderNames();

		Map imageMap = new HashMap<>();
		imageMap = this.productImageService.productFileUpload("000000000000", productImage, "N", imageCfcd);

		JSONObject json = new JSONObject(imageMap);
		return json;
	}

	@ResponseBody
	@PostMapping("/createIntegrated")
	public String addProductIntegratedInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		Map logMap = new HashMap<>();
		Map rtnMap = new HashMap<>();

		Map productMap = new HashMap<>();
		Map addProductMap = new HashMap<>();
		Map productTagInfoMap = new HashMap<>();

		addProductMap = (Map) map.get("dataset");

		productMap.put("dataset", addProductMap.get("productInfo"));

		// 상품정보
		ProductDTO productDTO = new ProductDTO();
		productDTO = (ProductDTO) ParameterUtils.setDto(productMap, productDTO, "insert", sessionBean);

		String _originalProductId = productDTO.getOriginalProductId();
		String _poductId = "";
		if (!(_originalProductId == null || "".equals(_originalProductId))) {
			// 상품복사 상품, 이미지, 태그
			_poductId = this.productService.productCopyOrigin(productDTO, sessionBean.getUserId());

			if ("".equals(_poductId)) {
				return JsonUtils.returnValue("0000", "복사할 상품정보가 없습니다", rtnMap).toString();
			} else {
				productDTO.setProductId(_poductId);
			}
		}

		if ("".equals(productDTO.getUseYn()) || productDTO.getUseYn() == null) {
			productDTO.setUseYn("Y");
		}

		if ("".equals(productDTO.getSellerId()) || productDTO.getSellerId() == null) {
			productDTO.setSellerId(sessionBean.getUserId());
		}

		if ("".equals(productDTO.getBeginDate()) || productDTO.getBeginDate() == null) {
			productDTO.setBeginDate("now()");
		}

		// 상품태그정보 리스트
		List<ProductTagInfoDTO> productTagInfoList = (List) addProductMap.get("productTagList");

		// 이미지리스트
		List<ProductImageListDTO> productImageList = (List) addProductMap.get("productImageList");

		this.productService.addProductInfoList(productDTO, productImageList, productTagInfoList);
		rtnMap.put("originalProductId", _originalProductId);
		rtnMap.put("copyProductId", _poductId);
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getOrderListInfo")
	public String getOrderListInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		String _soptId = "";

		AdminOrderCDTO adminOrderCDTO = new AdminOrderCDTO();

		adminOrderCDTO.setUserLvl(Integer.parseInt(sessionBean.getUserLvl()));

		List<BookIdListInfoDTO> bookIdList = (List<BookIdListInfoDTO>) map.get("dataset");
		List<AdminOrderDTO> adminOrderList = this.garakAdminService.getOrderListInfo(bookIdList, adminOrderCDTO);

		rtnMap.put("adminOrderList", adminOrderList);

		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/changeProductPrice")
	public String modifyProductPrice(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		String _soptId = "";

		AdminProductDTO adminProductDTO = new AdminProductDTO();
		adminProductDTO = (AdminProductDTO) ParameterUtils.setDto(map, adminProductDTO, "insert", sessionBean);

		this.productService.modifyProductPrice(adminProductDTO);

		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/deliveryCancel")
	public String deliveryCancel(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		String _soptId = "";

		AdminProductDTO adminProductDTO = new AdminProductDTO();
		adminProductDTO = (AdminProductDTO) ParameterUtils.setDto(map, adminProductDTO, "insert", sessionBean);

		this.commonService.deliveryCancel(adminProductDTO.getDeliveryOrderId());

		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/pointHistInfo")
	public String getPointHistInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		Map rtnMap = new HashMap<>();

		AdminUserCDTO adminUserCDTO = new AdminUserCDTO();
		adminUserCDTO = (AdminUserCDTO) ParameterUtils.setDto(map, adminUserCDTO, "insert", sessionBean);
		
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		
		pointInfoCDTO.setPageCount("0");
		pointInfoCDTO.setMaxPageCount("100");
		pointInfoCDTO.setUserId(adminUserCDTO.getAdminUserId());
		
		List<PointInfoHistInfoDTO> pointInfoHistInfoList = this.pointService.getPointHistInfo(pointInfoCDTO);
		
		BigDecimal point = this.pointService.getPointAmount(pointInfoCDTO);
		
		BigDecimal expirationPoint = this.pointService.getExpirationpointAmount(pointInfoCDTO);
		
		rtnMap.put("pointAmount", point);
		rtnMap.put("expirationPointAmount", expirationPoint);
		rtnMap.put("pointHistInfoList", pointInfoHistInfoList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/addPoint")
	public String addPoint(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws ClientProtocolException, IOException, ParseException {
		Map rtnMap = new HashMap<>();

		AdminUserCDTO adminUserCDTO = new AdminUserCDTO();
		adminUserCDTO = (AdminUserCDTO) ParameterUtils.setDto(map, adminUserCDTO, "insert", sessionBean);
		
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		
		pointInfoCDTO.setUserId(adminUserCDTO.getAdminUserId());
		
		String _rtnMsg = this.pointService.addPoint(pointInfoCDTO);

		return JsonUtils.returnValue("0000", _rtnMsg, rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/kakaoTargetList")
	public String getKakaoTargetList(HttpServletRequest request, @RequestBody(required = true) Map map) {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		if (Integer.parseInt(sessionBean.getUserLvl()) < 5) {
			return JsonUtils.returnValue("9999", "조회권한이없습니다", rtnMap).toString();
		}

		AdminUserCDTO adminUserCDTO = new AdminUserCDTO();
		adminUserCDTO = (AdminUserCDTO) ParameterUtils.setDto(map, adminUserCDTO, "insert", sessionBean);

		List kakaoTargetList = this.garakAdminService.getKakaoTargetList(adminUserCDTO);

		rtnMap.put("kakaoTargetList", kakaoTargetList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
}
