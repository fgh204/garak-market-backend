package com.lawzone.market.admin.dao;

import org.springframework.stereotype.Component;

import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.service.AdminProductCDTO;

@Component
public class AdminJdbcDAO {
	public String adminUserList(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n 	ui.user_id ")
				.append("\n 	, ui.user_name ")
				.append("\n 	, ui.nickname ") 
				.append("\n 	, concat(ui.user_lvl) ") 
				.append("\n 	, ui.email ")
				.append("\n 	, ui.phone_number ")
				.append("\n 	, si.shop_name ")
				.append("\n 	, si.login_id ")
				.append("\n 	, if(ifnull(si.password,'') = '', 'N', 'Y') as password ") 
				.append("\n 	, '' as passwordValue ")
				.append("\n 	, si.spot_id ")
				.append("\n 	, ui.seller_yn ")
				.append("\n 	, ui.use_yn ")
				.append("\n 	, si.spot_name  ")
				.append("\n 	, si.business_address ")
				.append("\n 	, si.seller_phone_number ")
				.append("\n 	, si.product_category_code ")
				.append("\n 	, ifnull((select ")
				.append("\n 		ceil(sum(point_value)) ")
				.append("\n 	from lz_market.point_detail_info pi2 ")
				.append("\n 	where pi2.user_id = ui.user_id ")
				.append("\n 	and pi2.point_expiration_datetime > now() ),0) ")
				.append("\n 	, ifnull(si.market_exposure_yn,'N') ")
				.append("\n 	, DATE_FORMAT(ui.create_datetime, '%Y.%m.%d %H:%i:%s') as createDatetime ")
				.append("\n 	, DATE_FORMAT(ui.withdrawal_date_time, '%Y.%m.%d %H:%i:%s') as withdrawalDatetime ")
				.append("\n from lz_market.user_info ui ")
				.append("\n 	left outer join lz_market.seller_info si ")
				.append("\n 	on ui.user_id = si.seller_id ")
				.append("\n where 1 = 1 ");
				if(!"%".equals(adminUserCDTO.getSellerYn())) {
					_query.append("\n and ui.seller_yn = ? ");
				}
				if(!"%".equals(adminUserCDTO.getUseYn())) {
					_query.append("\n and ui.use_yn = ? ");
				}
				if(!(adminUserCDTO.getUserName() == null || "".equals(adminUserCDTO.getUserName()))) {
					_query.append("\n and ui.user_name = ? ");
				}
				_query.append("\n order by ui.user_id ")
				.append("\n limit " + adminUserCDTO.getPageCnt() + ", 10 ");
		
		return _query.toString();
	}
	
	public String adminUserListPageing(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     page.cnt as total_count ")
				.append("\n     , page.p_cnt as total_pages ")
				.append("\n     , if(page.p_cnt > " + adminUserCDTO.getPageCnt() + ", 'Y', 'N') as has_next ")
				.append("\n     , if(1 < " + adminUserCDTO.getPageCnt() + ", 'Y', 'N') as has_previous ")
				.append("\n     , " + adminUserCDTO.getPageCnt() + " as page_number ")
				.append("\n from ")
				.append("\n     (select ")
				.append("\n 		    COUNT(1) as cnt ")
				.append("\n         , CEIL (COUNT(1)/10) as p_cnt ")
				.append("\n     from lz_market.user_info ui ")
				.append("\n 	left outer join lz_market.seller_info si ")
				.append("\n 		on ui.user_id = si.seller_id ")
				.append("\n     where 1 = 1 ");
				if(!"%".equals(adminUserCDTO.getSellerYn())) {
					_query.append("\n and ui.seller_yn = ? ");
				}
				if(!"%".equals(adminUserCDTO.getUseYn())) {
					_query.append("\n and ui.use_yn = ? ");
				}
				if(!(adminUserCDTO.getUserName() == null || "".equals(adminUserCDTO.getUserName()))) {
					_query.append("\n and ui.user_name = ? ");
				}
				_query.append("\n 	)page ");
					  		
		return _query.toString();
	}
	
	public String modifyAdminUserInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.user_info ui ")
				.append("\n set ui.user_lvl  = ? ")
				.append("\n 	, ui.seller_yn  = ? ")
				.append("\n 	, ui.use_yn  = ? ")
				.append("\n 	, ui.update_datetime = now() ")
				.append("\n 	, ui.update_user = ? ")
				.append("\n where ui.user_id = ? ");
					  		
