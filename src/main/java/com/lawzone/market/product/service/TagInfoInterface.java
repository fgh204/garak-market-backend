package com.lawzone.market.product.service;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public interface TagInfoInterface {
	BigInteger getTagId();
	String getTagName();
	Character getUseYn();
}
