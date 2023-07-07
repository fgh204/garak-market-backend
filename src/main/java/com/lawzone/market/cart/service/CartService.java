package com.lawzone.market.cart.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.cart.dao.CartInfoDAO;
import com.lawzone.market.cart.dao.CartInfoJdbcDAO;
import com.lawzone.market.event.dao.EventMstJdbcDAO;
import com.lawzone.market.event.service.EventMstService;
import com.lawzone.market.event.service.EventProductInfoDTO;
import com.lawzone.market.product.dao.ProductDAO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartService {
	private final CartInfoDAO cartInfoDAO;
	private final CartInfoJdbcDAO cartInfoJdbcDAO;
	private final ModelMapper modelMapper;
	private final ProductDAO productDAO;
	private final UtilService utilService;
	private final EventMstService eventMstService;
	private final EventMstJdbcDAO eventMstJdbcDAO;
	
	@Transactional
	public Map addCartInfo(CartInfoDTO cartInfoDTO) {
		Long cartNumber = (long) 0;
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		BigDecimal _personBuyCount = new BigDecimal("0");
		BigDecimal _zero = new BigDecimal(0);
		Map cartMap = new HashMap<>();
		
		List<CartInfo> _cartInfo = this.cartInfoDAO.findByUserIdAndProductId(cartInfoDTO.getUserId(), cartInfoDTO.getProductId());
		if(_cartInfo.size() > 0) {
			cartStock = _cartInfo.get(0).getProductCount().add(cartInfoDTO.getProductCount());
			cartInfoDTO.setCartNumber(_cartInfo.get(0).getCartNumber());
		}else {
			cartStock = cartInfoDTO.getProductCount();
		}
		
		List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
		
		String eventId = _productInfo.get(0).getEventId(); 
		
		productStock = _productInfo.get(0).getProductStock();
		
		if("".equals(eventId) || eventId == null) {
//			productStock = _productInfo.get(0).getProductStock();
		} else {
			List<EventProductInfoDTO> eventProductInfo = this.eventMstService.getEventProductInfo(cartInfoDTO.getProductId(), eventId);
//			productStock = _productInfo.get(0).getProductStock();
//			
			BigDecimal personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
			BigDecimal eventCount = eventProductInfo.get(0).getEventCount();
			BigDecimal personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
			BigDecimal eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
			_personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
//			
//			if(personBuyCount.compareTo(_zero) == 0) {
//				personBuyCount = productStock;
//			} else {
//				personBuyCount = personBuyCount.subtract(personPaymentCount);
//				
//				if(personBuyCount.compareTo(_zero) < 0) {
//					personBuyCount = _zero;
//				}
//			}
//			
			if(eventCount.compareTo(_zero) == 0) {
				eventCount = productStock;
			} else {
				eventCount = eventCount.subtract(eventPaymentCount);
				
				if(eventCount.compareTo(_zero) < 0) {
					eventCount = _zero;
				}
			}
			productStock = eventCount;
//			
//			if(eventCount.compareTo(_zero) > 0) {
//				productStock = eventCount;
//				
//				if(productStock.compareTo(personBuyCount) > 0) {
//					productStock = personBuyCount;
//				}
//			}
		}
		
		String _rtnMsg = "저장되었습니다.";
		String _rtnCd = "0000";		
		
		if(productStock.compareTo(cartStock) >= 0) {
			cartInfoDTO.setProductCount(cartStock);
			CartInfo cartInfo = new CartInfo();
			cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
			cartNumber =  this.cartInfoDAO.save(cartInfo).getCartNumber();
			
		} else {
			_rtnCd = "9999";
			_rtnMsg =  "재고가 없습니다.";
//			if("".equals(eventId) || eventId == null) {
//				_rtnMsg =  "재고가 없습니다.";
//			} else {
//				//cartMap.put("cartEventProductCount", cartStock);
//				if(_personBuyCount.compareTo(_zero) == 0) {
//					_rtnMsg = "재고가 없습니다.";
//				} else {
//					if(cartStock.compareTo(_zero) == 0) {
//						_rtnMsg = "재고가 없습니다.";
//					} else {
//						if(_personBuyCount.compareTo(cartStock) >= 0) {
//							_rtnMsg = "구매 가능한 수량을 초과하였습니다.";
//						} else {
//							_rtnMsg = "1인당 최대 구매 개수는 " + _personBuyCount.toString() + "개 입니다.";
//						}
//					}
//				}
//			}
		}
		
		cartMap.put("rtnMag", _rtnMsg);
		cartMap.put("rtnCd", _rtnCd);
		
		return cartMap;
	}
	
	@Transactional
	public Boolean modifyCartInfo(CartInfoDTO cartInfoDTO) {
		Map cartMap = new HashMap<>();
		Map cartDataMap = new HashMap<>();
		
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		
		productStock = new BigDecimal("0");
		cartStock = new BigDecimal("0");
		
		List<CartInfo> _cartInfo = this.cartInfoDAO.findByCartNumber(cartInfoDTO.getCartNumber());
		
		if(_cartInfo.size() > 0) {
			cartInfoDTO.setProductId(_cartInfo.get(0).getProductId());				
			cartInfoDTO.setUserId(_cartInfo.get(0).getUserId());
			
			cartStock = cartInfoDTO.getProductCount();
			
			List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
			
			productStock = _productInfo.get(0).getProductStock();
			String eventId = _productInfo.get(0).getEventId(); 
			productStock = _productInfo.get(0).getProductStock();
			
			if("".equals(eventId) || eventId == null) {
//				productStock = _productInfo.get(0).getProductStock();
			} else {
				List<EventProductInfoDTO> eventProductInfo = this.eventMstService.getEventProductInfo(cartInfoDTO.getProductId(), eventId);
//				productStock = _productInfo.get(0).getProductStock();
//				
				BigDecimal _zero = new BigDecimal(0);
				BigDecimal personBuyCount = eventProductInfo.get(0).getPersonBuyCount();
				BigDecimal eventCount = eventProductInfo.get(0).getEventCount();
				BigDecimal personPaymentCount = new BigDecimal(eventProductInfo.get(0).getPersonPaymentCount().toString());
				BigDecimal eventPaymentCount = new BigDecimal(eventProductInfo.get(0).getEventPaymentCount().toString());
//				
//				if(personBuyCount.compareTo(_zero) == 0) {
//					personBuyCount = productStock;
//				} else {
//					personBuyCount = personBuyCount.subtract(personPaymentCount);
//					
//					if(personBuyCount.compareTo(_zero) < 0) {
//						personBuyCount = _zero;
//					}
//				}
//				
				if(eventCount.compareTo(_zero) == 0) {
					eventCount = productStock;
				} else {
					eventCount = eventCount.subtract(eventPaymentCount);
					
					if(eventCount.compareTo(_zero) < 0) {
						eventCount = _zero;
					}
				}
				productStock = eventCount;
//				
//				if(eventCount.compareTo(_zero) > 0) {
//					productStock = eventCount;
//					
//					if(productStock.compareTo(personBuyCount) > 0) {
//						productStock = personBuyCount;
//					}
//				} 
			}
			
			if(productStock.compareTo(cartStock) >= 0) {
				CartInfo cartInfo = new CartInfo();
				cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
				this.cartInfoDAO.save(cartInfo).getCartNumber();
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Transactional
	public void modifyCartInfos(List cartItemList) {
		int _cnt = cartItemList.size();
		CartInfoDTO cartInfoDTO;
		
		Map cartMap = new HashMap<>();
		Map cartDataMap = new HashMap<>();
		
		BigDecimal productStock = new BigDecimal("0");
		BigDecimal cartStock = new BigDecimal("0");
		
		for( int i = 0; i < _cnt; i++){
			cartInfoDTO = new CartInfoDTO();
			
			cartMap = (Map) cartItemList.get(i);
			cartDataMap.put("dataset", cartMap);
			
			cartInfoDTO = (CartInfoDTO) ParameterUtils.setDto(cartDataMap, cartInfoDTO, "insert", null);
			
			productStock = new BigDecimal("0");
			cartStock = new BigDecimal("0");
			
			List<CartInfo> _cartInfo = this.cartInfoDAO.findByCartNumber(cartInfoDTO.getCartNumber());
			
			if(_cartInfo.size() > 0) {
				cartInfoDTO.setProductId(_cartInfo.get(0).getProductId());				
				cartInfoDTO.setUserId(_cartInfo.get(0).getUserId());
				
				cartStock = cartInfoDTO.getProductCount();
				
				List<ProductInfo> _productInfo = this.productDAO.findByProductId(cartInfoDTO.getProductId());
				
				productStock = _productInfo.get(0).getProductStock();
				
				if(productStock.compareTo(cartStock) >= 0) {
					CartInfo cartInfo = new CartInfo();
					cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
					this.cartInfoDAO.save(cartInfo).getCartNumber();
				}
			}
		}
	}
	
	@Transactional
	public void removeCartInfo(CartInfoDTO cartInfoDTO) {
		CartInfo cartInfo = new CartInfo();
		cartInfo = modelMapper.map(cartInfoDTO, CartInfo.class);
		
		this.cartInfoDAO.delete(cartInfo);
	}
	
	@Transactional
	public List getCartInfoList() {
		return this.cartInfoDAO.findAll();
	}
	
	public List getCartInfoList(CartInfoDTO cartInfoDTO) {
		String sql2 = this.cartInfoJdbcDAO.getCartSellerInfoList();
		
		CartInfoListDTO cartInfoListDTO = new CartInfoListDTO();
		
		ArrayList<String> _queryValue2 = new ArrayList<>();
		_queryValue2.add(0, cartInfoDTO.getUserId());
		
		CartSellerInfoListDTO cartSellerInfoListDTO = new CartSellerInfoListDTO();
		CartSellerInfoListPDTO cartSellerInfoListPDTO = new CartSellerInfoListPDTO();
		List<CartSellerInfoListPDTO> cartSellerProductInfoList = new ArrayList<>();
		
		List<CartSellerInfoListDTO> cartSellerInfoList = this.utilService.getQueryString(sql2,cartSellerInfoListDTO,_queryValue2);
		
		int cartSellerInfoListCnt = cartSellerInfoList.size();
		
		String sql = this.cartInfoJdbcDAO.getCartInfoList();
		ArrayList<String> _queryValue = new ArrayList<>();
		
		for(int j = 0; j < cartSellerInfoListCnt; j++) {
			cartSellerInfoListPDTO = new CartSellerInfoListPDTO();
			cartSellerInfoListPDTO.setSellerId(cartSellerInfoList.get(j).getSellerId());
			cartSellerInfoListPDTO.setProductPrice(cartSellerInfoList.get(j).getProductPrice());
			cartSellerInfoListPDTO.setCombinedDeliveryYn(cartSellerInfoList.get(j).getCombinedDeliveryYn());
			cartSellerInfoListPDTO.setShopName(cartSellerInfoList.get(j).getShopName());
			cartSellerInfoListPDTO.setDeliveryAmount(cartSellerInfoList.get(j).getDeliveryAmount());
			cartSellerInfoListPDTO.setCombinedDeliveryStandardAmount(cartSellerInfoList.get(j).getCombinedDeliveryStandardAmount());
			
			_queryValue = new ArrayList<>();
			
			_queryValue.add(0, cartInfoDTO.getUserId());
			_queryValue.add(1, cartSellerInfoListPDTO.getSellerId());
			
			List<CartInfoListDTO> cartInfoList = this.utilService.getQueryString(sql,cartInfoListDTO,_queryValue); 
	 		
			int cartInfoListCnt = cartInfoList.size();
			if(cartInfoListCnt > 0) {
				//영업일여부
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HHmm");
				Date date = new Date();
				String _date = sf.format(date);
				String _dateArray[] =  _date.split(" ");
				
				String _toDate = _dateArray[0];
				String _toTime = _dateArray[1];
				int _time = Integer.parseInt(_toTime);
				int _todayDeliveryTime = 0;
				String slsDayYn = "N";
				String slsDateList[];
				String slsDateText = "";
				String _eventId = "";
				
				String eventInfoSql = this.eventMstJdbcDAO.eventProductInfo();
				ArrayList<String> _eventInfoQueryValue = new ArrayList<>();
				EventProductInfoDTO eventProductInfoDTO = new EventProductInfoDTO();
				String productId = "";
				
				BigDecimal productStock = new BigDecimal(0);
				BigDecimal _zero = new BigDecimal(0);
				BigDecimal personBuyCount = new BigDecimal(0);
				BigDecimal eventCount = new BigDecimal(0);
				BigDecimal personPaymentCount = new BigDecimal(0);
				BigDecimal eventPaymentCount = new BigDecimal(0);
				for(int i = 0; i < cartInfoListCnt; i++) {
					slsDateText = cartInfoList.get(i).getSlsDateText();
					_eventId = cartInfoList.get(i).getEventId();
					productId = cartInfoList.get(i).getProductId();
					if(slsDateText.indexOf(_toDate) > -1) {
						slsDayYn = "N";
					} else {
						slsDayYn = this.utilService.getSlsDayYn(_toDate);
					}
					
					slsDateList = slsDateText.split(";");
					if(cartInfoList.get(i).getTodayDeliveryStandardTime() == null) {
						cartInfoList.get(i).setTodayDeliveryStandardTime("1000");
					}
					
					_todayDeliveryTime = Integer.parseInt((String) cartInfoList.get(0).getTodayDeliveryStandardTime());
					
					if(_todayDeliveryTime >= _time && "Y".equals(slsDayYn)) {
						cartInfoList.get(i).setTodayDeliveryYn("Y");
					}else {
						cartInfoList.get(i).setTodayDeliveryYn("N");
					}
					
					//if("N".equals(slsDayYn) || "N".equals(cartInfoList.get(0).getTodayDeliveryYn())) {
					//	cartInfoList.get(i).setSlsDate(this.utilService.getSlsDate(_toDate , "N"));
					//}else {
						cartInfoList.get(i).setSlsDate(this.utilService.getSlsDate(_toDate , cartInfoList.get(i).getTodayDeliveryYn(), slsDateList));
					//}
						
					if(!("".equals(_eventId) || _eventId == null)) {
						_eventInfoQueryValue = new ArrayList<>();
						_eventInfoQueryValue.add(0, cartInfoDTO.getUserId());
						_eventInfoQueryValue.add(1, "003");
						_eventInfoQueryValue.add(2, productId);
						_eventInfoQueryValue.add(3, productId);
						_eventInfoQueryValue.add(4, "003");
						_eventInfoQueryValue.add(5, _eventId);
					
						List<EventProductInfoDTO> eventProductInfo = this.utilService.getQueryString(eventInfoSql,eventProductInfoDTO,_eventInfoQueryValue);
						
						productStock = new BigDecimal(cartInfoList.get(i).getProductStock().toString());
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
					
						cartInfoList.get(i).setEventBeginTime(eventProductInfo.get(0).getEventBeginTime());
						cartInfoList.get(i).setEventEndTime(eventProductInfo.get(0).getEventEndTime());
						cartInfoList.get(i).setPersonBuyCount(new BigInteger(personBuyCount.toString()));
						cartInfoList.get(i).setEventCount(new BigInteger(eventCount.toString()));
						cartInfoList.get(i).setEventId(_eventId);
						cartInfoList.get(i).setEventBeginDate(eventProductInfo.get(0).getEventBeginDate());
						cartInfoList.get(i).setEventEndDate(eventProductInfo.get(0).getEventEndDate());
					}
				}
				cartSellerInfoListPDTO.setCartProductList((ArrayList<CartInfoListDTO>) cartInfoList);
			}
			cartSellerProductInfoList.add(cartSellerInfoListPDTO);
		}
		return cartSellerProductInfoList;
	}
}
