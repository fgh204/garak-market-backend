package com.lawzone.market.admin.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.admin.dto.order.AdminOrderItemListDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.telmsgLog.service.TelmsgLogService;
import com.lawzone.market.user.UserLoginForm;
import com.lawzone.market.user.UserService;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.user.service.UserInfoService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {
	private final ProductOrderService productOrderService;
	private final UserService userService;
	private final JwtTokenUtil jwtTokenUtil;
	private final TelmsgLogService telmsgLogService;
	
	@Resource
	private SessionBean sessionBean;
	
	@RequestMapping("/order/list")
	@PostMapping("/order/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page
    		, @RequestParam(value = "kw", defaultValue = "") String kw
    		, @RequestParam(value = "beginDate", defaultValue = "") String beginDate 
    		, @RequestParam(value = "endDate", defaultValue = "") String endDate
    		, @RequestParam(value = "sttatCd", defaultValue = "") String sttatCd
    		) {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", null);
    	//log.info("page:{}, kw:{}", page, kw,beginDate, endDate, sttatCd);
    	
    	//Page<AdminProductOrderListDTO> paging = this.productOrderService.getList(page, kw);
    	
    	Map orderMap = new HashMap<>();
    	
    	String _beginDateFormat = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	String _endDateFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	
    	if("".equals(beginDate)) {
    		beginDate = _beginDateFormat;
    	}
    	
    	if("".equals(endDate)) {
    		endDate = _endDateFormat;
    	}
    	
    	if("".equals(sttatCd)) {
    		sttatCd = "003";
    	}
    	
    	orderMap.put("userNm",kw);
    	orderMap.put("beginDate",beginDate);
    	orderMap.put("endDate",endDate);
    	orderMap.put("statCd",sttatCd);
    	orderMap.put("page", Integer.toString(page + 1));
    	orderMap.put("pageCount", Integer.toString(page * 10));
    	orderMap.put("sellerId",sessionBean.getUserId());
    	
    	//List<AdminOrderItemListDTO> adminOrder = this.productOrderService.getOrderList(orderMap);
    	
    	//List<AdminPageInfoDTO> paging = this.productOrderService.getAdminOrderListPageInfo(orderMap);
    	//List<AdminOrderItemListDTO> adminOrder = this.productOrderService.getAdminOrderList(orderMap); 
    	
    	//model.addAttribute("paging", paging.get(0));
    	//model.addAttribute("adminOrder", adminOrder);
        model.addAttribute("kw", kw);
        model.addAttribute("beginDate", beginDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statCd", sttatCd);
        
        return "th/product_list";
    }
	
	@GetMapping("/login")
    public String login(UserLoginForm userLoginForm, ServletRequest request, HttpServletResponse response) {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", null);
		String token = jwtTokenUtil.resolveToken((HttpServletRequest) request);
		if (token != null && jwtTokenUtil.validateToken(token)) {
			return "redirect:/admin/order/list";
        }else {
        	Cookie myCookie = new Cookie("Authorization", "");
			myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
			response.addCookie(myCookie);
			sessionBean.setUserId("");
			sessionBean.setUserNm("");
        	return "th/login_form";
        }
    }
	
	@PostMapping("/login")
    public String userLogin(@Valid UserLoginForm userLoginForm, BindingResult bindingResult, HttpServletResponse response) {
		//this.telmsgLogService.addTelmsgLog("00", "00", "1", null);
		System.out.println("UserCreateForm" + userLoginForm);
		System.out.println("UserCreateForm" + userLoginForm.getPassword());
		
		if(bindingResult.hasErrors()) {
			return "th/login_form";
		}
		
		Optional<UserInfo> userInfo = this.userService.getLogInInfo(userLoginForm.getEmail(), userLoginForm.getPassword());
		if (userInfo.isEmpty()) {
			bindingResult.rejectValue("password", "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
			return "th/login_form";
		}else {
			Cookie myCookie = new Cookie("Authorization", jwtTokenUtil.generateToken(userInfo.get(), null));
			myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
			response.addCookie(myCookie);
			return "redirect:/admin/order/list";
		}
    }
	
	@RequestMapping("/order/detail")
	 public String detail(Model model) {
		return "th/product_info";
	}
}