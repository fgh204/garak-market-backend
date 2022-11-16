package com.lawzone.market.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDateToStringConverter implements AttributeConverter<String, Date>{
	private static final Logger LOGGER = LoggerFactory.getLogger(DateToStringConverter.class);
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public Date convertToDatabaseColumn(String attribute) {
		Date date = new Date();
       
        return date;
	}

	@Override
	public String convertToEntityAttribute(Date dbData) {
		if(dbData != null) {
			String _date = sf.format(dbData);
			
            return _date;
        }else {
            return null;
        }
	}
}