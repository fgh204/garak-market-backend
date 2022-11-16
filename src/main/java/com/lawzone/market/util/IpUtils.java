package com.lawzone.market.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.lawzone.market.cart.controller.CartController;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IpUtils {
	private static String prodIpAddr = "15.165.62.37";
	private static String prodEc2IpAddr = "172.31.5.54";
	private static String prodDomain = "trans.lawan.co.kr";
	private static String testIpAddr = "210.101.205.64";
	private static String testDomain = "testtrans.lawan.co.kr";
	
	public static String getProdIp()
	{
		return prodIpAddr;
	}
	
	public static String getProdDomain()
	{
		return prodDomain;
	}
	
	public static String getTestIp()
	{
		return testIpAddr;
	}
	
	public static String getTestDomain()
	{
		return testDomain;
	}
	
	public static boolean getMyIpByTest()
	{
		String myIp = null;
		try
		{
			myIp = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			try
			{
				log.error(e.toString());
				log.error("HOST IP 체크 실패");
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		boolean check = false;

		if (testIpAddr.equals(myIp) || "192.168.0.5".equals(myIp))//테스트서버
		{
			check = true;
		}

		return check;
	}
	
	public static boolean getMyIpByProd()
	{
		String myIp = null;
		try
		{
			myIp = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			try
			{
				log.error(e.toString());
				log.error("HOST IP 체크 실패");
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		boolean check = false;

		if (prodIpAddr.equals(myIp) || prodEc2IpAddr.equals(myIp))//운영
		{
			check = true;
		}

		return check;
	}
	
	public static boolean getMyIpByProdAndTest()
	{
		String myIp = null;
		try
		{
			myIp = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			try
			{
				log.error(e.toString());
				log.error("HOST IP 체크 실패");
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		boolean check = false;

		if (prodIpAddr.equals(myIp) || testIpAddr.equals(myIp) || prodEc2IpAddr.equals(myIp) || "192.168.0.5".equals(myIp))
		{
			check = true;
		}

		return check;
	}
	
	public static String getDomainServerName(HttpServletRequest arg0) throws UnknownHostException
	{
		String name = null;
		
		if(IpUtils.getMyIpByProd() || IpUtils.getMyIpByTest())
		{
			name = "http://" + arg0.getServerName() + ":8080";
		}
		else
		{
			name = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/law";
		}
		
		return null;
	}
}