		return _query.toString();
	}
	
	public String modifyAdminSellerInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.seller_info si ")
				.append("\n set si.shop_name = ? ")
				.append("\n 	, si.login_id = ? ")
				.append("\n 	, si.password = case when IFNULL(?,'') = '' then si.password else ? end ")
				.append("\n 	, si.spot_id = ? ")
				.append("\n 	, si.spot_name = ? ")
				.append("\n 	, si.business_address = ? ")
				.append("\n 	, si.seller_phone_number = ? ")			
				.append("\n 	, si.product_category_code = ? ")
				.append("\n 	, si.market_exposure_yn = ? ")
				.append("\n 	, si.update_datetime = now() ")
				.append("\n 	, si.update_user = ? ")
				.append("\n where si.seller_id = ? ");
					  		
		return _query.toString();
	}
	
	public String adminBookIdBasicInfo(String sellerIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n     	poi.recipient_name ")
			.append("\n     	, poi.phone_number ")
			.append("\n     	, concat(poi.address , ' ',  poi.detail_address ) as address ")
			.append("\n     	, poi.zonecode ")
			.append("\n     	, lz_market.FN_PRDT_NM(poii.product_id) as product_name ")
			.append("\n     	, poi.delivery_message ")
			.append("\n     	, round(poii.product_price ,0) as product_price ")
			.append("\n     	, poi.order_no ")
			.append("\n     	, poii.product_count ")
			.append("\n     	, poii.product_id ")
			.append("\n     	, date_format(poi.order_date,'%Y-%m-%d') as order_date ")
			.append("\n     	, pi2.seller_id as sellerId ")
			.append("\n     	, pi2.product_category_code as productCategoryCode ")
			.append("\n     	, poi.access_method_text ")
			.append("\n     	, si.combined_delivery_yn ")
			.append("\n     from lz_market.product_order_item_info poii ")
			.append("\n     	, lz_market.product_info pi2 ")
			.append("\n     	, lz_market.product_order_info poi ")
			.append("\n     	, lz_market.seller_info si ")
			.append("\n     where poii.product_id = pi2.product_id ")
			.append("\n     and poii.order_no  = poi.order_no ")
			.append("\n     and pi2.seller_id = si.seller_id ")
			.append("\n     and poii.order_item_dlng_state_code = ? ")
			.append("\n     and si.combined_delivery_yn = ? ")
			.append("\n 	and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
			if("Y".equals(sellerIdYn)) {
				_query.append("\n and pi2.seller_id = ?");
			}
			_query.append("\n union all ")
			.append("\n select ")
			.append("\n     	poi.recipient_name ")
			.append("\n     	, poi.phone_number ")
			.append("\n     	, concat(poi.address , ' ',  poi.detail_address ) as address ")
			.append("\n     	, poi.zonecode ")
			.append("\n     	, poi.order_name  as product_name ")
			.append("\n     	, poi.delivery_message ")
			.append("\n     	, max(poii.product_price) as product_price ")
			.append("\n     	, poi.order_no ")
			.append("\n     	, 1 ")
			.append("\n     	, poii.product_id ")
			.append("\n     	, date_format(poi.order_date,'%Y-%m-%d') as order_date ")
			.append("\n     	, pi2.seller_id as sellerId ")
			.append("\n     	, pi2.product_category_code as productCategoryCode ")
			.append("\n     	, poi.access_method_text ")
			.append("\n     	, si.combined_delivery_yn ")
			.append("\n     from lz_market.product_order_item_info poii ")
			.append("\n     	, lz_market.product_info pi2 ")
			.append("\n     	, lz_market.product_order_info poi ")
			.append("\n     	, lz_market.seller_info si ")
			.append("\n     where poii.product_id = pi2.product_id ")
			.append("\n     and poii.order_no  = poi.order_no ")
			.append("\n     and pi2.seller_id = si.seller_id ")
			.append("\n     and poii.order_item_dlng_state_code = ? ")
			.append("\n     and si.combined_delivery_yn = ? ")
			.append("\n 	and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
			if("Y".equals(sellerIdYn)) {
				_query.append("\n and pi2.seller_id = ?");
			}
			_query.append("\n 	group by poi.order_no, pi2.seller_id");
				
		return _query.toString();
	}
	
	public String adminOrderStatConfirmAllUpdate(String sellerIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info poii ")
			.append("\n , lz_market.product_info pi2 ")
			.append("\n , lz_market.product_order_info poi ")
			.append("\n set poii.order_item_dlng_state_code = ? ")
			.append("\n   	, poii.delivery_state_code = ? ")
			.append("\n where poii.product_id = pi2.product_id ")
			.append("\n and poii.order_no  = poi.order_no ")
			.append("\n and poii.order_item_dlng_state_code = ? ")
			.append("\n and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
			if("Y".equals(sellerIdYn)) {
				_query.append("\n and pi2.seller_id = ?");
			}
				
		return _query.toString();
	}
	
	public String settlementList(AdminOrderCDTO adminOrderCDTO) {
		StringBuffer _query = new StringBuffer();
		
//		_query.append("\n select ")
//				.append("\n   d.shopName as shopName ")
//				.append("\n   , format(SUM(d.productAmt), 0) as productAmt ")
//				.append("\n   , format(SUM(d.deliveryCharge), 0) as deliveryCharge ")
//				.append("\n   , format(SUM(d.productAmt - d.deliveryCharge), 0) as salesAmount ")
//				.append("\n   , format(SUM((d.productAmt - d.deliveryCharge) * 0.89), 0) as netProfit ")
//				.append("\n   , format(SUM((d.productAmt - d.deliveryCharge) * 0.11), 0) as fee ")
//				.append("\n   , DATE_FORMAT(d.minDate ,'%Y-%m-%d') as saleBeginDate ")
//				.append("\n   , DATE_FORMAT(d.maxDate,'%Y-%m-%d') as saleEndDate ")
//				.append("\n from ( ")
//				.append("\n   select ")
//				.append("\n     si.shop_name as shopName ")
//				.append("\n     , poii.product_price * poii.product_count as productAmt ")
//				.append("\n     , 3000 * poii.product_count as deliveryCharge ")
//				.append("\n     , DATE_SUB(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d'), INTERVAL ( ")
//				//.append("\n         case when DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) = 1 then 0 else DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) -2 END ")
//				.append("\n         case when DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) = 1 then 6 else DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) -2 END ")
//				.append("\n         ) day)  as minDate ")
//				.append("\n     , DATE_ADD(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d'), INTERVAL ")
//				.append("\n         case when DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) = 1 then 0 else 8 - DAYOFWEEK(DATE_FORMAT(poii.create_datetime, '%Y-%m-%d')) END ")
//				.append("\n         day) as maxDate ")
//				.append("\n   from  lz_market.product_order_item_info poii ")
//				.append("\n       , lz_market.product_info pi3 ")
//				.append("\n       , lz_market.seller_info si ")
//				.append("\n       , lz_market.sls_date_info sdi ")
//				.append("\n   where poii.product_id = pi3.product_id ")
//				.append("\n   and pi3.seller_id = si.seller_id ")
//				.append("\n   and DATE_FORMAT(poii.create_datetime, '%Y-%m-%d') =  sdi.sls_date ")
//				//.append("\n   and poii.order_item_dlng_state_code = '400') d ")
//				.append("\n   and poii.order_item_state_code = '003') d ")
//				.append("\n group by d.shopName, d.minDate ,d.maxDate ");
		
		_query.append("\nselect ")
			  .append("\n      d.shopName as shopName     , ")
			  .append("\n      SUM( ")
			  .append("\n      case when d.delivery_amount > 0 then d.productAmt + d.deliveryCharge else ")
			  .append("\n      d.productAmt end) as productAmt     , ")
			  .append("\n      SUM(d.deliveryCharge) as deliveryCharge     , ")
			  .append("\n      SUM( ")
			  .append("\n      case when d.delivery_amount > 0 then d.productAmt else ")
			  .append("\n      d.d.productAmt - d.deliveryCharge end ")
			  .append("\n      )as salesAmount     , ")
			  .append("\n      ceil(SUM(( case when d.delivery_amount > 0 then d.productAmt else  ")
			  .append("\n      d.d.productAmt - d.deliveryCharge end) * 0.89)) as netProfit     , ")
			  .append("\n      ceil(SUM(( case when d.delivery_amount > 0 then d.productAmt else  ")
			  .append("\n      d.d.productAmt - d.deliveryCharge end) * 0.11)) as fee     , ")
			  .append("\n      DATE_FORMAT(d.minDate , ")
			  .append("\n      '%Y-%m-%d') as saleBeginDate     , ")
			  .append("\n      DATE_FORMAT(d.maxDate, ")
			  .append("\n      '%Y-%m-%d') as saleEndDate, ")
			  .append("\n      SUM( d.productCount) as productCount     , ")
			  .append("\n      SUM( d.supplyPrice) as supplyPrice     , ")
			  .append("\n      SUM( d.productAmt - d.supplyPrice) as subSupplyPrice     , ")
			  .append("\n      SUM( d.productAmt) as productAmt2      ")
			  .append("\n  from ")
			  .append("\n      (  ")
			  .append("\n      select ")
			  .append("\n          si.shop_name as shopName       , ")
			  .append("\n          poii.order_no as order_no , ")
			  //.append("\n          si.product_category_code as product_category_code , ")
			  .append("\n          si.delivery_amount as delivery_amount , ")
			  .append("\n          SUM(poii.product_price * poii.product_count) as productAmt       , ")
			  //.append("\n          case when si.product_category_code = '002000000' then 3000 else SUM(3000 * poii.product_count) end as deliveryCharge, ")
			  .append("\n          case when si.delivery_amount > 0 then si.delivery_amount else SUM(3000 * poii.product_count) end as deliveryCharge, ")
			  .append("\n          DATE_SUB(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n          '%Y-%m-%d'), ")
			  .append("\n          INTERVAL ( case ")
			  .append("\n              when DAYOFWEEK(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n              '%Y-%m-%d')) = 1 then 6  ")
			  .append("\n              else DAYOFWEEK(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n              '%Y-%m-%d')) -2  ")
			  .append("\n          END           ) day)  as minDate       , ")
			  .append("\n          DATE_ADD(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n          '%Y-%m-%d'), ")
			  .append("\n          INTERVAL           case ")
			  .append("\n              when DAYOFWEEK(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n              '%Y-%m-%d')) = 1 then 0  ")
			  .append("\n              else 8 - DAYOFWEEK(DATE_FORMAT(poii.create_datetime, ")
			  .append("\n              '%Y-%m-%d')) ")
			  .append("\n          END day) as maxDate , ")
			  .append("\n          SUM(poii.product_count) as productCount , ")
			  .append("\n          SUM(pi3.supply_price  * poii.product_count) as supplyPrice ")
			  .append("\n      from ")
			  .append("\n          lz_market.product_order_item_info poii         , ")
			  .append("\n          lz_market.product_info pi3         , ")
			  .append("\n          lz_market.seller_info si         , ")
			  .append("\n          lz_market.sls_date_info sdi ")
			  .append("\n      where ")
			  .append("\n          poii.product_id = pi3.product_id     ")
			  .append("\n          and pi3.seller_id = si.seller_id     ")
			  .append("\n          and DATE_FORMAT(poii.create_datetime, '%Y-%m-%d') =  sdi.sls_date     ")
			  .append("\n          and poii.order_item_state_code = '003'")
			  .append("\n          and poii.create_datetime BETWEEN concat(DATE_SUB(?, INTERVAL (if(DAYOFWEEK(?) = 1, 6, DAYOFWEEK(?) - 2)) DAY), ' 00:00:00') and concat(DATE_ADD(?, INTERVAL (if(DAYOFWEEK(?) = 1, 0, 8 - DAYOFWEEK(?))) DAY), ' 23:59:59') ");
		if(!"00".equals(adminOrderCDTO.getSearchGb())) {
			_query.append("\n          and si.shop_name like concat('%', ? ,'%')");
		}
		_query.append("\n          group by poii.order_no, si.seller_id ")
			  .append("\n          )d   ")
			  .append("\n  group by")
			  .append("\n      d.shopName, d.minDate, d.maxDate ");
		return _query.toString();
	}
	
	public String adminProductList(AdminProductCDTO adminProductCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     pi2.product_id as productId  , ")
				.append("\n     pi2.product_name as productName  , ")
				.append("\n     pi2.product_price as productPrice  , ")
				.append("\n     pi2.supply_price as supplyPrice  , ")
				.append("\n     pi2.product_stock as productStock  , ")
				.append("\n     pi2.product_weight as productWeight  , ")
				.append("\n     pi2.product_desc as productDesc  , ")
				.append("\n     si.shop_name as shopName   , ")
				.append("\n     si.today_delivery_standard_time as todayDeliveryStandardTime   , ")
				.append("\n     pi2.product_category_code as productCategoryCode   , ")
				.append("\n     pci.product_category_small_name as productCategorySmallName  , ")
				.append("\n     ifnull(pii.delegate_thumbnail_yn,'N') as imgRegstYn ")
				.append("\n     , 0 as changProductPrice ")
				.append("\n     , '' as deliveryOrderId")
				.append("\n     , si.combined_delivery_yn as combinedDeliveryYn")
				.append("\n     , si.delivery_amount as deliveryAmount")
				.append("\n from ")
				.append("\n     lz_market.product_info pi2   ")
				.append("\n     left outer join lz_market.product_image_info pii ")
				.append("\n     on pi2.product_id = pii.product_id ")
				.append("\n     and pii.delegate_thumbnail_yn = 'Y' ")
				.append("\n     and pii.image_cfcd = '01' , ")
				.append("\n     lz_market.seller_info si  , ")
				.append("\n     lz_market.product_category_info pci ")
				.append("\n where pi2.seller_id = si.seller_id  ")
				.append("\n and pi2.product_category_code = pci.product_category_code ")
				.append("\n and pi2.event_id is null ");
				if(adminProductCDTO.getUserLvl() < 5) {
					_query.append("\n and pi2.seller_id = ? ");
				}else if(!"000".equals(adminProductCDTO.getProductCategoryCode())){
					_query.append("\n and pi2.product_category_code = ? ");
				}
				
				if(!"000".equals(adminProductCDTO.getUseYn())) {
					_query.append("\n and pi2.use_yn = ? ");
				}
				
				if(!"000".equals(adminProductCDTO.getImgRegstYn())) {
					_query.append("\n and ifnull(pii.delegate_thumbnail_yn,'N') = ? ");
				}
		return _query.toString();
	}
	
	public String adminUserInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     si.shop_name ")
				.append("\n     , product_category_code ")
				.append("\n     , ui.user_name ")
				.append("\n     , ui.user_lvl ")
				.append("\n     , si.combined_delivery_yn ")
				.append("\n     , (select  ")
				.append("\n     dtl_code_name / 1000 ")
				.append("\n     from lz_market.cd_dtl_info ")
				.append("\n     where code_no = '26' ")
				.append("\n     and dtl_code_text <= now() ")
				.append("\n     order by dtl_code_text desc ")
				.append("\n     limit 1) as pee_rate ")
				.append("\n     , if(ifnull(si.delivery_amount,0), 0, (select  ")
				.append("\n     		dtl_code_name ")
				.append("\n     from lz_market.cd_dtl_info ")
				.append("\n     where code_no = '27' ")
				.append("\n     and dtl_code_text <= now() ")
				.append("\n     order by dtl_code_text desc ")
				.append("\n     limit 1)) as delivery_amount ")
				.append("\n from lz_market.seller_info si , ")
				.append("\n     lz_market.user_info ui  ")
				.append("\n where si.seller_id = ui.user_id ")
				.append("\n and ui.use_yn = ? ")
				.append("\n and si.login_id = ? ");
		return _query.toString();
	}
	
	public String getOrderListInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     poi.order_no as orderNo ")
				.append("\n     , poi.recipient_name as custNm ")
				.append("\n     , poii.product_id as productId ")
				.append("\n     , lz_market.FN_PRDT_NM(poii.product_id) as productNm ")
				.append("\n     , poii.product_count as productCount ")
				.append("\n from lz_market.product_order_item_info poii  ")
				.append("\n     , lz_market.product_order_info poi ")
				.append("\n where poi.order_no = poii.order_no ")
				.append("\n and poii.order_no = ? ")
				.append("\n and poii.seller_id = ? ");
		return _query.toString();
	}
	
	public String addExternalLinkInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n INSERT INTO lz_market.external_link_info ")
				.append("\n (external_link_company_code, access_token, refresh_token, create_datetime, create_user, update_datetime, update_user) ")
				.append("\n VALUES ( ")
				.append("\n   ? ")
				.append("\n  , ? ")
				.append("\n  , ? ")
				.append("\n  , now() ")
				.append("\n  , ? ")
				.append("\n  , now() ")
				.append("\n  , ? ")
				.append("\n ) ")
				.append("\n on duplicate KEY ")
				.append("\n update access_token = ? ")
				.append("\n , refresh_token = ? ")
				.append("\n , update_datetime = now() ");
		return _query.toString();
	}
	
	public String addExternalLinkInfoByBiztalk() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n INSERT INTO lz_market.external_link_info ")
				.append("\n (external_link_company_code, access_token, create_datetime, create_user, update_datetime, update_user) ")
				.append("\n VALUES ( ")
				.append("\n   ? ")
				.append("\n  , ? ")
				.append("\n  , now() ")
				.append("\n  , ? ")
				.append("\n  , now() ")
				.append("\n  , ? ")
				.append("\n ) ")
				.append("\n on duplicate KEY ")
				.append("\n update access_token = ? ")
				.append("\n , update_datetime = now() ");
		return _query.toString();
	}
	
	public String kakaoTargetPointList(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n     ceil(sum(pi2.point_value)) as pointAmount ")
		.append("\n     , pi2.user_id as userId ")
		//.append("\n     , DATE_FORMAT(pi2.point_expiration_datetime, '%Y.%m.%d') as pointExpirationDate ")
		//.append("\n     , DATE_FORMAT( DATE_SUB(pi2.point_expiration_datetime, INTERVAL 3 day) , '%Y.%m.%d') as stnDate ")
		.append("\n     , DATE_FORMAT(pi2.point_expiration_datetime,'%y년 %m월 %d일') as pointExpirationDate ")
		.append("\n     , DATE_FORMAT( DATE_SUB(pi2.point_expiration_datetime,INTERVAL 3 day) ,'%y년 %m월 %d일') as stnDate ")
		.append("\n     , ui.phone_number as phoneNumber ")
		.append("\n     , ui.user_name as userName ")
		.append("\n     , ui.use_yn ")
		.append("\n from lz_market.point_detail_info pi2 ")
		.append("\n     , lz_market.user_info ui ")
		.append("\n where pi2.user_id = ui.user_id ")
		.append("\n and DATE_FORMAT(pi2.point_expiration_datetime, '%Y-%m-%d') between ? and ? ");
		
		if(!"%".equals(adminUserCDTO.getReviewYn())) {
			_query.append("\n and ui.use_yn = ? ");
		}
		_query.append("\n group by pi2.user_id , DATE_FORMAT(pi2.point_expiration_datetime, '%Y.%m.%d') ")
		.append("\n having ceil(sum(pi2.point_value)) > 0 ");
		return _query.toString();
	}
	
	public String kakaoTargetReviewList(AdminUserCDTO adminUserCDTO) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	DATE_FORMAT(poi.order_date , '%Y.%m.%d') as orderDate ")
		.append("\n 	, ui.user_name as userName ")
		.append("\n 	, ui.phone_number as phoneNumber ")
		.append("\n 	, case when pri.review_number is null then 'N' else 'Y' end as reviewYn ")
		.append("\n 	, DATE_FORMAT( DATE_ADD(poii.update_datetime , INTERVAL 1 day) , '%Y.%m.%d') as stnDate ")
		.append("\n from lz_market.product_order_info poi ")
		.append("\n , lz_market.product_order_item_info poii ")
		.append("\n left outer join lz_market.product_review_info pri ")
		.append("\n on pri.product_id = poii.product_id ")
		.append("\n and pri.order_no = poii.order_no ")
		.append("\n , lz_market.user_info ui ")
		.append("\n where poi.order_no = poii.order_no ")
		.append("\n and poi.user_id = ui.user_id ")
		.append("\n and poii.delivery_state_code = '400' ")
		.append("\n and poii.order_item_state_code = '003' ")
		.append("\n and poi.order_date between concat( ?, ' 00:00:00') and concat(?, ' 23:59:59') ");
		
		if(!"%".equals(adminUserCDTO.getReviewYn())) {
			_query.append("\n and case when pri.review_number is null then 'N' else 'Y' end = ? ");
		}
		
		_query.append("\n group by DATE_FORMAT(poi.order_date , '%Y.%m.%d') , ui.user_id ");
		return _query.toString();
	}
	
	public String deliveryOrderKey() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info a ")
				.append("\n 	set a.delivery_order_key = ? ")
				.append("\n where a.order_no = ? ")
				.append("\n and a.seller_id = ? ")
				.append("\n and a.delivery_order_key is null ");
		
		return _query.toString();
	}
	
	public String deliveryOrderKeyByProduct() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info a ")
				.append("\n 	set a.delivery_order_key = ? ")
				.append("\n where a.order_no = ? ")
				.append("\n and a.product_id = ? ")
				.append("\n and a.seller_id = ? ")
				.append("\n and a.delivery_order_key is null ");
		
		return _query.toString();
	}
}
