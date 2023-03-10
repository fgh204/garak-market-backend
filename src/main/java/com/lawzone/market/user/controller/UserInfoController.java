package com.lawzone.market.user.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.image.service.ProductImageDTO;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.product.service.ProductService;
import com.lawzone.market.review.service.ProductReviewAverageScoreDTO;
import com.lawzone.market.review.service.ProductReviewInfoService;
import com.lawzone.market.telmsgLog.service.TelmsgLogDTO;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.UserLoginForm;
import com.lawzone.market.user.service.DeliveryAddressInfoDTO;
import com.lawzone.market.user.service.MarketSignupDTO;
import com.lawzone.market.user.service.MarketUserDTO;
import com.lawzone.market.user.service.MarketUserInfoDTO;
import com.lawzone.market.user.service.SellerFavoriteCDTO;
import com.lawzone.market.user.service.SellerFavoriteDTO;
import com.lawzone.market.user.service.StoreInfoCDTO;
import com.lawzone.market.user.service.StoreInfoDTO;
import com.lawzone.market.user.service.StoreInfoPDTO;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.SlackWebhook;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/market")
public class UserInfoController {
	private final UserInfoService userInfoService;

	private final JwtTokenUtil jwtTokenUtil;
	private final ProductService productService;
	private final PointService pointService;
	private final ProductReviewInfoService productReviewInfoService;
	private final SlackWebhook slackWebhook;
	
	@Value("${lzmarket.service}") 
	private String service;
	
	@Resource
	private SessionBean sessionBean;

	@GetMapping("/login")
	public String login(UserLoginForm userLoginForm) {
		return "market_login_form";
	}

	private final TelmsgLogService telmsgLogService;

	public static final String TEAM_ID = "H645RW4U34";
	public static final String REDIRECT_URL = "https://trans.domaado.me/login/oauth2/code/apple";
	public static final String CLIENT_ID = "domaado.me.service";
	public static final String KEY_ID = "65S86F3A58";
	public static final String AUTH_URL = "https://appleid.apple.com";
	public static final String KEY_PATH = "65S86F3A58.p8";

	@PostMapping("/login")
	public String login(@Valid UserLoginForm userLoginForm, BindingResult bindingResult, HttpServletResponse response,
			HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			return "market_login_form";
		}

		Optional<UserInfo> _userInfo = this.userInfoService.marketLogin(userLoginForm.getEmail(),
				userLoginForm.getPassword());

		if (_userInfo.isEmpty()) {
			bindingResult.rejectValue("", "", "사용자정보가 없습니다.");
			return "market_login_form";
		}

//		List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));

