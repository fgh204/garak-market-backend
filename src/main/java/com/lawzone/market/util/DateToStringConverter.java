package com.lawzone.market.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawzone.market.file.controller.FileController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class DateToStringConverter implements AttributeConverter<String, Date>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DateToStringConverter.class);
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public Date convertToDatabaseColumn(String attribute) {
		Date date = null;
        try {
        	if(attribute != null || "now()".equals(attribute)) {
        		if("now()".equals(attribute)) {
        			date = new Date();
        		}else {
	        		if(attribute.indexOf("T") > -1) {
	        			Date _date = sf.parse(attribute.replaceAll("T", " "));
	        			Calendar cal = Calendar.getInstance();
	                    cal.setTime(_date);
	                    cal.add(Calendar.HOUR, + 9);
	                    _date = cal.getTime();
	                    date = _date;
	        		} else {
	        			if(attribute.length() == 10) {
	        				attribute = attribute + " 00:00:00";
	        			}
	        			date = sf.parse(attribute);
	        		}
        		}
        	}else {
        		date = null;
        	}
        } catch (ParseException e) {
            LOGGER.error("[ERROR] 문자열을 Date 타입으로 변환하는데 실패하였습니다.", e);
        }
        return date;
	}

	@Override
	public String convertToEntityAttribute(Date dbData) {
		if(dbData != null) {
			String _date = sf.format(dbData);
			_date = _date.replace(" 00:00:00", "");
            return _date;
        }else {
            return null;
        }
	}
}
