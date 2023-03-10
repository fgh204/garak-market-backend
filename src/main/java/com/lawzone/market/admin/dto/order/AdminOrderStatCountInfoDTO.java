package com.lawzone.market.admin.dto.order;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AdminOrderStatCountInfoDTO {
	private BigDecimal orderStayCnt;
	private BigDecimal orderReceivedCnt;
	private BigDecimal orderConfirmCnt;
	private BigDecimal deliveryRequestCnt;
	private BigDecimal orderCancellationRequestCnt;
	private BigDecimal orderCancellationCnt;
	private BigDecimal deliveryCompleteCnt;
	
	public AdminOrderStatCountInfoDTO() {
		
	}
}