		// Cookie myCookie = new Cookie("Authorization",
		// jwtTokenUtil.generateToken(_userInfo.get().getSocialId()));
		// myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
		// response.addCookie(myCookie);
		return "market_login_form";
	}

	@ResponseBody
	@PostMapping("/deliveryAddress/create")
	public String addDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert",
				sessionBean);

		deliveryAddressInfoDTO.setDeliveryAddressNumber(null);

		this.userInfoService.addDeliveryAddressInfo(deliveryAddressInfoDTO);

		Map rtnMap = new HashMap<>();

		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/deliveryAddress/modify")
	public String modifyDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert",
				sessionBean);
		this.userInfoService.addDeliveryAddressInfo(deliveryAddressInfoDTO);

		Map rtnMap = new HashMap<>();

		return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/deliveryAddress/remove")
	public String removeDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert",
				sessionBean);
		this.userInfoService.removeDeliveryAddressInfo(deliveryAddressInfoDTO);

		Map rtnMap = new HashMap<>();

		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/deliveryAddress/list")
	public String getDeliveryAddressInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		DeliveryAddressInfoDTO deliveryAddressInfoDTO = new DeliveryAddressInfoDTO();
		deliveryAddressInfoDTO = (DeliveryAddressInfoDTO) ParameterUtils.setDto(map, deliveryAddressInfoDTO, "insert",
				sessionBean);
		List<DeliveryAddressInfoDTO> _deliveryAddressInfoDTO = this.userInfoService
				.getDeliveryAddressInfo(deliveryAddressInfoDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("deliveryAddressList", _deliveryAddressInfoDTO);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/userInfo")
	public String getUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		List<MarketUserInfoDTO> marketUserInfo = this.userInfoService.getUserInfo(pointInfoCDTO.getUserId());

		BigDecimal point = this.pointService.getPointAmount(pointInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		Map pointMap = new HashMap<>();
		pointMap.put("amount", point);
		rtnMap.put("userInfo", marketUserInfo.get(0));
		rtnMap.put("pointInfo", pointMap);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/userInfo/modify")
	public String modifyUserInfo(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketUserDTO marketUserDTO = new MarketUserDTO();
		marketUserDTO = (MarketUserDTO) ParameterUtils.setDto(map, marketUserDTO, "insert", sessionBean);

		String _msg = this.userInfoService.modifyMarketUserInfo(marketUserDTO);

		Map rtnMap = new HashMap<>();
		if ("".equals(_msg)) {
			return JsonUtils.returnValue("0000", "수정되었습니다", rtnMap).toString();
		} else {
			return JsonUtils.returnValue("9999", _msg, rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/userInfo/nicknameduplicated")
	public String getUserNicknameChk(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketUserDTO marketUserDTO = new MarketUserDTO();
		marketUserDTO = (MarketUserDTO) ParameterUtils.setDto(map, marketUserDTO, "insert", sessionBean);

		Boolean chkValue = this.userInfoService.getUserNicknameChk(marketUserDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("isDuplicated", chkValue);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/userInfo/addProfileImages")
	public String addProfileImages(HttpServletRequest request,
			@RequestPart(value = "file", required = false) MultipartFile[] uploadFile) throws IOException {
		Map rtnMap = new HashMap<>();
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", rtnMap);

		this.userInfoService.addProfileImages(uploadFile, sessionBean.getUserId());

		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/userInfo/addProfileImage")
	public String addProfileImage(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductImageDTO productImageDTO = new ProductImageDTO();
		productImageDTO = (ProductImageDTO) ParameterUtils.setDto(map, productImageDTO, "insert", sessionBean);

		String msg = this.userInfoService.addProfileImage(productImageDTO, sessionBean.getUserId());

		Map rtnMap = new HashMap<>();
		return JsonUtils.returnValue("0000", msg, rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/userInfo/removeProfileImage")
	public String removeProfileImage(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		Map rtnMap = new HashMap<>();
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		this.userInfoService.removeProfileImage(sessionBean.getUserId());

		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@RequestMapping("/seller/productList")
	public String getSellerProductList(HttpServletRequest request, HttpServletResponse response, @RequestBody() Map map)
			throws IllegalAccessException, InvocationTargetException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();

		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);

		if ("".equals(productCDTO.getMaxPageCount()) || productCDTO.getMaxPageCount() == null) {
			productCDTO.setMaxPageCount("10");
		}

		if ("".equals(productCDTO.getPageCount()) || productCDTO.getPageCount() == null) {
			productCDTO.setPageCount("0");
		} else {
			int _currentCnt = Integer.parseInt(productCDTO.getPageCount());
			int _limitCnt = Integer.parseInt(productCDTO.getMaxPageCount());

			productCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
		}

		productCDTO.setSellerSearchYn("Y");
		productCDTO.setSellerIdYn("Y");

		List<PageInfoDTO> pageInfo = this.productService.getPageInfo(productCDTO);

		List<ProductInfoListDTO> productList = this.productService.getList2(productCDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("productList", productList);

		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();

		return rtnValue;
	}

	@ResponseBody
	@RequestMapping("/seller/productMainInfo")
	public String getSellerAverageScoreInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestBody() Map map) throws IllegalAccessException, InvocationTargetException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		ProductCDTO productCDTO = new ProductCDTO();
		productCDTO = (ProductCDTO) ParameterUtils.setDto(map, productCDTO, "insert", sessionBean);

		if ("".equals(productCDTO.getMaxPageCount()) || productCDTO.getMaxPageCount() == null) {
			productCDTO.setMaxPageCount("10");
		}

		if ("".equals(productCDTO.getPageCount()) || productCDTO.getPageCount() == null) {
			productCDTO.setPageCount("0");
		} else {
			int _currentCnt = Integer.parseInt(productCDTO.getPageCount());
			int _limitCnt = Integer.parseInt(productCDTO.getMaxPageCount());

			productCDTO.setPageCount(Integer.toString(_currentCnt * _limitCnt));
		}

		productCDTO.setSellerSearchYn("Y");
		productCDTO.setSellerIdYn("Y");
		// 판매자정보
		List<MarketUserInfoDTO> marketUserInfo = this.userInfoService.getUserInfo(productCDTO.getSellerId());
		// 판매상품리뷰점수
		List<ProductReviewAverageScoreDTO> productReviewAverageScoreDTO = this.productReviewInfoService
				.getSellerAverageScoreInfo(productCDTO);
		// 판매상품목록
		List<ProductInfoListDTO> productList = this.productService.getList2(productCDTO);

		Map orderMap = new HashMap<>();
		orderMap.put("productId", "");
		orderMap.put("sellerId", productCDTO.getSellerId());
		orderMap.put("pageCount", "0");
		orderMap.put("maxPageCount", "3");
		orderMap.put("orderCd", "01");
		// 상품리뷰정보
		List productReviewList = this.productReviewInfoService.getProductReviewList(orderMap);

		// 상품리뷰건수
		List productReviewScoreCountInfo = this.productReviewInfoService.getProductReviewCountInfo(productCDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("sellerInfo", marketUserInfo.get(0));
		rtnMap.put("averageScoreInfo", productReviewAverageScoreDTO.get(0));
		rtnMap.put("productList", productList);
		rtnMap.put("productReviewList", productReviewList);
		rtnMap.put("productReviewCountInfo", productReviewScoreCountInfo.get(0));
		String rtnValue = JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();

		return rtnValue;
	}

	@ResponseBody
	@PostMapping("/domaado/signup")
	public String getDomaadoUserSignup(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		String _rtnCd = this.userInfoService.getDomaadoUserSignup(marketSignupDTO);

		Map rtnMap = new HashMap<>();
		if ("0000".equals(_rtnCd)) {
			return JsonUtils.returnValue("0000", "등록되었습니다.", rtnMap).toString();
		} else {
			return JsonUtils.returnValue("9999", "고객정보가 중복되었습니다.", rtnMap).toString();
		}

	}

	@ResponseBody
	@PostMapping("/domaado/signin")
	public String getDomaadoUserSignin(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		Optional<UserInfo> _userInfo = this.userInfoService.getDomaadoUserSignin(marketSignupDTO);

		Map rtnMap = new HashMap<>();

		if (_userInfo.isPresent()) {
			String _token = this.jwtTokenUtil.generateToken(_userInfo.get(), null);
			ResponseCookie accessTokenCookie;
			
			String serviceNm = "";

			accessTokenCookie = ResponseCookie.from("7i7e9BCzFOXqOZAj5", _token).path("/").secure(true).httpOnly(true)
					.sameSite("None").domain(serviceNm + "domaado.me").build();
			response.setHeader("Set-Cookie", accessTokenCookie.toString());

			return JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
		} else {
			return JsonUtils.returnValue("9999", "로그인정보를 확인하세요.", rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/domaado/emailDuplicated")
	public String getDomaadoUserEmailDuplicated(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		Boolean chkValue = this.userInfoService.getDomaadoUserEmailDuplicated(marketSignupDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("isDuplicated", chkValue);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/domaado/phoneNumberDuplicated")
	public String getDomaadoUserPhoneNumberDuplicated(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		Boolean chkValue = this.userInfoService.getDomaadoUserphoneNumberDuplicated(marketSignupDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("isDuplicated", chkValue);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/domaado/loginIdDuplicated")
	public String getDomaadoUserLoginIdDuplicated(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		Boolean chkValue = this.userInfoService.getDomaadoUserLoginIdDuplicated(marketSignupDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("isDuplicated", chkValue);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/domaado/membershipWithdrawal")
	public String getDomaadoUserMembershipWithdrawal(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		Map rtnMap = new HashMap<>();

		List<UserInfo> userInfo = this.userInfoService.getDomaadoUserMembershipWithdrawal(sessionBean.getUserId());

		if (userInfo.size() > 0) {
			if (userInfo.get(0).getAccessToken() != null) {
				if ("kakao".equals(userInfo.get(0).getSocialName())) {
					this.access(userInfo.get(0).getAccessToken());
				} else if ("apple".equals(userInfo.get(0).getSocialName())) {
					this.appleAccess(userInfo.get(0).getAccessToken());
				}

				session.invalidate();
			}
			String _token = this.jwtTokenUtil.generateToken(userInfo.get(0), (long) 0);
			ResponseCookie accessTokenCookie;

			accessTokenCookie = ResponseCookie.from("7i7e9BCzFOXqOZAj5", _token).path("/").secure(true).httpOnly(true)
					.sameSite("None").domain("domaado.me").build();
			response.setHeader("Set-Cookie", accessTokenCookie.toString());

			return JsonUtils.returnValue("0000", "탈퇴되었습니다", rtnMap).toString();
		} else {
			return JsonUtils.returnValue("0000", "회원정보가 없습니다.", rtnMap).toString();
		}
	}

	@ResponseBody
	@PostMapping("/domaado/signingOut")
	public String getDomaadoUserSigningOut(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		List<UserInfo> _userInfo = this.userInfoService.getDomaadoUserInfo(sessionBean.getUserId());

		String _token = this.jwtTokenUtil.generateToken(_userInfo.get(0), (long) 0);

		ResponseCookie accessTokenCookie;

		accessTokenCookie = ResponseCookie.from("7i7e9BCzFOXqOZAj5", _token).path("/").secure(true).httpOnly(true)
				.sameSite("None").domain("domaado.me").build();
		response.setHeader("Set-Cookie", accessTokenCookie.toString());

		return JsonUtils.returnValue("0000", "logout되었습니다", rtnMap).toString();
	}

	public void appleAccess(String socialAccessToken) throws ClientProtocolException, IOException {
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		String revokeUrl = "https://appleid.apple.com/auth/revoke";

		String client_secret = createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);

		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", "domaado.me.service");
		params.add("client_secret", client_secret);
		params.add("token", socialAccessToken);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

		restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
	}

	public String access(String socialAccessToken) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpPost request = new HttpPost("https://kapi.kakao.com/v1/user/unlink");
		request.addHeader("content-type", "application/json");
		request.addHeader("Authorization", "Bearer " + socialAccessToken);

		CloseableHttpResponse httpResponse = httpClient.execute(request);

		String response = EntityUtils.toString(httpResponse.getEntity());

		return response;
	}

	@ResponseBody
	@PostMapping("/userInfo/addPushId")
	public String addPushid(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		MarketUserDTO marketUserDTO = new MarketUserDTO();
		marketUserDTO = (MarketUserDTO) ParameterUtils.setDto(map, marketUserDTO, "insert", sessionBean);

		this.userInfoService.addPushid(marketUserDTO);

		Map rtnMap = new HashMap<>();

		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addLog")
	public String addLog(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		TelmsgLogDTO telmsgLogDTO = new TelmsgLogDTO();
		telmsgLogDTO = (TelmsgLogDTO) ParameterUtils.setDto(map, telmsgLogDTO, "insert", sessionBean);

		Map dataMap = new HashMap<>();

		dataMap.put("telmsgDtaInfo", telmsgLogDTO.getTelmsgDtaInfo());

		String dlngTpcd = telmsgLogDTO.getDlngTpcd();

		if ("".equals(dlngTpcd) || dlngTpcd == null) {
			dlngTpcd = "00";
		}

		this.telmsgLogService.addTelmsgLog("50", telmsgLogDTO.getDlngTpcd(), "1", dataMap,"");
		Map rtnMap = new HashMap<>();
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/getSellerFavorite")
	public String getSellerFavorite(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		SellerFavoriteCDTO sellerFavoriteCDTO = new SellerFavoriteCDTO();
		sellerFavoriteCDTO = (SellerFavoriteCDTO) ParameterUtils.setDto(map, sellerFavoriteCDTO, "insert", sessionBean);

		List<SellerFavoriteDTO> SellerFavoriteList = this.userInfoService.getSellerFavorite(sellerFavoriteCDTO);

		Map rtnMap = new HashMap<>();
		rtnMap.put("sellerFavoriteList", SellerFavoriteList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/addSellerFavorite")
	public String addSellerFavorite(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		SellerFavoriteCDTO sellerFavoriteCDTO = new SellerFavoriteCDTO();
		sellerFavoriteCDTO = (SellerFavoriteCDTO) ParameterUtils.setDto(map, sellerFavoriteCDTO, "insert", sessionBean);

		this.userInfoService.addSellerFavorite(sellerFavoriteCDTO);

		Map rtnMap = new HashMap<>();
		return JsonUtils.returnValue("0000", "저장되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/removeSellerFavorite")
	public String removeSellerFavorite(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		SellerFavoriteCDTO sellerFavoriteCDTO = new SellerFavoriteCDTO();
		sellerFavoriteCDTO = (SellerFavoriteCDTO) ParameterUtils.setDto(map, sellerFavoriteCDTO, "insert", sessionBean);

		this.userInfoService.removeSellerFavorite(sellerFavoriteCDTO);

		Map rtnMap = new HashMap<>();
		return JsonUtils.returnValue("0000", "삭제되었습니다", rtnMap).toString();
	}

	@ResponseBody
	@PostMapping("/autoSignin")
	public String autoSignin(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) Map map) throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);
		Map rtnMap = new HashMap<>();
		Map userMap = new HashMap<>();

		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		userMap = this.userInfoService.autoSignin(marketSignupDTO);
		
		String token = userMap.get("token").toString();
		String accessToken = userMap.get("accessToken").toString();
		
		if (!("".equals(token) || token == null)) {
			if (this.jwtTokenUtil.validateToken(token)) {
				ResponseCookie accessTokenCookie;

				accessTokenCookie = ResponseCookie.from("7i7e9BCzFOXqOZAj5", token).path("/").secure(true)
						.httpOnly(true).sameSite("None").domain("domaado.me").build();
				response.setHeader("Set-Cookie", accessTokenCookie.toString());
				userMap.put("token", accessToken);
				return JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
			} else {
				return JsonUtils.returnValue("9999", "만료된 토큰입니다.", rtnMap).toString();
			}
		} else {
			String _token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyNTc4NDgwMzA4IiwidXNlckZvcm0iOiJ7XCJ1c2VyTm1cIjpcIuq5gO2YgeyImOydtOumhOyImOyglVwiLFwicGhvbmVOdW1iZXJcIjpcIjAxMDMzNTI0NTA5XCIsXCJzb2NpYWxJZFwiOlwiMjU3ODQ4MDMwOFwiLFwidXNlcklkXCI6XCIwMDAwMDAwMlwiLFwic2VsbGVyWW5cIjpcIllcIixcInVzZXJMdmxcIjo5LFwiZW1haWxcIjpcImZnaDIwNEBrYWthby5jb21cIn0iLCJpYXQiOjE2NzM1ODY3OTcsImV4cCI6MTY3MzU4Njc5N30.qTbdTjRWgWRtIg8gqi7xPELjsLaCfgMVtgsrl4gp-FE";
			ResponseCookie accessTokenCookie;

			accessTokenCookie = ResponseCookie.from("7i7e9BCzFOXqOZAj5", _token).path("/").secure(true).httpOnly(true)
					.sameSite("None").domain("domaado.me").build();
			response.setHeader("Set-Cookie", accessTokenCookie.toString());
			return JsonUtils.returnValue("9999", "로그인정보를 확인하세요.", rtnMap).toString();
		}
	}

	public String createClientSecret(String teamId, String clientId, String keyId, String keyPath, String authUrl)
			throws IOException {

		ClassPathResource resource = new ClassPathResource(keyPath);
		InputStream is = new BufferedInputStream(resource.getInputStream());
		String privateKey = IOUtils.toString(is);
		is.close();
		Reader pemReader = new StringReader(privateKey);
		PEMParser pemParser = new PEMParser(pemReader);
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
		log.info("pKey===============");
		PrivateKey pKey = converter.getPrivateKey(object);
		log.info("pKey===============" + pKey);

		Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
		return Jwts.builder().setHeaderParam("kid", keyId).setHeaderParam("alg", "ES256").setIssuer(teamId)
				.setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(expirationDate)
				.setAudience("https://appleid.apple.com").setSubject(clientId).signWith(SignatureAlgorithm.ES256, pKey)
				.compact();
	}

	@ResponseBody
	@PostMapping("/domaadoVersion")
	public String getdomaadoVersion(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		// this.telmsgLogService.addTelmsgLog("00", "00", "1", map);

		MarketSignupDTO marketSignupDTO = new MarketSignupDTO();
		marketSignupDTO = (MarketSignupDTO) ParameterUtils.setDto(map, marketSignupDTO, "insert", sessionBean);

		String domaadoVersion = this.userInfoService.getdomaadoVersion();

		Map rtnMap = new HashMap<>();
		rtnMap.put("domaadoVersion", domaadoVersion);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/getStore")
	public String getStore(HttpServletRequest request, @RequestBody(required = true) Map map)
			throws JsonMappingException, JsonProcessingException {
		
		StoreInfoCDTO storeInfoCDTO = new StoreInfoCDTO();
		storeInfoCDTO = (StoreInfoCDTO) ParameterUtils.setDto(map, storeInfoCDTO, "insert", sessionBean);
		
		storeInfoCDTO.setUserId(sessionBean.getUserId());
		
		List<PageInfoDTO> pageInfo = this.userInfoService.getStorePageInfo(storeInfoCDTO);
		
		List<StoreInfoPDTO> storeInfoList = this.userInfoService.getStoreInfoList(storeInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("pageInfo", pageInfo.get(0));
		rtnMap.put("shopList", storeInfoList);
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
}
